package cn.nfsn.transaction.service.impl;

import cn.nfsn.common.core.exception.WxPayException;
import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.mapper.OrderInfoMapper;
import cn.nfsn.transaction.model.dto.ProductDTO;
import cn.nfsn.transaction.model.entity.App;
import cn.nfsn.transaction.model.entity.OrderInfo;
import cn.nfsn.transaction.service.AppService;
import cn.nfsn.transaction.service.OrderInfoService;
import cn.nfsn.transaction.utils.OrderNoUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import static cn.nfsn.common.core.enums.ResultCode.*;
import static cn.nfsn.transaction.constant.OrderConstant.*;


/**
 * @ClassName: OrderInfoServiceImpl
 * @Description: 订单相关服务的实现类，包括订单的生成、更新状态、查询等功能
 * @Author: atnibamaitay
 * @CreateTime: 2023-08-31 23:04
 **/
@Service
@Slf4j
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
        implements OrderInfoService {

    @Resource
    private AppService appService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 创建订单
     *
     * @param productDTO  商品信息
     * @param paymentType 支付类型
     * @return 返回新建的或者已存在但未支付的订单
     * @throws InterruptedException 获取分布式锁时可能会抛出此异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo createOrderByProductId(ProductDTO productDTO, String paymentType) throws InterruptedException {

        // 校验参数
        if (productDTO == null || StringUtils.isEmpty(paymentType)) {
            throw new WxPayException(PRODUCT_OR_PAY_TYPE_NULL);
        }

        // 校验 App
        App app = appService.validPayApp(productDTO.getAppId());

        // 查找商品id对应的已存在但未支付的订单
        OrderInfo orderInfo = this.getNoPayOrderByProductId(productDTO.getAppId(), productDTO.getUserId(), paymentType);

        if (orderInfo != null) {
            log.warn("订单已存在，订单号：{}", orderInfo.getOrderNo());
            return orderInfo;
        }

        //指定锁的名称，将锁的粒度为订单级别
        String lockName = ORDER_LOCK_PREFIX + productDTO.getAppId() + productDTO.getUserId();
        //获取锁
        RLock lock = redissonClient.getLock(lockName);

        log.info("开始尝试获取锁: {}", lockName);

        //尝试获取锁，参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
        boolean isLock = lock.tryLock(MAX_WAIT_TIME, EXPIRE_TIME, TIME_UNIT);

        //判断获取锁成功
        if (isLock) {
            try {
                log.info("成功获取到锁: {}", lockName);

                // 生成新的订单
                orderInfo = OrderInfo.builder()
                        .title(productDTO.getTitle())
                        // 设置订单号
                        .orderNo(OrderNoUtils.getOrderNo())
                        .productId(productDTO.getId())
                        // 设置订单金额
                        .totalFee(productDTO.getPrice())
                        // 设置订单状态为未支付
                        .orderStatus(OrderStatus.NOTPAY.getType())
                        .paymentType(paymentType)
                        // 设置用户id
                        .userId(productDTO.getUserId())
                        // 设置退款回调地址refundCallbackUrl
                        .refundCallBackUrl(app.getRefundCallBackUrl())
                        // 设置支付应用id
                        .appId(app.getId())
                        // 设置支付结果回调地址
                        .paymentCallBackUrl(app.getPaymentCallBackUrl())
                        .build();

                //插入数据
                int insertResult = baseMapper.insert(orderInfo);
                if (insertResult <= 0) {
                    throw new WxPayException(INSERT_ORDER_FAIL);
                }

                //TODO: 使用MQ设置订单超时时间，如果30分钟内，订单状态没有变成OrderStatus.SUCCESS，则30分钟超时后将订单状态设置为OrderStatus.CLOSED

                log.info("订单创建成功，订单号：{}", orderInfo.getOrderNo());

            } finally {
                // 判断当前线程是否持有锁
                if (lock.isHeldByCurrentThread()) {
                    //释放锁
                    lock.unlock();
                    log.info("锁释放成功: {}", lockName);
                } else {
                    throw new WxPayException(CREATE_ORDER_CONTRAST_LOCK_FAIL);
                }
            }
            return orderInfo;
        }
        log.warn("未获得锁，订单创建失败: {}", lockName);

        // 未获取到锁，抛出异常
        throw new WxPayException(CREATE_ORDER_FAIL);
    }

    /**
     * 查询未支付订单
     * 防止在同一个APP中重复创建订单对象
     *
     * @param appId       AppId
     * @param userId      用户id
     * @param paymentType 支付类型
     * @return 返回查询到的订单，如果没有则返回null
     */
    private OrderInfo getNoPayOrderByProductId(Integer appId, Long userId, String paymentType) {

        // 参数校验
        if (appId == null || userId == null || paymentType == null || paymentType.isEmpty()) {
            throw new WxPayException(PARAM_IS_BLANK);
        }

        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(APP_ID, appId);
        queryWrapper.eq(ORDER_STATUS, OrderStatus.NOTPAY.getType());
        queryWrapper.eq(USER_ID, userId);
        queryWrapper.eq(PAYMENT_TYPE, paymentType);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 存储订单二维码
     *
     * @param orderNo 订单号
     * @param codeUrl 二维码地址
     */
    @Override
    public void saveCodeUrl(String orderNo, String codeUrl) {
        // TODO: 并发问题：如果有多个线程同时调用这个函数更新同一个订单的二维码，可能导致数据不一致的问题。可以考虑使用乐观锁或悲观锁来解决这个问题。

        try {
            // 使用UpdateWrapper仅更新codeUrl字段
            UpdateWrapper<OrderInfo> updateWrapper = new UpdateWrapper<>();

            updateWrapper.eq(ORDER_NO, orderNo).set(CODE_URL, codeUrl);

            // 执行更新操作，并验证结果
            int rowsAffected = baseMapper.update(null, updateWrapper);

            // 如果没有行受到影响，则抛出异常
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("保存微信Native支付二维码失败，订单编号为: " + orderNo);
            }
        } catch (Exception e) {
            // 捕获并处理可能的异常
            log.error("更新订单号为{}的codeUrl时发生错误", orderNo, e);
            throw e;
        }
    }

}
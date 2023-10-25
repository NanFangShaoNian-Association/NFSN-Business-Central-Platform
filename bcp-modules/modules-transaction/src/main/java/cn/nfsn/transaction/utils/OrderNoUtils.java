package cn.nfsn.transaction.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static cn.nfsn.transaction.constant.OrderConstant.ORDER_PREFIX;
import static cn.nfsn.transaction.constant.OrderConstant.REFUND_PREFIX;

/**
 * @ClassName: OrderNoUtils
 * @Description: 订单号工具类，提供获取订单编号、退款单编号的方法
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-02 22:36
 **/
public class OrderNoUtils {

    /**
     * 获取订单编号
     *
     * @return 返回以"ORDER_"为前缀的订单编号
     */
    public static String getOrderNo() {
        return ORDER_PREFIX + getNo();
    }

    /**
     * 获取退款单编号
     *
     * @return 返回以"REFUND_"为前缀的退款单编号
     */
    public static String getRefundNo() {
        return REFUND_PREFIX + getNo();
    }

    /**
     * 获取编号
     * TODO: 考虑改用雪花算法来实现
     *
     * @return 返回由当前时间（精确到秒）和三位随机数构成的字符串
     */
    public static String getNo() {
        // 创建日期格式化对象，格式为：年月日时分秒（24小时制）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        // 使用日期格式化对象对当前时间进行格式化，获取形如"yyyyMMddHHmmss"的时间字符串
        String newDate = sdf.format(new Date());

        // 初始化结果字符串
        String result = "";

        // 创建随机对象，用于生成随机数
        Random random = new Random();

        // 循环三次，每次生成一个0-9的随机数，并添加到结果字符串中
        for (int i = 0; i < 3; i++) {
            result += random.nextInt(10);
        }

        // 将时间字符串和随机数字符串拼接在一起，作为最终结果返回
        return newDate + result;
    }
}
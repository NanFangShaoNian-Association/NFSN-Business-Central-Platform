package cn.nfsn.transaction.service.impl;

import cn.nfsn.common.core.enums.CommonStatusEnum;

import cn.nfsn.common.core.exception.AppException;

import cn.nfsn.transaction.mapper.AppMapper;
import cn.nfsn.transaction.model.entity.App;
import cn.nfsn.transaction.service.AppService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.nfsn.common.core.enums.ResultCode.APP_IS_DISABLE;
import static cn.nfsn.common.core.enums.ResultCode.APP_NOT_FOUND;


/**
* @author Atnibam Aitay
* @description 针对表【app】的数据库操作Service实现
* @createDate 2023-09-11 15:07:19
*/
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>
    implements AppService {

    @Resource
    private AppMapper appMapper;

    /**
     * 校验应用是否有效
     *
     * @param id 应用ID
     * @return 如果应用有效，则返回true；否则，抛出业务异常
     */
    @Override
    public App validPayApp(Integer id) {
        App app = appMapper.selectById(id);

        // 校验是否存在
        if (app == null) {
            throw new AppException(APP_NOT_FOUND);
        }

        // 校验是否禁用
        if (CommonStatusEnum.DISABLE.getStatus().equals(app.getStatus())) {
            throw new AppException(APP_IS_DISABLE);
        }

        return app;
    }

}
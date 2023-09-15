package cn.nfsn.transaction.service;

import cn.nfsn.transaction.model.entity.App;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Atnibam Aitay
* @description 针对表【app】的数据库操作Service
* @createDate 2023-09-11 15:07:19
*/
public interface AppService extends IService<App> {
    /**
     * 校验应用是否有效
     *
     * @param id 应用ID
     * @return 如果应用有效，则返回true；否则，抛出业务异常
     */
    App validPayApp(Integer id);

}

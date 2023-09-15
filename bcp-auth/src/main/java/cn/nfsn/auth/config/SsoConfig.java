package cn.nfsn.auth.config;

import cn.dev33.satoken.config.SaSsoConfig;
import com.dtflys.forest.Forest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: gaojianjie
 * @Description 单点配置类
 * @date 2023/9/10 21:19
 */
@Configuration
public class SsoConfig {
    /**
     * 配置SSO相关参数
     */
    @Autowired
    private void configSso(SaSsoConfig sso) {
        // 配置：未登录时返回的View
//        sso.setNotLoginView(() -> {
//            return new ModelAndView("sa-login.html");
//        });
        // 配置：未登录时返回的View

        // 配置 Http 请求处理器 （在模式三的单点注销功能下用到，如不需要可以注释掉）
        sso.setSendHttp(url -> {
            try {
                // 发起 http 请求
                return Forest.get(url).executeAsString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}

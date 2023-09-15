package cn.nfsn.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [Sa-Token 权限认证] 配置类
 *
 * @author click33
 */
@Configuration
public class SaTokenConfigure {

    /**
     * 注册 Sa-Token全局过滤器
     *
     * @return 返回SaReactorFilter对象
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                // 拦截全部路径，对所有请求进行处理
                .addInclude("/**")
                // 开放地址
                .addExclude("/favicon.ico")
                //.addExclude("/api/votes/stomp/websocket/**")
                // 鉴权方法：每次访问进入
                .addInclude("/**")
                .addExclude("/sso/*", "/favicon.ico")
                .setAuth(obj -> {
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    return SaResult.error(e.getMessage());
                });
    }
}

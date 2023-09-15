package cn.nfsn.common.swagger.constant;

import java.util.Arrays;
import java.util.List;

public class SwaggerConstants {

    /**
    * 默认校验KEY
    */
    public static String ACCESS_TOKEN_KEY = "Authorization";

    /**
    * 默认的排除路径，排除Spring Boot默认的错误处理路径和端点
    */
    public static List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

    /**
    * 默认根路径
    */
    public static String DEFAULT_BASE_PATH = "/**";

    /**
    * 默认包
    */
    public static String BASE_PACKAGE = "cn.nfsn";
}
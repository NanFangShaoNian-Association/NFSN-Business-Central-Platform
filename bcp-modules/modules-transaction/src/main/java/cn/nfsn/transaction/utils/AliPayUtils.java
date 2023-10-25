package cn.nfsn.transaction.utils;

import cn.hutool.core.net.URLDecoder;
import com.alipay.api.internal.util.file.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AliPayUtils
 * @Description:
 * @Author: atnibamaitay
 * @CreateTime: 2023/10/6 0006 23:42
 **/
public class AliPayUtils {
    /**
     * 将Map的值转化为字符串类型.
     *
     * @param params 参数
     * @return 转换后的Map
     */
    public static Map<String, String> convertParamsToStringKey(Map<String, Object> params) {
        return params.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue() != null ? e.getValue().toString() : ""
                ));
    }

    /**
     * 获取指定键的参数值。
     *
     * @param params 包含了所有需要验证的参数
     * @param key    需要获取值的键
     * @return 返回对应键的值，如果没有找到则抛出异常
     */
    public static String getStringParam(Map<String, String> params, String key) {
        String value = params.get(key);
        if (value == null) {
            throw new RuntimeException("Missing required param: " + key);
        }
        return value;
    }

    /**
     * 从请求中解析参数。
     *
     * @param request HttpServletRequest对象，用于获取请求参数等信息
     * @return 返回值为一个Map，键和值都是字符串类型
     * @throws IOException 如果从request中读取参数出现异常
     */
    public static Map<String, String> parseParamsFromRequest(HttpServletRequest request) throws IOException {
        String paramsStr = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        try {
            return Arrays.stream(paramsStr.split("&"))
                    .map(AliPayUtils::splitQueryParameter)
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing params: " + paramsStr, e);
        }
    }

    /**
     * 将查询参数字符串分割为键值对.
     *
     * @param it 查询参数字符串
     * @return 键值对
     */
    public static AbstractMap.SimpleEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        return new AbstractMap.SimpleEntry<>(
                URLDecoder.decode(key, StandardCharsets.UTF_8),
                URLDecoder.decode(value, StandardCharsets.UTF_8)
        );
    }


}

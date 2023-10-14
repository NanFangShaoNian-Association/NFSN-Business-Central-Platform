package cn.nfsn.chat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * https://cloud.tencent.com/developer/article/2144183
 * @author: Tuanzi
 */
@Api("")
@RestController
@RequestMapping("/")
@ApiImplicitParams({
        @ApiImplicitParam(name = "token", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class)
})
public class TestController {
    @ApiOperation("")
    @GetMapping("/test")
    public String test(String name) {
        return name == null || "".equals(name) ? "hello" : name;
    }
}

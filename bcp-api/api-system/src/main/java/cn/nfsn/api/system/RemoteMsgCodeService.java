package cn.nfsn.api.system;

import cn.nfsn.common.core.domain.R;
import cn.nfsn.common.core.domain.dto.VerifyCodeDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author: gaojianjie
 * @Description 验证码远程调用接口
 * @date 2023/9/19 11:18
 */
public interface RemoteMsgCodeService {
    @RequestMapping(method = RequestMethod.POST,value = "/message-code")
    public R sendCodeByAccount(@RequestBody @Validated VerifyCodeDTO verifyCodeDTO);
}

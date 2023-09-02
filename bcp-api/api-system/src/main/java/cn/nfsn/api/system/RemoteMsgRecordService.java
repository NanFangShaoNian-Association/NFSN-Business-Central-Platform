package cn.nfsn.api.system;

import cn.nfsn.api.system.interceptor.SystemFeignInterceptor;
import cn.nfsn.common.core.domain.LocalMessageRecord;
import cn.nfsn.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/28 21:39
 */
@FeignClient(value = "modules-system",configuration = SystemFeignInterceptor.class)
public interface RemoteMsgRecordService {
    @PostMapping("/msg-record")
    public R saveMsgRecord(@RequestBody LocalMessageRecord localMessageRecord);

    @PutMapping("/msg-record")
    public R updateMsgRecord(@RequestBody LocalMessageRecord localMessageRecord);

    @GetMapping("/msg-record/fail-record")
    public R<List<LocalMessageRecord>> queryFileStateMsgRecord();
}

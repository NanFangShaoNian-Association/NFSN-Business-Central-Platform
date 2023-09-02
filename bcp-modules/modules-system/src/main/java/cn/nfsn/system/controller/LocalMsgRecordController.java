package cn.nfsn.system.controller;

import cn.nfsn.api.system.RemoteMsgRecordService;
import cn.nfsn.common.core.domain.LocalMessageRecord;
import cn.nfsn.common.core.domain.R;
import cn.nfsn.system.service.LocalMessageRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/28 16:32
 */
@RestController
@RequestMapping("/msg-record")
public class LocalMsgRecordController implements RemoteMsgRecordService {
    @Autowired
    private LocalMessageRecordService localMessageRecordService;
    @PostMapping
    public R saveMsgRecord(@RequestBody @Validated LocalMessageRecord localMessageRecord){
        localMessageRecordService.saveMsgRecord(localMessageRecord);
        return R.ok();
    }

    @PutMapping
    public R updateMsgRecord(@RequestBody @Validated LocalMessageRecord localMessageRecord){
        localMessageRecordService.updateMsgRecordByMsgKey(localMessageRecord);
        return R.ok();
    }

    @GetMapping("/fail-record")
    public R<List<LocalMessageRecord>> queryFileStateMsgRecord(){
        return R.ok(localMessageRecordService.queryFailStateMsgRecord());
    }
}

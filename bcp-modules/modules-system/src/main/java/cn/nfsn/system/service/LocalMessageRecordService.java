package cn.nfsn.system.service;


import cn.nfsn.common.core.domain.LocalMessageRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author gaojianjie
* @description 针对表【local_message_record】的数据库操作Service
* @createDate 2023-08-27 23:07:14
*/
public interface LocalMessageRecordService extends IService<LocalMessageRecord> {

    void saveMsgRecord(LocalMessageRecord localMessageRecord);

    void updateMsgRecordByMsgKey(LocalMessageRecord localMessageRecord);

    List<LocalMessageRecord> queryFailStateMsgRecord();
}



package cn.nfsn.system.mapper;


import cn.nfsn.common.core.domain.LocalMessageRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author gaojianjie
* @description 针对表【local_message_record】的数据库操作Mapper
* @createDate 2023-08-27 23:07:14
* @Entity generator.domain.LocalMessageRecord
*/
public interface LocalMessageRecordMapper extends BaseMapper<LocalMessageRecord> {
    List<LocalMessageRecord> queryFailStateMsgRecord();

    void updateMsgRecordByMsgKey(LocalMessageRecord localMessageRecord);
}





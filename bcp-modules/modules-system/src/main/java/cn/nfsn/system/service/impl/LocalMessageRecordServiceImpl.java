package cn.nfsn.system.service.impl;

import cn.nfsn.common.core.domain.LocalMessageRecord;
import cn.nfsn.system.mapper.LocalMessageRecordMapper;
import cn.nfsn.system.service.LocalMessageRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author gaojianjie
* @description 针对表【local_message_record】的数据库操作Service实现
* @createDate 2023-08-27 23:07:14
*/
@Service
public class LocalMessageRecordServiceImpl extends ServiceImpl<LocalMessageRecordMapper, LocalMessageRecord>
    implements LocalMessageRecordService {
    @Autowired
    private LocalMessageRecordMapper localMessageRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMsgRecord(LocalMessageRecord localMessageRecord) {
        this.save(localMessageRecord);
    }

    @Override
    public List<LocalMessageRecord> queryFailStateMsgRecord() {
        return localMessageRecordMapper.queryFailStateMsgRecord();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMsgRecordByMsgKey(LocalMessageRecord localMessageRecord) {
        try {
            localMessageRecordMapper.updateMsgRecordByMsgKey(localMessageRecord);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}





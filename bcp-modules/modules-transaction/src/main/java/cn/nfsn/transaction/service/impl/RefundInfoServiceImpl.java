package cn.nfsn.transaction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.nfsn.transaction.model.entity.RefundInfo;
import cn.nfsn.transaction.service.RefundInfoService;
import cn.nfsn.transaction.mapper.RefundInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author Atnibam Aitay
* @description 针对表【refund_info】的数据库操作Service实现
* @createDate 2023-09-07 14:15:09
*/
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo>
    implements RefundInfoService{

}





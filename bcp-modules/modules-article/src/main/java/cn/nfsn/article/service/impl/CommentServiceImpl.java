package cn.nfsn.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.nfsn.article.model.entity.Comment;
import cn.nfsn.article.service.CommentService;
import cn.nfsn.article.mapper.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author Atnibam Aitay
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2023-10-19 21:00:55
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}





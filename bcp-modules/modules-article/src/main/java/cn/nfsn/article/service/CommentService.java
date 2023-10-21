package cn.nfsn.article.service;

import cn.nfsn.article.model.entity.Comment;
import cn.nfsn.common.core.domain.R;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @ClassName: CommentService
 * @Description: 针对表【comment】的数据库操作Service
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-19 21:00
 **/
public interface CommentService extends IService<Comment> {

    /**
     * 根据对象ID获取评论
     *
     * @param objectId 对象的ID
     * @param pageNum  页码
     * @param pageSize 每页的数量
     * @return R 评论结果
     */
    R getCommentByObjectId(Integer objectId, Integer pageNum, Integer pageSize);

}

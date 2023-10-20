package cn.nfsn.article.service;

import cn.nfsn.article.model.dto.CommentNodeDTO;
import cn.nfsn.article.model.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @ClassName: CommentService
 * @Description: 针对表【comment】的数据库操作Service
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-19 21:00
 **/
public interface CommentService extends IService<Comment> {

    /**
     * 根据objectId、pageNum和pageSize构建评论树
     *
     * @param objectId 对象ID
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @return 评论节点列表
     */
    List<CommentNodeDTO> buildCommentTree(Integer objectId, Integer pageNum, Integer pageSize);

}

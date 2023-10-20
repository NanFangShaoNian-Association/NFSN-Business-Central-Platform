package cn.nfsn.article.model.dto;

import cn.nfsn.article.model.entity.Comment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: CommentNode
 * @Description: 评论节点类，包含评论本身以及其子评论
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-19 22:07
 **/
@Getter
@Setter
public class CommentNodeDTO {
    /**
     * 评论
     */
    @ApiModelProperty("评论")
    private Comment comment;

    /**
     * 子评论列表
     */
    @ApiModelProperty("子评论列表")
    private List<CommentNodeDTO> children = new ArrayList<>();

    /**
     * 构造函数，用于创建一个新的CommentNode实例
     *
     * @param comment 要包含在新节点中的评论
     */
    public CommentNodeDTO(Comment comment) {
        this.comment = comment;
    }
}

package cn.nfsn.article.mapper;

import cn.nfsn.article.model.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @ClassName: CommentMapper
 * @Description: 针对表【comment】的数据库操作Mapper，包含查找根评论和子评论的操作。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-19 21:00
 **/
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 根据对象ID获取主评论
     *
     * @param objectId 指定查询评论的对象ID
     * @param pageNum  指定查询评论的页码
     * @param pageSize 指定查询评论的每页大小
     * @return 返回主评论列表
     */
    @Select("SELECT * FROM comment WHERE object_id = #{objectId} AND object_type in ('0', '1', '2') LIMIT #{pageSize} OFFSET #{pageNum}")
    List<Comment> findRootCommentByObjectId(@Param("objectId") Integer objectId, @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    /**
     * 根据父评论ID获取子评论
     *
     * @param parentId 指定查询评论的父评论ID
     * @return 返回子评论列表
     */
    @Select("SELECT * FROM comment WHERE object_id = #{parentId} AND object_type = '3'")
    List<Comment> findSubCommentByParentId(@Param("parentId") Integer parentId);
}

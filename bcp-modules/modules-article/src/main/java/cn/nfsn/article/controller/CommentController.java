package cn.nfsn.article.controller;

import cn.nfsn.article.service.CommentService;
import cn.nfsn.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName: CommentController
 * @Description: 处理与评论相关动作的控制器。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-20 13:54
 **/
@Api(tags = "CommentAPI")
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 根据对象ID获取评论树
     * 这个端点允许你获取特定对象的评论树列表
     *
     * @param objectId 需要获取评论的对象的ID
     * @param pageNum  需要获取的页码，默认值为1
     * @param pageSize 每一页的大小，默认值为10
     * @return 返回构建的评论树
     */
    @ApiOperation(value = "通过对象ID获取评论树")
    @GetMapping("/{objectId}")
    public R getComments(
            @ApiParam(value = "需要获取评论的对象的ID。", required = true)
            @PathVariable Integer objectId,
            @ApiParam(value = "需要检索的评论的页数。", defaultValue = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "要检索的评论每一页的大小。", defaultValue = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(commentService.buildCommentTree(objectId, pageNum, pageSize));
    }

}

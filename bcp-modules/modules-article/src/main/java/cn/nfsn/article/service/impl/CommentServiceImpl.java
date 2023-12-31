package cn.nfsn.article.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.nfsn.article.constant.CommonCacheContant;
import cn.nfsn.article.mapper.CommentMapper;
import cn.nfsn.article.model.dto.CommentNodeDTO;
import cn.nfsn.article.model.entity.Comment;
import cn.nfsn.article.service.CommentService;
import cn.nfsn.common.core.domain.R;
import cn.nfsn.common.redis.utils.CacheUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.nfsn.article.constant.CommonCacheContant.COMMENT_CACHE_PREFIX;
import static cn.nfsn.article.constant.CommonCacheContant.COMMENT_REBUILD_LOCK_TTL;
import static cn.nfsn.common.redis.constant.CacheConstants.LOCK_KEY;
import static cn.nfsn.common.redis.constant.CacheConstants.REDIS_SEPARATOR;

/**
 * @ClassName: CommentServiceImpl
 * @Description: 评论相关业务的具体实现
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-19 21:00
 **/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    private final StringRedisTemplate stringRedisTemplate;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private CacheUtil cacheUtil;

    public CommentServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 根据对象ID获取评论
     * 使用了互斥锁防止缓存击穿的思路
     *
     * @param objectId 对象的ID
     * @param pageNum  页码
     * @param pageSize 每页的数量
     * @return R 评论结果
     */
    @Override
    public R getCommentByObjectId(Integer objectId, Integer pageNum, Integer pageSize) {
        // 用于存储评论的列表
        List<CommentNodeDTO> commentNodeDTOList;

        // 构造缓存键
        // TODO:这里需要改成一个更合理的缓存键
        String cacheKey = COMMENT_CACHE_PREFIX + pageNum + REDIS_SEPARATOR + pageSize + REDIS_SEPARATOR + objectId;

        // 尝试从缓存中查询评论
        String cacheJson = stringRedisTemplate.opsForValue().get(cacheKey);

        // 缓存中存在评论
        if (StrUtil.isNotBlank(cacheJson)) {
            return JSONUtil.toBean(cacheJson, R.class);
        }

        // 判断是否命中空值
        if (cacheJson != null) {
            return null;
        }

        // 构建互斥锁键
        // TODO:这里需要改成一个更合理的互斥锁键
        String lockKey = LOCK_KEY + objectId + pageNum + pageSize;

        while (true) {
            try {
                // 尝试获取互斥锁，防止缓存击穿
                boolean isLock = cacheUtil.tryLock(lockKey, COMMENT_REBUILD_LOCK_TTL, TimeUnit.SECONDS);

                // 成功获取锁
                if (isLock) {
                    // 从数据库中获取评论
                    commentNodeDTOList = buildCommentTree(objectId, pageNum, pageSize);

                    // 增添到缓存
                    cacheUtil.set(cacheKey, commentNodeDTOList,
                            CommonCacheContant.COMMENT_CACHE_TTL, TimeUnit.HOURS);

                    // 成功获取数据后跳出循环
                    break;
                } else {
                    // 获取锁失败，休眠一段时间后继续尝试
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                log.error("尝试获取锁时发生中断异常", e);
            } finally {
                // 无论是否获取成功，最后都需要释放锁
                cacheUtil.unlock(lockKey);
            }
        }

        return R.ok(commentNodeDTOList);
    }


    /**
     * 根据objectId、pageNum和pageSize构建评论树
     *
     * @param objectId 对象ID
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @return 评论节点列表
     */
    private List<CommentNodeDTO> buildCommentTree(Integer objectId, Integer pageNum, Integer pageSize) {

        // 获取objectId对应的所有根评论
        List<Comment> rootCommentList = getRootCommentByObjectId(objectId, pageNum, pageSize);

        // 初始化一个ArrayList，用于存储根评论的转化结果（由Comment类型转化为CommentNodeDTO类型）
        List<CommentNodeDTO> rootComments = new ArrayList<>();
        for (Comment comment : rootCommentList) {
            rootComments.add(new CommentNodeDTO(comment));
        }

        // 初始化一个HashMap ，用于存储每个根评论的子评论，其中key为父评论ID，value为该父评论下的所有子评论
        Map<Integer, List<CommentNodeDTO>> groupedSubComments = new HashMap<>();

        // 遍历根评论，并获取每个根评论的所有子评论
        for (CommentNodeDTO rootComment : rootComments) {

            // 使用父评论ID去查询子评论
            List<Comment> subCommentList = getSubCommentByParentId(rootComment.getComment().getId());

            // 遍历子评论，并将其添加到groupedSubComments中
            for (Comment comment : subCommentList) {
                groupedSubComments.computeIfAbsent(comment.getObjectId(), k -> new ArrayList<>())
                        .add(new CommentNodeDTO(comment));
            }

            // 对每个根评论进行深度优先搜索，找出其所有子评论，并将子评论添加到对应的根评论下
            dfs(rootComment, groupedSubComments);
        }

        // 返回包含所有根评论及其子评论的列表
        return rootComments;
    }

    /**
     * 根据objectId获取所有根评论
     *
     * @param objectId 对象ID
     * @return 根评论列表
     */
    private List<Comment> getRootCommentByObjectId(Integer objectId, Integer pageNum, Integer pageSize) {
        pageNum = (pageNum - 1) * pageSize;
        return commentMapper.findRootCommentByObjectId(objectId, pageNum, pageSize);
    }

    /**
     * 根据父评论ID获取所有子评论，如果子评论下还有子评论，也一并获取
     *
     * @param parentId 父评论ID
     * @return 所有的子评论列表
     */
    private List<Comment> getSubCommentByParentId(Integer parentId) {

        // 创建一个结果集合用于存储所有查到的子评论
        List<Comment> allSubComments = new ArrayList<>();

        // 使用parentId查找子评论
        List<Comment> subComments = commentMapper.findSubCommentByParentId(parentId);

        // 如果查找到了子评论，则继续查找其子评论
        if (!subComments.isEmpty()) {

            // 将查找到的子评论添加到结果集合中
            allSubComments.addAll(subComments);

            // 对每个子评论进行递归查找其子评论
            for (Comment comment : subComments) {
                // 从当前子评论开始，递归地获取其所有子评论
                List<Comment> childSubComments = getSubCommentByParentId(comment.getId());
                // 将递归获取的子评论添加到结果集合中
                allSubComments.addAll(childSubComments);
            }
        }

        // 返回所有查找到的子评论
        return allSubComments;
    }

    /**
     * 通过深度优先搜索（DFS）遍历并构建评论树。首先，我们在所有的子评论中通过父节点ID进行分组，
     * 这样就可以快速查找到给定节点的所有子节点。然后，对于每个节点，我们查找它的所有子节点，
     * 并进一步为每个子节点执行相同的操作。这是一个递归流程。
     *
     * @param current         当前节点
     * @param groupedComments 基于父节点ID分组的评论节点
     */
    private void dfs(CommentNodeDTO current, Map<Integer, List<CommentNodeDTO>> groupedComments) {
        // 从groupedComments中获取当前节点的所有子节点
        List<CommentNodeDTO> children = groupedComments.get(current.getComment().getId());

        // 如果当前节点有子节点则进入if语句块
        if (children != null) {
            // 遍历当前节点的所有子节点
            for (CommentNodeDTO child : children) {
                // 将子节点添加到当前节点的子节点列表中
                current.getChildren().add(child);
                // 递归处理该子节点，即对该子节点再次执行dfs方法，查找其子节点
                dfs(child, groupedComments);
            }
        }

    }

}

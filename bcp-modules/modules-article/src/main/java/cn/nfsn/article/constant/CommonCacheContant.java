package cn.nfsn.article.constant;

/**
 * @ClassName: CommonCacheContant
 * @Description: 评论模块的缓存常量
 * @Author: AtnibamAitay
 * @CreateTime: 2023/10/21 0021 15:44
 **/
public class CommonCacheContant {
    /**
     * 评论数据的缓存前缀
     */
    public static final String COMMENT_CACHE_PREFIX = "comment:";

    /**
     * 评论数据的缓存过期时间，单位：小时
     */
    public static final Long COMMENT_CACHE_TTL = 24L;

    /**
     * 获取评论时，重建数据的时候锁的过期时间
     */
    public static final Long COMMENT_REBUILD_LOCK_TTL = 5L;

}
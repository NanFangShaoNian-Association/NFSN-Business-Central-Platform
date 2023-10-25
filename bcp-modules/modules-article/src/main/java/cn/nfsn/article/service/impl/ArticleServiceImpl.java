package cn.nfsn.article.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.nfsn.article.model.dto.ArticleDTO;
import cn.nfsn.article.service.ArticleService;
import cn.nfsn.common.core.exception.UserOperateException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.nfsn.article.model.entity.Article;
import cn.nfsn.article.mapper.ArticleMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static cn.nfsn.common.core.enums.ResultCode.PARAM_IS_BLANK;

/**
* @author huanghuiyuan
* @description 针对表【article】的数据库操作Service实现
* @createDate 2023-08-16 15:15:05
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void publish(String userId, ArticleDTO articleDTO) {
        if (StrUtil.isBlank(userId)) {
            throw new UserOperateException(PARAM_IS_BLANK);
        }
        //获取文章标题和内容
        String title = articleDTO.getTitle();
        String originContent = articleDTO.getContent();
        //对原始的HtmlStr特殊字符进行转义
        String handledContent = HtmlUtil.escape(originContent);
        //创建Article对象
        Article article = new Article();
        article.setAuthorId(userId);
        article.setTitle(title);
        article.setContent(handledContent);

        // 获取当前的时间戳并写入文章实体
        long timestampInMillis = System.currentTimeMillis();
        article.setCreateTime(timestampInMillis);
        article.setUpdateTime(timestampInMillis);

        //写入MongoDB数据库
        //articleMapper.insert(article);
        mongoTemplate.insert(article);
    }

    @Override
    public Article selectArticleById(Integer articleId) {
        if (articleId == null) {
            throw new UserOperateException(PARAM_IS_BLANK);
        }
        //todo 增加缓存
        Article article = mongoTemplate.findById(articleId, Article.class);
        return article;
    }

    @Override
    public List<Article> selectMomentsOfUserByUserId(String userId) {
        if (StrUtil.isBlank(userId)) {
            throw new UserOperateException(PARAM_IS_BLANK);
        }
        //todo 根据用户id查出关注列表的动态(朋友圈)
        return null;
    }


}





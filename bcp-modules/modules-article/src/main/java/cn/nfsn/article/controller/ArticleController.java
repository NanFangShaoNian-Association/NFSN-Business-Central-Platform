package cn.nfsn.article.controller;

import cn.nfsn.article.model.dto.ArticleDTO;
import cn.nfsn.article.model.entity.Article;
import cn.nfsn.article.service.ArticleService;
import cn.nfsn.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @className: ArticleController
 * @author: huanghuiyuan
 * @createTime: 2023/8/16 15:10
 * @description: 文章的前后端交互的控制层
 */

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @PostMapping("/user/{userId}")
    public void publishArticle(@PathVariable("userId") String userId,
                               @RequestBody ArticleDTO articleDTO) {
        articleService.publish(userId, articleDTO);
    }

    @GetMapping("/{articleId}")
    public R selectArticle(@PathVariable("articleId") Integer articleId) {
        Article article = articleService.selectArticleById(articleId);
        return R.ok(article);
    }

    @GetMapping("/moment/user{userId}")
    public R selectMoment(@PathVariable("userId") String userId) {
        List<Article> articles = articleService.selectMomentsOfUserByUserId(userId);
        return R.ok(articles);
    }
}

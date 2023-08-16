package cn.nfsn.article.controller;

import cn.nfsn.article.model.dto.ArticleDTO;
import cn.nfsn.article.service.ArticleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @PostMapping
    public void publishArticle(ArticleDTO articleDTO) {
        articleService.publish(articleDTO);
    }
}

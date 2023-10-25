package cn.nfsn.article;

import cn.nfsn.ArticlePlatformApplication;
import cn.nfsn.article.model.dto.ArticleDTO;
import cn.nfsn.article.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = ArticlePlatformApplication.class)
public class ApplicationTest {

    @Resource
    private ArticleService articleService;

    @Test
    void test1() {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setContent("Today is Thursday and tomorrow is Friday");
        articleDTO.setTitle("Hello Mongo");
        articleService.publish(articleDTO);
    }

}

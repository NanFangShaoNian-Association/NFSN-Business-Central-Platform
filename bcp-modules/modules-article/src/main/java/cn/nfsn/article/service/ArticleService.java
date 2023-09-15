package cn.nfsn.article.service;

import cn.nfsn.article.model.dto.ArticleDTO;
import cn.nfsn.article.model.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author huanghuiyuan
* @description 针对表【article】的数据库操作Service
* @createDate 2023-08-16 15:15:05
*/
public interface ArticleService extends IService<Article> {

    void publish(ArticleDTO articleDTO);
}

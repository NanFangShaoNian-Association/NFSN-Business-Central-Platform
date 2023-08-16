package cn.nfsn.article.model.dto;

import java.io.Serializable;

/**
 * @className: ArticleDTO
 * @author: huanghuiyuan
 * @createTime: 2023/8/16 15:34
 * @description: 接收前端传入文章数据的类
 */
public class ArticleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

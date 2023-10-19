package cn.nfsn.article.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName comment
 */
@TableName(value ="comment")
@Data
public class Comment implements Serializable {
    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 对象ID（文章/视频/商品/父评论）
     */
    private Integer objectId;

    /**
     * 对象类型（0代表文章评论、1代表视频评论、2代表商品评论、3代表评论的子评论）
     */
    private String objectType;

    /**
     * 内容
     */
    private String content;

    /**
     * 内容类型（0代表文本，1代表表情包，2代表图片）
     */
    private Integer type;

    /**
     * 是否删除（0未删除，1已删除）
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
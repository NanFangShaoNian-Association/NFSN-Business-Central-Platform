package cn.nfsn.system.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 发送邮箱验证码工具类
 */
@Component
public class SendEmailUtil {
    /**
     * 发送验证码
     *
     * @param email  接收邮箱
     * @param code   验证码
     * @return void
     */
    //SpringBoot 提供了应该发邮件的简单抽象，
    // 使用的是下面这个接口，这里直接注入即可使用
    @Autowired
    private JavaMailSender javaMailSender;

    //根据配置文件中自己的QQ邮箱
    private static final String FROM = "1151214239@qq.com";

    public String loginPrefix = "登录验证码为:";

    public static final String SUFFIX = "，为了您的账号安全，请勿外泄，该验证码5分钟内有效";

    /*
    @Param to 收件人
    @Param subject 主题
    @Param content
     */

    public String getContent(String content,String subject) {
        return getContent(loginPrefix, content,subject);
    }

    public static String getContent(String prefix, String content,String subject) {
        return subject+prefix + content + SUFFIX;
    }

    //发送普通邮件
    public void sendSimpleMail(String to, String subject, String code) {
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送
        message.setFrom(FROM);
        //邮件接收人,可以同时发给很多人
        //message.setTo(string1,string2,string3,string4,);
        message.setTo(to);
        //邮件主体
        message.setSubject(subject);
        //邮件内容
        message.setText(getContent(code,subject));
        //通过JavaMailSender类把邮件发送出去
        javaMailSender.send(message);
    }

}
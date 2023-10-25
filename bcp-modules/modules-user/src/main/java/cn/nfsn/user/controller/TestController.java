package cn.nfsn.user.controller;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.nfsn.api.article.TestRemoteService;
import cn.nfsn.api.system.RemoteMsgRecordService;
import cn.nfsn.common.core.constant.Constants;
import cn.nfsn.common.core.domain.LocalMessageRecord;
import cn.nfsn.common.rocketmq.constant.RocketMQConstants;
import cn.nfsn.common.rocketmq.service.MQProducerService;
import com.dtflys.forest.Forest;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: gaojianjie
 * @Description 测试
 * @date 2023/8/10 16:29
 */
//@Api("测试案例")
@RestController
public class TestController {
    @Autowired
    private TestRemoteService testRemoteService;
    @Autowired
    private MQProducerService mqProducerService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RemoteMsgRecordService remoteMsgRecordService;

    @Value("${swagger.title}")
    private String title;
    @Value("${redis.password}")
    private String host;

//    @ApiOperation("Feign测试接口")
    @GetMapping("/test")
    public void test(){
        System.out.println(remoteMsgRecordService.queryFileStateMsgRecord().getData().toString());
//        return testRemoteService.testRemoteService();
    }

    @RequestMapping("/config/get")
    public String get() {
        return title+" "+host;
    }

    @GetMapping("/testLocalMsgRecord")
    @Transactional
    public void testLocalMsgRecord(){
        LocalMessageRecord messageRecord = mqProducerService.getMsgRecord(RocketMQConstants.DELAY_TOPIC, RocketMQConstants.LOGOUT_DELAY_TAG,"i am body", Constants.USER_SERVICE, Constants.DELAY_LOGOUT);
        remoteMsgRecordService.saveMsgRecord(messageRecord);
        System.out.println("something todo....");
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rocketMQTemplate.asyncSend(messageRecord.getTopic() + ":"+messageRecord.getTags(), MessageBuilder.withPayload(messageRecord.getBody()).build(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        remoteMsgRecordService.updateMsgRecord(mqProducerService.asyncMsgRecordOnSuccessHandler(messageRecord,sendResult));
                    }
                    @Override
                    public void onException(Throwable throwable) {
                        //log.error
                        remoteMsgRecordService.updateMsgRecord(mqProducerService.asyncMsgRecordOnFailHandler(messageRecord));
                    }
                });
            }
        });
    }

    // SSO-Client端：首页
    @RequestMapping("/")
    public String index() {
        String str = "<h2>Sa-Token SSO-Client 应用端</h2>" +
                "<p>当前会话是否登录：" + StpUtil.isLogin() + "</p>" +
                "<p><a href=\"javascript:location.href='/sso/login?back=' + encodeURIComponent(location.href);\">登录</a> " +
                "<a href='/sso/logout?back=self'>注销</a></p>";
        return str;
    }

    /*
     * SSO-Client端：处理所有SSO相关请求
     * 		http://{host}:{port}/sso/login			-- Client端登录地址，接受参数：back=登录后的跳转地址
     * 		http://{host}:{port}/sso/logout			-- Client端单点注销地址（isSlo=true时打开），接受参数：back=注销后的跳转地址
     * 		http://{host}:{port}/sso/logoutCall		-- Client端单点注销回调地址（isSlo=true时打开），此接口为框架回调，开发者无需关心
     */
    @RequestMapping("/sso/*")
    public Object ssoRequest() {
        return SaSsoProcessor.instance.clientDister();
    }

    // 配置SSO相关参数
    @Autowired
    private void configSso(SaSsoConfig sso) {
        // 配置Http请求处理器
        sso.setSendHttp(url -> {
            return Forest.get(url).executeAsString();
        });
    }

    // 查询我的账号信息
    @RequestMapping("/sso/myInfo")
    public Object myInfo() {
        // 组织请求参数
        Map<String, Object> map = new HashMap<>();
        map.put("loginId", StpUtil.getLoginId());
        map.put("appCode",2);
        // 发起请求
        return SaSsoUtil.getData(map);
    }



}

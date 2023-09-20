package cn.nfsn.transaction.model.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ResponseDto
 * @Description: 用于封装响应微信通知接口的数据传输对象，包含了响应的状态码和消息
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-14 15:20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePayNotifyDTO {
    /**
     * 响应的状态码
     */
    private String code;

    /**
     * 响应的消息
     */
    private String message;
}

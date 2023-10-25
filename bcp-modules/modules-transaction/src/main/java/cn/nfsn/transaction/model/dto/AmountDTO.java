package cn.nfsn.transaction.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName: AmountDTO
 * @Description: 封装金额和货币类型的数据传输对象。
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-14 12:25
 **/
@Data
@Builder
public class AmountDTO {

    /**
     * 总金额，单位为分，使用Integer以避免精度问题。
     */
    private Integer total;

    /**
     * 货币类型，例如"USD"表示美元，"CNY"表示人民币。
     */
    private String currency;
}

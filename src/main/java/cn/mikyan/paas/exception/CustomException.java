package cn.mikyan.paas.exception;

import cn.mikyan.paas.constant.enums.ResultEnum;
import lombok.Data;

@Data
public class CustomException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 9219561917744347469L;

    private Integer code;

    public CustomException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public CustomException(Integer code , String info) {
        super(info);
        this.code = code;
    }
    
}
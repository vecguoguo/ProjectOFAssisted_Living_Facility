package com.neusoft.yyzx.common.exception;

import com.neusoft.yyzx.common.result.ResultCode;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public Integer getCode() {
        return code;
    }
}

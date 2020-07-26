package com.genius.filemanage.common.entity;

import com.genius.filemanage.common.constants.CodeMsgConstant;

import java.io.Serializable;

public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    private T data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    private ResponseResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private ResponseResult(CodeMsg codeMsg) {
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    public ResponseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult(CodeMsgConstant.STATUS_SUCCESS.getCode(), CodeMsgConstant.STATUS_SUCCESS.getMsg(), data);
    }

    public static <T> ResponseResult<T> fail(T data) {
        return new ResponseResult(CodeMsgConstant.STATUS_FAIL.getCode(), CodeMsgConstant.STATUS_FAIL.getMsg(), data);
    }

    public static <T> ResponseResult<T> error() {
        return new ResponseResult(CodeMsgConstant.STATUS_SERVER_ERROR.getCode(), CodeMsgConstant.STATUS_SERVER_ERROR.getMsg());
    }

    public static <T> ResponseResult<T> error(CodeMsg codeMsg) {
        return new ResponseResult(codeMsg);
    }
}

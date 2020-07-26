package com.genius.filemanage.common.entity;

/**
 * 返回的code和消息类
 */
public class CodeMsg {

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

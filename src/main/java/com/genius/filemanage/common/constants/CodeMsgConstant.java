package com.genius.filemanage.common.constants;

import com.genius.filemanage.common.entity.CodeMsg;

/**
 * 返回的code和消息实体常量
 */
public class CodeMsgConstant {

    public static CodeMsg STATUS_SUCCESS = new CodeMsg(0, "success");

    public static CodeMsg STATUS_FAIL = new CodeMsg(60000, "fail");

    public static CodeMsg STATUS_SERVER_ERROR = new CodeMsg(50001, "server error");
}

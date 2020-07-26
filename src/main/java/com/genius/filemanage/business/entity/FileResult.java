package com.genius.filemanage.business.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传结果类
 */
public class FileResult implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 成功的集合
     */
    private List<File> success;

    /**
     * 失败的集合
     */
    private List<File> fail;

    /**
     * 非法的集合
     */
    private List<File> invalid;

    public List<File> getSuccess() {
        return success;
    }

    public List<File> getFail() {
        return fail;
    }

    public List<File> getInvalid() {
        return invalid;
    }

    private FileResult(List<File> success, List<File> fail, List<File> invalid) {
        this.success = success;
        this.fail = fail;
        this.invalid = invalid;
    }

    public static FileResult success(List<File> success, List<File> fail, List<File> invalid) {
        return new FileResult(success, fail, invalid);
    }

    public static FileResult success(List<File> success) {
        return new FileResult(success, new ArrayList<>(), new ArrayList<>());
    }

    public static FileResult fail(List<File> success, List<File> fail, List<File> invalid) {
        return new FileResult(success, fail, invalid);
    }
}

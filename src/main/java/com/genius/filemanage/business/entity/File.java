package com.genius.filemanage.business.entity;

import java.io.Serializable;

/**
 * 上传文件类
 */
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件名
     */
    private String name;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 图片的缩略图路径
     */
    private String thumbnailPath;
    /**
     * 图片的mini图路径
     */
    private String miniThumbnailPath;
    /**
     * 失败时的异常
     */
    private String errMsg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getMiniThumbnailPath() {
        return miniThumbnailPath;
    }

    public void setMiniThumbnailPath(String miniThumbnailPath) {
        this.miniThumbnailPath = miniThumbnailPath;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}

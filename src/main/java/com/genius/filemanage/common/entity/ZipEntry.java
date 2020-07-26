package com.genius.filemanage.common.entity;

/**
 * 压缩文件实体类
 */
public class ZipEntry {
    /**
     * 压缩包内相对路径
     */
    private String entryName;

    /**
     * 文件的实际路径
     */
    private String entryPath;

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getEntryPath() {
        return entryPath;
    }

    public void setEntryPath(String entryPath) {
        this.entryPath = entryPath;
    }
}

package com.genius.filemanage.common.utils;

import java.io.*;
import java.util.List;
import java.util.zip.*;

/**
 * 压缩文件类
 * @author liuxh 20180919
 */
public class CompressUtils {

    /**
     * @Description:
     *     压缩文件，支持将多个文件或目录压缩到同一个压缩文件中
     * @param zipEntries 压缩源
     * @param zipPath 生成压缩文件的路径，请使用绝对路径。该路不能为空，并且必须以“.zip”为结尾
     * @param comment 压缩注释
     */
    public static String compress(List<com.genius.filemanage.common.entity.ZipEntry> zipEntries, String zipPath, String comment)
            throws IOException {
        // 设置压缩文件路径，默认为将要压缩的路径的父目录为压缩文件的父目录
        if (zipPath == null || "".equals(zipPath) || !zipPath.endsWith(".zip")) {
            throw new FileNotFoundException("必须指定一个压缩路径，而且该路径必须以'.zip'为结尾");
        }
        // 要创建的压缩文件的父目录不存在，则创建
        File zipFile = new File(zipPath);
        if (!zipFile.getParentFile().exists()) {
            zipFile.getParentFile().mkdirs();
        }
        // 创建压缩文件输出流
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(zipPath);
        } catch (FileNotFoundException e) {
            if (fos != null) {
                try{ fos.close(); } catch (Exception e1) {}
            }
        }
        // 使用指定校验和创建输出流
        CheckedOutputStream csum = new CheckedOutputStream(fos, new CRC32());
        // 创建压缩流
        ZipOutputStream zos = new ZipOutputStream(csum);
        // 设置压缩包注释
        zos.setComment(comment);
        // 启用压缩
        zos.setMethod(ZipOutputStream.DEFLATED);
        // 设置压缩级别为最强压缩
        zos.setLevel(Deflater.BEST_COMPRESSION);
        // 压缩文件缓冲流
        BufferedOutputStream bout = null;
        try {
            // 封装压缩流为缓冲流
            bout = new BufferedOutputStream(zos);
            for (com.genius.filemanage.common.entity.ZipEntry entry : zipEntries) {

                File file = new File(entry.getEntryPath());
                if (file.exists()) {
                    // 开始写入新的ZIP文件条目并将流定位到条目数据的开始处
                    ZipEntry zipEntry = new ZipEntry(entry.getEntryName());
                    // 向压缩流中写入一个新的条目
                    zos.putNextEntry(zipEntry);
                    // 读取将要压缩的文件的输入流
                    BufferedInputStream bin = null;
                    try{
                        // 获取输入流读取文件
                        bin = new BufferedInputStream(new FileInputStream(entry.getEntryPath()));
                        // 读取文件，并写入压缩流
                        byte[] buffer = new byte[1024];
                        int readCount = -1;
                        while ((readCount = bin.read(buffer)) > 0) {
                            bout.write(buffer, 0, readCount);
                        }
                        // 注，在使用缓冲流写压缩文件时，一个条件完后一定要刷新，不然可能有的内容就会存入到后面条目中去了
                        bout.flush();
                        // 关闭当前ZIP条目并定位流以写入下一个条目
                        zos.closeEntry();
                    } finally {
                        if (bin != null) {
                            try { bin.close(); } catch (IOException e) {}
                        }
                    }
                }

            }
        } finally {
            if (bout != null) {
                try{ bout.close(); } catch (Exception e) {}
            }
        }
        return zipPath;
    }
}

package com.genius.filemanage.common.utils;

import java.util.ArrayList;
import java.util.List;

public class PathUtils {

    public static String prefix (List<String> paths) {
        String pre = "";
        List<String[]> pathChar = new ArrayList<>();
        // 得到最小的长度
        int minLength = paths.get(0).split("/").length;
        for (String path : paths) {
            path = path.replaceAll("\\\\", "/");
            if (path.split("/").length < minLength) {
                minLength = path.length();
            }
            pathChar.add(path.split("/"));
        }

        out:
        for (int i = 0; i < minLength; i++) {
            inner:
            for (int j = 0; j < paths.size() - 1; j++) {
                if (!pathChar.get(j)[i].equals(pathChar.get(j+1)[i])) {
                    break out;
                }
            }
            pre += pathChar.get(0)[i] + "/";
        }
        if (pre.indexOf("/") > 0) {
            pre = pre.substring(0, pre.lastIndexOf("/"));
        }
        return pre;
    }

    public static void main (String[] args) {
        List<String> paths = new ArrayList<>();
        paths.add("\\AA/AAAA/DAD/aa.txt");
        paths.add("\\AA/AAAA/DAD/aa2.txt");
        paths.add("/AA/AAAA/DAD/aa.txt");
        String pre = prefix(paths);
        System.out.println(pre);
    }
}

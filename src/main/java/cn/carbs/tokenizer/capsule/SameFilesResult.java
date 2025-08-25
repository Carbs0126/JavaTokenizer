package cn.carbs.tokenizer.capsule;

import cn.carbs.tokenizer.util.Utils;

import java.util.*;

public class SameFilesResult {

    public String basePath;

    public HashMap<String, StringOrArrayList> mapMd5ToFiles;

    public ArrayList<String> includedPrefixDirNameArr;

    public SameFilesResult(String basePath, HashMap<String, StringOrArrayList> mapMd5ToFiles, ArrayList<String> includedPrefixDirNameArr) {
        this.basePath = basePath;
        this.mapMd5ToFiles = mapMd5ToFiles;
        this.includedPrefixDirNameArr = includedPrefixDirNameArr;
    }

    private String getPrefixFolders() {
        if (includedPrefixDirNameArr == null || includedPrefixDirNameArr.size() == 0) {
            return "all";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String prefix : includedPrefixDirNameArr) {
            stringBuilder.append(prefix);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    private String getRelativePathFormatted(StringOrArrayList stringOrArrayList) {
        if (stringOrArrayList == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (stringOrArrayList.isArray()) {
            for (String absPath : stringOrArrayList.getArr()) {
                stringBuilder.append(absPath.substring(this.basePath.length()));
                stringBuilder.append("\n");
            }
        } else {
            stringBuilder.append(stringOrArrayList.getStr().substring(this.basePath.length()));
        }
        return stringBuilder.toString();
    }

    public ArrayList<SameFilesInfo> getSortedInfo() {
        ArrayList<SameFilesInfo> ret = new ArrayList<>(mapMd5ToFiles.size() * 2);
        for (Map.Entry<String, StringOrArrayList> entry : mapMd5ToFiles.entrySet()) {
            StringOrArrayList value = entry.getValue();
            if (value.isArray()) {
                long bytes = value.getTag0();
                int repeatCount = value.getArr().size() - 1;
                ret.add(new SameFilesInfo(value, bytes * repeatCount));
            }
        }
        Collections.sort(ret, Comparator.reverseOrder());
        return ret;
    }

    public void showSortedInfo() {
        ArrayList<SameFilesInfo> sortedInfo = getSortedInfo();
        if (sortedInfo == null) {
            System.out.println("showSortedInfo() wrong!");
            return;
        }
        System.out.println("===================== show sorted same files result =====================");
        System.out.println("Prefix folders include : " + getPrefixFolders());
        for (SameFilesInfo info : sortedInfo) {
            System.out.println("\n");
            System.out.println("重复资源: [\n" + getRelativePathFormatted(info.stringOrArrayList) + "]");
            long bytes = info.stringOrArrayList.getTag0();
            int repeatCount = info.stringOrArrayList.getArr().size() - 1;
            System.out.println("重复资源大小 : " + Utils.getReadableFileSize(bytes * repeatCount)
                    + " ( " + Utils.getReadableFileSize(bytes) + " x " + repeatCount + " )");
        }
    }

    public void show() {
        System.out.println("===================== show same files result =====================");
        System.out.println("Prefix folders include : " + getPrefixFolders());
        // 读取 HashMap
        for (Map.Entry<String, StringOrArrayList> entry : mapMd5ToFiles.entrySet()) {
            StringOrArrayList value = entry.getValue();
            if (value.isArray()) {
                System.out.println("\n");
                System.out.println("重复资源: [\n" + getRelativePathFormatted(value) + "]");
                long bytes = value.getTag0();
                int repeatCount = value.getArr().size() - 1;
                System.out.println("重复资源大小 : " + Utils.getReadableFileSize(bytes * repeatCount)
                        + " ( " + Utils.getReadableFileSize(bytes) + " x " + repeatCount + " )");
            }
        }
    }

}

package cn.carbs.tokenizer;

import cn.carbs.tokenizer.util.FileMD5Util;
import cn.carbs.tokenizer.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkFlowOfResource {

    // 测试 xml
    public static void test1() {
//        String rootResourceImport = "com.baidu.searchbox.novel.R";
        String rootResourcePath = "/Users/v_wangjianjun02/Desktop/code/honor/baidu/baiduapp-android/browser-honor/repos/business/lib_novel/lib-novel/src/main/res";
        ArrayList<String> includedPrefixDirNameArr = new ArrayList<>();
        includedPrefixDirNameArr.add("drawable");
        includedPrefixDirNameArr.add("layout");
        traverseFolderAndAnalyseResource(rootResourcePath, includedPrefixDirNameArr);
    }

    private static void traverseFolderAndAnalyseResource(String rootFolderAbsPath, ArrayList<String> includedPrefixDirNameArr) {
        ArrayList<File> files = Utils.findFilesInCertainPrefixDir(new File(rootFolderAbsPath), includedPrefixDirNameArr);
        System.out.println("Resource file count : " + files.size());
        int filesSize = files.size();
        int i = 0;
        HashMap<String, Object> mapMd5ToFiles = new HashMap<>();
        for (File file : files) {
            String fileMd5 = FileMD5Util.getFileMD5(file);
            System.out.println("Progress -> total : " + filesSize + ", current : " + i + ", file md5 : " + fileMd5 + ", file : ");
            System.out.println(file.getAbsolutePath());
            i++;
            if (!mapMd5ToFiles.containsKey(fileMd5)) {
                mapMd5ToFiles.put(fileMd5, file.getAbsolutePath());
            } else {
                Object fileStrArr = mapMd5ToFiles.get(fileMd5);
                if (fileStrArr instanceof String) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add((String) fileStrArr);
                    arrayList.add(file.getAbsolutePath());
                    mapMd5ToFiles.put(fileMd5, arrayList);
                } else if (fileStrArr instanceof ArrayList) {
                    ((ArrayList<String>) fileStrArr).add(file.getAbsolutePath());
                }
            }
        }

        for (Map.Entry<String, Object> entry : mapMd5ToFiles.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof ArrayList) {
                StringBuilder builder = new StringBuilder();
                ArrayList<String> arrayList = (ArrayList<String>) value;
                for (String str : arrayList) {
                    builder.append(str);
                    builder.append("\n");
                }
                System.out.println("重复资源: [\n" + builder.toString() + "]");
            }
        }
        System.out.println("Code analysis finished!");
    }

}

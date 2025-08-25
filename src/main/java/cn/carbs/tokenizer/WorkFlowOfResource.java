package cn.carbs.tokenizer;

import cn.carbs.tokenizer.capsule.SameFilesResult;
import cn.carbs.tokenizer.capsule.StringOrArrayList;
import cn.carbs.tokenizer.reference.CodeFileAndReferencedToken;
import cn.carbs.tokenizer.reference.IdentifierMatcher;
import cn.carbs.tokenizer.reference.ReferencedToken;
import cn.carbs.tokenizer.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 染色
 * /  layout-land/x_y.xml  ----   继续分析 x_y.xml 文件中对其他文件的引用
 * /                           |
 * R.layout.x_y                             |
 * \                           |
 * \  layout/x_y.xml          |
 * |
 * |
 * -------------------------------------
 * |
 * |            / ....
 * ↓           /
 * R.layout.sub_xxx
 * \
 * \ ....
 */
public class WorkFlowOfResource {

    // 测试 xml
    public static void test1() {
        String rootResourcePath = "/Users/v_wangjianjun02/Desktop/code/honor/baidu/baiduapp-android/browser-honor/repos/business/lib_novel/lib-novel/src/main/res";
        ArrayList<String> includedPrefixDirNameArr = new ArrayList<>();
        // todo mipmap
        includedPrefixDirNameArr.add("drawable");
        includedPrefixDirNameArr.add("layout");
        // todo wang数据结构需要封装一下
        String rootResourceImport = "com.baidu.searchbox.novel.R";
        // manifest
        ArrayList<String> extraCertainColoredSeedXMLFiles = new ArrayList<>();
        extraCertainColoredSeedXMLFiles.add(
                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/baiduapp-android/browser-honor/repos/business/lib_novel/lib-novel/src/main/AndroidManifest.xml");
//        ArrayList<String> extraConcernedXMLIDS = new ArrayList<>();
        ArrayList<CodeFileAndReferencedToken> extraCodeFileAndReferencedToken = new ArrayList<>(extraCertainColoredSeedXMLFiles.size());
        for (String xmlFilePath : extraCertainColoredSeedXMLFiles) {
            ArrayList<String> extraConcerned = Utils.pickConcernedXMLIDs(xmlFilePath);
            if (extraConcerned != null) {
//                extraConcernedXMLIDS.addAll(extraConcerned);
                ArrayList<ReferencedToken> referencedTokenArr = new ArrayList<>();
                for (String resourceID : extraConcerned) {
                    if (resourceID != null && resourceID.length() > 0) {
                        ReferencedToken referencedToken = new ReferencedToken(resourceID)
                                .setIdentifierMatcher(new IdentifierMatcher().setStandardImport(rootResourceImport))
                                .setStandardReferenceStr(resourceID);
                        referencedTokenArr.add(referencedToken);
                    }
                }
                CodeFileAndReferencedToken codeFileAndReferencedToken = new CodeFileAndReferencedToken(referencedTokenArr, xmlFilePath);
                extraCodeFileAndReferencedToken.add(codeFileAndReferencedToken);
            }
        }

        // 所有 资源文件
        HashMap<String, StringOrArrayList> mapResourceToFiles = traverseResourceFolderAndAnalyseResource(
                rootResourcePath, includedPrefixDirNameArr);

        // 所有 code 引用到的资源：
        ArrayList<String> resourceRFilePaths = new ArrayList<>();
        resourceRFilePaths.add("com.baidu.searchbox.novel.R");
        ArrayList<CodeFileAndReferencedToken> referencedArr = WorkFlowOfCode.analyseReferencedResourceTokenForAbsCodeFolderPath(
                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel/lib-novel",
                resourceRFilePaths);
        if (referencedArr == null) {
            System.out.println("traverseFolderAndAnalyseJavaAndKotlinCode return null");
            return;
        }
        referencedArr.addAll(extraCodeFileAndReferencedToken);
        // todo 打印
        System.out.println("=======> ArrayList<CodeFileAndReferencedToken> size : " + referencedArr.size());
        for (CodeFileAndReferencedToken codeFileAndReferencedToken : referencedArr) {
            System.out.println(codeFileAndReferencedToken);
        }

        // todo
        // 1. referencedArr 中，找到 simpleReferenceStr 是以 R.drawable 和 R.layout 开头的，其余都忽略
        HashSet<ReferencedToken> filteredFlatReferencedToken = new HashSet<>(referencedArr.size() * 2);
        for (CodeFileAndReferencedToken ref : referencedArr) {
            if (ref == null) {
                continue;
            }
            if (ref.referencedTokenArr == null) {
                continue;
            }
            for (ReferencedToken referencedToken : ref.referencedTokenArr) {
                if (referencedToken == null) {
                    continue;
                }
                if (referencedToken.standardReferenceStr == null) {
                    continue;
                }
                if (referencedToken.standardReferenceStr.startsWith("R.drawable.")
                        || referencedToken.standardReferenceStr.startsWith("R.layout.")) {
                    // 关注
                    filteredFlatReferencedToken.add(referencedToken);
                }
            }
        }

        System.out.println("filteredFlatReferencedToken size() ==========> " + filteredFlatReferencedToken.size());

        // 初次染色
        for (ReferencedToken t : filteredFlatReferencedToken) {
            System.out.println("--> " + t);
            // todo 目前暂时只根据 simpleReferenceStr 来染色，因为当前例子中，之前 standardImport 相同了
            if (mapResourceToFiles.containsKey(t.standardReferenceStr)) {
                mapResourceToFiles.get(t.standardReferenceStr).setState(1);
            }
        }

        // 循环染色
        System.out.println("=========================== coloring start! ===========================");
        Utils.startColor(mapResourceToFiles);
        System.out.println("=========================== coloring end! =============================");
        for (Map.Entry<String, StringOrArrayList> entry : mapResourceToFiles.entrySet()) {
            StringOrArrayList value = entry.getValue();
            if (value != null && value.getState() == 0) {
                System.out.println("#### uncolored : \n" + value);
            }
        }
    }

    // rootResourceFolderAbsPath 改为 paths，可以指定 root path 或者直接指定一个xml
    private static HashMap<String, StringOrArrayList> traverseResourceFolderAndAnalyseResource(String rootResourceFolderAbsPath,
                                                                                               ArrayList<String> includedPrefixDirNameArr) {

        ArrayList<File> files = Utils.findFilesInCertainPrefixDir(new File(rootResourceFolderAbsPath), includedPrefixDirNameArr);

//        // todo wang 用于作为 colored seed file
//        if (extraCertainFiles != null) {
//            for (String extraCertainFilePath : extraCertainFiles) {
//                File file = new File(extraCertainFilePath);
//                if (file.exists()) {
//                    files.add(file);
//                    System.out.println("==** >> " + file.getAbsolutePath());
//                }
//            }
//        }


        System.out.println("Resource file count : " + files.size());
        int filesSize = files.size();
        int i = 0;
        // 检测重复 md5 文件
        HashMap<String, StringOrArrayList> mapMd5ToFiles = new HashMap<>();
        // 存入 HashMap
        for (File file : files) {
            String fileMd5 = Utils.getFileMD5(file);
            System.out.println("Progress -> total : " + filesSize + ", current : " + i + ", file md5 : " + fileMd5 + ", file : ");
            System.out.println(file.getAbsolutePath());
            i++;
            if (!mapMd5ToFiles.containsKey(fileMd5)) {
                mapMd5ToFiles.put(fileMd5, new StringOrArrayList(file.getAbsolutePath()));
            } else {
                StringOrArrayList strOrArrObj = mapMd5ToFiles.get(fileMd5);
                strOrArrObj.addString(file.getAbsolutePath());
            }
        }

        // 读取 HashMap
        for (Map.Entry<String, StringOrArrayList> entry : mapMd5ToFiles.entrySet()) {
            StringOrArrayList value = entry.getValue();
            if (value.isArray()) {
                System.out.println("重复资源: [\n" + value.toString() + "]");
            }
        }

        // 将所有的文件变为 R.layout.xxx 和 R.drawable.xxx
        // HashMap<String, ArrayList>  ArrayList 是文件路径
        HashMap<String, StringOrArrayList> mapResourceToFiles = new HashMap<>();
        // todo wang
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            // R.layout.xxx
            StringBuilder resourceStringBuilder = new StringBuilder();
            String resourceString = Utils.getLastFolderName(file);

            boolean isConcernedResourceName = false;
            if (resourceString.startsWith("layout")) {
                resourceStringBuilder.append("R.layout.");
                isConcernedResourceName = true;
            } else if (resourceString.startsWith("drawable")) {
                resourceStringBuilder.append("R.drawable.");
                isConcernedResourceName = true;
            }
            if (!isConcernedResourceName) {
                continue;
            }
            String simpleFileNameWithoutPostfix = Utils.getFileNameBeforeFirstDot(file);
            resourceStringBuilder.append(simpleFileNameWithoutPostfix);
            String resourceStr = resourceStringBuilder.toString();
            if (!mapResourceToFiles.containsKey(resourceStr)) {
                mapResourceToFiles.put(resourceStr, new StringOrArrayList(file.getAbsolutePath()));
            } else {
                StringOrArrayList stringOrArrayList = mapResourceToFiles.get(resourceStr);
                stringOrArrayList.addString(file.getAbsolutePath());
            }
        }

        System.out.println("=================== resource id ===================");

        // 读取 HashMap
        for (Map.Entry<String, StringOrArrayList> entry : mapResourceToFiles.entrySet()) {
            String key = entry.getKey();
            StringOrArrayList value = entry.getValue();
            System.out.println(key + ": [\n" + value.toString() + "]");
        }

        System.out.println("Code analysis finished!");
        return mapResourceToFiles;
    }


    // rootResourceFolderAbsPath 改为 paths，可以指定 root path 或者直接指定一个xml

    /**
     * 分析所有资源文件夹下，命中 includedPrefixDirNameArr 的文件，比如，分析 xxx/src/main/res 文件下的所有 "drawable" 和 "layout" 中的重复文件
     *
     * @param baseResourcePath                基础文件路径，针对 rootResourceFolderAbsPaths 所有的文件路径，便于打印
     * @param rootResourceFoldersRelatedPaths 针对 baseResourcePath 的相对路径
     * @param includedPrefixDirNameArr        传入 "drawable" "layout" 等
     * @param extraAbsFilePaths               传入直接指定的文件
     * @return
     */
    public static SameFilesResult analyseResourceFoldersForSameFiles(String baseResourcePath,
                                                                     ArrayList<String> rootResourceFoldersRelatedPaths,
                                                                     ArrayList<String> includedPrefixDirNameArr,
                                                                     ArrayList<String> extraAbsFilePaths) {
        if (rootResourceFoldersRelatedPaths == null) {
            return null;
        }
        if (baseResourcePath == null) {
            baseResourcePath = "";
        }
        ArrayList<File> files = new ArrayList<>(256);
        boolean baseResourcePathEndWithSeparator = baseResourcePath.endsWith(File.separator);
        for (String relatedPath : rootResourceFoldersRelatedPaths) {
            String absResourceFolderPath = null;
            if (baseResourcePathEndWithSeparator) {
                if (relatedPath.startsWith(File.separator)) {
                    // 需要删除一个文件夹分隔符
                    absResourceFolderPath = baseResourcePath.substring(0, baseResourcePath.length() - 1) + relatedPath;
                } else {
                    absResourceFolderPath = baseResourcePath + relatedPath;
                }
            } else {
                if (!relatedPath.startsWith(File.separator)) {
                    // 需要增加一个文件夹分隔符
                    absResourceFolderPath = baseResourcePath + File.separator + relatedPath;
                } else {
                    absResourceFolderPath = baseResourcePath + relatedPath;
                }
            }
            ArrayList<File> filesInOneFolder = Utils.findFilesInCertainPrefixDir(new File(absResourceFolderPath), includedPrefixDirNameArr);
            files.addAll(filesInOneFolder);
        }

        if (extraAbsFilePaths != null) {
            for (String absFilePath : extraAbsFilePaths) {
                File file = new File(absFilePath);
                if (file.exists()) {
                    files.add(file);
                }
            }
        }
        // 检测重复 md5 文件
        HashMap<String, StringOrArrayList> mapMd5ToFiles = new HashMap<>();
        // 存入 HashMap
        for (File file : files) {
            String fileMd5 = Utils.getFileMD5(file);
            if (!mapMd5ToFiles.containsKey(fileMd5)) {
                mapMd5ToFiles.put(fileMd5, new StringOrArrayList(file.getAbsolutePath()));
            } else {
                StringOrArrayList strOrArrObj = mapMd5ToFiles.get(fileMd5);
                strOrArrObj.addString(file.getAbsolutePath());
                strOrArrObj.setLongTag0(file.length());
            }
        }
        return new SameFilesResult(baseResourcePath, mapMd5ToFiles, includedPrefixDirNameArr);
    }


}

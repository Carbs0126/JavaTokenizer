package cn.carbs.tokenizer;

import cn.carbs.tokenizer.capsule.SameFilesResult;

import java.util.ArrayList;

public class Main {

    public static void main(String[] argv) {

//        Test.analyseJavaAndKotlinFilesForSealedTokens(
//                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel"              // 1119 files, 851ms, pass
//                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel_core"           // 2594 files, 2162 ms, pass
//                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel_custome"      // 808 files, 925ms, pass
//                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel_reader"       // 576 files, 1039ms, pass
//                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel_reader_frame" // 365 files, 656ms, pass
//        );

//        Test.analyseJavaAndKotlinFilesForSealedTokens("/Users/wangchao/Downloads/jdk-master");

//        Test.analyseOneJavaOrKotlinFileForSealedTokens(
//                "/Users/wangchao/Desktop/learning/JavaTokenizer/TODO6.kt",
//                true);

//        String baseResourcePath = "";
//        ArrayList<String> rootResourceFoldersRelatedPaths = new ArrayList<>();
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel/lib-novel/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel/lib-novel-interface/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel/lib-novel-stub-interface/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_core/lib-novel-audio/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_core/lib-novel-audio-interface/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_core/lib-novel-core/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_core/lib-novel-res/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_core/lib-novel-service-ui/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_core/lib-novel-shelf/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_custome/androidx/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_reader/lib-novel-reader/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_reader_frame/lib-reader-components/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_reader_frame/lib-reader-core/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_reader_frame/lib-reader-res/src/main/res");
//        rootResourceFoldersRelatedPaths.add("/Users/wangchao/Downloads/novel-sdk/repos/business/lib_novel_reader_frame/lib-reader-view/src/main/res");

        String baseResourcePath = "/Users/wangchao/Downloads/";
        ArrayList<String> rootResourceFoldersRelatedPaths = new ArrayList<>();
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel/lib-novel/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel/lib-novel-interface/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel/lib-novel-stub-interface/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_core/lib-novel-audio/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_core/lib-novel-audio-interface/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_core/lib-novel-core/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_core/lib-novel-res/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_core/lib-novel-service-ui/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_core/lib-novel-shelf/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_custome/androidx/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_reader/lib-novel-reader/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_reader_frame/lib-reader-components/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_reader_frame/lib-reader-core/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_reader_frame/lib-reader-res/src/main/res");
        rootResourceFoldersRelatedPaths.add("novel-sdk/repos/business/lib_novel_reader_frame/lib-reader-view/src/main/res");


        ArrayList<String> includedPrefixDirNameArr = new ArrayList<>();
        includedPrefixDirNameArr.add("layout");
        includedPrefixDirNameArr.add("drawable");

        SameFilesResult result = WorkFlowOfResource.analyseResourceFoldersForSameFiles(
                baseResourcePath, rootResourceFoldersRelatedPaths, includedPrefixDirNameArr, null);
        result.showSortedInfo();

    }

}

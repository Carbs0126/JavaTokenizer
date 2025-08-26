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

//        Test.analyseJavaAndKotlinFilesForSealedTokens("/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/");
//    /Users/v_wangjianjun02/Downloads/kotlin-master
//        Test.analyseJavaAndKotlinFilesForSealedTokens("/Users/v_wangjianjun02/Downloads/kotlin-master");

        Test.analyseOneJavaOrKotlinFileForSealedTokens(
                "/Users/wangchao/Desktop/learning/JavaTokenizer/TODO6.kt",
                true);

        // todo 优化
        // traverseResourceFolderAndAnalyseResource

    }

}

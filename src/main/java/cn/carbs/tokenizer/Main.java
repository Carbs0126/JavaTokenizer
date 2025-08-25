package cn.carbs.tokenizer;

import cn.carbs.tokenizer.reference.ReferencedToken;

import java.util.ArrayList;

public class Main {

    public static void main(String[] argv) {

        Test.analyseJavaAndKotlinFiles(
//                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel"              // 1119 files, 851ms, pass
                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel_core"           // 2594 files, 2162 ms, pass
//                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel_custome"      // 808 files, 925ms, pass
//                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel_reader"       // 576 files, 1039ms, pass
//                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel_reader_frame" // 365 files, 656ms, pass
        );

//        Test.analyseJavaAndKotlinFiles("/Users/wangchao/Downloads/jdk-master");

//        Test.analyseOneJavaOrKotlinFile("/Users/wangchao/Downloads/jdk-master/src/jdk.javadoc/share/classes/jdk/javadoc/internal/doclets/toolkit/util/CommentHelper.java", true);

    }

    private static void testOneFileForReferencedToken() {
        ArrayList<String> resourceRFilePaths = new ArrayList<>();
        resourceRFilePaths.add("com.baidu.searchbox.novel.R");
        resourceRFilePaths.add("com.baidu.novel.R");
        resourceRFilePaths.add("com.baidu.searchbox.R");
        resourceRFilePaths.add("com.searchbox.novel.R");
        String filePath = "/Users/v_wangjianjun02/Desktop/code/honor/baidu/baiduapp-android/browser-honor/repos/business/lib_novel/" +
                "lib-novel/src/main/java/com/baidu/searchbox/noveladapter/spswitch/NovelCommentVerticalDefaultInputLayout.kt";
        ArrayList<ReferencedToken> referencedTokens = WorkFlowOfCode.analyseReferencedResourceTokenForOneCodeFile(filePath, resourceRFilePaths);

        System.out.println("TEST referencedTokens for file : " + filePath);
        for (ReferencedToken referencedToken : referencedTokens) {
            System.out.println(referencedToken);
        }
    }

}

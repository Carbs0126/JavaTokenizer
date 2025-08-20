package cn.carbs.tokenizer;

// import static 可以指向静态内部类？
// 1. 首先提供完整R包名。
// 2. 命中R包名后，所有命中的，后缀都记下来，作为一个 HashSet
// 3. 如果import中没有命中 R包名，则把当前package路径作为R的开头，即 some.package.path.R

import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.search.IdentifierMatcher;
import cn.carbs.tokenizer.search.ReferencedToken;
import cn.carbs.tokenizer.type.TokenType;
import cn.carbs.tokenizer.util.Utils;
import cn.carbs.tokenizer.xml.XMLParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.carbs.tokenizer.WorkFlowOfCode;

public class Main {

    public static void main(String[] argv) {
//        test();
//        analyseJavaFileAndShow("data09.java");
//        analyseAbsJavaFileAndShow(
//                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel_reader/lib-novel-reader/src/main/java/org/geometerplus/android/fbreader/newreader/FBReader.java");
//        analyseAbsJavaFileAndShow("/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel_core/lib-novel-core/src/main/java/com/baidu/searchbox/newreader/story/readflow/ReadFlowViewManager.java");
//        searchTokensForResourceId("data09.java");
//        testKotlin("data10.kt");
//        testXML("data20.xml");

//        testTraverseFolder("/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business");
//        testTraverseFolder("/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel/lib-novel");

//        WorkFlowOfCode.test1();
        // todo wang 有问题
        WorkFlowOfResource.test1();
//        testOneFileForReferencedToken();
    }

    private static void testOneFileForReferencedToken() {
        ArrayList<String> resourceRFilePaths = new ArrayList<>();
        resourceRFilePaths.add("com.baidu.searchbox.novel.R");
//        String filePath = "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel/lib-novel/src/main/java/com/baidu/searchbox/noveladapter/spswitch/NovelCommentBaseInputLayout.kt";
        String filePath = "/Users/v_wangjianjun02/Desktop/code/honor/baidu/baiduapp-android/browser-honor/repos/business/lib_novel/lib-novel/src/main/java/com/baidu/searchbox/noveladapter/spswitch/NovelCommentVerticalDefaultInputLayout.kt";
        ArrayList<ReferencedToken> referencedTokens = WorkFlowOfCode.analyseReferencedResourceForFilePaths(filePath, resourceRFilePaths);

        System.out.println("TEST referencedTokens for file : " + filePath);
        for (ReferencedToken referencedToken : referencedTokens) {
            System.out.println(referencedToken);
        }
    }

}

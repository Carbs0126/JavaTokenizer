package cn.carbs.tokenizer;

import cn.carbs.tokenizer.core.ReferencedTokenAnalyser;
import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.reference.CodeFileAndReferencedToken;
import cn.carbs.tokenizer.reference.ReferencedToken;
import cn.carbs.tokenizer.util.Log;
import cn.carbs.tokenizer.util.Utils;

import java.io.File;
import java.util.ArrayList;

public class WorkFlowOfCode {

    /**
     * 传入代码目录 + 标准R代码包名，分析 java kotlin 代码，并返回代码中对 resource 的引用
     * @param rootFolderAbsPath
     * @param standardResourceImportPaths
     */
    public static ArrayList<CodeFileAndReferencedToken> analyseReferencedResourceTokenForAbsCodeFolderPath(String rootFolderAbsPath,
                                                                                                           ArrayList<String> standardResourceImportPaths) {
        ArrayList<String> postfixArr = new ArrayList<>();
        ArrayList<CodeFileAndReferencedToken> retArr = new ArrayList<>();
        postfixArr.add("java");
        postfixArr.add("kt");
        ArrayList<File> files = Utils.findCertainFormatFiles(new File(rootFolderAbsPath), postfixArr);
        Log.v("java and kotlin files count : " + files.size());
        int filesSize = files.size();
        int i = 0;
        for (File file : files) {
            Log.v("Progress -> total : " + filesSize + ", current : " + (i++) + ", file : ");
            Log.v(file.getAbsolutePath());
            ArrayList<ReferencedToken> referencedTokens = analyseReferencedResourceTokenForOneCodeFile(file.getAbsolutePath(), standardResourceImportPaths);
            if (referencedTokens != null && referencedTokens.size() > 0) {
                retArr.add(new CodeFileAndReferencedToken(referencedTokens, file.getAbsolutePath()));
            }
        }
        return retArr;
    }

    /**
     * 指定解析一个 java 或者 kotlin 文件，分析内部的 ReferencedToken
     *
     * @param absCodeFilePath             只能是 java 或者 kotlin 文件
     * @param standardResourceImportPaths 可能的标准 R 引包列表，列表中包括如：cn.carbs.tools.R  cn.carbs.library.R
     * @return
     */
    public static ArrayList<ReferencedToken> analyseReferencedResourceTokenForOneCodeFile(String absCodeFilePath,
                                                                                          ArrayList<String> standardResourceImportPaths) {
        ArrayList<String> arrayList = Utils.readLinesForAbsFilePath(absCodeFilePath);
        ArrayList<SealedToken> tokens = Utils.genCodeTokenParserByFileName(absCodeFilePath).getTokens(arrayList);
        return ReferencedTokenAnalyser.analyseReferencedTokensFromSealedTokens(tokens, standardResourceImportPaths);
    }

    // ================================= 测试代码 =================================
    // 测试
    public static void test1() {
        ArrayList<String> resourceRFilePaths = new ArrayList<>();
        resourceRFilePaths.add("com.baidu.searchbox.novel.R");
        ArrayList<CodeFileAndReferencedToken> arr = analyseReferencedResourceTokenForAbsCodeFolderPath(
                "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel/lib-novel",
                resourceRFilePaths);
        if (arr == null) {
            System.out.println("traverseFolderAndAnalyseJavaAndKotlinCode return null");
        } else {
            System.out.println("ArrayList<CodeFileAndReferencedToken> size : " + arr.size());
            for (CodeFileAndReferencedToken codeFileAndReferencedToken : arr) {
                System.out.println(codeFileAndReferencedToken);
            }
        }
    }

}

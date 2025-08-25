package cn.carbs.tokenizer;

import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.reference.ReferencedToken;
import cn.carbs.tokenizer.type.TokenType;
import cn.carbs.tokenizer.util.Log;
import cn.carbs.tokenizer.util.Utils;

import java.io.File;
import java.util.ArrayList;

public class Test {

    /**
     * standardResourceImports 举例：
     *  ArrayList<String> resourceRFilePaths = new ArrayList<>();
     *  resourceRFilePaths.add("com.baidu.searchbox.novel.R");
     *  resourceRFilePaths.add("com.baidu.novel.R");
     *  resourceRFilePaths.add("com.baidu.searchbox.R");
     *  resourceRFilePaths.add("com.searchbox.novel.R");
     * @param absCodeFilePath 绝对路径
     * @param standardResourceImports R 的标准引包
     */
    private static void analyseOneCodeFileForReferencedTokens(String absCodeFilePath, ArrayList<String> standardResourceImports) {
        ArrayList<ReferencedToken> referencedTokens =
                WorkFlowOfCode.analyseReferencedResourceTokenForOneCodeFile(absCodeFilePath, standardResourceImports);
        System.out.println("TEST referencedTokens for file : " + absCodeFilePath);
        for (ReferencedToken referencedToken : referencedTokens) {
            System.out.println(referencedToken);
        }
    }

    /**
     * 分析某个目录下所有的 java 和 kotlin 代码，用来验证 parser 的准确性
     * @param rootFolderAbsPath
     */
    public static void analyseJavaAndKotlinFilesForSealedTokens(String rootFolderAbsPath) {
        long startTime = System.currentTimeMillis();

        ArrayList<String> postfixArr = new ArrayList<>();
        postfixArr.add("java");
        postfixArr.add("kt");
        ArrayList<File> files = Utils.findCertainFormatFiles(new File(rootFolderAbsPath), postfixArr);
        Log.v("java and kotlin files count : " + files.size());
        System.out.println("java and kotlin files count : " + files.size());
        int filesSize = files.size();
        int i = 0;
        for (File file : files) {
            Log.v("Progress -> total : " + filesSize + ", current : " + (i++) + ", file : ");
            Log.v(file.getAbsolutePath());
            analyseOneJavaOrKotlinFileForSealedTokens(file.getAbsolutePath(), false);
        }
        long endTime = System.currentTimeMillis();
        long durationInMillis = endTime - startTime;
        Log.v("analyseJavaAndKotlinFiles() finish! duration : " + durationInMillis + " ms");
    }

    /**
     * 传入一个本地的 .java 或者 .kt 文件绝对路径，打印这个文件解析后的 token，用来验证 parser 的准确性
     * @param absFilePath
     */
    public static void analyseOneJavaOrKotlinFileForSealedTokens(String absFilePath, boolean printTokens) {

        // 读取文件
        ArrayList<String> arrayList = Utils.readLinesForAbsFilePath(absFilePath);

        // 解析 tokens
        ArrayList<SealedToken> tokens = Utils.genCodeTokenParserByFileName(absFilePath).getTokens(arrayList);

        if (!printTokens) {
            return;
        }
        // 展示 tokens
        StringBuilder tokensStr = new StringBuilder();
        if (tokens != null) {
            SealedToken preToken = null;
            SealedToken curToken = null;
            boolean needSpace = false;
            for (SealedToken sealedToken : tokens) {
                curToken = sealedToken;
                if (preToken != null) {
                    if (preToken.type == TokenType.Identifier && curToken.type == TokenType.Identifier) {
                        needSpace = true;
                    } else {
                        needSpace = false;
                    }
                }
                if (needSpace) {
                    tokensStr.append(" ");
                }
                tokensStr.append(sealedToken.getLiteralStr());
                preToken = curToken;
            }
        }
        System.out.println("=================== ↓ display tokens as string ↓ ===================");
        System.out.println(tokensStr);
        System.out.println("=================== ↓ display tokens one by one ↓ ===================");
        for (SealedToken sealedToken : tokens) {
            if (sealedToken.type == TokenType.NotExistTokenNewLine) {
                continue;
            }
            System.out.println(sealedToken);
        }
        System.out.println("File : [ " + absFilePath + " ]");
        System.out.println("Token size : " + tokens.size());
    }
}

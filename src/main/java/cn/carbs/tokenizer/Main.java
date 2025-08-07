package cn.carbs.tokenizer;

// import static 可以指向静态内部类？
// 1. 首先提供完整R包名。
// 2. 命中R包名后，所有命中的，后缀都记下来，作为一个 HashSet
// 3. 如果import中没有命中 R包名，则把当前package路径作为R的开头，即 some.package.path.R
import static cn.carbs.tokenizer.backup.R.id;
//import static cn.carbs.tokenizer.backup.R.id.my_button;
import cn.carbs.tokenizer.core.JavaTokenParser;
import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.type.TokenType;
import cn.carbs.tokenizer.util.Utils;

import java.util.ArrayList;

public class Main {

    public static void main(String[] argv) {

        // analyseJavaFileAndShow("data3.txt");
//        int x = my_button;
//        testAllCases();
//        int x = id.my_button;
        ArrayList<String> resourceRFilePaths = new ArrayList<>();
        resourceRFilePaths.add("cn.carbs.tokenizer.backup.R");
        resourceRFilePaths.add("cn.carbs.tokenizer.R");
        analysePackageAndImports("data2.txt", resourceRFilePaths);
    }

    /**
     *
     * @param fileName
     * @param resourceRFileImportPath 如 cn.carbs.tokenizer.backup.R  cn.carbs.tokenizer.R
     */
    private static void analysePackageAndImports(String fileName, ArrayList<String> resourceRFileImportPath) {
        ArrayList<String> arrayList = Utils.readLines(fileName);
        ArrayList<SealedToken> tokens = new JavaTokenParser().getTokens(arrayList);
        String packagePath = getPackage(tokens);
        ArrayList<String> importsPaths = getImports(tokens);

        System.out.println("================= Analyse Package And Imports =================");
        System.out.println("[File Name]");
        System.out.println(fileName);
        System.out.println("[Package Path]");
        System.out.println(packagePath);
        System.out.println("[Imports Paths]");
        for (String importPath : importsPaths) {
            System.out.println(importPath);
        }
    }

    private static String getPackage(ArrayList<SealedToken> tokens) {
        if (tokens == null || tokens.size() == 0) {
            return null;
        }
        for (SealedToken sealedToken : tokens) {
            if (sealedToken == null) {
                continue;
            }
            if (sealedToken.type == TokenType.PackagePath) {
                if (sealedToken.literalStr != null) {
                    int length = sealedToken.literalStr.length();
                    if (length > 0 && sealedToken.literalStr.charAt(length - 1) == ';') {
                        return sealedToken.literalStr.substring(0, length - 1);
                    }
                }
                return sealedToken.literalStr;
            }
        }
        return null;
    }

    private static ArrayList<String> getImports(ArrayList<SealedToken> tokens) {
        ArrayList<String> list = new ArrayList<>();
        if (tokens == null) {
            return list;
        }
        for (SealedToken sealedToken : tokens) {
            if (sealedToken == null) {
                continue;
            }
            if (sealedToken.type == TokenType.ImportPath) {
                if (sealedToken.literalStr != null) {
                    int length = sealedToken.literalStr.length();
                    if (length > 0 && sealedToken.literalStr.charAt(length - 1) == ';') {
                        list.add(sealedToken.literalStr.substring(0, length - 1));
                        continue;
                    } else {
                        list.add(sealedToken.literalStr);
                        continue;
                    }
                }
                continue;
            }
            if (sealedToken.type == TokenType.Identifier) {
                break;
            }
        }
        return list;
    }

    private static void testAllCases() {
        String[] allFiles = new String[]{
                "data0.txt", "data1.txt", "data2.txt", "data3.txt", "data4.txt",
                "data5.txt", "data6.txt", "data7.txt", "data8.txt", "data9.txt",};
        int[] tokenCount = new int[]{150, 9976, 3262, 11138, 8251, 3519, 24, 0, 0, 0};
        for (int i = 0; i < allFiles.length; i++) {
            assertTokens(allFiles[i], tokenCount[i]);
        }
    }

    private static void assertTokens(String fileName, int tokenCount) {
        ArrayList<String> arrayList = Utils.readLines(fileName);
        ArrayList<SealedToken> tokens = new JavaTokenParser().getTokens(arrayList);
        if (tokens.size() == tokenCount) {
            System.out.println("[Pass] " + fileName + "'s token count is " + tokenCount);
        } else {
            System.err.println("[Fail] " + fileName + "'s token count is " + tokens.size()
                    + ", target token count is " + tokenCount);
        }
    }

    private static void analyseJavaFileAndShow(String fileName) {
        ArrayList<String> arrayList = Utils.readLines(fileName);

        ArrayList<SealedToken> tokens = new JavaTokenParser().getTokens(arrayList);

        StringBuilder tokensStr = new StringBuilder();
        // 展示 tokens
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
        System.out.println("=================== ↓ display tokens ↓ ===================");
        System.out.println(tokensStr);
        System.out.println("token size : " + tokens.size());
    }
}

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

//         analyseJavaFileAndShow("data0.txt");

//        int x = my_button;
//        testAllCases();
//        int x = id.my_button;

//        ArrayList<String> resourceRFilePaths = new ArrayList<>();
//        resourceRFilePaths.add("cn.carbs.tokenizer.backup.R");
//        resourceRFilePaths.add("cn.carbs.tokenizer.R");
//        analysePackageAndImports("data2.txt", resourceRFilePaths);
    }

    /**
     *
     * @param fileName
     * @param resourceRFileImportPaths 如 cn.carbs.tokenizer.backup.R  cn.carbs.tokenizer.R
     */
    private static void analysePackageAndImports(String fileName, ArrayList<String> resourceRFileImportPaths) {
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

        // 1. 首先假设引入了一个 current.package.path.R 这样一个文件;
        // 2. 检查预设的 resourceRFileImportPaths 中是否有这样一个 current.package.path.R 开头的 import 路径，
        //    如果有，则把 current.package.path.R 加入到 referencedResourcePaths 中；
        //    如果没有，则不再关注 current.package.path.R
        // 3. 遍历所有当前文件的 importPaths，并逐一判断 importPath 是否以 resourceRFileImportPaths 开头，
        //    如果是，则把这个 importPath 最后一个 dot 后面的 string（记作 lastId） 加入到 availableResourceStarters 数据结构数组中，
        //    并把 importPath 和 resourceRFileImportPaths 对比剩余的部分，即 R. 的部分拿出来记下（如记作postfix），用于后续 identifier 的判断
        // 4. 遍历所有 SealedToken，找到以 identifier 开头的 token，用这个 token 和 availableResourceStarters 中的每一个元素（即lastId）进行 equals 比较，
        //    todo 这里有点疑问，要把静态引入的情况也包含进来，比如 int x = my_button;
        //    todo 后续思路还没想好
        //    如果相同，说明当前这个 identifier 有可能是要找的资源文件，
        //        如果这个 identifier == R 后续不以 . 结尾，说明这个identifier 代表的就是最后一个，
        //    然后继续看下一个
        // 各个 的字符串是否等于 判断后续的token是否为 DotForIdentifier 类型，
        //    如果是，则把 . 连接到当前 token
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

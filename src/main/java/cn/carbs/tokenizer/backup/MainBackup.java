package cn.carbs.tokenizer.backup;

// import static 可以指向静态内部类？
// 1. 首先提供完整R包名。
// 2. 命中R包名后，所有命中的，后缀都记下来，作为一个 HashSet
// 3. 如果import中没有命中 R包名，则把当前package路径作为R的开头，即 some.package.path.R

import cn.carbs.tokenizer.WorkFlowOfCode;
import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.reference.IdentifierMatcher;
import cn.carbs.tokenizer.reference.ReferencedToken;
import cn.carbs.tokenizer.type.TokenType;
import cn.carbs.tokenizer.util.Utils;
import cn.carbs.tokenizer.xml.XMLParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainBackup {

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
//        WorkFlowOfResource.test1();
        testOneFileForReferencedToken();
    }

    private static void testOneFileForReferencedToken() {
        ArrayList<String> resourceRFilePaths = new ArrayList<>();
        resourceRFilePaths.add("com.baidu.searchbox.novel.R");
//        String filePath = "/Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business/lib_novel/lib-novel/src/main/java/com/baidu/searchbox/noveladapter/spswitch/NovelCommentBaseInputLayout.kt";
        String filePath = "/Users/v_wangjianjun02/Desktop/code/honor/baidu/baiduapp-android/browser-honor/repos/business/lib_novel/lib-novel/src/main/java/com/baidu/searchbox/noveladapter/spswitch/NovelCommentVerticalDefaultInputLayout.kt";
        ArrayList<ReferencedToken> referencedTokens = WorkFlowOfCode.analyseReferencedResourceTokenForOneCodeFile(filePath, resourceRFilePaths);

        System.out.println("TEST referencedTokens for file : " + filePath);
        for (ReferencedToken referencedToken : referencedTokens) {
            System.out.println(referencedToken);
        }
    }

    private static void testTraverseFolder(String rootFolderAbsPath) {
        // /Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business 下：
        // java 文件有 5424 个
        // kt 文件有 39 个
        // xml 文件有 3965 个
        ArrayList<String> postfixArr = new ArrayList<>();
        postfixArr.add("java");
        postfixArr.add("kt");
        ArrayList<File> files = Utils.findCertainFormatFiles(new File(rootFolderAbsPath), postfixArr);
        System.out.println("java file count : " + files.size());
        int filesSize = files.size();
        int i = 0;
        for (File file : files) {
            System.out.println("Progress -> total : " + filesSize + ", current : " + i);
            System.out.println("file : " + file.getAbsolutePath());
            analyseAbsJavaFileAndShow(file.getAbsolutePath());
            i++;
        }
        System.out.println("Code analysis finished!");
    }


    private static void testXML(String fileName) {
        ArrayList<String> arrayList = Utils.readLinesForFileInResourcesFolder(fileName);
        XMLParser xmlParser = new XMLParser();
        ArrayList<String> arr = xmlParser.getResourceRefs(arrayList);
        System.out.println("testXML() file name : " + fileName);
        for (String s : arr) {
            System.out.println(s);
        }
    }

    private static void testKotlin(String fileName) {
        ArrayList<ReferencedToken> referencedTokens = analysePackageAndImports(fileName, null);
    }

    private static void searchTokensForResourceId(String fileName) {
        ArrayList<String> resourceRFilePaths = new ArrayList<>();
        resourceRFilePaths.add("cn.carbs.tokenizer.R");
        resourceRFilePaths.add("cn.carbs.tools.R");
        resourceRFilePaths.add("cn.carbs.ttt.R");
//        ArrayList<ReferencedToken> referencedTokens = analysePackageAndImports("data00.java", resourceRFilePaths);
        ArrayList<ReferencedToken> referencedTokens = analysePackageAndImports(fileName, resourceRFilePaths);
        if (referencedTokens == null) {
            System.out.println("result referencedTokens == null");
        } else {
            System.out.println("result referencedTokens.size : " + referencedTokens.size());
        }
    }

    private static void test() {
        Utils.isStringMatchAndroidResourcePattern("\"Zmy_fragment_layout\"");
    }


    /**
     * @param fileName
     * @param resourceRFileImportPaths 如 cn.carbs.tokenizer.backup.R  cn.carbs.tokenizer.R
     */
    private static ArrayList<ReferencedToken> analysePackageAndImports(String fileName, ArrayList<String> resourceRFileImportPaths) {
        ArrayList<String> arrayList = Utils.readLinesForFileInResourcesFolder(fileName);
        ArrayList<SealedToken> tokens = Utils.genCodeTokenParserByFileName(fileName).getTokens(arrayList);
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

//        System.out.println("[Tokens]");
//        for (SealedToken sealedToken : tokens) {
//            if (sealedToken.type == TokenType.NotExistTokenNewLine) {
//                continue;
//            }
//            System.out.println(sealedToken);
//        }

        // todo wang
//        if (true) {
//            return null;
//        }


        if (resourceRFileImportPaths == null) {
            return null;
        }

        // 1. 首先假设引入了一个 current.package.path.R 这样一个文件;（必须到 .R 结束）
        String possibleRPath = packagePath + ".R";
        // 2. 检查预设的 resourceRFileImportPaths 中是否有这样一个 current.package.path.R 开头的 import 路径，
        boolean needAddPossibleRPath = false;
        for (String certainRPath : resourceRFileImportPaths) {
            if (certainRPath != null && certainRPath.equals(possibleRPath)) {
                needAddPossibleRPath = true;
            }
        }
        // 加到 import 中，作为默认
        if (needAddPossibleRPath) {
            importsPaths.add(possibleRPath);
        }

        HashMap<String, IdentifierMatcher> identifierMatcherMap = new HashMap();
        int minLengthOfImportPath = Integer.MAX_VALUE;
        // 3. 遍历所有当前文件的 importPaths，并逐一判断 importPath 是否以 resourceRFileImportPaths 开头，
        //    如果是，则把这个 importPath 最后一个 dot 后面的 string（记作 lastId） 加入到 availableResourceStarters 数据结构数组中，
        //    并把 importPath 和 resourceRFileImportPaths 对比剩余的部分，即 R. 的部分拿出来记下（如记作 postfix），用于后续 identifier 的判断
        for (String importPath : importsPaths) {
            for (String certainRPath : resourceRFileImportPaths) {
                if (importPath != null && importPath.startsWith(certainRPath)) {
                    // 例如 importPath = cn.carbs.tools.R  certainRPath = cn.carbs.tools.R
                    IdentifierMatcher identifierMatcher = Utils.getIdentifierMatcherForImport(importPath, certainRPath);
                    if (identifierMatcher != null) {
                        identifierMatcherMap.put(identifierMatcher.lastIdentifier, identifierMatcher);
                        System.out.println("[IdentifierMatcher] --> ");
                        System.out.println("importPath : " + importPath);
                        System.out.println("lastIdentifier : " + identifierMatcher.lastIdentifier);
                        System.out.println("iPrefix : " + identifierMatcher.iPrefix);
                        System.out.println("rPostfix : " + identifierMatcher.rPostfix);
                        if (identifierMatcher.importPath.length() < minLengthOfImportPath) {
                            minLengthOfImportPath = identifierMatcher.importPath.length();
                        }
                    }
                }
            }
        }
        System.out.println("minLengthOfImportPath : " + minLengthOfImportPath);
        System.out.println("==================================================");
        // 4. 遍历所有 SealedToken，如果 SealedToken 是identifier类型，一直往后连dot类型，直到停止，即找出了一个完整对象，记作 completeIdentifierToken。
        //    如果当前token不是identifier类型，则继续遍历。

        ArrayList<ReferencedToken> referencedTokens = new ArrayList<>();
        ArrayList<SealedToken> completeIdentifierToken = new ArrayList<>();
        StringBuilder completeToken = new StringBuilder();
        TokenType prevAvailableTokenType = TokenType.None;
        for (int i = 0; i < tokens.size(); i++) {
            SealedToken currentToken = tokens.get(i);
            System.out.println("=====> " + currentToken.literalStr);
            if (currentToken.type == TokenType.NotExistTokenNewLine) {
                continue;
            }
            if (currentToken.type == TokenType.Identifier) {
                if (prevAvailableTokenType != TokenType.Identifier) {
                    // 继续收集
                    completeIdentifierToken.add(currentToken);
                } else {
                    completeToken.setLength(0);
                    if (completeIdentifierToken.size() > 0) {
                        for (int m = 0; m < completeIdentifierToken.size(); m++) {
                            completeToken.append(completeIdentifierToken.get(m).literalStr);
                        }
                        // todo wang
                        System.out.println("-> " + completeToken);

                        if (completeToken.length() >= minLengthOfImportPath) {
                            String completeTokenStr = completeToken.toString();

                            for (String s : resourceRFileImportPaths) {
                                if (completeTokenStr.startsWith(s)) {
                                    // 全路径的 cn.carbs.tools.R.layout.xxx
                                    // todo wang
                                    // 可疑
                                    IdentifierMatcher wholePackagePathMatcher = new IdentifierMatcher(s, completeTokenStr)
                                            .setStandardImport(s);
                                    ReferencedToken referencedToken = new ReferencedToken(wholePackagePathMatcher, completeTokenStr)
                                            .setStandardReferenceStr(completeTokenStr.substring(s.lastIndexOf('.') + 1));

                                    System.out.println("[MAYBE] ReferencedToken 1 --> " + referencedToken);
                                    referencedTokens.add(referencedToken);
                                    break;
                                }
                            }
                        } else {
                            // 小于全路径
                            ReferencedToken referencedToken = Utils.checkTokenMatched(identifierMatcherMap, completeIdentifierToken, completeToken);
                            if (referencedToken != null) {
                                // todo wang 可能命中！
                                System.out.println("[MAYBE] ReferencedToken 2 --> " + referencedToken);
                                referencedTokens.add(referencedToken);
                            }
                        }
                    }
                    // 清空
                    completeIdentifierToken.clear();
                    completeIdentifierToken.add(currentToken);
                }
                prevAvailableTokenType = TokenType.Identifier;
                continue;
            } else if (currentToken.type == TokenType.DotForIdentifier) {
                if (prevAvailableTokenType != TokenType.DotForIdentifier) {
                    // 继续收集
                    completeIdentifierToken.add(currentToken);
                } else {
                    // 连着两个 .. 清空
                    completeIdentifierToken.clear();
                }
                prevAvailableTokenType = TokenType.DotForIdentifier;
                continue;
            } else {
                // todo wang 还有 NotExistTokenNewLine
                prevAvailableTokenType = TokenType.None;
                if (currentToken.type == TokenType.String) {
                    // todo wang string
                    System.out.println("currentToken.type == TokenType.String ******");
                    if (Utils.isStringMatchAndroidResourcePattern(currentToken.literalStr)) {
                        ReferencedToken referencedToken
                                = new ReferencedToken(Utils.trimStringIfWithQuotation(currentToken.literalStr));
                        // todo wang 可能命中！
                        System.out.println("[MAYBE] ReferencedToken 10 --> " + referencedToken);
                        referencedTokens.add(referencedToken);
                    }
                }
                if (completeIdentifierToken.size() == 0) {
                    continue;
                } else {
                    completeToken.setLength(0);
                    for (int m = 0; m < completeIdentifierToken.size(); m++) {
                        completeToken.append(completeIdentifierToken.get(m).literalStr);
                    }
                    // todo wag
                    if (completeIdentifierToken.size() > 0) {
                        // todo 有问题---
                        if (completeToken.length() >= minLengthOfImportPath) {
                            String completeTokenStr = completeToken.toString();

                            for (String s : resourceRFileImportPaths) {
                                if (completeTokenStr.startsWith(s)) {
                                    // 全路径的 cn.carbs.tools.R.layout.xxx
                                    // todo wang
                                    // 可疑
                                    IdentifierMatcher wholePackagePathMatcher = new IdentifierMatcher(s, completeTokenStr)
                                            .setStandardImport(s);

                                    ReferencedToken referencedToken = new ReferencedToken(wholePackagePathMatcher, completeTokenStr)
                                            .setStandardReferenceStr(completeTokenStr.substring(s.lastIndexOf('.') + 1));
                                    System.out.println("[MAYBE] ReferencedToken 3 --> " + referencedToken);
                                    referencedTokens.add(referencedToken);
                                    break;
                                }
                            }
                            // 没有命中 比如 R.layout.xxxxxxx_xxxxxxxxxx
                            // 小于全路径
                            ReferencedToken referencedToken = Utils.checkTokenMatched(identifierMatcherMap, completeIdentifierToken, completeToken);
                            if (referencedToken != null) {
                                // todo wang 可能命中！
                                System.out.println("[MAYBE] ReferencedToken 4 --> " + referencedToken);
                                referencedTokens.add(referencedToken);
                            }
                        } else {
                            // 小于全路径
                            ReferencedToken referencedToken = Utils.checkTokenMatched(identifierMatcherMap, completeIdentifierToken, completeToken);
                            if (referencedToken != null) {
                                // todo wang 可能命中！
                                System.out.println("[MAYBE] ReferencedToken 5 --> " + referencedToken);
                                referencedTokens.add(referencedToken);
                            }
                        }
                    }
                    // 清空
                    completeIdentifierToken.clear();
                }
            }
        }
        return referencedTokens;

        /*
         1. 首先假设引入了一个 current.package.path.R 这样一个文件;（必须到 .R 结束）
         2. 检查预设的 resourceRFileImportPaths 中是否有这样一个 current.package.path.R 开头的 import 路径，
            如果有，则把 current.package.path.R 加入到 referencedResourcePaths 中；(这是为了确保不会有故意写成R的其他java类)
            如果没有，则不再关注 current.package.path.R
         3. 遍历所有当前文件的 importPaths，并逐一判断 importPath 是否以 resourceRFileImportPaths 开头，
            如果是，则把这个 importPath 最后一个 dot 后面的 string（记作 lastId） 加入到 availableResourceStarters 数据结构数组中，
            并把 importPath 和 resourceRFileImportPaths 对比剩余的部分，即 R. 的部分拿出来记下（如记作 postfix），用于后续 identifier 的判断
         4. 遍历所有 SealedToken，如果 SealedToken 是identifier类型，一直往后连dot类型，直到停止，即找出了一个完整对象，记作 completeIdentifierToken。
         如果当前token不是identifier类型，则继续遍历。
              // 先获取 resourceRFileImportPaths 中最短元素 length
              if (completeIdentifierToken .length > resourceRFileImportPaths 中最短元素 length
                    && completeIdentifierToken 以某个 预设的 resourceRFileImportPaths 中的某一个开头) {
                  // 命中 结束判断 return
              }
              // 走 下面的第 5 条判断

            用 completeIdentifierToken 的第一个token（即 如果有dot，则取第一个 dot 之前的token） 和 availableResourceStarters 中的每一个元素（即lastId）进行 equals 比较，
            如果相同，说明当前这个 identifier 有可能是要找的资源文件，
                如果这个 identifier == R，则需要判断后续的 identifier 必须命中 某种正则，比如 R.layout.xxx 或者 R.drawable.xxx
                如果这个 identifier != R，比如是 identifier == layout，则找到 命中 availableResourceStarters 的那个元素对应的 postfix（比如形式是 layout 或者 layout.xxx）
                    如果这个 postfix 的形式 是 layout，则说明 identifier 还需要继续往后找 identifier 来判断是否命中
                    如果这个 postfix 的形式 是 layout.xxx，则说明 identifier 当前命中的是 xxx，此时要确认 identifier 后面不再连着 dot

         5. 遍历所有 SealedToken，找到以 identifier 开头的 token，用这个 token 和 availableResourceStarters 中的每一个元素（即lastId）进行 equals 比较，
            todo 这里有点疑问，要把静态引入的情况也包含进来，比如 int x = my_button;
            todo 思路差不多好了
            如果相同，说明当前这个 identifier 有可能是要找的资源文件，
                如果这个 identifier == R，则需要判断后续的 identifier 必须命中 某种正则，比如 R.layout.xxx 或者 R.drawable.xxx
                如果这个 identifier != R，比如是 identifier == layout，则找到 命中 availableResourceStarters 的那个元素对应的 postfix（比如形式是 layout 或者 layout.xxx）
                    如果这个 postfix 的形式 是 layout，则说明 identifier 还需要继续往后找 identifier 来判断是否命中
                    如果这个 postfix 的形式 是 layout.xxx，则说明 identifier 当前命中的是 xxx，此时要确认 identifier 后面不再连着 dot
         */
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
                "data00.java", "data01.java", "data02.java", "data03.java", "data04.java",
                "data05.java", "data06.java", "data07.java", "data08.java", "data09.java",};
        int[] tokenCount = new int[]{150, 9976, 3262, 11138, 8251, 3519, 24, 0, 0, 0};
        for (int i = 0; i < allFiles.length; i++) {
            assertTokens(allFiles[i], tokenCount[i]);
        }
    }

    private static void assertTokens(String fileName, int tokenCount) {
        ArrayList<String> arrayList = Utils.readLinesForFileInResourcesFolder(fileName);
        ArrayList<SealedToken> tokens = Utils.genCodeTokenParserByFileName(fileName).getTokens(arrayList);
        if (tokens.size() == tokenCount) {
            System.out.println("[Pass] " + fileName + "'s token count is " + tokenCount);
        } else {
            System.err.println("[Fail] " + fileName + "'s token count is " + tokens.size()
                    + ", target token count is " + tokenCount);
        }
    }

    private static void analyseJavaFileAndShow(String fileName) {
        ArrayList<String> arrayList = Utils.readLinesForFileInResourcesFolder(fileName);

        ArrayList<SealedToken> tokens = Utils.genCodeTokenParserByFileName(fileName).getTokens(arrayList);

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

    private static void analyseAbsJavaFileAndShow(String fileNamePath) {
        ArrayList<String> arrayList = Utils.readLinesForAbsFilePath(fileNamePath);

        ArrayList<SealedToken> tokens = Utils.genCodeTokenParserByFileName(fileNamePath).getTokens(arrayList);

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
//        System.out.println(tokensStr);
        System.out.println("File : [ " + fileNamePath + " ]");
        System.out.println("Token size : " + tokens.size());
    }
}

package cn.carbs.tokenizer;

import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.search.CodeFileAndReferencedToken;
import cn.carbs.tokenizer.search.IdentifierMatcher;
import cn.carbs.tokenizer.search.ReferencedToken;
import cn.carbs.tokenizer.type.TokenType;
import cn.carbs.tokenizer.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkFlowOfCode {

    public static void test1() {
        ArrayList<String> resourceRFilePaths = new ArrayList<>();
        resourceRFilePaths.add("com.baidu.searchbox.novel.R");
        ArrayList<CodeFileAndReferencedToken> arr = traverseFolderAndAnalyseJavaAndKotlinCode(
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

    /**
     * @param rootFolderAbsPath
     * @param resourceRFilePaths
     */
    public static ArrayList<CodeFileAndReferencedToken> traverseFolderAndAnalyseJavaAndKotlinCode(String rootFolderAbsPath, ArrayList<String> resourceRFilePaths) {
        ArrayList<String> postfixArr = new ArrayList<>();
        ArrayList<CodeFileAndReferencedToken> retArr = new ArrayList<>();
        postfixArr.add("java");
        postfixArr.add("kt");
        ArrayList<File> files = Utils.findCertainFormatFiles(new File(rootFolderAbsPath), postfixArr);
        System.out.println("java and kotlin file count : " + files.size());
        int filesSize = files.size();
        int i = 0;
//        int hasRefCount = 0;
        for (File file : files) {
            System.out.println("Progress -> total : " + filesSize + ", current : " + i + ", file : ");
            System.out.println(file.getAbsolutePath());
            i++;
//            analyseAbsJavaAndKotlinFileAndShow(file.getAbsolutePath());
            // todo wang 接收一下
//            searchTokensForResourceId(file.getAbsolutePath(), resourceRFilePaths);
            ArrayList<ReferencedToken> referencedTokens = analyseReferencedResourceForFilePaths(file.getAbsolutePath(), resourceRFilePaths);

            if (referencedTokens != null && referencedTokens.size() > 0) {
//                hasRefCount++;
//                for (ReferencedToken referencedToken : referencedTokens) {
//                    System.out.println("Referenced Token : " + referencedToken);
//                }
                retArr.add(new CodeFileAndReferencedToken(referencedTokens, file.getAbsolutePath()));
            }
            // 测试用
//            if (hasRefCount > 20) {
//                break;
//            }
        }
        System.out.println("Code analysis finished!");
        return retArr;
    }

//    private static void traverseFolderAndAnalyseJavaAndKotlinCode(String rootFolderAbsPath) {
//        ArrayList<String> postfixArr = new ArrayList<>();
//        postfixArr.add("java");
//        postfixArr.add("kt");
//        ArrayList<File> files = Utils.findCertainFormatFiles(new File(rootFolderAbsPath), postfixArr);
//        System.out.println("java and kotlin file count : " + files.size());
//        int filesSize = files.size();
//        int i = 0;
//        for (File file : files) {
//            System.out.println("Progress -> total : " + filesSize + ", current : " + i);
//            System.out.println("file : " + file.getAbsolutePath());
//            i++;
////            analyseAbsJavaAndKotlinFileAndShow(file.getAbsolutePath());
//            searchTokensForResourceId(file.getAbsolutePath(), null);
//        }
//        System.out.println("Code analysis finished!");
//    }

    public static ArrayList<ReferencedToken> analyseReferencedResourceForFilePaths(String fileName, ArrayList<String> resourceRFileImportPaths) {
//        ArrayList<String> arrayList = Utils.readLinesForFileInResourcesFolder(fileName);
        ArrayList<String> arrayList = Utils.readLinesForAbsFilePath(fileName);
        ArrayList<SealedToken> tokens = Utils.genCodeTokenParserByFileName(fileName).getTokens(arrayList);
        String packagePath = getPackage(tokens);
        ArrayList<String> importsPaths = getImports(tokens);

//        System.out.println("================= Analyse Package And Imports =================");
//        System.out.println("[File Name]");
//        System.out.println(fileName);
//        System.out.println("[Package Path]");
//        System.out.println(packagePath);
//        System.out.println("[Imports Paths]");
//        for (String importPath : importsPaths) {
//            System.out.println(importPath);
//        }
//        System.out.println("[Tokens]");
//        for (SealedToken sealedToken : tokens) {
//            if (sealedToken.type == TokenType.NotExistTokenNewLine) {
//                continue;
//            }
//            System.out.println(sealedToken);
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
//                        System.out.println("[IdentifierMatcher] --> ");
//                        System.out.println("importPath : " + importPath);
//                        System.out.println("lastIdentifier : " + identifierMatcher.lastIdentifier);
//                        System.out.println("iPrefix : " + identifierMatcher.iPrefix);
//                        System.out.println("rPostfix : " + identifierMatcher.rPostfix);
                        if (identifierMatcher.importPath.length() < minLengthOfImportPath) {
                            minLengthOfImportPath = identifierMatcher.importPath.length();
                        }
                    }
                }
            }
        }
//        System.out.println("minLengthOfImportPath : " + minLengthOfImportPath);
//        System.out.println("==================================================");
        // 4. 遍历所有 SealedToken，如果 SealedToken 是identifier类型，一直往后连dot类型，直到停止，即找出了一个完整对象，记作 completeIdentifierToken。
        //    如果当前token不是identifier类型，则继续遍历。

        ArrayList<ReferencedToken> referencedTokens = new ArrayList<>();
        ArrayList<SealedToken> completeIdentifierToken = new ArrayList<>();
        StringBuilder completeToken = new StringBuilder();
        TokenType prevAvailableTokenType = TokenType.None;
        for (int i = 0; i < tokens.size(); i++) {
            SealedToken currentToken = tokens.get(i);
//            System.out.println("=====> " + currentToken.literalStr);
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
                        if (completeToken.length() >= minLengthOfImportPath) {
                            String completeTokenStr = completeToken.toString();

                            for (String s : resourceRFileImportPaths) {
                                if (completeTokenStr.startsWith(s)) {
                                    // 可疑
                                    IdentifierMatcher wholePackagePathMatcher = new IdentifierMatcher(s, completeTokenStr)
                                            .setStandardImport(s);
                                    ReferencedToken referencedToken = new ReferencedToken(wholePackagePathMatcher, completeTokenStr)
                                            .setStandardSimpleReference(completeTokenStr.substring(s.lastIndexOf('.') + 1));
//                                    System.out.println("[MAYBE] ReferencedToken 1 --> " + referencedToken);
                                    referencedTokens.add(referencedToken);
                                    break;
                                }
                            }
                            // 没有命中 比如 R.layout.xxxxxxx_xxxxxxxxxx
                            // 小于全路径
                            ReferencedToken referencedToken = Utils.checkTokenMatched(identifierMatcherMap, completeIdentifierToken, completeToken);
                            if (referencedToken != null) {
//                                System.out.println("[MAYBE] ReferencedToken 4 --> " + referencedToken);
                                referencedTokens.add(referencedToken);
                            }
                        } else {
                            // 小于全路径
                            ReferencedToken referencedToken = Utils.checkTokenMatched(identifierMatcherMap, completeIdentifierToken, completeToken);
                            if (referencedToken != null) {
//                                System.out.println("[MAYBE] ReferencedToken 2 --> " + referencedToken);
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
//                    System.out.println("currentToken.type == TokenType.String ******");
                    if (Utils.isStringMatchAndroidResourcePattern(currentToken.literalStr)) {
                        ReferencedToken referencedToken
                                = new ReferencedToken(Utils.trimStringIfWithQuotation(currentToken.literalStr));
                        // todo wang 可能命中！
//                        System.out.println("[MAYBE] ReferencedToken 10 --> " + referencedToken);
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
                    if (completeIdentifierToken.size() > 0) {
                        if (completeToken.length() >= minLengthOfImportPath) {
                            String completeTokenStr = completeToken.toString();
                            for (String s : resourceRFileImportPaths) {
                                if (completeTokenStr.startsWith(s)) {
                                    IdentifierMatcher wholePackagePathMatcher = new IdentifierMatcher(s, completeTokenStr)
                                            .setStandardImport(s);
                                    ReferencedToken referencedToken = new ReferencedToken(wholePackagePathMatcher, completeTokenStr)
                                            .setStandardSimpleReference(completeTokenStr.substring(s.lastIndexOf('.') + 1));
//                                    System.out.println("[MAYBE] ReferencedToken 3 --> " + referencedToken);
                                    referencedTokens.add(referencedToken);
                                    break;
                                }
                            }
                            // 没有命中 比如 R.layout.xxxxxxx_xxxxxxxxxx
                            // 小于全路径
                            ReferencedToken referencedToken = Utils.checkTokenMatched(identifierMatcherMap, completeIdentifierToken, completeToken);
                            if (referencedToken != null) {
//                                System.out.println("[MAYBE] ReferencedToken 4 --> " + referencedToken);
                                referencedTokens.add(referencedToken);
                            }
                        } else {
                            // 小于全路径
                            ReferencedToken referencedToken = Utils.checkTokenMatched(identifierMatcherMap, completeIdentifierToken, completeToken);
                            if (referencedToken != null) {
//                                System.out.println("[MAYBE] ReferencedToken 5 --> " + referencedToken);
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

    private static void analyseAbsJavaAndKotlinFileAndShow(String fileNamePath) {
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

// /Users/v_wangjianjun02/Desktop/code/honor/baidu/browser-android/novel-sdk/repos/business 下：
// java 文件有 5424 个
// kt 文件有 39 个
// xml 文件有 3965 个

import state.CommentOrString;
import state.ImportState;

import java.io.BufferedReader;
import java.io.InputStream;
import /**/ java.io./*DAFAFA    */InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Main {

    private static TokenCache sCurrentToken = new TokenCache();
    private static CommentOrString sCommentOrString = CommentOrString.None;

    public static void main(String[] argv) {

        ArrayList<String> arrayList = readLines();

        ArrayList<SealedToken> tokens = getTokens(arrayList);

        StringBuilder tokensStr = new StringBuilder();
        // test
        if (tokens != null) {
            for (SealedToken sealedToken : tokens) {
//                System.out.println(sealedToken.getLiteralStr());
                tokensStr.append(sealedToken.getLiteralStr());
            }
        }
        System.out.println("======================================");
        System.out.println(tokensStr);
    }

    // 获取一个 file 的 tokens
    private static ArrayList<SealedToken> getTokens(ArrayList<String> arrayList) {
        if (arrayList == null || arrayList.size() == 0) {
            return new ArrayList<>();
        }
        ArrayList<SealedToken> tokens = new ArrayList<>(arrayList.size() * 10);
        // 开始了
        SectionType sectionType = SectionType.None;
        StringBuilder packageStr = new StringBuilder();

        ArrayList<String> importStrArr = new ArrayList<>();
        StringBuilder importStrCache = new StringBuilder();
        ImportState importState = ImportState.None;

        int penetratePackageAndImportSectionState = 0;

        int lineIndex = -1;
        for (String s : arrayList) {
            lineIndex++;
            System.out.println("line -> " + s);
            // 人工添加一个 换行 token，便于打印
            int strLength = s.length();
            if (sCommentOrString == CommentOrString.InSlashComment) {
                // 新的一行，跳出行注释
                sCommentOrString = CommentOrString.None;
            } else if (sCommentOrString == CommentOrString.MayEndBlockComment) {
                sCommentOrString = CommentOrString.InBlockComment;
            }
            if (sectionType != SectionType.ContentSection) {
                // 当 section 位于 none 或者 package 或者 import 时
                for (int i = 0; i < strLength; i++) {
                    char c = s.charAt(i);
                    if (sectionType == SectionType.None) {
                        if (sCommentOrString == CommentOrString.None) {
                            if (isSpace(c)) {
                                continue;
                            } else if (c == 'p') {
                                sectionType = SectionType.PackageSection;
                                packageStr.append(c);
                                continue;
                            } else if (c == 'i' && i < strLength - 1 && s.charAt(i + 1) == 'm') {
                                // 有可能是 interface
                                // 无 package 声明，直接进入 import
                                sectionType = SectionType.ImportSection;
                                importState = ImportState.Processing;
                                importStrCache.append("i");
                                continue;
                            } else if (isCommentStarter(c)) {
                                sCommentOrString = CommentOrString.MayCommentStarter;
                                // 全局变量存储当前是否为 string，是否为 comment
                                // todo wang
                                // sectionType 不变，是一层
                                continue;
                            } else {
                                // todo 这里需要判断是否有注释
                                // 无 package 无 import，直接进入 content
                                sectionType = SectionType.ContentSection;
                                penetratePackageAndImportSectionState = 1;
                                break;
                            }
                        } else if (sCommentOrString == CommentOrString.MayCommentStarter) {
                            if (isCommentStarter(c)) {
                                sCommentOrString = CommentOrString.InSlashComment;
                                continue;
                            } else if (c == '*') {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            } else {
                                fatal("000");
                            }
                        } else if (sCommentOrString == CommentOrString.InSlashComment) {
                            continue;
                        } else if (sCommentOrString == CommentOrString.InBlockComment) {
                            if (c == '*') {
                                sCommentOrString = CommentOrString.MayEndBlockComment;
                            }
                            continue;
                        } else if (sCommentOrString == CommentOrString.MayEndBlockComment) {
                            if (c == '*') {
                                continue;
                            } else if (isCommentStarter(c)) {
                                // 收 comment
                                sCommentOrString = CommentOrString.None;
                                // todo
//                                curToken.type = TokenType.CommentBlock;
                                continue;
                            } else {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            }
                        }
                    } else if (sectionType == SectionType.PackageSection) {
                        if (sCommentOrString == CommentOrString.None) {
                            if (isSpace(c)) {
                                continue;
                            } else if (isLegalIdentifierPostfix(c) || isDot(c)) {
                                sectionType = SectionType.PackageSection;
                                packageStr.append(c);
                                continue;
                            } else if (isExpressionEnd(c)) {
                                sectionType = SectionType.ImportSection;
                                packageStr.append(c);
                                continue;
                            } else if (isCommentStarter(c)) {
                                sCommentOrString = CommentOrString.MayCommentStarter;
                                continue;
                            }
                        } else if (sCommentOrString == CommentOrString.MayCommentStarter) {
                            if (isCommentStarter(c)) {
                                // package 中应该没有行注释
                                fatal("11");
                            } else if (c == '*') {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            } else {
                                fatal("22");
                            }
                        } else if (sCommentOrString == CommentOrString.InBlockComment) {
                            if (c == '*') {
                                sCommentOrString = CommentOrString.MayEndBlockComment;
                            }
                            continue;
                        } else if (sCommentOrString == CommentOrString.MayEndBlockComment) {
                            if (c == '*') {
                                continue;
                            } else if (isCommentStarter(c)) {
                                // 收 block comment
                                // todo
                                sCommentOrString = CommentOrString.None;
                                continue;
                            } else {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            }
                        }
                    } else if (sectionType == SectionType.ImportSection) {
                        // 可能有注释
                        if (sCommentOrString == CommentOrString.None) {
                            if (importState == ImportState.None) {
                                if (isSpace(c)) {
                                    continue;
                                } else if (c == 'i' && i < strLength - 1 && s.charAt(i + 1) == 'm') {
                                    importState = ImportState.Processing;
                                    importStrCache.append("i");
                                    continue;
                                } else if (isExpressionEnd(c)) {
                                    continue;
                                } else if (isCommentStarter(c)) {
                                    sCommentOrString = CommentOrString.MayCommentStarter;
                                    continue;
                                } else {
                                    // todo 待验证
//                            fatal("1");
                                    sectionType = SectionType.ContentSection;
                                    // 在后面的 for 循环中，此行重新循环
                                    penetratePackageAndImportSectionState = 1;
                                    break;
                                }
                            } else if (importState == ImportState.Processing) {
                                if (isSpace(c)) {
                                    continue;
                                } else if (isLegalIdentifierPostfix(c) || isDot(c)) {
                                    importStrCache.append(c);
                                    continue;
                                } else if (isExpressionEnd(c)) {
                                    importStrCache.append(c);
                                    importStrArr.add(importStrCache.toString());
                                    // todo 下一行，怎么处理
                                    // 添加标志位，进入下一行
                                    // 不对，有可能一行有两个import
                                    importState = ImportState.None;
                                    continue;
                                } else if (isCommentStarter(c)) {
                                    sCommentOrString = CommentOrString.MayCommentStarter;
                                    continue;
                                }
                            }
                        } else if (sCommentOrString == CommentOrString.MayCommentStarter) {
                            if (isCommentStarter(c)) {
                                sCommentOrString = CommentOrString.InSlashComment;
                                continue;
                            } else if (c == '*') {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            } else {
                                fatal("33");
                            }
                        } else if (sCommentOrString == CommentOrString.InSlashComment) {
                            continue;
                        } else if (sCommentOrString == CommentOrString.InBlockComment) {
                            if (c == '*') {
                                sCommentOrString = CommentOrString.MayEndBlockComment;
                            }
                            continue;
                        } else if (sCommentOrString == CommentOrString.MayEndBlockComment) {
                            if (c == '*') {
                                continue;
                            } else if (isCommentStarter(c)) {
                                // 收 comment
                                sCommentOrString = CommentOrString.None;
                                // todo
                                continue;
                            } else {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            }
                        }
                    }
                }
            }

            if (sectionType == SectionType.None
                    || sectionType == SectionType.PackageSection
                    || sectionType == SectionType.ImportSection) {
                continue;
            }

            // todo
//            if (false) {
//                System.out.println("package is --> " + packageStr + "\n");
//                for (String im : importStrArr) {
//                    System.out.println("import is --> " + im + "\n");
//                }
//                return tokens;
//            }
            if (penetratePackageAndImportSectionState == 1) {
                penetratePackageAndImportSectionState = 2;
                // 在这里收集 package 和 import
                tokens.add(SealedToken.genPackageToken(packageStr.toString()));
                tokens.add(SealedToken.genNewLineToken());
                for (String im : importStrArr) {
                    tokens.add(SealedToken.genPackageToken(im));
                    tokens.add(SealedToken.genNewLineToken());
                }
            }

            tokens.add(SealedToken.genNewLineToken());

            // 只有等到 sectionType == SectionType.ContentSection ，才会进入下面的代码中

            // 上一行以 identifier 或者 number 或者
            if (sCurrentToken.type == TokenType.Identifier || sCurrentToken.type == TokenType.Number) {
                collectTokenAndResetCache(tokens, sCurrentToken);
            }

            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                // todo wang 先判断是否为 commentOrString
                if (sCommentOrString == CommentOrString.None) {
                    if (sCurrentToken.type == TokenType.None) {
                        if (isCommentStarter(c)) {
                            sCommentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            sCommentOrString = CommentOrString.InString;
                            sCurrentToken.type = TokenType.String;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isSpace(c)) {
                            // 如果是空白，继续前进
                            continue;
                        } else if (isLegalIdentifierStarter(c)) {
                            // 如果是合法的起始 identifier
                            sCurrentToken.type = TokenType.Identifier;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isOperator(c)) {
                            // 每一个 operator 都回收
                            sCurrentToken.type = TokenType.Operator;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isParentheses(c)) {
                            sCurrentToken.type = TokenType.Parentheses;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isLegalNumberStarter(c)) {
                            sCurrentToken.type = TokenType.Number;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isCharSymbol(c)) {
                            sCurrentToken.type = TokenType.Char;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            sCurrentToken.type = TokenType.End;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isDot(c)) {
                            sCurrentToken.type = TokenType.Dot;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else {
                            System.out.println("[ else 0 continue lineIndex : " + lineIndex + " columnIndex : " + i + " ] ");
                            continue;
                        }
                    } else if (sCurrentToken.type == TokenType.Identifier) {
                        if (isCommentStarter(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCommentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            // todo 不可能，输出log

                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCommentOrString = CommentOrString.InString;
                            sCurrentToken.type = TokenType.String;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isSpace(c)) {
                            // identifier 确实应该回收，后续再分析一下 identifier 后面 + dot + identifier 的情况
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isLegalIdentifierPostfix(c)) {
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isDot(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.Dot;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isOperator(c)) {
                            // 收 identifier
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            // 暂时不收 operator，有可能有多个连续operator字符
                            sCurrentToken.type = TokenType.Operator;
                            sCurrentToken.appendLiteralChar(c);
                            // 每一个 operator 都回收
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isParentheses(c)) {
                            // 收 identifier
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            // 收 括号
                            sCurrentToken.type = TokenType.Operator;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.End;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else {
                            System.out.println("[ else 1 continue lineIndex : " + lineIndex + " columnIndex : " + i + " ] ");
                            continue;
                        }
                    } else if (sCurrentToken.type == TokenType.Number) {
                        if (isCommentStarter(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCommentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            // todo 不可能，输出log
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCommentOrString = CommentOrString.InString;
                            sCurrentToken.type = TokenType.String;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isSpace(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isOperator(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.Operator;
                            sCurrentToken.appendLiteralChar(c);
                            // 收 operator
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isLegalNumberPostfix(c)) {
                            // 继续
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isParentheses(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.Parentheses;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.End;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                        } else {
                            System.out.println("[ else 3 curToken.type == " + sCurrentToken.type.name() + " continue lineIndex : " + lineIndex + " columnIndex : " + i + " ] ");
                            continue;
                        }
                    } else if (sCurrentToken.type == TokenType.Char) {
                        if (isCharSymbol(c)) {
                            // 收
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else {
                            // append
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        }
                    } else {
                        // curToken.type == TokenType.Operator 这种情况不存在，因为 每次遇到 operator 都会回收并重置
                        System.out.println("[ curToken.type == " + sCurrentToken.type.name() + "  continue lineIndex : " + lineIndex + " columnIndex : " + i + " ] ");
                        continue;
                    }
                } else if (sCommentOrString == CommentOrString.MayCommentStarter) {
                    // todo
                    if (isCommentStarter(c)) {
                        sCommentOrString = CommentOrString.InSlashComment;
                        continue;
                    } else if (c == '*') {
                        sCommentOrString = CommentOrString.InBlockComment;
                        continue;
                    } else {
                        // 前一个 收为 除号
                        sCurrentToken.type = TokenType.Operator;
                        sCurrentToken.appendLiteralChar('/');
                        collectTokenAndResetCache(tokens, sCurrentToken);

                        // 当前 判断当前的字符
                        sCommentOrString = CommentOrString.None;
                        // todo 以下代码 和上面 commentOrString == CommentOrString.None && curToken.type == TokenType.None 的情况重复
                        if (isCommentStarter(c)) {
                            sCommentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            // todo 不可能，输出log
                            sCommentOrString = CommentOrString.InString;
                            sCurrentToken.type = TokenType.String;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isSpace(c)) {
                            // 如果是空白，继续前进
                            continue;
                        } else if (isLegalIdentifierStarter(c)) {
                            // 如果是合法的起始 identifier
                            sCurrentToken.type = TokenType.Identifier;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isOperator(c)) {
                            // 每一个 operator 都回收
                            sCurrentToken.type = TokenType.Operator;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isParentheses(c)) {
                            sCurrentToken.type = TokenType.Parentheses;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isLegalNumberStarter(c)) {
                            // todo 怎样区分 dot 和 number ?
                            // 根据前面的判断？如果是 ；或者 operator  或者是 number
                            // todo wang 前面应该是
                            sCurrentToken.type = TokenType.Number;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isCharSymbol(c)) {
                            sCurrentToken.type = TokenType.Char;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            sCurrentToken.type = TokenType.End;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isDot(c)) {
                            sCurrentToken.type = TokenType.Dot;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else {
                            System.out.println("[ else 0 continue lineIndex : " + lineIndex + " columnIndex : " + i + " ] ");
                            continue;
                        }
                    }
                } else if (sCommentOrString == CommentOrString.InSlashComment) {
                    // 当前行 跳过任何字符
                    continue;
                } else if (sCommentOrString == CommentOrString.InBlockComment) {
                    if (isStar(c)) {
                        sCommentOrString = CommentOrString.MayEndBlockComment;
                    }
                    continue;
                } else if (sCommentOrString == CommentOrString.MayEndBlockComment) {
                    if (isBlockCommentEnd(c)) {
                        sCommentOrString = CommentOrString.None;
                    } else {
                        sCommentOrString = CommentOrString.InBlockComment;
                    }
                    continue;
                } else if (sCommentOrString == CommentOrString.InString) {
                    // todo 转义字符
                    String x = "\n\"\' ";
                    if (isStringSymbol(c)) {
                        // todo
                        // 检查前一个字符是否为转义符
                        if (sCurrentToken.literalStrLength() > 0 && isEscape(sCurrentToken.getLastChar())) {
                            // 前一个字符为转义字符，当前 " 字符仍然在 字符串内
                            sCurrentToken.appendLiteralChar(c);
                        } else {
                            // 结束当前字符串
                            sCommentOrString = CommentOrString.None;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                        }
                        continue;
                    } else {
                        // 字符串继续
                        sCurrentToken.appendLiteralChar(c);
                        continue;
                    }
                }
            }
        }

        return tokens;
    }

    private static void collectTokenAndResetCache(ArrayList<SealedToken> tokens, TokenCache tokenCache) {
        SealedToken sealedToken = tokenCache.sealAndReset();
        tokens.add(sealedToken);
    }

    private static boolean isSpace(char c) {
        return c == '\n' || c == '\t' || c == ' ';
    }

    private static boolean isExpressionEnd(char c) {
        return c == ';';
    }

    private static boolean isLegalIdentifierStarter(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || (c == '_') || (c == '$') || (c == '@');
    }

    private static boolean isLegalIdentifierPostfix(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || (c == '_') || (c == '$');
    }

    private static boolean isParentheses(char c) {
        if (c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}') {
            return true;
        }
        return false;
    }

    private static boolean isDot(char c) {
        if (c == '.') {
            return true;
        }
        return false;
    }

    private static boolean isStar(char c) {
        return c == '*';
    }

    private static boolean isCommentStarter(char c) {
        return c == '/';
    }

    // 是否为转义字符
    private static boolean isEscape(char c) {
        return c == '\\';
    }

    private static boolean isBlockCommentEnd(char c) {
        return c == '/';
    }

    // 除了 c == '/'
    private static boolean isOperator(char c) {
        if (c == '+' || c == '-' || c == '*' || c == '%' || c == '=' || c == '!' || c == '>' || c == '<'
                || c == '&' || c == '|' || c == '^' || c == '~' || c == '?') {
            return true;
        }
        return false;
    }

    private static boolean isLegalNumberStarter(char c) {
        if ('0' <= c && c <= '9') {
            return true;
        }
        // todo 需要判断 前一个 token 是什么？还是要做语法分析？
        // 前一个是 identifier 或者 括号，后面的 . 就是调用
        // 前一个是 纯数字、operator 或者是 ; 那么 . 就可以作为 number
//        if (c == '.') {
//            return true;
//        }
        return false;
    }

    private static boolean isLegalNumberPostfix(char c) {
        if ('0' <= c && c <= '9') {
            return true;
        }
        if (c == '.' || c == 'l' || c == 'L' || c == 'f' || c == 'F' || c == '_'
                || c == 'x' || c == 'X' || c == 'B' || c == 'b' || c == 'o' || c == 'O') {
            return true;
        }
        return false;
    }

    private static boolean isCharSymbol(char c) {
        return c == '\'';
    }

    private static boolean isStringSymbol(char c) {
        return c == '"';
    }

    private static ArrayList<String> readLines() {

        ArrayList arrayList = new ArrayList();

        String fileName = "data.txt"; // 注意：不要加 "/"

        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                arrayList.add(line);
            }

        } catch (Exception e) {
            System.err.println("读取文件失败: " + e.getMessage());
        }

        return arrayList;
    }

    private static void print(String message) {
        System.out.println(" " + message);
    }

    private static void fatal(String message) {
        throw new RuntimeException("error " + message);
    }
}

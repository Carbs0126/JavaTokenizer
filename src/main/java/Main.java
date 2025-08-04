import state.CommentOrString;
import state.ImportState;

import java.io.BufferedReader;
import java.io.InputStream;
import /**/ java.io./*DAFAFA    */InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Main {

    private static TokenCache curToken = new TokenCache();
    private static CommentOrString commentOrString = CommentOrString.None;

    public static void main(String[] argv) {
        ArrayList<String> arrayList = readLines();

        ArrayList<SealedToken> tokens = getTokens(arrayList);

        // test
        if (tokens != null) {
            for (SealedToken sealedToken : tokens) {
                System.out.println(sealedToken);
            }
        }
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

        int lineIndex = -1;
        for (String s : arrayList) {
            lineIndex++;
            System.out.println("line -> " + s);
            int strLength = s.length();
            if (commentOrString == CommentOrString.InSlashComment) {
                // 新的一行，跳出行注释
                commentOrString = CommentOrString.None;
            } else if (commentOrString == CommentOrString.MayEndBlockComment) {
                commentOrString = CommentOrString.InBlockComment;
            }
            if (sectionType != SectionType.ContentSection) {
                // 当 section 位于 none 或者 package 或者 import 时
                for (int i = 0; i < strLength; i++) {
                    char c = s.charAt(i);
                    if (sectionType == SectionType.None) {
                        if (commentOrString == CommentOrString.None) {
                            if (isSpace(c)) {
                                continue;
                            } else if (c == 'p') {
                                sectionType = SectionType.PackageSection;
                                packageStr.append(c);
                                continue;
                            } else if (c == 'i' && i < strLength - 1 && s.charAt(i + 1) == 'm') {
                                // 有可能是 interface
//                        fatal("0");
                                // 无 package 声明，直接进入 import
                                sectionType = SectionType.ImportSection;
                                importState = ImportState.Processing;
                                importStrCache.append("i");
                                continue;
//                            } else if (c == '/' && i < strLength - 1 && s.charAt(i + 1) == '/') {
                            } else if (c == '/') {
                                commentOrString = CommentOrString.MayCommentStarter;
                                // 全局变量存储当前是否为 string，是否为 comment
                                // todo wang
                                // sectionType 不变，是一层
                                continue;
                            } else {
                                // todo 这里需要判断是否有注释
                                // 无 package 无 import，直接进入 content
                                sectionType = SectionType.ContentSection;
                                break;
                            }
                        } else if (commentOrString == CommentOrString.MayCommentStarter) {
                            if (c == '/') {
                                commentOrString = CommentOrString.InSlashComment;
                                continue;
                            } else if (c == '*') {
                                commentOrString = CommentOrString.InBlockComment;
                                continue;
                            } else {
                                fatal("000");
                            }
                        } else if (commentOrString == CommentOrString.InSlashComment) {
                            continue;
                        } else if (commentOrString == CommentOrString.InBlockComment) {
                            if (c == '*') {
                                commentOrString = CommentOrString.MayEndBlockComment;
                            }
                            continue;
                        } else if (commentOrString == CommentOrString.MayEndBlockComment) {
                            if (c == '*') {
                                continue;
                            } else if (c == '/') {
                                // 收 comment
                                commentOrString = CommentOrString.None;
                                // todo
//                                curToken.type = TokenType.CommentBlock;
                                continue;
                            } else {
                                commentOrString = CommentOrString.InBlockComment;
                                continue;
                            }
                        }
                    } else if (sectionType == SectionType.PackageSection) {
                        if (commentOrString == CommentOrString.None) {
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
                            } else if (c == '/') {
                                commentOrString = CommentOrString.MayCommentStarter;
                                continue;
                            }
                        } else if (commentOrString == CommentOrString.MayCommentStarter) {
                            if (c == '/') {
                                // package 中应该没有行注释
                                fatal("11");
                            } else if (c == '*') {
                                commentOrString = CommentOrString.InBlockComment;
                                continue;
                            } else {
                                fatal("22");
                            }
                        } else if (commentOrString == CommentOrString.InBlockComment) {
                            if (c == '*') {
                                commentOrString = CommentOrString.MayEndBlockComment;
                            }
                            continue;
                        } else if (commentOrString == CommentOrString.MayEndBlockComment) {
                            if (c == '*') {
                                continue;
                            } else if (c == '/') {
                                // 收 block comment
                                // todo
                                commentOrString = CommentOrString.None;
                                continue;
                            } else {
                                commentOrString = CommentOrString.InBlockComment;
                                continue;
                            }
                        }
                    } else if (sectionType == SectionType.ImportSection) {
                        // 可能有注释
                        if (commentOrString == CommentOrString.None) {
                            if (importState == ImportState.None) {
                                if (isSpace(c)) {
                                    continue;
                                } else if (c == 'i' && i < strLength - 1 && s.charAt(i + 1) == 'm') {
                                    importState = ImportState.Processing;
                                    importStrCache.append("i");
                                    continue;
                                } else if (isExpressionEnd(c)) {
                                    continue;
                                } else if (c == '/') {
                                    commentOrString = CommentOrString.MayCommentStarter;
                                    continue;
                                } else {
                                    // todo 待验证
//                            fatal("1");
                                    sectionType = SectionType.ContentSection;
                                    // 在后面的 for 循环中，此行重新循环
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
                                } else if (c == '/') {
                                    commentOrString = CommentOrString.MayCommentStarter;
                                    continue;
                                }
                            }
                        } else if (commentOrString == CommentOrString.MayCommentStarter) {
                            if (c == '/') {
                                commentOrString = CommentOrString.InSlashComment;
                                continue;
                            } else if (c == '*') {
                                commentOrString = CommentOrString.InBlockComment;
                                continue;
                            } else {
                                fatal("33");
                            }
                        } else if (commentOrString == CommentOrString.InSlashComment) {
                            continue;
                        } else if (commentOrString == CommentOrString.InBlockComment) {
                            if (c == '*') {
                                commentOrString = CommentOrString.MayEndBlockComment;
                            }
                            continue;
                        } else if (commentOrString == CommentOrString.MayEndBlockComment) {
                            if (c == '*') {
                                continue;
                            } else if (c == '/') {
                                // 收 comment
                                commentOrString = CommentOrString.None;
                                // todo
                                continue;
                            } else {
                                commentOrString = CommentOrString.InBlockComment;
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

            if (true) {
                System.out.println("package is --> " + packageStr + "\n");
                for (String im : importStrArr) {
                    System.out.println("import is --> " + im + "\n");
                }
                return tokens;
            }
            // 只有等到 sectionType == SectionType.ContentSection ，才会进入下面的代码中

            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                // todo wang 先判断是否为 commentOrString
                if (commentOrString == CommentOrString.None) {
                    if (curToken.type == TokenType.None) {
                        if (isCommentStarter(c)) {
                            commentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            commentOrString = CommentOrString.InString;
                            continue;
                        } else if (isSpace(c)) {
                            // 如果是空白，继续前进
                            continue;
                        } else if (isLegalIdentifierStarter(c)) {
                            // 如果是合法的起始 identifier
                            curToken.type = TokenType.Identifier;
                            curToken.appendLiteralChar(c);
                            continue;
                        } else if (isOperator(c)) {
                            // todo
                            curToken.type = TokenType.Operator;
                            curToken.appendLiteralChar(c);
                            continue;
                        } else if (isParentheses(c)) {
                            curToken.type = TokenType.Parentheses;
                            curToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, curToken);
                            continue;
                        } else if (isLegalNumberStarter(c)) {
                            curToken.type = TokenType.Number;
                            curToken.appendLiteralChar(c);
                            continue;
                        } else if (isCharSymbol(c)) {
                            curToken.type = TokenType.Char;
                            curToken.appendLiteralChar(c);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            curToken.type = TokenType.End;
                            curToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, curToken);
                            continue;
                        } else if (isDot(c)) {
                            curToken.type = TokenType.Dot;
                            curToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, curToken);
                            continue;
                        } else {
                            System.out.println("[ else 0 continue lineIndex : " + lineIndex + " columnIndex : " + i + " ] ");
                            continue;
                        }
                    } else if (curToken.type == TokenType.Identifier) {
                        if (isCommentStarter(c)) {
                            collectTokenAndResetCache(tokens, curToken);
                            commentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            collectTokenAndResetCache(tokens, curToken);
                            commentOrString = CommentOrString.InString;
                            continue;
                        } else if (isSpace(c)) {
                            // todo wang 如果 R . id . someLayout 呢？
                            // space 不应该
                            // 不对，identifier 确实应该回收，后续再分析一下 identifier 后面 + dot + identifier 的情况
                            collectTokenAndResetCache(tokens, curToken);
                            continue;
                        } else if (isLegalIdentifierPostfix(c)) {
                            curToken.appendLiteralChar(c);
                            continue;
                        } else if (isDot(c)) {
                            collectTokenAndResetCache(tokens, curToken);
                            curToken.type = TokenType.Dot;
                            curToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, curToken);
                            continue;
                        } else if (isOperator(c)) {
                            // 收 identifier
                            collectTokenAndResetCache(tokens, curToken);
                            // 暂时不收 operator，有可能有多个连续operator字符
                            curToken.type = TokenType.Operator;
                            curToken.appendLiteralChar(c);
                            continue;
                        } else if (isParentheses(c)) {
                            // 收 identifier
                            collectTokenAndResetCache(tokens, curToken);
                            // 收 括号
                            curToken.type = TokenType.Operator;
                            curToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, curToken);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            collectTokenAndResetCache(tokens, curToken);
                            curToken.type = TokenType.End;
                            curToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, curToken);
                            continue;
                        } else {
                            System.out.println("[ else 1 continue lineIndex : " + lineIndex + " columnIndex : " + i + " ] ");
                            continue;
                        }
                    } else if (curToken.type == TokenType.Operator) {
                        // curToken.type == TokenType.Operator 这种情况不存在，因为 每次遇到 operator 都会回收并重置
                        if (true) {
                            System.out.println("[ curToken.type == TokenType.Operator continue lineIndex : " + lineIndex + " columnIndex : " + i + " ] ");
                            continue;
                        }
                        if (isSpace(c)) {
                            collectTokenAndResetCache(tokens, curToken);
                            continue;
                        } else if (isOperator(c)) {
                            curToken.appendLiteralChar(c);
                            continue;
                        } else if (isParentheses(c)) {
                            // 收 identifier
                            collectTokenAndResetCache(tokens, curToken);
                            // 收 括号
                            curToken.type = TokenType.Operator;
                            curToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, curToken);
                            continue;
                        } else if (isLegalNumberStarter(c)) {
                            // 收 operator
                            collectTokenAndResetCache(tokens, curToken);
                            curToken.type = TokenType.Number;
                            curToken.appendLiteralChar(c);
                        } else if (isLegalIdentifierStarter(c)) {
                            // 收 operator
                            collectTokenAndResetCache(tokens, curToken);
                            curToken.type = TokenType.Identifier;
                            curToken.appendLiteralChar(c);
                        } else if (isExpressionEnd(c)) {
                            // 收 operator
                            collectTokenAndResetCache(tokens, curToken);
                            curToken.type = TokenType.End;
                            curToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, curToken);
                        }
                    } else if (curToken.type == TokenType.Number) {
                        if (isCommentStarter(c)) {
                            // todo
                            collectTokenAndResetCache(tokens, curToken);
                            commentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            collectTokenAndResetCache(tokens, curToken);
                            commentOrString = CommentOrString.InString;
                            continue;
                        } else if (isSpace(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, curToken);
                            continue;
                        } else if (isOperator(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, curToken);
                            curToken.type = TokenType.Operator;
                            curToken.appendLiteralChar(c);
                            continue;
                        } else if (isLegalNumberPostfix(c)) {
                            // 继续
                            curToken.appendLiteralChar(c);
                            continue;
                        } else if (isParentheses(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, curToken);
                            curToken.type = TokenType.Parentheses;
                            curToken.appendLiteralChar(c);
                            continue;
                        }
                        if (isLegalIdentifierStarter(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, curToken);
                            curToken.type = TokenType.Identifier;
                            curToken.appendLiteralChar(c);
                        } else if (isExpressionEnd(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, curToken);
                            curToken.type = TokenType.End;
                            curToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, curToken);
                        }
                    } else if (isStringSymbol(c)) { // string 开头
                        // todo
                    }
                } else if (commentOrString == CommentOrString.MayCommentStarter) {
                    if (c == '/') {
                        commentOrString = CommentOrString.InSlashComment;
                        continue;
                    } else if (c == '*') {
                        commentOrString = CommentOrString.InBlockComment;
                        continue;
                    } else {
                        // 前一个 收为 除号
                        curToken.type = TokenType.Operator;
                        curToken.appendLiteralChar('/');
                        collectTokenAndResetCache(tokens, curToken);
                        // 当前 判断

                    }
                } else if (commentOrString == CommentOrString.InSlashComment) {

                } else if (commentOrString == CommentOrString.InBlockComment) {

                } else if (commentOrString == CommentOrString.MayEndBlockComment) {

                }


                // 一行结束，token收尾
                if (curToken.type == TokenType.Identifier) {
                    SealedToken sealedToken = curToken.sealAndReset();
                    tokens.add(sealedToken);
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

    private static boolean isCommentStarter(char c) {
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
        if (c == '.') {
            return true;
        }
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

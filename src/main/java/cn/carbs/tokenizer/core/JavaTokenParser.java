package cn.carbs.tokenizer.core;

import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.type.SectionType;
import cn.carbs.tokenizer.entity.TokenCache;
import cn.carbs.tokenizer.type.TokenType;
import cn.carbs.tokenizer.state.CommentOrString;
import cn.carbs.tokenizer.state.ImportState;

import java.util.ArrayList;

public class JavaTokenParser {

    private TokenCache sCurrentToken = new TokenCache();
    private CommentOrString sCommentOrString = CommentOrString.None;

    // 获取一个 file 的 tokens
    public ArrayList<SealedToken> getTokens(ArrayList<String> arrayList) {
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
            // print("line ->|" + (lineIndex + 1) + "|" + s);
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
                                printError("package or import 0", " current char : " + c);
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
                                printError("package or import 1", " current char : " + c);
                            } else if (c == '*') {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            } else {
                                printError("package or import 2", " current char : " + c);
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
                                    importStrCache.setLength(0);
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
                                printError("package or import 3", " current char : " + c);
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

            if (penetratePackageAndImportSectionState == 1) {
                penetratePackageAndImportSectionState = 2;
                // 在这里收集 package 和 import
                if (packageStr.length() > 0) {
                    tokens.add(SealedToken.genPackageToken(packageStr.substring(7))); // "package".length() == 7
                    tokens.add(SealedToken.genNewLineToken());
                }
                for (String importStr : importStrArr) {
                    tokens.add(SealedToken.genImportToken(importStr.substring(6))); // "import".length() == 6
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
                            sCurrentToken.extraInt = 0;
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
                        } else if (isDot(c)) {
                            sCurrentToken.type = TokenType.DotConfirmLater;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isPureNumber(c)) {
                            sCurrentToken.type = TokenType.Number;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isCharSymbol(c)) {
                            sCurrentToken.type = TokenType.Char;
                            sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_IDLE;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isComma(c)) {
                            sCurrentToken.type = TokenType.Comma;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isColon(c)) {
                            sCurrentToken.type = TokenType.Colon;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            sCurrentToken.type = TokenType.End;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else {
                            printError("CommentOrString.None & TokenType.None",
                                    "line : " + (lineIndex + 1) + ", columnIndex : " + i + ", current char : " + c);
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
                            sCurrentToken.extraInt = 0;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isSpace(c)) {
                            // identifier 确实应该回收，后续再分析一下 identifier 后面 + dot + identifier 的情况
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isComma(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.Comma;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isColon(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.Colon;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isLegalIdentifierPostfix(c)) {
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isDot(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.DotForIdentifier;
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
                            printError("CommentOrString.None & TokenType.Identifier",
                                    "line : " + (lineIndex + 1) + ", columnIndex : " + i + ", current char : " + c);
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
                            sCurrentToken.extraInt = 0;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isComma(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.Comma;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isColon(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.Colon;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
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
                            printError("CommentOrString.None & TokenType.Number",
                                    "line : " + (lineIndex + 1) + ", columnIndex : " + i + ", current char : " + c);
                            continue;
                        }
                    } else if (sCurrentToken.type == TokenType.Char) {
                        // todo todo 仿照
                        if (sCurrentToken.extraInt == TokenCache.IN_STRING_MODE_ESCAPE_IDLE) {
                            // 前一个字符不是 escape 转义字符
                            if (isCharSymbol(c)) {
                                // 当前字符为 ' 则结束当前 char
                                sCurrentToken.appendLiteralChar(c);
                                collectTokenAndResetCache(tokens, sCurrentToken);
                                continue;
                            } else {
                                // 当前字符不为 "
                                if (isEscape(c)) {
                                    // 当前字符为 /
                                    sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_READY;
                                } else {
                                    sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_IDLE;
                                }
                                // 字符串继续
                                sCurrentToken.appendLiteralChar(c);
                                continue;
                            }
                        } else {
                            // 前一个字符是 escape 转义字符：
                            // 1. 把当前字符加入到string中
                            sCurrentToken.appendLiteralChar(c);
                            // 2. 取消转义模式
                            sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_IDLE;
                            // 3. 并继续string模式
                            continue;
                        }
                    } else if (sCurrentToken.type == TokenType.DotConfirmLater) {
                        // float x = . 9f; // 格式错误
                        // float x = .9f;  // 格式正确
                        if (isPureNumber(c)) {
                            // 前面的 dot 按照 DotForNumber 处理
                            sCurrentToken.type = TokenType.Number;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isComma(c)) {
                            // 上一个是 number，并回收
                            sCurrentToken.type = TokenType.Number;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);

                            // 当前是 ',' 回收
                            sCurrentToken.type = TokenType.Comma;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isColon(c)) {
                            // 上一个是 number，并回收
                            sCurrentToken.type = TokenType.Number;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);

                            // 当前是 ':' 回收
                            sCurrentToken.type = TokenType.Colon;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        }
                        // 前面的 dot 按照 DotForIdentifier 处理
                        sCurrentToken.type = TokenType.DotForIdentifier;
                        collectTokenAndResetCache(tokens, sCurrentToken);
                        // todo 处理当前字符，前一个字符是 .
                        if (isCommentStarter(c)) {
                            sCommentOrString = CommentOrString.MayCommentStarter;
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
                        } else if (isDot(c)) {
                            // todo wang
                            sCurrentToken.type = TokenType.DotConfirmLater;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            sCurrentToken.type = TokenType.End;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else {
                            printError("CommentOrString.None & TokenType.DotConfirmLater",
                                    "line : " + (lineIndex + 1) + ", columnIndex : " + i + ", current char : " + c);
                            continue;
                        }
                    } else {
                        // curToken.type == TokenType.Operator 这种情况不存在，因为 每次遇到 operator 都会回收并重置
                        printError("CommentOrString.None & TokenType else",
                                "line : " + (lineIndex + 1) + ", columnIndex : " + i + ", current char : " + c);
                        continue;
                    }
                } else if (sCommentOrString == CommentOrString.MayCommentStarter) {
                    // todo
                    if (isCommentStarter(c)) {
                        sCommentOrString = CommentOrString.InSlashComment;
                        continue;
                    } else if (isStar(c)) {
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
                            sCurrentToken.extraInt = 0;
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
                        } else if (isPureNumber(c) || isDot(c)) {
                            sCurrentToken.type = TokenType.Number;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isCharSymbol(c)) {
                            sCurrentToken.type = TokenType.Char;
                            sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_IDLE;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            sCurrentToken.type = TokenType.End;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else {
                            printError("CommentOrString.MayCommentStarter & current c else",
                                    "line : " + (lineIndex + 1) + ", columnIndex : " + i + ", current char : " + c);
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
                    if (sCurrentToken.extraInt == TokenCache.IN_STRING_MODE_ESCAPE_IDLE) {
                        // 前一个字符不是 escape 转义字符
                        if (isStringSymbol(c)) {
                            // 当前字符为 " 则结束当前字符串
                            sCommentOrString = CommentOrString.None;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
//                            // 检查前一个字符是否为转义符
//                            if (sCurrentToken.literalStrLength() > 0 && isEscape(sCurrentToken.getLastChar())) {
//                                // 前一个字符为转义字符，当前 " 字符仍然在 字符串内
//                                sCurrentToken.appendLiteralChar(c);
//                            } else {
//                                // 结束当前字符串
//                                sCommentOrString = CommentOrString.None;
//                                sCurrentToken.appendLiteralChar(c);
//                                collectTokenAndResetCache(tokens, sCurrentToken);
//                            }
//                            continue;
                        } else {
                            // 当前字符不为 "
                            if (isEscape(c)) {
                                // 当前字符为 /
                                sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_READY;
                            } else {
                                sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_IDLE;
                            }
                            // 字符串继续
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        }
                    } else {
                        // 前一个字符是 escape 转义字符：
                        // 1. 把当前字符加入到string中
                        sCurrentToken.appendLiteralChar(c);
                        // 2. 取消转义模式
                        sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_IDLE;
                        // 3. 并继续string模式
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

    private static boolean isComma(char c) {
        return c == ',';
    }

    private static boolean isColon(char c) {
        return c == ':';
    }

    private static boolean isPureNumber(char c) {
        if ('0' <= c && c <= '9') {
            return true;
        }
        return false;
    }

    private static boolean isLegalNumberPostfix(char c) {
        if ('0' <= c && c <= '9') {
            return true;
        }
        if (('a' <= c && c <= 'f')
                || ('A' <= c && c <= 'F')
                || c == '.' || c == '_'
                || c == 'l' || c == 'L'
                || c == 'x' || c == 'X'
                || c == 'o' || c == 'O') {
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

    private static void printError(String tag, String message) {
        System.err.println("PARSE ERROR --> [" + tag + "] " + message);
    }

    private static void print(String message) {
        System.out.println(" " + message);
    }
}

package cn.carbs.tokenizer.core;

import cn.carbs.tokenizer.entity.Brace;
import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.entity.TokenCache;
import cn.carbs.tokenizer.state.CommentOrString;
import cn.carbs.tokenizer.state.ImportState;
import cn.carbs.tokenizer.type.CodeSectionType;
import cn.carbs.tokenizer.type.TokenType;
import cn.carbs.tokenizer.util.Log;

import java.util.ArrayList;

public class KotlinTokenParserBackup3 implements ITokenParser {

    private TokenCache sCurrentToken = new TokenCache();
    private CommentOrString sCommentOrString = CommentOrString.None;
    private String absFileName;

    public KotlinTokenParserBackup3(String absFileName) {
        this.absFileName = absFileName;
    }

    // 获取一个 file 的 tokens
    public ArrayList<SealedToken> getTokens(ArrayList<String> arrayList) {
        if (arrayList == null || arrayList.size() == 0) {
            return new ArrayList<>();
        }
        ArrayList<SealedToken> tokens = new ArrayList<>(arrayList.size() * 10);
        // 开始了
        CodeSectionType codeSectionType = CodeSectionType.None;
        StringBuilder packageStr = new StringBuilder();

        ArrayList<String> importStrArr = new ArrayList<>();
        StringBuilder importStrCache = new StringBuilder();

        TokenType prePackageTokenType = TokenType.None;
        TokenType preValidPackageTokenType = TokenType.None;

        ImportState importState = ImportState.None;
        TokenType preImportTokenType = TokenType.None;
        TokenType preValidImportTokenType = TokenType.None;

        int penetratePackageAndImportSectionState = 0;

        int lineIndex = -1;
        for (String s : arrayList) {
            lineIndex++;
            int strLength = s.length();
            if (sCommentOrString == CommentOrString.InSlashComment) {
                // 新的一行，跳出行注释
                sCommentOrString = CommentOrString.None;
            } else if (sCommentOrString == CommentOrString.MayEndBlockComment) {
                sCommentOrString = CommentOrString.InBlockComment;
            }
            if (codeSectionType != CodeSectionType.ContentSection) {
                // 当 section 位于 none 或者 package 或者 import 时
                // 新的一行，把换行符作为space
                prePackageTokenType = TokenType.Space;
                preImportTokenType = TokenType.Space;
                for (int i = 0; i < strLength; i++) {
                    char c = s.charAt(i);
                    if (codeSectionType == CodeSectionType.None) {
                        if (sCommentOrString == CommentOrString.None) {
                            if (isSpace(c)) {
                                continue;
                            } else if (c == 'p' && i < strLength - 1 && s.charAt(i + 1) == 'a') {
                                packageStr.append(c);
                                codeSectionType = CodeSectionType.PackageSection;
                                prePackageTokenType = TokenType.Identifier;
                                preValidPackageTokenType = TokenType.Identifier;
                                continue;
                            } else if (c == 'i' && i < strLength - 1 && s.charAt(i + 1) == 'm') {
                                // 有可能是 interface
                                // 无 package 声明，直接进入 import
                                importStrCache.append(c);
                                codeSectionType = CodeSectionType.ImportSection;
                                importState = ImportState.Processing;
                                preImportTokenType = TokenType.Identifier;
                                preValidImportTokenType = TokenType.Identifier;
                                continue;
                            } else if (isCommentStarter(c)) {
                                sCommentOrString = CommentOrString.MayCommentStarter;
                                continue;
                            } else {
                                // 无 package 无 import，直接进入 content
                                codeSectionType = CodeSectionType.ContentSection;
                                penetratePackageAndImportSectionState = 1;
                                break;
                            }
                        } else if (sCommentOrString == CommentOrString.MayCommentStarter) {
                            if (isCommentStarter(c)) {
                                sCommentOrString = CommentOrString.InSlashComment;
                                continue;
                            } else if (isStar(c)) {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            } else {
                                Log.e("package or import 0", "current line : " + (lineIndex + 1)
                                        + " current char : ->" + c + "<-, this char's int value is : " + ((int) c), this.absFileName);
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
                                continue;
                            } else {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            }
                        }
                    } else if (codeSectionType == CodeSectionType.PackageSection) {
                        // kotlin 中，package xxx 后面可以不以分号结尾
                        if (sCommentOrString == CommentOrString.None) {
                            if (isSpace(c)) {
                                prePackageTokenType = TokenType.Space;
                                continue;
                            } else if (isDot(c)) {
                                packageStr.append(c);
                                prePackageTokenType = TokenType.DotForIdentifier;
                                preValidPackageTokenType = TokenType.DotForIdentifier;
                                continue;
                            } else if (isExpressionEnd(c)) {
                                packageStr.append(c);
                                codeSectionType = CodeSectionType.ImportSection;
                                prePackageTokenType = TokenType.None;
                                preValidPackageTokenType = TokenType.None;
                                continue;
                            } else if (isLegalIdentifierPostfix(c)) {
                                if (prePackageTokenType == TokenType.Space && preValidPackageTokenType == TokenType.Identifier) {
                                    // 直接进入import
                                    if ("package".equals(packageStr.toString())) {
                                        // 继续解析
                                        packageStr.append(c);
                                        prePackageTokenType = TokenType.Identifier;
                                        preValidPackageTokenType = TokenType.Identifier;
                                    } else {
                                        if (c == 'i' && i < strLength - 1 && s.charAt(i + 1) == 'm') {
                                            importStrCache.append(c);
                                            codeSectionType = CodeSectionType.ImportSection;
                                            importState = ImportState.Processing;
                                            preImportTokenType = TokenType.Identifier;
                                            preValidImportTokenType = TokenType.Identifier;
                                        } else {
                                            codeSectionType = CodeSectionType.ContentSection;
                                            penetratePackageAndImportSectionState = 1;
                                        }
                                    }
                                    continue;
                                } else {
                                    packageStr.append(c);
                                    prePackageTokenType = TokenType.Identifier;
                                    preValidPackageTokenType = TokenType.Identifier;
                                    continue;
                                }
                            } else if (isCommentStarter(c)) {
                                sCommentOrString = CommentOrString.MayCommentStarter;
                                continue;
                            }
                        } else if (sCommentOrString == CommentOrString.MayCommentStarter) {
                            if (isCommentStarter(c)) {
                                // package 中应该没有行注释
                                codeSectionType = CodeSectionType.ImportSection;
                                prePackageTokenType = TokenType.None;
                                preValidPackageTokenType = TokenType.None;
                                sCommentOrString = CommentOrString.InSlashComment;
                                // package 结束了
                            } else if (c == '*') {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            } else {
                                Log.e("package or import 2", "current line : " + (lineIndex + 1)
                                        + " current char : ->" + c + "<-, this char's int value is : " + ((int) c), this.absFileName);
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
                                sCommentOrString = CommentOrString.None;
                                continue;
                            } else {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            }
                        }
                    } else if (codeSectionType == CodeSectionType.ImportSection) {
                        // 可能有注释
                        if (sCommentOrString == CommentOrString.None) {
                            // TODO 这里不完善，有可能会有问题，比如进入
                            if (isSpace(c)) {
                                preImportTokenType = TokenType.Space;
                                continue;
                            } else if (isDot(c)) {
                                importStrCache.append(c);
                                preImportTokenType = TokenType.DotForIdentifier;
                                preValidImportTokenType = TokenType.DotForIdentifier;
                                continue;
                            } else if (isExpressionEnd(c)) {
                                importStrCache.append(c);
                                importStrArr.add(importStrCache.toString());
                                importStrCache.setLength(0);
                                preImportTokenType = TokenType.None;
                                preValidImportTokenType = TokenType.None;
                                continue;
                            } else if (isLegalIdentifierPostfix(c) || isStar(c)) {
                                // todo
//                                if (preImportTokenType == TokenType.Space && preValidImportTokenType == TokenType.Identifier) {
                                if (preImportTokenType == TokenType.Space) {
                                    if (preValidImportTokenType == TokenType.Identifier || preValidImportTokenType == TokenType.DotForIdentifier) {
                                        if ("import".equals(importStrCache.toString())) {
                                            // 继续解析
                                            preImportTokenType = TokenType.Identifier;
                                            preValidImportTokenType = TokenType.Identifier;
                                            importStrCache.append(c);
                                        } else {
                                            if (c == 'i' && i < strLength - 1 && s.charAt(i + 1) == 'm') {
                                                // 结束当前的import，进入下一个 import
                                                // 前一个 import 回收
                                                importStrArr.add(importStrCache.toString());
                                                importStrCache.setLength(0);

                                                importStrCache.append(c);
                                                importState = ImportState.Processing;
                                                preImportTokenType = TokenType.Identifier;
                                                preValidImportTokenType = TokenType.Identifier;
                                            } else {
                                                // todo 这里为什么注释掉？
                                                importStrArr.add(importStrCache.toString());
                                                importStrCache.setLength(0);
                                                codeSectionType = CodeSectionType.ContentSection;
                                                preImportTokenType = TokenType.None;
                                                preValidImportTokenType = TokenType.None;
                                                penetratePackageAndImportSectionState = 1;
                                            }
                                        }
                                    } else if (preValidImportTokenType == TokenType.None) {
                                        // 没有 import，直接就是注释
                                        if ("import".equals(importStrCache.toString())) {
                                            // 继续解析
                                            preImportTokenType = TokenType.Identifier;
                                            preValidImportTokenType = TokenType.Identifier;
                                            importStrCache.append(c);
                                        } else {
                                            if (c == 'i' && i < strLength - 1 && s.charAt(i + 1) == 'm') {
                                                // 结束当前的import，进入下一个 import
                                                // 前一个 import 回收
                                                importStrArr.add(importStrCache.toString());
                                                importStrCache.setLength(0);

                                                importStrCache.append(c);
                                                importState = ImportState.Processing;
                                                preImportTokenType = TokenType.Identifier;
                                                preValidImportTokenType = TokenType.Identifier;
                                            } else {
//                                            importStrArr.add(importStrCache.toString());
                                                importStrCache.setLength(0);
                                                codeSectionType = CodeSectionType.ContentSection;
                                                preImportTokenType = TokenType.None;
                                                preValidImportTokenType = TokenType.None;
                                                penetratePackageAndImportSectionState = 1;
                                            }
                                        }
                                    }
                                    continue;
                                } else {
                                    preImportTokenType = TokenType.Identifier;
                                    preValidImportTokenType = TokenType.Identifier;
                                    importStrCache.append(c);
                                    continue;
                                }
                            } else if (isCommentStarter(c)) {
                                sCommentOrString = CommentOrString.MayCommentStarter;
                                continue;
                            } else {
                                Log.e("package or import 3", "current line : " + (lineIndex + 1)
                                        + " current char : ->" + c + "<-, this char's int value is : " + ((int) c), this.absFileName);
                            }
                        } else if (sCommentOrString == CommentOrString.MayCommentStarter) {
                            if (isCommentStarter(c)) {
                                sCommentOrString = CommentOrString.InSlashComment;
                                continue;
                            } else if (c == '*') {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            } else {
                                Log.e("package or import 4", "current line : " + (lineIndex + 1)
                                        + " current char : ->" + c + "<-, this char's int value is : " + ((int) c), this.absFileName);
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
                                continue;
                            } else {
                                sCommentOrString = CommentOrString.InBlockComment;
                                continue;
                            }
                        }
                    }
                }
            }

            if (codeSectionType == CodeSectionType.None
                    || codeSectionType == CodeSectionType.PackageSection
                    || codeSectionType == CodeSectionType.ImportSection) {
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
            // 上一行的identifier 还没收，先加了换行符了
            tokens.add(SealedToken.genNewLineToken());

            // 只有等到 sectionType == SectionType.ContentSection ，才会进入下面的代码中

            // 上一行以 identifier 或者 number 或者
            if (sCurrentToken.type == TokenType.Identifier || sCurrentToken.type == TokenType.Number) {
                collectTokenAndResetCache(tokens, sCurrentToken);
            }

            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                // 先判断是否为 commentOrString
                if (sCommentOrString == CommentOrString.None) {
                    if (sCurrentToken.type == TokenType.None) {
                        if (isCommentStarter(c)) {
                            sCommentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            sCommentOrString = CommentOrString.MayStringStarter0;
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
                            collectParentheses(c, tokens, lineIndex, i);
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
                            Log.e("CommentOrString.None & TokenType.None",
                                    "line : " + (lineIndex + 1) + ", columnIndex : " + i
                                            + ", current char : ->" + c + "<-, this char's int value is : " + ((int) c)
                                            + ", currentToken literal str is : ->" + sCurrentToken.literalStr + "<-", this.absFileName);
                            continue;
                        }
                    } else if (sCurrentToken.type == TokenType.Identifier) {
                        if (isCommentStarter(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCommentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCommentOrString = CommentOrString.MayStringStarter0;
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
                            collectParentheses(c, tokens, lineIndex, i);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.End;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else if (isCharSymbol(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.Char;
                            sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_IDLE;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else {
                            Log.e("CommentOrString.None & TokenType.Identifier",
                                    "line : " + (lineIndex + 1) + ", columnIndex : " + i
                                            + ", current char : ->" + c + "<-, this char's int value is : " + ((int) c)
                                            + ", currentToken literal str is : ->" + sCurrentToken.literalStr + "<-", this.absFileName);
                            continue;
                        }
                    } else if (sCurrentToken.type == TokenType.Number) {
                        if (isCommentStarter(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCommentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCommentOrString = CommentOrString.MayStringStarter0;
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
                        } else if (isDot(c)) {
                            // todo wang
//                            sCurrentToken.extraInt = TokenCache.IN_NUMBER_MODE;
                            sCurrentToken.type = TokenType.DotConfirmLater;
                            sCurrentToken.appendLiteralChar(c);
                        } else if (isLegalNumberPostfix(c)) {
                            // 继续
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isParentheses(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            collectParentheses(c, tokens, lineIndex, i);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            // 收 number
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            sCurrentToken.type = TokenType.End;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                        } else {
                            Log.e("CommentOrString.None & TokenType.Number",
                                    "line : " + (lineIndex + 1) + ", columnIndex : " + i
                                            + ", current char : ->" + c + "<-, this char's int value is : " + ((int) c)
                                            + ", currentToken literal str is : ->" + sCurrentToken.literalStr + "<-", this.absFileName);
                            continue;
                        }
                    } else if (sCurrentToken.type == TokenType.Char) {
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
                        } else if (isCharSymbol(c)) {
//                            if (sCurrentToken.extraInt == TokenCache.IN_RANGE_MODE) {
                            if (false) {

                                sCurrentToken.type = TokenType.DotForRange;
                                collectTokenAndResetCache(tokens, sCurrentToken);

                                sCurrentToken.type = TokenType.Char;
                                sCurrentToken.appendLiteralChar(c);
                                continue;
                            } else {
                                // 可能出错
                                Log.e("CommentOrString.None & TokenType.DotConfirmLater",
                                        "isCharSymbol(), line : " + (lineIndex + 1) + ", columnIndex : " + i
                                                + ", current char : ->" + c + "<-, this char's int value is : " + ((int) c)
                                                + ", currentToken literal str is : ->" + sCurrentToken.literalStr + "<-", this.absFileName);

                            }
                            continue;
                        }
                        // 前面的 dot 按照 DotForIdentifier 处理
                        sCurrentToken.type = TokenType.DotForIdentifier;
                        collectTokenAndResetCache(tokens, sCurrentToken);
                        // 处理当前字符，前一个字符是 .
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
                            collectParentheses(c, tokens, lineIndex, i);
                            continue;
                        } else if (isDot(c)) {
                            // todo wang
//                            sCurrentToken.extraInt = TokenCache.IN_RANGE_MODE;
                            sCurrentToken.type = TokenType.DotConfirmLater;
                            sCurrentToken.appendLiteralChar(c);
                            continue;
                        } else if (isExpressionEnd(c)) {
                            sCurrentToken.type = TokenType.End;
                            sCurrentToken.appendLiteralChar(c);
                            collectTokenAndResetCache(tokens, sCurrentToken);
                            continue;
                        } else {
                            Log.e("CommentOrString.None & TokenType.DotConfirmLater",
                                    "else, line : " + (lineIndex + 1) + ", columnIndex : " + i
                                            + ", current char : ->" + c + "<-, this char's int value is : " + ((int) c)
                                            + ", currentToken literal str is : ->" + sCurrentToken.literalStr + "<-", this.absFileName);
                            continue;
                        }
                    } else {
                        // curToken.type == TokenType.Operator 这种情况不存在，因为 每次遇到 operator 都会回收并重置
                        Log.e("CommentOrString.None & TokenType else",
                                "line : " + (lineIndex + 1) + ", columnIndex : " + i
                                        + ", current char : ->" + c + "<-, this char's int value is : " + ((int) c)
                                        + ", currentToken literal str is : ->" + sCurrentToken.literalStr + "<-", this.absFileName);
                        continue;
                    }
                } else if (sCommentOrString == CommentOrString.MayCommentStarter) {
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
                        if (isCommentStarter(c)) {
                            sCommentOrString = CommentOrString.MayCommentStarter;
                            continue;
                        } else if (isStringSymbol(c)) {
                            sCommentOrString = CommentOrString.MayStringStarter0;
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
                            collectParentheses(c, tokens, lineIndex, i);
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
                            Log.e("CommentOrString.MayCommentStarter & current c else",
                                    "line : " + (lineIndex + 1) + ", columnIndex : " + i
                                            + ", current char : ->" + c + "<-, this char's int value is : " + ((int) c)
                                            + ", currentToken literal str is : ->" + sCurrentToken.literalStr + "<-", this.absFileName);
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
                } else if (sCommentOrString == CommentOrString.MayStringStarter0) {
                    if (isStringSymbol(c)) {
                        sCommentOrString = CommentOrString.MayStringStarter1;
                    } else {
                        sCommentOrString = CommentOrString.InString;
                        sCurrentToken.type = TokenType.String;
                        if (isEscape(c)) {
                            // 当前字符为 /
                            sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_READY;
                        } else {
                            sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_IDLE;
                        }
                        // todo wang
                        // string中的第一个字符，之前不可能转义
                        if (isDollar(c)) {
                            // 可能是 el 表达式开始，当判断到el表达式时，再进行截取
                            sCurrentToken.elExpStarterState = TokenCache.EL_STARTER_STATE_MAY_START;
                        } else {
                            sCurrentToken.elExpStarterState = TokenCache.EL_STARTER_STATE_NONE;
                        }
                    }
                    // 字符串继续
                    sCurrentToken.appendLiteralChar(c);
                    continue;
                } else if (sCommentOrString == CommentOrString.MayStringStarter1) {
                    if (isStringSymbol(c)) {
                        // 连着三个 """
                        sCommentOrString = CommentOrString.InBlockString;
                        sCurrentToken.type = TokenType.StringBlock;
                        // 字符串继续
                        sCurrentToken.appendLiteralChar(c);
                    } else {
                        // 上一个 空 string 结束了 ""
                        sCommentOrString = CommentOrString.None;
                        // 上一个是普通 string
                        sCurrentToken.type = TokenType.String;
                        collectTokenAndResetCache(tokens, sCurrentToken);
                        // 把当前的字符收进去
                        sCurrentToken.appendLiteralChar(c);
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
                        } else {
                            // 当前字符不为 "
                            if (isEscape(c)) {
                                // 当前字符为 /
                                sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_READY;
                            } else {
                                sCurrentToken.extraInt = TokenCache.IN_STRING_MODE_ESCAPE_IDLE;
                            }
                            if (sCurrentToken.elExpStarterState == TokenCache.EL_STARTER_STATE_MAY_START) {
                                if (isLeftBrace(c)) {
                                    // ${ el表达式开始了
                                    sCurrentToken.elExpStarterState = TokenCache.EL_STARTER_STATE_NONE;
                                    // 收起之前的 token 作为 string
                                    // todo wang
                                    String strBeforeDollar = sCurrentToken.literalStr.substring(0, sCurrentToken.literalStr.length() - 1);
                                    sCurrentToken.literalStr.setLength(0);
                                    sCurrentToken.literalStr.append(strBeforeDollar);
                                    // 此时没有右侧 "
                                    collectTokenAndResetCache(tokens, sCurrentToken);
                                    sCurrentToken.type = TokenType.ELExprStart;
                                    sCurrentToken.elLayer++;
                                    sCurrentToken.appendLiteralString("${");
                                    collectTokenAndResetCache(tokens, sCurrentToken);
                                    sCurrentToken.pushELStarterBrace(Brace.EL_CAPSULE_STRING_TYPE_STRING);
                                    sCommentOrString = CommentOrString.None;
                                } else {
                                    sCurrentToken.elExpStarterState = TokenCache.EL_STARTER_STATE_NONE;
                                    sCurrentToken.appendLiteralChar(c);
                                }
                                continue;
                            } else {
                                if (isDollar(c)) {
                                    sCurrentToken.elExpStarterState = TokenCache.EL_STARTER_STATE_MAY_START;
                                    // 当做字符串继续
                                    sCurrentToken.appendLiteralChar(c);
                                    continue;
                                } else {
                                    sCurrentToken.elExpStarterState = TokenCache.EL_STARTER_STATE_NONE;
                                    sCurrentToken.appendLiteralChar(c);
                                    continue;
                                }
                            }
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
                } else if (sCommentOrString == CommentOrString.InBlockString) {
                    // 位于 """ """ 之间，kotlin 中，""" """ 之间没有转义字符，因此不需要关注转义字符
                    if (isStringSymbol(c)) {
                        // 如果当前字符为 "
                        sCommentOrString = CommentOrString.MayStringEnd0;
                        sCurrentToken.appendLiteralChar(c);
                        continue;
                    } else {
                        // 当前字符不为 "
                        // 字符串继续
//                        sCurrentToken.appendLiteralChar(c);
//                        continue;
                        if (sCurrentToken.elExpStarterState == TokenCache.EL_STARTER_STATE_MAY_START) {
                            if (isLeftBrace(c)) {
                                // ${ el表达式开始了
                                sCurrentToken.elExpStarterState = TokenCache.EL_STARTER_STATE_NONE;
                                // 收起之前的 token 作为 string
                                // todo wang
                                String strBeforeDollar = sCurrentToken.literalStr.substring(0, sCurrentToken.literalStr.length() - 1);
                                sCurrentToken.literalStr.setLength(0);
                                sCurrentToken.literalStr.append(strBeforeDollar);
                                // 此时没有右侧 "
                                collectTokenAndResetCache(tokens, sCurrentToken);
                                sCurrentToken.type = TokenType.ELExprStart;
                                sCurrentToken.elLayer++;
                                sCurrentToken.appendLiteralString("${");
                                collectTokenAndResetCache(tokens, sCurrentToken);
                                sCurrentToken.pushELStarterBrace(Brace.EL_CAPSULE_STRING_TYPE_STRING_BLOCK);
                                sCommentOrString = CommentOrString.None;
                            } else {
                                sCurrentToken.elExpStarterState = TokenCache.EL_STARTER_STATE_NONE;
                                sCurrentToken.appendLiteralChar(c);
                            }
                            continue;
                        } else {
                            if (isDollar(c)) {
                                sCurrentToken.elExpStarterState = TokenCache.EL_STARTER_STATE_MAY_START;
                                // 当做字符串继续
                                sCurrentToken.appendLiteralChar(c);
                                continue;
                            } else {
                                sCurrentToken.elExpStarterState = TokenCache.EL_STARTER_STATE_NONE;
                                sCurrentToken.appendLiteralChar(c);
                                continue;
                            }
                        }
                    }
                } else if (sCommentOrString == CommentOrString.MayStringEnd0) {
                    if (isStringSymbol(c)) {
                        sCommentOrString = CommentOrString.MayStringEnd1;
                        sCurrentToken.appendLiteralChar(c);
                        continue;
                    } else {
                        // 回退到 InBlockString
                        sCommentOrString = CommentOrString.InBlockString;
                        sCurrentToken.appendLiteralChar(c);
                        continue;
                    }
                } else if (sCommentOrString == CommentOrString.MayStringEnd1) {
                    if (isStringSymbol(c)) {
                        sCommentOrString = CommentOrString.None;
                        sCurrentToken.type = TokenType.StringBlock;
                        sCurrentToken.appendLiteralChar(c);
                        collectTokenAndResetCache(tokens, sCurrentToken);
                        continue;
                    } else {
                        // 回退到 InBlockString
                        sCommentOrString = CommentOrString.InBlockString;
                        sCurrentToken.appendLiteralChar(c);
                        continue;
                    }
                }
            }
            // 换行时，identifier 和 number 类型应该收吗
            if (sCurrentToken.type == TokenType.Identifier || sCurrentToken.type == TokenType.Number) {
                collectTokenAndResetCache(tokens, sCurrentToken);
            }
        }

        if (sCurrentToken.type != TokenType.None) {
            collectTokenAndResetCache(tokens, sCurrentToken);
        }

        if (codeSectionType != CodeSectionType.ContentSection && tokens.size() == 0) {
            if (packageStr.length() > 0) {
                tokens.add(SealedToken.genPackageToken(packageStr.substring(7))); // "package".length() == 7
                tokens.add(SealedToken.genNewLineToken());
                packageStr.setLength(0);
            }
            if (importStrCache.length() > 0) {
                importStrArr.add(importStrCache.toString());
                importStrCache.setLength(0);
            }
            for (String importStr : importStrArr) {
                tokens.add(SealedToken.genImportToken(importStr.substring(6))); // "import".length() == 6
                tokens.add(SealedToken.genNewLineToken());
            }
        }

        return tokens;
    }

    private void collectParentheses(char c, ArrayList<SealedToken> tokens, int lineIndex, int columnIndex) {
        if (sCurrentToken.elLayer == 0) {
            // 如果当前不在 el 表达式中
            sCurrentToken.type = TokenType.Parentheses;
            sCurrentToken.appendLiteralChar(c);
            collectTokenAndResetCache(tokens, sCurrentToken);
            return;
        } else if (sCurrentToken.elLayer > 0) {
            // 如果是在 el 表达式中
            if (isLeftBrace(c)) {
                // leftBrace 必然不会抵消，el 表达式还会继续
                sCurrentToken.pushBrace(false, Brace.DIRECTION_LEFT);
                sCurrentToken.type = TokenType.Parentheses;
                sCurrentToken.appendLiteralChar(c);
                collectTokenAndResetCache(tokens, sCurrentToken);
                return;
            } else if (isRightBrace(c)) {
                sCurrentToken.pushBrace(false, Brace.DIRECTION_RIGHT);
                // text
                Brace.CheckPairResult operateResult = sCurrentToken.operateLastTwoBracesResult();
                if (operateResult.result == Brace.PAIR_RESULT_YES) {
                    // 收集 token，sCurrentToken.elBraceArr 已经在 operateLastTwoBracesResult() 内部做改变了
                    sCurrentToken.type = TokenType.Parentheses;
                    sCurrentToken.appendLiteralChar(c);
                    collectTokenAndResetCache(tokens, sCurrentToken);
                    return;
                } else if (operateResult.result == Brace.PAIR_RESULT_NO) {
                    // 收集 token
                    sCurrentToken.type = TokenType.Parentheses;
                    sCurrentToken.appendLiteralChar(c);
                    collectTokenAndResetCache(tokens, sCurrentToken);
                    return;
                } else if (operateResult.result == Brace.PAIR_RESULT_EL_END) {
                    // elCapsuleStringType 这里应该在 el 表达式开始时，在brace中记录一个
                    // 当前elLayer 的 el表达式结束了
                    // 进入 字符串模式
                    sCurrentToken.type = TokenType.ELExprEnd;
                    sCurrentToken.appendLiteralChar(c);
                    collectTokenAndResetCache(tokens, sCurrentToken);

                    sCurrentToken.elLayer--;

                    if (operateResult.stringType == Brace.EL_CAPSULE_STRING_TYPE_STRING) {
                        sCurrentToken.type = TokenType.String;
                        sCommentOrString = CommentOrString.InString;
                    } else {
                        sCurrentToken.type = TokenType.StringBlock;
                        sCommentOrString = CommentOrString.InBlockString;
                    }
                    return;
                }
            } else {
                sCurrentToken.type = TokenType.Parentheses;
                sCurrentToken.appendLiteralChar(c);
                collectTokenAndResetCache(tokens, sCurrentToken);
                return;
            }
        } else {
            Log.e("elLayer < 0",
                    "line : " + (lineIndex + 1) + ", columnIndex : " + columnIndex
                            + ", current char : ->" + c + "<-, this char's int value is : " + ((int) c)
                            + ", currentToken literal str is : ->" + sCurrentToken.literalStr + "<-", this.absFileName);

            throw new IllegalArgumentException("elLayer < 0, " + sCurrentToken.elLayer);
        }
    }

    private static void collectTokenAndResetCache(ArrayList<SealedToken> tokens, TokenCache tokenCache) {
        SealedToken sealedToken = tokenCache.sealAndReset();
        tokens.add(sealedToken);
    }

    private static boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\t';
    }

    private static boolean isExpressionEnd(char c) {
        return c == ';';
    }

    private static boolean isLegalIdentifierStarter(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || (c == '_') || (c == '$') || (c == '@') || (c == '`');
    }

    private static boolean isLegalIdentifierPostfix(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || (c == '_') || (c == '$') || (c == '@') || (c == '`');
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

    private static boolean isDollar(char c) {
        return c == '$';
    }

    private static boolean isLeftBrace(char c) {
        return c == '{';
    }

    private static boolean isRightBrace(char c) {
        return c == '}';
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
                || c == 'o' || c == 'O'
                || c == 'p' || c == 'P'
                || c == 'u' || c == 'U') {
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

}

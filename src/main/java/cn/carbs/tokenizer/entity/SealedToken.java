package cn.carbs.tokenizer.entity;

import cn.carbs.tokenizer.type.TokenType;

public class SealedToken {

    public TokenType type;

    public String literalStr;

    public int extraInt;

    @Override
    public String toString() {
        return "SealedToken{" +
                "Str='" + literalStr + '\'' +
                ", type=" + type +
                ", extra='" + extraInt + '\'' +
                '}';
    }

    public String getLiteralStr() {
        return literalStr;
    }

    public static SealedToken genNewLineToken() {
        SealedToken sealedToken = new SealedToken();
        sealedToken.literalStr = "\n";
        sealedToken.type = TokenType.NotExistTokenNewLine;
        return sealedToken;
    }

    public static SealedToken genImportToken(String importStr) {
        SealedToken sealedToken = new SealedToken();
        sealedToken.literalStr = importStr;
        sealedToken.type = TokenType.ImportPath;
        return sealedToken;
    }

    public static SealedToken genPackageToken(String packageStr) {
        SealedToken sealedToken = new SealedToken();
        sealedToken.literalStr = packageStr;
        sealedToken.type = TokenType.PackagePath;
        return sealedToken;
    }
}

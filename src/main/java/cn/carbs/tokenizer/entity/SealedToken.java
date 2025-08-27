package cn.carbs.tokenizer.entity;

import cn.carbs.tokenizer.type.TokenType;

public class SealedToken {

    public TokenType type;

    public String literalStr;

    public int extraInt;

    // 所处层级，进 el 表达式 +1，出 el 表达式 -1
    public int elLayer = 0;

    // 当在string内部时，el表达式中的token会设置此属性，用于表示紧靠外边的string 的type
//    public int elCapsuleStringType = 0;

    @Override
    public String toString() {
        String strExtraInt = extraInt != 0 ? (", extra=" + extraInt) : "";
        String strELLayer = elLayer > 0 ? (", elLayer=" + elLayer) : "";
//        String strELCapsuleStringType = elCapsuleStringType > 0 ? (", elCapsuleStringType=" + elCapsuleStringType) : "";
        return "SealedToken { " +
                "Str='" + literalStr + '\'' +
                ", type=" + type +
                strELLayer
//                + strELCapsuleStringType
                + strExtraInt + " }";
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

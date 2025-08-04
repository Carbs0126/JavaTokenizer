public class SealedToken {

    public TokenType type;

    public String literalStr;

    // 类，存放类的全称
    public String extra;

    @Override
    public String toString() {
        return " SealedToken{" +
                "type=" + type +
                ", literalStr='" + literalStr + '\'' +
                ", extra='" + extra + '\'' +
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

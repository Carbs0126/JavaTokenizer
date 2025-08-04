
// 1. 每个词一个 token
// 2. 每个完整注释一个 token
public class TokenCache {

    public TokenType type;

    public StringBuilder literalStr;

    // 类，存放类的全称
    public StringBuilder extra;

    // 行列位置等

    public TokenCache() {
        type = TokenType.None;
        literalStr = new StringBuilder();
        extra = new StringBuilder();
    }

    public void appendLiteralChar(char c) {
        this.literalStr.append(c);
    }

    public int literalStrLength() {
        return this.literalStr.length();
    }

    public char getLastChar() {
        return this.literalStr.charAt(this.literalStr.length() - 1);
    }

    public SealedToken sealAndReset() {
        SealedToken sealedToken = this.seal();
        this.reset();
        return sealedToken;
    }

    private SealedToken seal() {
        SealedToken sealedToken = new SealedToken();
        sealedToken.type = type;
        if (literalStr != null && literalStr.length() > 0) {
            sealedToken.literalStr = literalStr.toString();
        }
        if (extra != null && extra.length() > 0) {
            sealedToken.extra = extra.toString();
        }
        return sealedToken;
    }

    private void reset() {
        this.type = TokenType.None;
        this.literalStr.setLength(0);
        this.extra.setLength(0);
    }

}

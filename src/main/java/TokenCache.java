
// 1. 每个词一个 token
// 2. 每个完整注释一个 token
public class TokenCache {

    public TokenType type;

    public StringBuilder literalStr;

    // 类，存放类的全称
    public StringBuilder extra;

    // 行列位置等

    public TokenCache() {
        literalStr = new StringBuilder();
        extra = new StringBuilder();
    }

    public void appendLiteralChar(char c) {
        this.literalStr.append(c);
    }

    public SealedToken sealAndReset() {
        SealedToken sealedToken = this.seal();
        this.reset();
        return sealedToken;
    }

    private SealedToken seal() {
        SealedToken sealedToken = new SealedToken();
        sealedToken.type = type;
        sealedToken.literalStr = literalStr.toString();
        sealedToken.extra = extra.toString();
        return sealedToken;
    }

    private void reset() {
        this.type = TokenType.None;
        this.literalStr.setLength(0);
        this.extra.setLength(0);
    }

}

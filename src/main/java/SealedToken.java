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
}

package cn.carbs.tokenizer.entity;

import cn.carbs.tokenizer.type.TokenType;

import java.util.ArrayList;

// 1. 每个词一个 token
// 2. 每个完整注释一个 token
public class TokenCache {

    public static final int IN_STRING_MODE_ESCAPE_IDLE = 0;
    public static final int IN_STRING_MODE_ESCAPE_READY = 1;

    public static final int IN_NUMBER_MODE = 10;
    public static final int IN_RANGE_MODE = 11;

    public static final int EL_CAPSULE_IN_STRING = 1;
    public static final int EL_CAPSULE_IN_STRING_BLOCK = 2;

    public static final int EL_STARTER_STATE_NONE = 0;
    public static final int EL_STARTER_STATE_MAY_START = 1;

    // 下面的
    public static final int EL_NONE = 0;
    public static final int EL_MAY_START = 20;
    public static final int EL_IN_EXPRESSION = 21;
    public static final int EL_END = 22;

    public static final int EL_EXP_IN_STRING = 1;
    public static final int EL_EXP_IN_STRING_BLOCK = 2;



    public TokenType type;

    public StringBuilder literalStr;

    // 类，存放类的全称
    public int extraInt;

    // 当前 token 层级，进 el 表达式 +1，出 el 表达式 -1。普通为0
    public int elLayer = 0;

    // todo wang 没用了
    // 当在string内部时，el表达式中的token会设置此属性，用于表示紧靠外边的string 的type
    // 1 普通 string
    // 2 string block
    public int elCapsuleStringType = 0;

    // 只有位于 el 表达式中的 大括号，才能放到 stack 中
    public ArrayList<Brace> elBraceArr = new ArrayList<>();

    public void pushBrace(boolean elStarter, int direction) {
        elBraceArr.add(new Brace(elStarter, direction, this.elLayer));
    }

    public void pushELStarterBrace(int stringType) {
        elBraceArr.add(new Brace(true, Brace.DIRECTION_LEFT, this.elLayer).setELStarterStringType(stringType));
    }

    // ${ 时，记录一个 string 类型
    public Brace.CheckPairResult operateLastTwoBracesResult() {
        int size = elBraceArr.size();
        if (size >= 2) {
            Brace l = elBraceArr.get(size - 2);
            Brace r = elBraceArr.get(size - 1);
            Brace.CheckPairResult result = Brace.checkIfPair(l, r);
            if (result.result == Brace.PAIR_RESULT_YES) {
                // 说明两个是一对，{}
                elBraceArr.remove(r);
                elBraceArr.remove(l);
                return result;
            } else if (result.result == Brace.PAIR_RESULT_NO) {
                // 两个不是一对
                return result;
            } else if (result.result == Brace.PAIR_RESULT_EL_END) {
                // 两个是一对，并且是 el 表达式中的最前面和最后面两个括号 ${}
                elBraceArr.remove(r);
                elBraceArr.remove(l);
                return result;
            } else {
                throw new IllegalArgumentException("");
            }
        }
        return null;
    }


    //////// =========  下面的后续优化  ===========
    // kotlin 可以在字符串中使用 el 表达式
    public int elExpState = 0; // 不需要了，用 elLayer 代替 // 不对，还需要，用来判断是否为el表达式的起始

    public int elExpStarterState = 0;
    // 在el表达式模式时，净剩余的左括号的数量
    // 遇到一个左大括号，就 +1；
    // 遇到一个右大括号，就 -1；
    public int elNetLeftBraceCount = 0; // 不需要了，用上面的stack

    // 当前el表达式所处的字符串，用于当el表达式结束时，恢复原来的字符串。
    // 只有两种正常取值：
    // 1 EL_EXP_IN_STRING
    // 2 EL_EXP_IN_STRING_BLOCK
    // todo wang 由于el可以嵌套，那么这里是不是得用队列了？stack
    public int elExpInStringType = 0;  // 不需要了，用 elCapsuleStringType 代替

    // todo 错了，应该用嵌套，el表达式可以嵌套
    // 是否为整个字符串的一部分，用于被 el 表达式切割的字符串
//    public boolean partOfEL = false;

    public TokenCache() {
        type = TokenType.None;
        literalStr = new StringBuilder();
    }

    public void appendLiteralChar(char c) {
        this.literalStr.append(c);
    }

    public void appendLiteralString(String s) {
        this.literalStr.append(s);
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
        sealedToken.elLayer = elLayer;
//        sealedToken.elCapsuleStringType = elCapsuleStringType;
//        sealedToken.partOfEL = partOfEL;
        if (literalStr != null && literalStr.length() > 0) {
            sealedToken.literalStr = literalStr.toString();
        }
        sealedToken.extraInt = extraInt;
        return sealedToken;
    }

    private void reset() {
        this.type = TokenType.None;
        this.literalStr.setLength(0);
        this.extraInt = 0;
        // 其它的不变，如是否处于el表达式的状态不做改变
    }

}

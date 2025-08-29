package cn.carbs.tokenizer.entity;

import cn.carbs.tokenizer.type.TokenType;

import java.util.ArrayList;

// 1. 每个词一个 token
// 2. 每个完整注释一个 token
public class TokenCache {

    public static final int IN_STRING_MODE_ESCAPE_IDLE = 0;
    public static final int IN_STRING_MODE_ESCAPE_READY = 1;

    public static final int IN_IDENTIFIER_STANDARD = 0;
    public static final int IN_IDENTIFIER_BACKTICK = 21;

    public static final int EL_STARTER_STATE_NONE = 0;
    public static final int EL_STARTER_STATE_MAY_START = 1;

    public TokenType type;

    public StringBuilder literalStr;

    // 取值
    // IN_STRING_MODE_ESCAPE_IDLE   字符串正常
    // IN_STRING_MODE_ESCAPE_READY  字符串转义符
    // IN_NUMBER_MODE   连续的点点 即 . 处于数字模式
    // IN_RANGE_MODE    连续的点点 即 . 处于范围模式
    // IN_IDENTIFIER_STANDARD   普通的 identifier
    // IN_IDENTIFIER_BACKTICK   被``包裹的 identifier
    public int extraInt;

    // 当前 token 层级，进 el 表达式 +1，出 el 表达式 -1。普通为0
    public int elLayer = 0;

    // 取值
    // 0 EL_STARTER_STATE_NONE
    // 1 EL_STARTER_STATE_MAY_START
    public int elExpStarterState = EL_STARTER_STATE_NONE;

    public int dotLocationIndexInNumber = -1;

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

    public TokenCache() {
        type = TokenType.None;
        literalStr = new StringBuilder();
    }

    public void appendLiteralChar(char c) {
        this.literalStr.append(c);
    }

    public void appendLiteralStr(String str) {
        this.literalStr.append(str);
    }

    public void appendLiteralString(String s) {
        this.literalStr.append(s);
    }

    public int getLiteralLength() {
        return this.literalStr.length();
    }

    public char getLastChar() {
        return this.literalStr.charAt(this.literalStr.length() - 1);
    }

    public boolean pop() {
        if (this.literalStr.length() > 0) {
            String literStr = this.literalStr.toString();
            this.literalStr.setLength(0);
            this.literalStr.append(literStr.substring(0, literStr.length() - 1));
            return true;
        } else {
            return false;
        }
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
        this.dotLocationIndexInNumber = -1;
        // 其它的不变，如是否处于el表达式的状态不做改变
    }

}

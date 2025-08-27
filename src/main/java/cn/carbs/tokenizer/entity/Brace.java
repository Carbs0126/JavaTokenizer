package cn.carbs.tokenizer.entity;

/**
 * 只在 el 表达式中使用
 */
public class Brace {

    public static final int DIRECTION_LEFT = -1;

    public static final int DIRECTION_RIGHT = 1;

    public static final int PAIR_RESULT_YES = 1;

    public static final int PAIR_RESULT_NO = 2;

    public static final int PAIR_RESULT_EL_END = 3;

    public static final int EL_CAPSULE_STRING_TYPE_STRING = 1;

    public static final int EL_CAPSULE_STRING_TYPE_STRING_BLOCK = 2;

    public boolean elStarter = false;
    // 1 string, 2 string block
    public int elStarterStringType = 0;
    // left = -1; right = 1;
    public int direction = 0;

    public int elLayer = 0;

    public Brace(boolean elStarter, int direction, int elLayer) {
        this.elStarter = elStarter;
        this.direction = direction;
        this.elLayer = elLayer;
    }

    public Brace setELStarterStringType(int elStarterStringType) {
        this.elStarterStringType = elStarterStringType;
        return this;
    }

    /**
     * 检查两个 brace 是否能抵消
     * 如果返回 PAIR_RESULT_NO，则继续向 stack 中添加 Brace 对象
     * @param l 左侧的大括号
     * @param r 右侧的大括号
     * @return PAIR_RESULT_YES PAIR_RESULT_NO PAIR_RESULT_EL_END
     */
    public static CheckPairResult checkIfPair(Brace l, Brace r) {
        if (l.elLayer == l.elLayer) {
            if (l.direction == DIRECTION_LEFT && r.direction == DIRECTION_RIGHT) {
                if (l.elStarter) {
                    // 如果左侧的是el表达式的开头
                    return new CheckPairResult(PAIR_RESULT_EL_END, l.elStarterStringType);
                } else {
                    return new CheckPairResult( PAIR_RESULT_YES, 0);
                }
            } else {
                return new CheckPairResult( PAIR_RESULT_NO, 0);
            }
        } else {
            return new CheckPairResult( PAIR_RESULT_NO, 0);
        }
    }

    public static class CheckPairResult {
        public int result;
        // 当返回 result == PAIR_RESULT_EL_END 时，stringType 会赋值
        public int stringType;

        public CheckPairResult(int result, int stringType) {
            this.result = result;
            this.stringType = stringType;
        }
    }
}

package cn.carbs.tokenizer.state;

// 单个星号不能作为 block comment /*/

public enum CommentOrString {
    None,
    MayCommentStarter,      //  即 /
    InSlashComment,         //  即 //
    InBlockComment,         //  即 /*
    MayEndBlockComment,     //  即 *
    InString,               //  即 "
    InBlockString,               //  即 """  """
    MayStringStarter0,      //  即 """ 中的第一个
    MayStringStarter1,      //  即 """ 中的第二个
    MayStringEnd0,          //  即 """ 中的第一个
    MayStringEnd1,          //  即 """ 中的第二个
    MayBlockStringEnd;

    // 由于 kotlin 的注释支持嵌套，因此当处理 kotlin 中的 InBlockComment 时，用于处理嵌套注释
    private int blockCommentLayer = 0;

    CommentOrString() {

    }

    public CommentOrString setBlockCommentLayer(int layer) {
        this.blockCommentLayer = layer;
        return this;
    }

    // 获取当前所处的 block comment 的层级，只要进入 block comment，层级至少是 1
    public int getBlockCommentLayer() {
        return this.blockCommentLayer;
    }

    public CommentOrString resetBlockCommentLayer() {
        this.blockCommentLayer = 0;
        return this;
    }
}

package cn.carbs.tokenizer.state;

// 单个星号不能作为 block comment /*/

public enum CommentOrString {
    None,
    MayCommentStarter,      //  即 /
    InSlashComment,         //  即 //
    InBlockComment,         //  即 /*
    MayEndBlockComment,     //  即 *
    InString,               //  即 "
}

package cn.carbs.tokenizer.xml;

public enum XmlCommentOrString {
    None,
    MayCommentStart0,       //  即 <!-- 中的  <
    MayCommentStart1,       //  即 <!-- 中的  !
    MayCommentStart2,       //  即 <!-- 中的  第一个 -
    InComment,
    MayCommentEnd0,         //  即 --> 中的 第一个 -
    MayCommentEnd1,         //  即 --> 中的 第二个 -
    InString,               //  即 "
}

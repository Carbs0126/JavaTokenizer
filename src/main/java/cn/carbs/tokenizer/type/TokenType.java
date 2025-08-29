package cn.carbs.tokenizer.type;

public enum TokenType {

    None,
    Identifier,
    Operator, // + - * / ( )
    Parentheses,
    Number,
    String,
    StringBlock,        // """

    DotConfirmLater,    // kotlin中没有用到这个

    DotConfirmLaterForNone, // none，或者跟在 char 和 identifier 后面。none后面如果是数字，则合入数字；如果是dot，则变成range；如果是其他，则变成DotForIdentifier
    DotConfirmLaterForNumber, // 跟在number后面，有可能变成 DotForRange，有可能合入到number中

    DotForIdentifier,   // 用于 identifier 合成
    DotForRange,        // 两个 .. 合在一起，kotlin中没有 ... 运算法；java 中有 ... 代表可变参数

    Char,
    CommentLine,
    CommentBlock,
    PackagePath,
    ImportPath,
    Comma,
    Colon,
    End,
    ELExprStart,
    ELExprEnd,
    NotExistTokenNewLine,
    Space
}

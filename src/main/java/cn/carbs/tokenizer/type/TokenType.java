package cn.carbs.tokenizer.type;

public enum TokenType {

    None,
    Identifier,
    Operator, // + - * / ( )
    Parentheses,
    Number,
    String,
    StringBlock,        // """
    DotConfirmLater,
    DotForIdentifier,   // 用于 identifier 合成
    DotForRange,        // ..
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

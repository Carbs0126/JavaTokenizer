package cn.carbs.tokenizer.type;

public enum TokenType {

    None,
    Identifier,
    Operator, // + - * / ( )
    Parentheses,
    Number,
    String,
    StringBlock,        // """
    DotForIdentifier,    // 用于 identifier 合成
    DotConfirmLater,
    Char,
    CommentLine,
    CommentBlock,
    PackagePath,
    ImportPath,
    Comma,
    Colon,
    End,
    NotExistTokenNewLine,
    Space
}

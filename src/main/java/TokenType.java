public enum TokenType {

    None,
    Identifier,
    Operator, // + - * / ( )
    Parentheses,
    Number,
    String,
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
    NotExistTokenNewLine
}

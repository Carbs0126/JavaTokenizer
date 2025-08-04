public enum TokenType {

    None,
    Identifier,
    Operator, // + - * / ( )
    Parentheses,
    Number,
    String,
    Dot,    // 用于 identifier 合成
    Char,
    CommentLine,
    CommentBlock,
    PackagePath,
    ImportPath,
    End // ;
}

{
    val leniencyMessage = if (errorTypePolicy == KaSubtypingErrorTypePolicy.LENIENT) " with error type leniency" else ""
// 注释中的能过，即 "" 后面没有直接跟括号可以过

    // else ""} 此时没过，"" 被正常解析为string，但是 }和to连在一起了
    "Expected `$type`${if (!expectedResult) " not" else ""} to be a subtype of `$classId`$leniencyMessage (`$resultDirective`)."

}
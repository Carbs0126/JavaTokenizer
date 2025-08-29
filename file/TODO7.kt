package Unknown

fun main() {

//    val a = "${name} = ${if (name !in parameterDescriptorsWithDefaultValue) value else 0}" // ok
//    val a = "${name} = ${if (name !in parameterDescriptorsWithDefaultValue) value else "abcdefg"}" // ok
//    val a = "${name} = ${if (name !in parameterDescriptorsWithDefaultValue) value else "abc${name, x}defg"}" // ok
//    val a = "${name} = ${if (name !in parameterDescriptorsWithDefaultValue) value else "abc${}defg"}" // ok
//    val a = "${}" // ok
//    val a = "${name} = ${if (name !in ) value else "abc${wfawfwaf, "some ${xxx} string"}defg"}" // ok


//    val a = """${name} = ${if (name !in parameterDescriptorsWithDefaultValue) value else 0}"""  // ok

//    val a = """${name} = ${if (name !in parameterDescriptorsWithDefaultValue) value else "abcdefg"}""" // ok
//    val a = """${name} = ${if (name !in parameterDescriptorsWithDefaultValue) value else "abc ${yyyyy}defg"}""" // ok

//    val a = """${name} = ${if (name !in parameterDescriptorsWithDefaultValue) value else """abc ${yyyyy}defg"""}""" // ok

    val a = "${name} = ${if (name !in parameterDescriptorsWithDefaultValue) value else """abc ${yyyyy}defg"""}" // ok

}
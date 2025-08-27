// 测试 kotlin

// ahiehaungajldka;d
/**
 * afdafdadvcadsca fadfadfa
 */
package Unknown./*fdafjqwklef,'a*/carbs
//afdajlifejiawo
//sf'
/*fdafjqwklef,'a*/

private fun String.isUpperCaseCharAt(index: Int, asciiOnly: Boolean): Boolean {
    val c = this[index]
    return if (asciiOnly) c in 'A'..'Z' else c.isUpperCase()
}

fun main() {

    val u = 11.elemAndListWithReceiver(4, list("7"))

    var sxsdfad = """
        eqfeqwfewadsafewqefadsfadsfa thisi is wqfa w\\\\\ dajfewajfada
        \\\\aefwafadfa'fwafdafa\'wgadgaf"" fdafew
    """.trimIndent()

    val quote = "\"\"\""
    var x = """\\
    \"\"\"tsdafd\${"$"}afaf
    """
    println(x)

    var xasdfa = "hello\nwo\"rld";
    var afdaw = 'a';
    var daf = 1000f;


}

fun main2() {

    // 模拟有默认值的参数名集合
    val parameterDescriptorsWithDefaultValue = setOf("age", "gender")

    // 模拟参数键值对
    val allValueArguments = mapOf(
        "name" to "Alice",
        "age" to 30,
        "gender" to "Female",
        "address" to "New York"
    )

    // el表达式，$ 和 { 之间不能有空格
    // 转换为字符串列表（核心逻辑）
    val argumentList = allValueArguments.entries
        .map { (name, value) ->
            // 如果是 在el表达式中，则字符串不应该算作在el表达式内部
            // 存储嵌套层级
            // 1. 每一个 token 都应该有表示层级的值，即layer。当 layer == 0 时，表示最外层的代码，即 非 string 中的 el 表达式
            // 2. 当进入string模式后，如果遇到el表达式，则将 string token 中的层级，透传给 el 表达式的层级，此el表达式中的所有token的layer，
            //    都等于 外部string 的 layer + 1
            // 3. 当在 el 表达式中遇到 string 时，此 string 的layer 等同于el表达式的layer，
            // 4. 当在string中遇到 el 表达式时，layer + 1
            // 5. 总结规律即：每次遇到 el 表达式，layer + 1
            // 存储当前 string 的 type，比如有可能是 string，有可能是 block string
            // 1. 每一个token中使用一个变量存放当前所处的string 的type， 0 是没有处于string中，1 是处于 string，2是处于 block string
            // 2. 全局维护一个 专门放 leftEl 的stack，同时这个 leftEl token中还有层级，这样在遇到 leftEl 时，压栈
//            "${name} = ${ if (name !in parameterDescriptorsWithDefaultValue) value else ".xxx."}"
            "${name} = ${ if (name !in parameterDescriptorsWithDefaultValue) value else 0}"
        }

    // 打印结果
    argumentList.forEach { println(it) }

//    println();

//    xxx {
//        return
//    }

//    Nat._0

//    Succ::class.memberExtensionFunctions.map { println(it.name) }
//    Nat()::class.members

//    val list = listOf<String>("How")
//    println(list::class.typeParameters)
}
fun test() = List::class

fun other(): Int {
    return "foo".length
}

class Foo {
    fun foo() {
        require(other() == 3)
    }
}
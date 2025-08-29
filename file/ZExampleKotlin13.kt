    @Test
    fun `test - MutableMap`() {
        val header = header(
            """
            class A
            class B
            val foo: MutableMap<A, B> get() = error("stub")
            """.trimIndent(),
            configuration = HeaderGenerator.Configuration(frameworkName = "Shared")
        )

        assertEquals("SharedMutableDictionary<SharedA *, SharedB *> *", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - function type - 0`() {
        val header = header(
            """
            fun foo(action: () -> Unit) = Unit
        """.trimIndent()
        )

        assertEquals("void (^)(void) -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - function type - 1`() {
        val header = header(
            """
            class A
            fun foo(action: () -> A) = Unit
        """.trimIndent()
        )

        assertEquals("A *(^)(void) -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - function type - 2`() {
        val header = header(
            """
            class A
            class B
            fun foo(action: (a: A) -> B) = Unit
        """.trimIndent()
        )

        assertEquals("B *(^)(A *) -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - function type - 3`() {
        val header = header(
            """
            class A
            class B
            class C
            fun foo(action: (a: A, b: B) -> C) = Unit
        """.trimIndent()
        )

        assertEquals("C *(^)(A *, B *) -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - function type - receiver`() {
        val header = header(
            """
            class A
            class B
            class C
            fun foo(action: A.(b: B) -> C) = Unit
        """.trimIndent()
        )

        assertEquals("C *(^)(A *, B *) -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - value inline class`() {
        val header = header(
            """
            class A
            value class Inlined(val a: A)
            
            val foo: Inlined get() = error("stub")
        """.trimIndent()
        )

        assertEquals("id", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - NativePtr`() {
        val header = header(
            """
            val foo: kotlin.native.internal.NativePtr get() = error("stub")
            """.trimIndent()
        )

        assertEquals("void * _Nullable", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - NonNullNativePtr`() {
        val header = header(
            """
            val foo: kotlin.native.internal.NonNullNativePtr get() = error("stub")
            """.trimIndent()
        )

        assertEquals("void *", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - CPointer`() {
        val header = header(
            """
            import kotlinx.cinterop.CPointer
            import kotlinx.cinterop.CPointed
            val foo: CPointer<CPointed> get() = error("stub")
            """.trimIndent()
        )

        assertEquals("void *", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - generics - class`() {
        val header = header(
            """
            class A<T>
            class B
            val foo: A<B> get() = error("stub")
            """.trimIndent()
        )
        assertEquals("A<B *> *", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - generic function`() {
        val header = header(
            """
            fun <T> foo(value: T) = Unit
            """.trimIndent()
        )
        assertEquals("id _Nullable -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - generic class with function`() {
        val header = header(
            """
            class A<T> {
                fun foo(value: T) = Unit
            }
            """.trimIndent()
        )
        assertEquals("T _Nullable -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - class with generic function`() {
        val header = header(
            """
            class A {
               fun <T: Any> foo(value: T) = Unit
            }
            """.trimIndent()
        )

        assertEquals("id -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - generic class with bounds with function`() {
        val header = header(
            """
            interface I
            class A<T: I> {
                fun foo(value: T) = Unit
            }
            """.trimIndent()
        )
        assertEquals("T -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - nested classes with same type parameter`() {
        val header = header(
            """
            class A<T: Any> {
                class B<T: Any> {
                    fun foo(value: T) = Unit
                }
            }
            """.trimIndent()
        )

        assertEquals("T -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - classes with same type parameter as function`() {
        val header = header(
            """
            class A<T: Any> {
                fun <T: Any> foo(value: T) = Unit
            }
            """.trimIndent()
        )

        assertEquals("id -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - ObjCObject types`() {
        val header = header(
            """
                class A: kotlinx.cinterop.ObjCObject
                val foo : A get() = A
                val bar: kotlinx.cinterop.ObjCObject
            """.trimIndent()
        )

        assertEquals("id", header.renderTypesOfSymbol("foo"))
        assertEquals("id", header.renderTypesOfSymbol("bar"))
    }

    @Test
    fun `test - unresolved error type`() {
        val header = header(
            """
            val property : Unresolved get() = error("stub")
            fun function(a:  Unresolved): Unresolved = error("stub")
            """.trimIndent()
        )
        assertEquals("ERROR *", header.renderTypesOfSymbol("property"))
        assertEquals("ERROR * -> ERROR *", header.renderTypesOfSymbol("function"))
    }

    @Test
    fun `test - char - property`() {
        val header = header(
            """
                val property : Char get() = error("stub")
            """.trimIndent()
        )

        assertEquals("unichar", header.renderTypesOfSymbol("property"))
    }

    @Test
    fun `test - char - function parameter`() {
        val header = header(
            """
                fun foo(x: Char) = Unit
            """.trimIndent()
        )

        assertEquals("unichar -> void", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - char - as return type`() {
        val header = header(
            """
                fun foo(): Char = error("stub")
            """.trimIndent()
        )

        assertEquals(" -> unichar", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - function type returning char`() {
        val header = header(
            """
            val foo: () -> Char
        """.trimIndent()
        )

        assertEquals("id (^)(void)", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - custom List implementation`() {
        val header = header(
            """
                interface MyList<T>: List<T>
                val foo: MyList<String> get() = error("stub")
            """.trimIndent()
        )

        assertEquals("NSArray<NSString *> *", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - ObjCObject - no ExternalObjCClass annotation`() {
        val header = header(
            """
                class Foo  : kotlinx.cinterop.ObjCObject 
                val foo: Foo get() = error("stub")
            """.trimIndent()
        )

        assertEquals("id", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - ObjCObject - with ExternalObjCClass annotation`() {
        val header = header(
            """
                @kotlinx.cinterop.ExternalObjCClass
                class Foo: kotlinx.cinterop.ObjCObject
                val foo: Foo get() = error("stub")
            """.trimIndent()
        )

        assertEquals("Foo *", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - ObjCObject - with ExternalObjCClass annotation - in super class`() {
        val header = header(
            """
                @kotlinx.cinterop.ExternalObjCClass
                open class A: kotlinx.cinterop.ObjCObject
                class Foo: A()
                val foo: Foo get() = error("stub")
            """.trimIndent()
        )

        assertEquals("A *", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - ObjCObject - with ObjCClass supertype`() {
        val header = header(
            """
                class Foo: kotlinx.cinterop.ObjCClass
                val foo: Foo get() = error("stub")
            """.trimIndent()
        )

        assertEquals("Class", header.renderTypesOfSymbol("foo"))
    }

    //

    @Test
    fun `test - function type - returns function with no arguments and no return type`() {
        val header = header(
            """
                val foo: () -> () -> Unit = { {} }
            """.trimIndent()
        )
        assertEquals("KotlinUnit *(^(^)(void))(void)", header.renderTypesOfSymbol("foo"))
    }

    @Test
    fun `test - function type - returns chain of functions with different arguments`() {
        val header = header(
            """
                class Bar
                val foo: () -> (String, Int) -> (String?) -> (Long, Bar) -> (Any) -> Bar = null!!
            """.trimIndent()
        )

        assertEquals(
            "Bar *(^(^(^(^(^)(void))(NSString *, Int *))(NSString * _Nullable))(Long *, Bar *))(id)",
            header.renderTypesOfSymbol("foo")
        )
    }

    private fun header(
        @Language("kotlin") vararg sourceCode: String,
        configuration: HeaderGenerator.Configuration = HeaderGenerator.Configuration(),
    ): ObjCHeader {
        sourceCode.forEachIndexed { index, code ->
            val sourceKt = tempDir.resolve("source$index.kt")
            sourceKt.writeText(code)
        }
        return headerGenerator.generateHeaders(tempDir.toFile(), configuration)
    }

    private fun ObjCHeader.renderTypesOfSymbol(name: String): String {
        val stub = stubs.closureSequence().find { it.origin?.name?.asString() == name }
        return when (stub) {
            is ObjCMethod -> stub.parameters.joinToString(", ") { it.type.render() } + " -> ${stub.returnType.render()}"
            is ObjCProperty -> stub.type.render()
            is ObjCParameter -> stub.type.render()
            is ObjCInterface -> "${stub.name}: ${stub.superClass.orEmpty()}" + "${
                stub.superClassGenerics.joinToString(", ", "<", ">")
            }, ${stub.superProtocols.joinToString(", ")}"
            is ObjCProtocol -> "${stub.name}: ${stub.superProtocols.joinToString(", ")}"
            null -> error("Missing symbol '$name' in \n${render().joinToString("\n")}")
            else -> error("No rendering defined for $stub")
        }
    }
}
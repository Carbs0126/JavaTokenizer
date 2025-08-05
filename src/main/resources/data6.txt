    /**
     * {@preview Associated with text blocks, a preview feature of
     *           the Java language.
     *
     *           This method is associated with <i>text blocks</i>, a preview
     *           feature of the Java language. Programs can only use this
     *           method when preview features are enabled. Preview features
     *           may be removed in a future release, or upgraded to permanent
     *           features of the Java language.}
     *
     * Returns a string whose value is this string, with incidental
     * {@linkplain Character#isWhitespace(int) white space} removed from
     * the beginning and end of every line.
     * <p>
     * Incidental {@linkplain Character#isWhitespace(int) white space}
     * is often present in a text block to align the content with the opening
     * delimiter. For example, in the following code, dots represent incidental
     * {@linkplain Character#isWhitespace(int) white space}:
     * <blockquote><pre>
     * String html = """
     * ..............&lt;html&gt;
     * ..............    &lt;body&gt;
     * ..............        &lt;p&gt;Hello, world&lt;/p&gt;
     * ..............    &lt;/body&gt;
     * ..............&lt;/html&gt;
     * ..............""";
     * </pre></blockquote>
     * This method treats the incidental
     * {@linkplain Character#isWhitespace(int) white space} as indentation to be
     * stripped, producing a string that preserves the relative indentation of
     * the content. Using | to visualize the start of each line of the string:
     * <blockquote><pre>
     * |&lt;html&gt;
     * |    &lt;body&gt;
     * |        &lt;p&gt;Hello, world&lt;/p&gt;
     * |    &lt;/body&gt;
     * |&lt;/html&gt;
     * </pre></blockquote>
     * First, the individual lines of this string are extracted as if by using
     * {@link String#lines()}.
     * <p>
     * Then, the <i>minimum indentation</i> (min) is determined as follows.
     * For each non-blank line (as defined by {@link String#isBlank()}), the
     * leading {@linkplain Character#isWhitespace(int) white space} characters are
     * counted. The leading {@linkplain Character#isWhitespace(int) white space}
     * characters on the last line are also counted even if
     * {@linkplain String#isBlank() blank}. The <i>min</i> value is the smallest
     * of these counts.
     * <p>
     * For each {@linkplain String#isBlank() non-blank} line, <i>min</i> leading
     * {@linkplain Character#isWhitespace(int) white space} characters are removed,
     * and any trailing {@linkplain Character#isWhitespace(int) white space}
     * characters are removed. {@linkplain String#isBlank() Blank} lines are
     * replaced with the empty string.
     *
     * <p>
     * Finally, the lines are joined into a new string, using the LF character
     * {@code "\n"} (U+000A) to separate lines.
     *
     * @apiNote
     * This method's primary purpose is to shift a block of lines as far as
     * possible to the left, while preserving relative indentation. Lines
     * that were indented the least will thus have no leading
     * {@linkplain Character#isWhitespace(int) white space}.
     * The line count of the result will be the same as line count of this
     * string.
     * If this string ends with a line terminator then the result will end
     * with a line terminator.
     *
     * @implNote
     * This method treats all {@linkplain Character#isWhitespace(int) white space}
     * characters as having equal width. As long as the indentation on every
     * line is consistently composed of the same character sequences, then the
     * result will be as described above.
     *
     * @return string with incidental indentation removed and line
     *         terminators normalized
     *
     * @see String#lines()
     * @see String#isBlank()
     * @see String#indent(int)
     * @see Character#isWhitespace(int)
     *
     * @since 13
     *
     */
    @jdk.internal.PreviewFeature(feature=jdk.internal.PreviewFeature.Feature.TEXT_BLOCKS,
                                 essentialAPI=true)
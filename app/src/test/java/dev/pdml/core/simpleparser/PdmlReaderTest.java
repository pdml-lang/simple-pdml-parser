package dev.pdml.core.simpleparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdmlReaderTest {

    @Test
    void generalTest() throws InvalidPdmlException {

        CorePdmlReader reader = new CorePdmlReader ( "[root [child\nfoo bar]]" );
        assertEquals ( 0, reader.currentPosition() );
        assertEquals ( 1, reader.currentLineNumber() );
        assertEquals ( 1, reader.currentColumnNumber() );
        assertTrue ( reader.readNodeStart() );
        assertEquals ( 1, reader.currentPosition() );
        assertEquals ( 1, reader.currentLineNumber() );
        assertEquals ( 2, reader.currentColumnNumber() );
        assertEquals ( "root", reader.readTag() );
        assertTrue ( reader.readSeparator() );
        assertTrue ( reader.readNodeStart() );
        assertEquals ( "child", reader.readTag() );
        assertTrue ( reader.readSeparator() );
        assertEquals ( 13, reader.currentPosition() );
        assertEquals ( 2, reader.currentLineNumber() );
        assertEquals ( 1, reader.currentColumnNumber() );
        assertEquals ( "foo bar", reader.readText() );
        assertTrue ( reader.readNodeEnd() );
        assertTrue ( reader.readNodeEnd() );
        assertTrue ( reader.isAtEnd() );
        assertEquals ( 22, reader.currentPosition() );
        assertEquals ( 2, reader.currentLineNumber() );
        assertEquals ( 10, reader.currentColumnNumber() );
    }

    @Test
    void readTag() throws InvalidPdmlException {

        expectTag ( "tag1 ", "tag1" );
        expectTag ( "tag_2] ", "tag_2" );
        expectTag ( "2025-01-07] ", "2025-01-07" );
        expectTag ( "tag_.-] ", "tag_.-" );
        expectTag ( "_]", "_" );
        expectTag ( "คุณภาพ]", "คุณภาพ" );

        // Escape sequences
        expectTag ( "tag_3\\]] ", "tag_3]" );
        expectTag ( "tag\\s4] ", "tag 4" );
        expectTag (
            "\\[\\]\\s\\t\\n\\r\\f\\^\\(\\)\\=\\\"\\~\\|\\:\\,\\`\\!\\$\\\\]",
            "[] \t\n\r\f^()=\"~|:,`!$\\" );

        CorePdmlReader reader = new CorePdmlReader ( "]" );
        assertNull ( reader.readTag() );

        // Invalid
        expectInvalidTag ( "tag|" ); // invalid char |
        expectInvalidTag ( "tag\\m]" ); // invalid escape char \m

        // Invalid Unicode control code points
        expectInvalidTag ( "tag\u0000]" );
        expectInvalidTag ( "tag\u001F]" );
        expectInvalidTag ( "tag\u0080]" );
        expectInvalidTag ( "tag\u009F]" );

        // Invalid Unicode surrogate code points
        expectInvalidTag ( "tag\uD800]" );
        expectInvalidTag ( "tag\uDFFF]" );
    }

    @Test
    void readSeparator() throws InvalidPdmlException {

        expectSeparator ( " ", true );
        expectSeparator ( "\t", true );
        expectSeparator ( "\n", true );
        expectSeparator ( "\r\n", true );

        expectSeparator ( "a", false );
        expectSeparator ( "\\s", false );
    }

    @Test
    void readText() throws InvalidPdmlException {

        expectText ( "text1[", "text1" );
        expectText ( "text2]", "text2" );
        expectText ( "123 _.- คุณภาพ]", "123 _.- คุณภาพ" );

        expectText (
            "\\[\\] \t\n\r\f\\^()=\"~|:,`!$\\\\]",
            "[] \t\n\r\f^()=\"~|:,`!$\\" );

        // Escape sequences
        expectText ( "text_3\\]] ", "text_3]" );
        expectText ( "text\\s4] ", "text 4" );
        expectText (
            "\\[\\]\\s\\t\\n\\r\\f\\^\\(\\)\\=\\\"\\~\\|\\:\\,\\`\\!\\$\\\\]",
            "[] \t\n\r\f^()=\"~|:,`!$\\" );

        CorePdmlReader reader = new CorePdmlReader ( "]" );
        assertNull ( reader.readText() );

        // Invalid escape char
        expectInvalidText ( "text\\m]" );

        // Invalid Unicode control code points
        expectInvalidText ( "text\u0000]" );
        expectInvalidText ( "text\u001F]" );
        expectInvalidText ( "text\u0080]" );
        expectInvalidText ( "text\u009F]" );

        // Invalid Unicode surrogate code points
        expectInvalidText ( "text\uD800]" );
        expectInvalidText ( "text\uDFFF]" );
    }


    // Helpers

    private void expectTag ( String code, String expectedTag ) throws InvalidPdmlException {

        CorePdmlReader reader = new CorePdmlReader ( code );
        assertEquals ( expectedTag, reader.readTag() );
    }

    private void expectSeparator ( String code, boolean expectedResult ) throws InvalidPdmlException {

        CorePdmlReader reader = new CorePdmlReader ( code );
        assertEquals ( expectedResult, reader.readSeparator() );
    }

    private void expectText ( String code, String expectedText ) throws InvalidPdmlException {

        CorePdmlReader reader = new CorePdmlReader ( code );
        assertEquals ( expectedText, reader.readText() );
    }

    private void expectInvalidTag ( String code ) {

        CorePdmlReader reader = new CorePdmlReader ( code );
        assertThrows ( InvalidPdmlException.class, reader::readTag );
    }

    private void expectInvalidText ( String code ) {

        CorePdmlReader reader = new CorePdmlReader ( code );
        assertThrows ( InvalidPdmlException.class, reader::readText );
    }
}

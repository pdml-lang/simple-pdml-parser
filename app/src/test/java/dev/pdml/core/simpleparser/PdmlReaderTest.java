package dev.pdml.core.simpleparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdmlReaderTest {

    @Test
    void test() throws InvalidPdmlException {

        PdmlReader reader = new PdmlReader ( "[root [child foo bar]]" );
        assertTrue ( reader.readNodeStart() );
        assertEquals ( "root", reader.readTag () );
        assertTrue ( reader.readSeparator() );
        assertTrue ( reader.readNodeStart() );
        assertEquals ( "child", reader.readTag () );
        assertTrue ( reader.readSeparator() );
        assertEquals ( "foo bar", reader.readText() );
        assertTrue ( reader.readNodeEnd() );
        assertTrue ( reader.readNodeEnd() );
        assertTrue ( reader.isAtEnd() );
    }

    @Test
    void readTag() throws InvalidPdmlException {

        PdmlReader reader = new PdmlReader ( "tag1" );
        assertEquals ( "tag1", reader.readTag () );

        reader = new PdmlReader ( "tag_2[" );
        assertEquals ( "tag_2", reader.readTag () );

        reader = new PdmlReader ( "tag\\s3\\\\a\\[b\\]\\!]" );
        assertEquals ( "tag 3\\a[b]!", reader.readTag () );

        reader = new PdmlReader ( "[" );
        assertNull ( reader.readTag () );

        reader = new PdmlReader ( "tag\\4" );
        assertThrows ( InvalidPdmlException.class, reader::readTag );
    }

    @Test
    void readText() throws InvalidPdmlException {

        PdmlReader reader = new PdmlReader ("text 1\r\n" );
        assertEquals ( "text 1\r\n", reader.readText() );

        reader = new PdmlReader ("text \\\\ \\[2\\]\\t\\n\\r]" );
        assertEquals ( "text \\ [2]\t\n\r", reader.readText() );

        reader = new PdmlReader ("[foo]" );
        assertNull ( reader.readText() );

        reader = new PdmlReader ("text \\4" );
        assertThrows ( InvalidPdmlException.class, reader::readText );
    }
}

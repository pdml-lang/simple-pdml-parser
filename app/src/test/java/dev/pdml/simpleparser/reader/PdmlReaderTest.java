package dev.pdml.simpleparser.reader;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PdmlReaderTest {

    private static PdmlReader createReader ( String code ) {
        return PdmlReader.forString ( code );
    }

    @Test
    void readNodeName() throws IOException, InvalidPdmlException {

        readNodeNameTest ( "foo", "foo" );
        readNodeNameTest ( "_foo_-.2]", "_foo_-.2" );
        readNodeNameTest ( "bar[", "bar" );
        readNodeNameTest ( "name value", "name" );
        readNodeNameTest ( "qqq@", "qqq" );
        readNodeNameTest ( "_", "_" );

        PdmlReader reader = createReader ( "[foo]" );
        String name = reader.readNodeName();
        assertNull ( name );
        reader.readNodeStart();
        name = reader.readNodeName();
        assertEquals ( "foo", name );
        name = reader.readNodeName();
        assertNull ( name );
        reader.readNodeEnd();
        name = reader.readNodeName();
        assertNull ( name );
    }

    void readNodeNameTest ( String code, String expected ) throws IOException, InvalidPdmlException {

        PdmlReader reader = createReader ( code );
        String name = reader.readNodeName();
        assertEquals ( expected, name );
    }

    @Test
    void readNodeText() throws IOException, InvalidPdmlException {

        PdmlReader reader = createReader ( "abc123_!#[s\t\r\ne]end" );

        String text = reader.readNodeText();
        assertEquals ( "abc123_!#", text );

        // at node start
        assertNull ( reader.readNodeText() );
        assertTrue ( reader.readNodeStart() );

        text = reader.readNodeText();
        assertEquals ( "s\t\r\ne", text );

        // at node end
        assertNull ( reader.readNodeText() );
        assertTrue ( reader.readNodeEnd() );

        text = reader.readNodeText();
        assertEquals ( "end", text );

        // at eof
        assertNull ( reader.readNodeText() );


        // Escape sequences

        reader = createReader ( "__\\[\\]\\\\__\\t\\r\\n__\\u0041__\\U00000041__\\U0001F600" );
        text = reader.readNodeText();
        assertEquals ( "__[]\\__\t\r\n__A__A__\uD83D\uDE00", text );

        // invalid escape sequences
        reader = createReader ( "__\\5__" );
        assertThrows ( InvalidPdmlException.class, reader::readNodeText );

        reader = createReader ( "__\\u123__" );
        assertThrows ( InvalidPdmlException.class, reader::readNodeText );
    }

    @Test
    void readComment() throws IOException, InvalidPdmlException {

        PdmlReader reader = createReader ( "[- comment -]" );
        String comment = reader.readComment();
        assertEquals ( "[- comment -]", comment );

        reader = createReader ( "[- [- nested -] -]" );
        comment = reader.readComment();
        assertEquals ( "[- [- nested -] -]", comment );

        reader = createReader ( "1[--]2" );
        assertNull ( reader.readComment() );
        reader.readNodeText();
        comment = reader.readComment();
        assertEquals ( "[--]", comment );
        assertNull ( reader.readComment() );
        reader.readNodeText();
        assertNull ( reader.readComment() );

        // invalid
        reader = createReader ( "[- comment" );
        assertThrows ( InvalidPdmlException.class, reader::readComment );

        reader = createReader ( "[- [- nested -] -" );
        assertThrows ( InvalidPdmlException.class, reader::readComment );
    }
}

package dev.pdml.core.simpleparser;

import dev.pdml.core.simpleparser.data.TaggedNode;
import dev.pdml.core.simpleparser.data.TextLeaf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdmlParserTest {

    @Test
    public void test() throws InvalidPdmlException {

        PdmlParser parser = new PdmlParser();
        TaggedNode rootNode = parser.parse ( "[root]" );
        assertEquals ( "root", rootNode.tag() );
        assertTrue ( rootNode.isLeaf () );

        rootNode = parser.parse ( "[root [child foo bar]]" );
        assertEquals ( "root", rootNode.tag() );
        assertFalse ( rootNode.isLeaf () );

        assertEquals ( 1, rootNode.childNodes().size() );
        TaggedNode childNode = (TaggedNode) rootNode.childNodes().get ( 0 );
        assertEquals ( "child", childNode.tag() );
        assertFalse ( childNode.isLeaf () );

        TextLeaf textNode = (TextLeaf) childNode.childNodes().get ( 0 );
        assertEquals ( "foo bar", textNode.text () );

        // Invalid
        assertThrows ( InvalidPdmlException.class, () -> parser.parse ( "[root" ) );
        assertThrows ( InvalidPdmlException.class, () -> parser.parse ( "[root*]" ) );
        assertThrows ( InvalidPdmlException.class, () -> parser.parse ( "[ root]" ) );
        assertThrows ( InvalidPdmlException.class, () -> parser.parse ( "[root ]" ) );
        assertThrows ( InvalidPdmlException.class, () -> parser.parse ( "[root[child]]" ) );
    }
}

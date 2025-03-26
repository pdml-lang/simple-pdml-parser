package dev.pdml.core.simpleparser;

import dev.pdml.core.simpleparser.data.TaggedNode;
import dev.pdml.core.simpleparser.data.TextLeaf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdmlParserTest {

    private static final CorePdmlParser PARSER = new CorePdmlParser();

    @Test
    public void test() throws InvalidPdmlException {

        TaggedNode rootNode = PARSER.parse ( "[root]" );
        assertEquals ( "root", rootNode.tag() );
        assertTrue ( rootNode.isLeaf () );

        rootNode = PARSER.parse ( "[root [child foo bar]]" );
        assertEquals ( "root", rootNode.tag() );
        assertFalse ( rootNode.isLeaf () );

        assertEquals ( 1, rootNode.childNodes().size() );
        TaggedNode childNode = (TaggedNode) rootNode.childNodes().get ( 0 );
        assertEquals ( "child", childNode.tag() );
        assertFalse ( childNode.isLeaf () );

        TextLeaf textNode = (TextLeaf) childNode.childNodes().get ( 0 );
        assertEquals ( "foo bar", textNode.text() );

        testKeyValue ( "[color light green]", "color", "light green" );
        testKeyValue ( "[a\\sb \\[text\\n\\]]", "a b", "[text\n]" );

        // Whitespace before and after root node is ignored
        rootNode = PARSER.parse ( " \t\n\r\n\f[root] \t\n\r\n\f" );
        assertEquals ( "root", rootNode.tag() );
        assertTrue ( rootNode.isLeaf () );

        // Other chars not allowed
        assertThrows ( InvalidPdmlException.class, () -> PARSER.parse ( " a [root]" ) );

        // Invalid
        assertThrows ( InvalidPdmlException.class, () -> PARSER.parse ( "[root" ) );
        assertThrows ( InvalidPdmlException.class, () -> PARSER.parse ( "[ root]" ) );
        assertThrows ( InvalidPdmlException.class, () -> PARSER.parse ( "[root ]" ) );
        assertThrows ( InvalidPdmlException.class, () -> PARSER.parse ( "[root[child]]" ) );
    }

    @Test
    public void testSeparator() throws InvalidPdmlException {

        testKeyValue ( "[color green]", "color", "green" );
        testKeyValue ( "[color\tgreen]", "color", "green" );
        testKeyValue ( "[color\ngreen]", "color", "green" );
        testKeyValue ( "[color\r\ngreen]", "color", "green" );

        testKeyValue ( "[color  green]", "color", " green" );
        testKeyValue ( """
                        [color
                            green
                        ]""", "color", "    green\n" );
        testKeyValue ( "[a\\sb c]", "a b", "c" );
        testKeyValue ( "[a b\\sc]", "a", "b c" );

        assertThrows ( InvalidPdmlException.class, () -> new CorePdmlParser().parse ( "[b[i huge]]" ) );
    }

    public void testKeyValue ( String code, String expectedTag, String expectedText ) throws InvalidPdmlException {

        TaggedNode rootNode = PARSER.parse ( code );
        assertEquals ( expectedTag, rootNode.tag() );
        TextLeaf textLeaf = (TextLeaf) rootNode.childNodes().get ( 0 );
        assertEquals ( expectedText, textLeaf.text() );
    }
}

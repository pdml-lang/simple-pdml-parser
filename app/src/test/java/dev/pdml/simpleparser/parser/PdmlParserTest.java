package dev.pdml.simpleparser.parser;

import dev.pdml.simpleparser.node.branch.BranchNode;
import dev.pdml.simpleparser.node.leaf.TextNode;
import dev.pdml.simpleparser.node.root.RootNode;
import dev.pdml.simpleparser.reader.InvalidPdmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PdmlParserTest {

    @Test
    void parseString() throws IOException, InvalidPdmlException {

        PdmlParser parser = new PdmlParser();

        RootNode rootNode = parser.parseString ( "[doc]" );
        assertEquals ( "doc", rootNode.getName() );
        assertTrue ( rootNode.isRoot() );
        assertFalse ( rootNode.isBranch() );
        assertTrue ( rootNode.isEmpty() );


        rootNode = parser.parseString ( "[doc text 1]" );
        assertEquals ( "doc", rootNode.getName() );
        assertTrue ( rootNode.isRoot() );
        assertFalse ( rootNode.isBranch() );
        assertFalse ( rootNode.isEmpty() );
        assertEquals ( 1, rootNode.getChildNodes().size() );

        TextNode texNode = (TextNode) rootNode.getChildNodes().get ( 0 );
        assertEquals ( "text 1", texNode.getText() );
        assertSame ( rootNode, texNode.getParent() );


        rootNode = parser.parseString ( "[doc [child1 text 1 ] text 2 [- [- comment -] -][child3 text 3]]" );
        assertEquals ( 3, rootNode.getChildNodes().size() );

        BranchNode branchNode1 = (BranchNode) rootNode.getChildNodes().get ( 0 );
        assertEquals ( "child1", branchNode1.getName() );
        assertEquals ( 1, branchNode1.getChildNodes().size() );
        assertSame ( rootNode, branchNode1.getParent() );
        assertEquals ( "doc/child1", branchNode1.path() );
        TextNode childNode1Text = (TextNode) branchNode1.getChildNodes().get ( 0 );
        assertEquals ( "text 1 ", childNode1Text.getText() );
        assertSame ( branchNode1, childNode1Text.getParent() );

        TextNode childNode2 = (TextNode) rootNode.getChildNodes().get ( 1 );
        assertEquals ( " text 2 ", childNode2.getText() );
        assertSame ( rootNode, childNode2.getParent() );
    }
}

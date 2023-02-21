package dev.pdml.simpleparser.utils;

import dev.pdml.simpleparser.node.branch.BranchNode;
import dev.pdml.simpleparser.node.branch.RootOrBranchNode;
import dev.pdml.simpleparser.node.leaf.TextNode;
import dev.pdml.simpleparser.node.root.RootNode;
import dev.pdml.simpleparser.parser.PdmlParser;
import dev.pdml.simpleparser.reader.InvalidPdmlException;
import dev.pdml.simpleparser.utils.NodeUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeUtilsTest {

    @Test
    void forEachNodeInTree() throws IOException, InvalidPdmlException {

        PdmlParser parser = new PdmlParser();
        RootNode rootNode = parser.parseString ( """
            [root
                [child_1
                    [child_11]
                    [child_12 text_12]
                ]
                [child_2
                    [child_21
                        [child_211 text 211]text_21[child_213]
                    ]
                ]
            ]
            """ );

        StringBuilder names = new StringBuilder();
        NodeUtils.forEachNodeInTree ( rootNode, node -> {
            switch ( node ) {
                case RootNode rn -> names.append ( rn.getName () ).append ( ", " );
                case BranchNode bn -> names.append ( bn.getName () ).append ( ", " );
                case TextNode tn -> {}
                default -> throw new IllegalStateException ( "Unexpected value: " + node );
            }
        });
        assertEquals (
            "root, child_1, child_11, child_12, child_2, child_21, child_211, child_213, ",
            names.toString() );
    }


    @Test
    void forEachTextInTree() throws IOException, InvalidPdmlException {

        PdmlParser parser = new PdmlParser();
        RootNode rootNode = parser.parseString ( "[p This sentence contains [b bold] and [i italic] words.]" );

        StringBuilder fullText = new StringBuilder();
        NodeUtils.forEachTextInTree ( rootNode, fullText::append );
        assertEquals ( "This sentence contains bold and italic words.", fullText.toString() );
    }
}

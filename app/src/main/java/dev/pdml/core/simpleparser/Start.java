package dev.pdml.core.simpleparser;

import dev.pdml.core.simpleparser.data.TaggedNode;
import dev.pdml.core.simpleparser.data.Node;
import dev.pdml.core.simpleparser.data.TextLeaf;

import java.io.IOException;
import java.nio.file.Path;

public class Start {

    public static void main ( String[] args ) throws IOException, InvalidPdmlException {

        if ( args.length != 1 ) {
            System.err.println ( "Expecting a PDML file path as CLI argument." );
            System.exit ( 1 );
        }

        Path filePath = Path.of ( args[0] );
        TaggedNode rootNode = parse ( filePath );
        traverseTree ( rootNode );
    }

    private static TaggedNode parse ( Path filePath ) {

        try {
            CorePdmlParser parser = new CorePdmlParser();
            return parser.parse ( filePath );

        } catch ( IOException ioe ) {
            System.err.println ( "IO error: " + ioe.getMessage() );

        } catch ( InvalidPdmlException ipe ) {
            System.err.println ( "Error : " + ipe.getMessage() );
            System.err.println ( "Line  : " + ipe.getLineNumber() );
            System.err.println ( "Column: " + ipe.getColumnNumber() );
        }

        System.exit ( 1 );
        return null;
    }

    /**
     * Traverse a PDML tree and write the node tags and text leaves encountered to STDOUT.
     * @param branchNode the root node
     */
    private static void traverseTree ( TaggedNode branchNode ) {

        System.out.println ( "Tag: " + branchNode.tag() );

        for ( Node childNode : branchNode.childNodes() ) {
            if ( childNode instanceof TaggedNode taggedChild ) {
                traverseTree ( taggedChild );
            } else if ( childNode instanceof TextLeaf textChild ) {
                System.out.println ( "Text: <" + textChild.text() + ">" );
            }
        }
    }
}

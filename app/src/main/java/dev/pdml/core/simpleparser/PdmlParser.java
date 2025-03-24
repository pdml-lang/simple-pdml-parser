package dev.pdml.core.simpleparser;

import dev.pdml.core.simpleparser.data.TaggedNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PdmlParser {


    private PdmlReader reader;


    public PdmlParser() {}


    public TaggedNode parse ( Path filePath ) throws IOException, InvalidPdmlException {

        String code = Files.readString ( filePath, StandardCharsets.UTF_8 );
        return parse ( code );
    }

    public TaggedNode parse ( String PdmlCode ) throws InvalidPdmlException {

        reader = new PdmlReader ( PdmlCode );
        return parseRootNode();
    }


    // Private Methods

    private TaggedNode parseRootNode() throws InvalidPdmlException {

        reader.skipSpacesAndTabsAndLineBreaks();

        TaggedNode rootNode = parseTaggedNode ();
        if ( rootNode == null ) {
            errorDetected ( "Root node expected (e.g. \"[root\")" );
        }

        reader.skipSpacesAndTabsAndLineBreaks();
        if ( ! reader.isAtEnd () ) {
            errorDetected ( "No more text expected" );
        }

        return rootNode;
    }

    private TaggedNode parseTaggedNode() throws InvalidPdmlException {

        if ( ! reader.readNodeStart() ) return null;

        String tag = reader.readTag();
        if ( tag == null ) {
            errorDetected ( "Node tag required" );
        }

        TaggedNode taggedNode = new TaggedNode ( tag );

        if ( reader.readNodeEnd() ) {
            // it's an empty node
            return taggedNode;
        }

        requireSeparator();

        requireChildren ( taggedNode );

        return taggedNode;
    }

    private void requireSeparator() throws InvalidPdmlException {

        if ( ! reader.readSeparator() ) {
            errorDetected ( "Separator required" );
        }
    }

    private void requireChildren ( TaggedNode parentNode ) throws InvalidPdmlException {

        while ( ! reader.isAtEnd() ) {

            if ( reader.readNodeEnd() ) break;

            String text = reader.readText();
            if ( text != null ) {
                parentNode.appendText ( text );
            } else {
                TaggedNode childNode = parseTaggedNode ();
                if ( childNode != null ) {
                    parentNode.appendChild ( childNode );
                } else {
                    errorDetected ( "Invalid character" );
                }
            }
        }

        if ( parentNode.isLeaf () ) {
            errorDetected ( "Child nodes required" );
        }
    }

    private void errorDetected ( String message ) throws InvalidPdmlException {

        String position =
            reader.isAtEnd () ?
                " at the end of document." :
                " at position " + (reader.currentPosition() + 1) + ".";
        throw new InvalidPdmlException ( message + position );
    }
}

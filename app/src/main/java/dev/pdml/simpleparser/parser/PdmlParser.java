package dev.pdml.simpleparser.parser;

import dev.pdml.simpleparser.annotations.NotNull;
import dev.pdml.simpleparser.annotations.Nullable;
import dev.pdml.simpleparser.node.branch.BranchNode;
import dev.pdml.simpleparser.node.branch.RootOrBranchNode;
import dev.pdml.simpleparser.node.leaf.TextNode;
import dev.pdml.simpleparser.node.root.RootNode;
import dev.pdml.simpleparser.reader.InvalidPdmlException;
import dev.pdml.simpleparser.reader.PdmlReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PdmlParser {


    private @NotNull PdmlReader reader = PdmlReader.forString ( "[d]" );
    private @Nullable String resourceName = null;


    /**
     * Create a PDML parser
     */
    public PdmlParser () {}


    /**
     * Parse a PDML document read from a Java <code>Reader</code>.
     * @param reader the Java <code>Reader</code> from which the PDML document is read
     * @param resourceName the name of the resource that contains the PDML document (used in error messages)
     * @return a PDML root node
     * @throws IOException if an IO exception occurs
     * @throws InvalidPdmlException if the PDML document is invalid
     */
    public @NotNull RootNode parse (
        @NotNull Reader reader,
        @Nullable String resourceName ) throws IOException, InvalidPdmlException {

        this.reader = new PdmlReader ( reader, resourceName );
        this.resourceName = resourceName;

        return parse();
    }

    /**
     * Parse a PDML document read from a Java <code>Reader</code>.
     * @param reader the Java <code>Reader</code> from which the PDML document is read
     * @return a PDML root node
     * @throws IOException if an IO exception occurs
     * @throws InvalidPdmlException if the PDML document is invalid
     */
    public @NotNull RootNode parse ( @NotNull Reader reader ) throws IOException, InvalidPdmlException {

        return parse ( reader, null );
    }

    /**
     * Parse a PDML document stored in a UTF-8 encoded file.
     * @param filePath the file path. If a relative path is supplied, it is relative to the current working directory.
     * @return a PDML root node
     * @throws IOException if an IO exception occurs
     * @throws InvalidPdmlException if the PDML document is invalid
     */
    public @NotNull RootNode parseFile ( @NotNull Path filePath ) throws IOException, InvalidPdmlException {

        if ( ! Files.exists (filePath) ) throw new IOException ( "File '" + filePath + "' does not exist." );

        try ( Reader fileReader = new FileReader ( filePath.toFile(), StandardCharsets.UTF_8 ) ) {
            return parse ( fileReader, filePath.toString() );
        }
    }

    /**
     * Parse a PDML document stored in a string.
     * @param string a string that contains a PDML document
     * @return a PDML root node
     * @throws IOException if an IO exception occurs
     * @throws InvalidPdmlException if the PDML document is invalid
     */
    public @NotNull RootNode parseString ( @NotNull String string ) throws IOException, InvalidPdmlException {

        try ( Reader stringReader = new StringReader ( string ) ) {
            return parse ( stringReader );
        }
    }


    private @NotNull RootNode parse() throws IOException, InvalidPdmlException {

        reader.skipSpacesAndTabsAndNewlines();

        RootNode node = (RootNode) parseNode ( null );
        if ( node == null ) throwInvalidPDMLException ( "A PDML document must start with a node (e.g. \"[root\")." );

        reader.skipSpacesAndTabsAndNewlines();

        if ( reader.hasChar () ) throwInvalidPDMLException ( "No more text expected." );

        return node;
    }

    private @Nullable RootOrBranchNode parseNode ( @Nullable RootOrBranchNode parent ) throws IOException, InvalidPdmlException {

        if ( ! reader.readNodeStart() ) return null;

        String name = reader.readNodeName();
        if ( name == null ) throwInvalidPDMLException ( "Valid node name expected." );
        requireNameValueSeparator();

        RootOrBranchNode result = parent == null ? new RootNode ( name ) : new BranchNode ( name, parent );

        parseChildren ( result );

        if ( ! reader.readNodeEnd() ) throwInvalidPDMLException ( "Node end expected." );

        return result;
    }

    private void parseChildren ( @NotNull RootOrBranchNode parent ) throws IOException, InvalidPdmlException {

        while ( reader.hasChar () && ! reader.isAtNodeEnd() ) {

            reader.skipComments();

            String text = reader.readNodeText();
            if ( text != null ) {
                parent.appendChild ( new TextNode ( text, parent ) );
            } else {
                BranchNode branchNode = (BranchNode) parseNode ( parent );
                if ( branchNode != null ) {
                    parent.appendChild ( branchNode );
                } else {
                    throwInvalidPDMLException ( "Invalid character." );
                }
            }
        }
    }

    private void requireNameValueSeparator() throws IOException, InvalidPdmlException {

        if ( reader.isAtNodeStart() ||
            reader.isAtNodeEnd() ||
            reader.skipSpaceOrTabOrNewline() ) return;

        if ( reader.hasChar () ) {
            throwInvalidPDMLException ( "Illegal character." );
        } else {
            throwInvalidPDMLException ( "More text expected." );
        }
    }

    private void throwInvalidPDMLException ( @NotNull String errorMessage ) throws InvalidPdmlException {

        throw new InvalidPdmlException (
            errorMessage, resourceName, reader.currentLineNumber (), reader.currentColumnNumber () );
    }
}

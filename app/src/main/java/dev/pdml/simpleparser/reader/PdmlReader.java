package dev.pdml.simpleparser.reader;

import dev.pdml.simpleparser.annotations.NotNull;
import dev.pdml.simpleparser.annotations.Nullable;
import dev.pdml.simpleparser.PdmlConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.NoSuchElementException;

/**
 * A character-based PDML reader.
 *
 * <p>Besides reading <i>Core PDML</i>, the following extensions are supported:</p>
 * <ul>
 * <li>Comments</li>
 * <li>Character escape sequences</li>
 * </ul>
 * @author Christian Neumanns
 */
public class PdmlReader {

    private interface CharPredicate {
        boolean accept ( char c );
    }


    private final @NotNull Reader reader;
    private final @Nullable String resourceName;

    private boolean hasChar;
    private char currentChar;

    private long currentLineNumber;
    private long currentColumnNumber;


    /**
     * @param reader the character reader from which PDML code is read
     * @param resourceName the name of the resource that contains the PDML code (used in error messages)
     */
    public PdmlReader ( @NotNull Reader reader, @Nullable String resourceName ) throws IOException {

        Reader readerWithMarkSupport = reader.markSupported() ? reader : new BufferedReader ( reader );
        this.reader = readerWithMarkSupport;
        this.resourceName = resourceName;

        this.hasChar = true;
        this.currentChar = 0;

        this.currentLineNumber = 1;
        this.currentColumnNumber = 0;

        advanceChar();
    }

    /**
     * @param reader the character reader from which PDML code is read
     */
    public PdmlReader ( @NotNull Reader reader ) throws IOException {
        this ( reader, null );
    }

    /**
     * A utility method to create a reader for a given string<br>
     *
     * Note:<br>
     * This method creates a <code>StringReader</code> that will not be closed in the source code.<br>
     * Should therefore not be used in production code.
     * @param PDMLCode the PDML code to be read
     */
    public static PdmlReader forString ( @NotNull String PDMLCode ) {

        Reader stringReader = new StringReader ( PDMLCode );
        try {
            return new PdmlReader ( stringReader );
        } catch ( IOException e ) {
            throw new RuntimeException ( "Should never happen." );
        }
    }


    // Reader State

    /**
     * Check if the reader is positioned at a character, or is at the end of input
     * @return <code>true</code> if the reader is positioned at a character, <code>false</code> if all characters have been read already
     */
    public boolean hasChar() { return hasChar; }

    /**
     * Get the character at which the reader is currently positioned.
     * @return the current character (0 if all characters have been read already)
     */
    public char currentChar() { return currentChar; }

    /**
     * Line number at which the reader is currently positioned. Line numbers start with 1.
     * @return current line number
     */
    public long currentLineNumber () { return currentLineNumber; }

    /**
     * Column number at which the reader is currently positioned. Column numbers start with 1.
     * @return current column number
     */
    public long currentColumnNumber () { return currentColumnNumber; }


    // Node

    /**
     * Check if the reader is positioned at a node start symbol
     * @return <code>true</code> if the reader is positioned at a node start symbol, else <code>false</code>
     */
    public boolean isAtNodeStart() {
        return isAtChar ( PdmlConstants.NODE_START );
    }

    /**
     * Check if the reader is positioned at a node end symbol
     * @return <code>true</code> if the reader is positioned at a node end symbol, else <code>false</code>
     */
    public boolean isAtNodeEnd() {
        return isAtChar ( PdmlConstants.NODE_END );
    }

    /**
     * Read (consume) a node start symbol
     * @return <code>true</code> if the reader consumed a node start symbol, else <code>false</code>
     */
    public boolean readNodeStart() throws IOException {
        return acceptChar ( PdmlConstants.NODE_START );
    }

    /**
     * Read (consume) a node end symbol
     * @return <code>true</code> if the reader consumed a node end symbol, else <code>false</code>
     */
    public boolean readNodeEnd() throws IOException {
        return acceptChar ( PdmlConstants.NODE_END );
    }


    // Node Name

    /**
     * Read a node name
     * @return the node name if the reader is positioned at a node name, else <code>null</code> is returned
     */
    public @Nullable String readNodeName() throws IOException {

        if ( ! isValidFirstCharOfName ( currentChar ) ) return null;

        final StringBuilder sb = new StringBuilder();
        sb.append ( currentChar );
        advanceChar();

        appendWhile ( this::isValidCharOfName, sb );

        return sb.toString();
    }

    private boolean isValidFirstCharOfName ( char c ) {

        return ( ( c >= 'A' && c <= 'Z' )
            || ( c >= 'a' && c <= 'z' )
            || c == '_' );
    }

    private boolean isValidCharOfName ( char c ) {

        return isValidFirstCharOfName ( c )
            || c >= '0' && c <= '9'
            || c == '-'
            || c == '.';
    }


    // Node Text

    /**
     * Read text contained in a node
     * @return a string holding the text, or <code>null</code> if the reader is not positioned at text
     */
    public @Nullable String readNodeText() throws IOException, InvalidPdmlException {

        final StringBuilder result = new StringBuilder();

        while ( hasChar ) {

            if ( isAtNodeStart() || isAtNodeEnd() ) break;

            if ( currentChar == PdmlConstants.ESCAPE_CHAR ) {
                appendEscapedCharacter ( result );
            } else {
                result.append ( currentChar );
            }

            advanceChar();
        }

        return result.length() == 0 ? null : result.toString();
    }

    private void appendEscapedCharacter ( StringBuilder result )
        throws IOException, InvalidPdmlException {

        // now positioned at the \ of \u1234

        advanceChar(); // consume \

        if ( ! hasChar ) throwInvalidPDMLException (
            "Expecting another character after the escape character '" + PdmlConstants.ESCAPE_CHAR +
                "' at the end of the document." );

        switch ( currentChar ) {
            case PdmlConstants.NODE_START , PdmlConstants.NODE_END, PdmlConstants.ESCAPE_CHAR -> result.append ( currentChar );
            case 't' -> result.append ( '\t' );
            case 'r' -> result.append ( '\r' );
            case 'n' -> result.append ( '\n' );
            case 'u' -> appendUnicodeEscapeSequence ( result, 4 );
            case 'U' -> appendUnicodeEscapeSequence ( result, 8 );

            default -> throwInvalidPDMLException (
                "Invalid character escape sequence \"" + PdmlConstants.ESCAPE_CHAR + currentChar + "\"." );
        }
    }

    private void appendUnicodeEscapeSequence ( StringBuilder result, int hexCount ) throws IOException, InvalidPdmlException {

        // now positioned at the u of \u1234

        StringBuilder hexSb = new StringBuilder();
        for ( int i = 1; i <= hexCount; i++ ) {
            advanceChar();
            if ( ! hasChar ) throwInvalidPDMLException (
                "Expecting " + hexCount + " hex digits to define a Unicode escape sequence. But found only " + (i - 1) + "." );
            char hexChar = requireHexChar ( currentChar );
            hexSb.append ( hexChar );
        }

        // NumberFormatException should never happen because the validity has been checked already
        try {
            int codePoint = Integer.parseInt( hexSb.toString(), 16 );
            result.appendCodePoint ( codePoint );
        } catch ( NumberFormatException e ) {
            throw new RuntimeException ( e );
        }
    }

    private char requireHexChar ( char c ) throws InvalidPdmlException {

        if ( ( c >= '0' && c <= '9' )
            || ( c >= 'a' && c <= 'f' )
            || ( c >= 'A' && c <= 'F' ) ) {
            return c;
        } else {
            throwInvalidPDMLException (
                "Invalid hexadecimal character '" + c + "'. Only 0..9, a..f, and A..F are allowed." );
            return '0';
        }
    }


    // Comment

    /**
     * Read a PDML comment
     * @return a string holding the comment, or <code>null</code> if the reader is not positioned at a comment
     */
    public @Nullable String readComment() throws IOException, InvalidPdmlException {

        if ( ! isAtCommentStart() ) return null;

        StringBuilder result = new StringBuilder();
        readCommentSnippet ( result );

        return result.toString();
    }

    /**
     * Skip a sequence of comments
     */
    public void skipComments() throws IOException, InvalidPdmlException {

        while ( skipComment() ) {}
    }

    /**
     * Skip a single comment
     * @return <code>true</code> if a comment has been skipped, otherwise <code>false</code>
     */
    public boolean skipComment() throws IOException, InvalidPdmlException {

        // Can be made faster by writing a specific version that doesn't use a StringBuilder
        // to build and return the comment's content
        return readComment() != null;
    }

    private void readCommentSnippet ( @NotNull StringBuilder result ) throws IOException, InvalidPdmlException {

        long startLine = currentLineNumber;
        long startColumn = currentColumnNumber;

        // we are at the start of a comment
        consumeCommentStartOrEnd ( result );

        while ( true ) {

            if ( ! hasChar ) throwInvalidPDMLException (
                "The comment starting at line " + startLine + ", column " + startColumn + " is never closed." );

            if ( isAtCommentEnd() ) {
                consumeCommentStartOrEnd ( result );
                return;

            } else if ( isAtCommentStart() ) {
                readCommentSnippet ( result ); // recursive call for nested comments

            } else {
                result.append ( currentChar );
                if ( hasChar ) advanceChar();
            }
        }
    }

    private void consumeCommentStartOrEnd ( @NotNull StringBuilder sb ) throws IOException {

        sb.append ( currentChar );
        advanceChar ();
        sb.append ( currentChar );
        advanceChar ();
    }

    private boolean isAtCommentStart() throws IOException {

        if ( ! isAtNodeStart() ) return false;
        return isNextChar ( PdmlConstants.COMMENT_SYMBOL );
    }

    private boolean isAtCommentEnd() throws IOException {

        if ( ! isAtChar ( PdmlConstants.COMMENT_SYMBOL ) ) return false;
        return isNextChar ( PdmlConstants.NODE_END );
    }


    // Whitespace

    /**
     * Skip a single whitespace character (space, tab, or new line)
     * @return <code>true</code> if a whitespace character has been skipped, otherwise <code>false</code>
     */
    public boolean skipSpaceOrTabOrNewline() throws IOException {

        if ( ! hasChar ) return false;

        if  ( ! isAtSpaceOrTabOrNewline() ) return false;

        boolean isWindowsNewline = currentChar == '\r';
        advanceChar();
        if ( isWindowsNewline && currentChar == '\n' ) advanceChar();
        return true;
    }

    /**
     * Skip a sequence of whitespace characters (spaces, tabs, and new lines)
     * @return <code>true</code> if a whitespace sequence has been skipped, otherwise <code>false</code>
     */
    public boolean skipSpacesAndTabsAndNewlines() throws IOException {

        if  ( ! skipSpaceOrTabOrNewline() ) return false;
        while ( skipSpaceOrTabOrNewline() ) {}
        return true;
    }

    /**
     * Check if the reader is positioned at whitespace (space, tab, or new line)
     * @return <code>true</code> if the reader is positioned at whitespace, otherwise <code>false</code>
     */
    private boolean isAtSpaceOrTabOrNewline() {

        return switch ( currentChar ) {
            case ' ', '\t', '\n', '\r' -> true;
            default -> false;
        };
    }


    // Reader utilities

    private void appendWhile ( @NotNull CharPredicate predicate, @NotNull StringBuilder stringBuilder )
        throws IOException {

        while ( hasChar ) {
            if ( ! predicate.accept ( currentChar ) ) return;
            stringBuilder.append ( currentChar );
            advanceChar();
        }
    }

    private void advanceChar() throws IOException {

        assert checkHasChar();

        if ( currentChar == '\n' ) {
            currentLineNumber += 1;
            currentColumnNumber = 1;
        } else {
            currentColumnNumber += 1;
        }

        int nextInt = reader.read();
        if ( nextInt == -1 ) {
            hasChar = false;
            currentChar = 0;
        } else {
            hasChar = true;
            currentChar = (char) nextInt;
        }
    }

    private boolean checkHasChar() {

        if ( hasChar ) {
            return true;
        } else {
            throw new NoSuchElementException (
                "There are no more characters to read at position " + currentLineNumber + ":" + currentColumnNumber + "." );
        }
    }

    private boolean acceptChar ( char c ) throws IOException {

        if ( currentChar == c ) {
            advanceChar();
            return true;
        } else {
            return false;
        }
    }

    private boolean isAtChar ( char c ) { return currentChar == c; }

    private boolean isNextChar ( char c ) throws IOException { return peekNextChar() == c; }

    private char peekNextChar() throws IOException {

        if ( ! hasChar ) return 0;

        reader.mark ( 1 );
        int peekedInt = reader.read();
        reader.reset();

        if ( peekedInt == -1 ) {
            return 0;
        } else {
            return (char) peekedInt;
        }
    }


    // Error handling

    private void throwInvalidPDMLException ( @NotNull String errorMessage ) throws InvalidPdmlException {

        throw new InvalidPdmlException ( errorMessage, resourceName, currentLineNumber, currentColumnNumber );
    }
}

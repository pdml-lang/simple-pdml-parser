package dev.pdml.core.simpleparser;

import java.util.Map;
import java.util.Set;

public class CorePdmlReader {

    private final String PdmlCode;
    private char currentChar;
    private int currentPosition;     // starts at 0 (not 1)
    private int currentLineNumber;   // starts at 1 (not 0)
    private int currentColumnNumber; // starts at 1 (not 0)


    public CorePdmlReader ( String PdmlCode ) {

        this.PdmlCode = PdmlCode;
        this.currentChar = 0;
        this.currentPosition = -1;
        this.currentLineNumber = 1;
        this.currentColumnNumber = 0;

        advanceChar();
    }


    public int currentPosition() { return currentPosition; }

    public int currentLineNumber() { return currentLineNumber; }

    public int currentColumnNumber() { return currentColumnNumber; }

    public boolean isAtEnd() { return currentPosition >= PdmlCode.length(); }


    public boolean readNodeStart() { return acceptChar ( CorePdmlConstants.NODE_START_CHAR ); }

    public boolean readNodeEnd() { return acceptChar ( CorePdmlConstants.NODE_END_CHAR ); }

    public String readTag() throws InvalidPdmlException {
        return readTagOrText (
            CorePdmlConstants.TAG_END_CHARS, CorePdmlConstants.INVALID_TAG_CHARS, CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CHARS );
    }

    public String readText() throws InvalidPdmlException {
        return readTagOrText (
            CorePdmlConstants.TEXT_END_CHARS, CorePdmlConstants.INVALID_TEXT_CHARS, CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CHARS );
    }

    public boolean readSeparator() {

        boolean isWindowsNewline = currentChar == '\r';
        if ( isWhiteSpaceChar ( currentChar ) ) {
            advanceChar();
            if ( isWindowsNewline ) advanceChar(); // \r\n
            return true;
        } else {
            return false;
        }
    }

    public void skipWhitespace() {

        while ( ! isAtEnd () && isWhiteSpaceChar ( currentChar ) ) {
            advanceChar();
        }
    }


    // Private Methods

    private String readTagOrText (
        Set<Character> endChars,
        Set<Character> invalidChars,
        Map<Character, Character> escapeChars ) throws InvalidPdmlException {

        final StringBuilder result = new StringBuilder();

        while ( true ) {

            if ( isAtEnd() ) {
                break;

            } else if ( endChars.contains ( currentChar ) ) {
                break;

            } else if ( invalidChars.contains ( currentChar ) ) {
                errorDetected ( "Character '" + currentChar + "' is not allowed in this context." );

            } else if ( currentChar <= 0X001F &&
                currentChar != '\n' && currentChar != '\r' && currentChar != '\t' && currentChar != '\f' ) {
                errorDetected ( "Unicode code points below U+001F (control characters) are not allowed, except U+0009 (Character Tabulation), U+000A (End of Line), U+000C (Form Feed), and U+000D (Carriage Return)." );

            } else if ( currentChar >= 0X0080 && currentChar <= 0X009F ) {
                errorDetected ( "Unicode code points in the range U+0080 to U+009F (control characters) are not allowed." );

            /*
                This doesn't work because Java uses UTF-16 to store strings in memory
                Each char in Java is a 16-bit (2-byte) code unit, which follows UTF-16 encoding rules.
                } else if ( currentChar >= 0XD800 && currentChar <= 0XDFFF ) {
                    errorDetected ( "Unicode code points in the range U+D800 to U+DFFF are not allowed (they are surrogates reserved to encode code points beyond U+FFFF in UTF-16)." );
             */

            } else if ( currentChar == CorePdmlConstants.ESCAPE_CHAR ) {
                appendEscapedCharacter ( result, escapeChars );

            } else {
                result.append ( currentChar );
            }

            advanceChar();
        }

        return result.isEmpty() ? null : result.toString();
    }

    private void appendEscapedCharacter (
        StringBuilder result,
        Map<Character, Character> escapeChars ) throws InvalidPdmlException {

        // now positioned at '\'
        advanceChar(); // consume \

        if ( isAtEnd () ) {
            errorDetected (
                "Expecting another character after the escape character '" + CorePdmlConstants.ESCAPE_CHAR + "'." );
        }

        Character escapedChar = escapeChars.get ( currentChar );
        if ( escapedChar != null ) {
            result.append ( escapedChar );
        } else {
            errorDetected ( "Invalid character escape sequence \"" +
                CorePdmlConstants.ESCAPE_CHAR + currentChar + "\"" );
        }
    }

    private static boolean isWhiteSpaceChar ( char c ) {

        return c == ' ' ||
            c == '\n' ||
            c == '\r' ||
            c == '\t' ||
            c == '\f';
    }

    private boolean acceptChar ( char c ) {

        if ( currentChar == c ) {
            advanceChar();
            return true;
        } else {
            return false;
        }
    }

    private void advanceChar() {

        currentPosition++;

        if ( currentChar == '\n' ) {
            currentLineNumber += 1;
            currentColumnNumber = 1;
        } else {
            currentColumnNumber += 1;
        }

        if ( currentPosition < PdmlCode.length() ) {
            currentChar = PdmlCode.charAt ( currentPosition );
        } else {
            currentChar = 0;
        }
    }

    private void errorDetected ( String message ) throws InvalidPdmlException {
        throw new InvalidPdmlException ( message, currentLineNumber, currentColumnNumber );
    }
}

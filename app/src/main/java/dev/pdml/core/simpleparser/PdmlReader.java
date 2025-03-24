package dev.pdml.core.simpleparser;

import java.util.Map;
import java.util.Set;

public class PdmlReader {

    private final String PdmlCode;
    private int currentPosition; // starts at 0 (not 1)
    private char currentChar;


    public PdmlReader ( String PdmlCode ) {

        this.PdmlCode = PdmlCode;
        this.currentPosition = -1;
        this.currentChar = 0;

        advanceChar();
    }


    public int currentPosition() { return currentPosition; }

    public boolean isAtEnd() { return currentPosition >= PdmlCode.length(); }

    public boolean readNodeStart() { return acceptChar ( PdmlConstants.NODE_START ); }

    public boolean readNodeEnd() { return acceptChar ( PdmlConstants.NODE_END ); }

    public String readTag() throws InvalidPdmlException {
        return readTagOrText ( PdmlConstants.INVALID_TAG_CHARS, PdmlConstants.TAG_ESCAPE_CHARS );
    }

    public String readText() throws InvalidPdmlException {
        return readTagOrText ( PdmlConstants.INVALID_TEXT_CHARS, PdmlConstants.TEXT_ESCAPE_CHARS );
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

    public void skipSpacesAndTabsAndLineBreaks() {

        while ( ! isAtEnd () && isWhiteSpaceChar ( currentChar ) ) {
            advanceChar();
        }
    }


    // Private Methods

    private String readTagOrText (
        Set<Character> invalidChars,
        Map<Character, Character> escapeChars ) throws InvalidPdmlException {

        final StringBuilder result = new StringBuilder();

        while ( ! isAtEnd() && ! invalidChars.contains ( currentChar ) ) {

            if ( currentChar == PdmlConstants.ESCAPE_CHAR ) {
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
                "Expecting another character after the escape character '" + PdmlConstants.ESCAPE_CHAR );
        }

        Character escapedChar = escapeChars.get ( currentChar );
        if ( escapedChar != null ) {
            result.append ( escapedChar );
        } else {
            errorDetected ( "Invalid character escape sequence \"" +
                PdmlConstants.ESCAPE_CHAR + currentChar + "\"" );
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

        if ( currentPosition < PdmlCode.length() ) {
            currentChar = PdmlCode.charAt ( currentPosition );
        } else {
            currentChar = 0;
        }
    }

    private void errorDetected ( String message ) throws InvalidPdmlException {

        String position =
            isAtEnd () ?
                " at the end of document." :
                " at position " + (currentPosition + 1) + ".";
        throw new InvalidPdmlException ( message + position );
    }
}

package dev.pdml.core.simpleparser;

public class InvalidPdmlException extends Exception {

    private final int lineNumber;    // starts at 1 (not 0)
    public int getLineNumber() { return lineNumber; }

    private final int columnNumber; // starts at 1 (not 0)
    public int getColumnNumber() { return columnNumber; }


    public InvalidPdmlException (
        String message,
        int lineNumber,
        int columnNumber ) {

        super ( message );

        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    @Override
    public String toString() {
        return "Error at line " + lineNumber + ", column " + columnNumber + ": " + getMessage();
    }
}

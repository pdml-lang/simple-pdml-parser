package dev.pdml.simpleparser.reader;

import dev.pdml.simpleparser.annotations.NotNull;
import dev.pdml.simpleparser.annotations.Nullable;

public class InvalidPdmlException extends Exception {

    private final @Nullable String resourceName;

    private final @Nullable Long lineNumber;
    private final @Nullable Long columnNumber;


    /**
     *
     * @param message the error message
     * @param resourceName the name of the resource in which the error was detected
     * @param lineNumber the line number (1-based)
     * @param columnNumber the column number (1-based)
     */
    public InvalidPdmlException (
        @NotNull String message,
        @Nullable String resourceName,
        @Nullable Long lineNumber,
        @Nullable Long columnNumber ) {

        super ( message );

        this.resourceName = resourceName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }


    /**
     * Get the name of the resource in which the error was detected
     * @return resource name
     */
    public @Nullable String getResourceName() { return resourceName; }

    /**
     * Get the line number in which the error was detected
     * @return line number (first line is 1)
     */
    public @Nullable Long getLineNumber() { return lineNumber; }

    /**
     * Get the column number in which the error was detected
     * @return column number (first column is 1)
     */
    public @Nullable Long getColumnNumber() { return columnNumber; }

    @Override
    public @NotNull String toString() {

        StringBuilder sb = new StringBuilder();

        if ( resourceName != null ) {
            sb.append ( resourceName );
            sb.append ( ' ' );
        }

        if ( lineNumber != null ) {
            sb.append ( lineNumber );
            sb.append ( ',' );
        }

        if ( columnNumber != null ) {
            sb.append ( columnNumber );
            sb.append ( ' ' );
        }

        sb.append ( getMessage() );

        return sb.toString();
    }
}

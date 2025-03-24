package dev.pdml.core.simpleparser;

import java.util.*;

public class PdmlConstants {

    public static final char NODE_START = '[';
    public static final char NODE_END = ']';

    public static final char ESCAPE_CHAR = '\\';

    public static final Set<Character> INVALID_TAG_CHARS =
        Set.of (
            '[', ']',
            ' ', '\t', '\n', '\r',
            '*', '@', '=', '(', ')', '"', '~', ':', '/', ',', '`', '!', '$' );

    public static final Set<Character> INVALID_TEXT_CHARS =
        Set.of ( '[', ']' );

    public static final Map<Character, Character> TAG_ESCAPE_CHARS = createTagEscapeMap ();
    private static Map<Character, Character> createTagEscapeMap() {

        Map<Character, Character> map = new HashMap<>();
        map.put ( '\\', '\\' );
        map.put ( 's', ' ' );
        map.put ( 't', '\t' );
        map.put ( 'n', '\n' );
        map.put ( 'r', '\r' );

        for ( Character c : INVALID_TAG_CHARS ) {
            if ( c != ' '
                && c != '\t'
                && c != '\n'
                && c != '\r') {

                map.put ( c, c );
            }
        }

        return Collections.unmodifiableMap ( map );
    }

    public static final Map<Character, Character> TEXT_ESCAPE_CHARS = Map.of (
        '\\', '\\',
        '[', '[',
        ']', ']',
        't', '\t',
        'n', '\n',
        'r', '\r' );
}

package dev.pdml.core.simpleparser.data;

import java.util.ArrayList;
import java.util.List;

public record TaggedNode(
    String tag,
    List<Node> childNodes ) implements Node {


    public TaggedNode ( String tag ) {
        this ( tag, new ArrayList<>() );
    }


    public boolean isLeaf() { return childNodes.isEmpty(); }

    public void appendChild ( Node childNode ) {
        childNodes.add ( childNode );
    }

    public void appendText ( String text ) {
        childNodes.add ( new TextLeaf ( text ) );
    }

    @Override
    public String toString() { return tag; }
}

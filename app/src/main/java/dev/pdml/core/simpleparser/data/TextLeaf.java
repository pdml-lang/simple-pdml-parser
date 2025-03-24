package dev.pdml.core.simpleparser.data;

public record TextLeaf (
    String text) implements Node {

    @Override
    public String toString() { return text; }
}

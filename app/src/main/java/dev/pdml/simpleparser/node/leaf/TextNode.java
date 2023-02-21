package dev.pdml.simpleparser.node.leaf;

import dev.pdml.simpleparser.annotations.NotNull;
import dev.pdml.simpleparser.node.branch.RootOrBranchNode;
import dev.pdml.simpleparser.node.branch.ChildNode;

/**
 * A PDML text node
 */
public class TextNode implements ChildNode {

    private final @NotNull RootOrBranchNode parent;
    public @NotNull RootOrBranchNode getParent() { return parent; }

    private final @NotNull String text;
    /**
     * Get the text of this node
     * @return the text of this node
     */
    public @NotNull String getText() { return text; }


    /**
     * Create a PDML text node
     * @param text the text of the node
     * @param parent the parent node
     */
    public TextNode (
        @NotNull String text,
        @NotNull RootOrBranchNode parent ) {

        this.text = text;
        this.parent = parent;
    }
}

package dev.pdml.simpleparser.node.branch;

import dev.pdml.simpleparser.annotations.NotNull;
import dev.pdml.simpleparser.node.PdmlNode;
import dev.pdml.simpleparser.node.root.RootNode;

/**
 * A PDML child node
 */
public interface ChildNode extends PdmlNode {

    /**
     * Get the parent node of this node
     * @return the parent node
     */
    @NotNull RootOrBranchNode getParent();

    /**
     * Get the root node of this node
     * @return the root node
     */
    default @NotNull RootNode root() {

        return switch ( getParent() ) {
            case RootNode r -> r;
            case BranchNode c -> c.root();
            default -> throw new IllegalStateException ( "Unexpected value: " + getParent () );
        };
    }
}

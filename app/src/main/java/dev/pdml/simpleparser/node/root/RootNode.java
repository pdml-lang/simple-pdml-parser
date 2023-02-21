package dev.pdml.simpleparser.node.root;

import dev.pdml.simpleparser.annotations.NotNull;
import dev.pdml.simpleparser.node.branch.RootOrBranchNode;
import dev.pdml.simpleparser.node.branch.ChildNode;

import java.util.ArrayList;
import java.util.List;

/**
 * A PDML root node
 */
public class RootNode extends RootOrBranchNode {


    /**
     * Create a PDML root node
     * @param name the node name
     * @param childNodes the child nodes
     */
    public RootNode (
        @NotNull String name,
        @NotNull List<ChildNode> childNodes ) {

        super ( name, childNodes );
    }

    /**
     * Create a PDML root node with no child nodes
     * @param name the node name
     */
    public RootNode ( @NotNull String name ) {
        this ( name, new ArrayList<>() );
    }


    public boolean isRoot() { return true; }

    public boolean isBranch() { return false; }

    public @NotNull List<String> pathNames() { return List.of ( name); }
}

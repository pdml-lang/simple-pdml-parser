package dev.pdml.simpleparser.node.branch;

import dev.pdml.simpleparser.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A PDML branch node
 */
public class BranchNode extends RootOrBranchNode implements ChildNode {


    @NotNull RootOrBranchNode parent;
    public @NotNull RootOrBranchNode getParent() { return parent; }


    /**
     * Create a PDML branch node
     * @param name the node name
     * @param childNodes the child nodes
     * @param parent the parent nodes
     */
    public BranchNode (
        @NotNull String name,
        @NotNull List<ChildNode> childNodes,
        @NotNull RootOrBranchNode parent ) {

        super ( name, childNodes );
        this.parent = parent;
    }

    /**
     * Create a PDML branch node with no child nodes
     * @param name the node name
     * @param parent the parent nodes
     */
    public BranchNode (
        @NotNull String name,
        @NotNull RootOrBranchNode parent ) {

        this ( name, new ArrayList<>(), parent );
    }


    public boolean isRoot() { return false; }

    public boolean isBranch() { return true; }

    public @NotNull List<String> pathNames() {

        List<String> names = new ArrayList<> ( parent.pathNames() );
        names.add ( name );
        return names;
    }
}

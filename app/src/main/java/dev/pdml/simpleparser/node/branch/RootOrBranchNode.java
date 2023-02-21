package dev.pdml.simpleparser.node.branch;

import dev.pdml.simpleparser.PdmlConstants;
import dev.pdml.simpleparser.annotations.NotNull;
import dev.pdml.simpleparser.node.PdmlNode;
import dev.pdml.simpleparser.node.leaf.TextNode;

import java.util.ArrayList;
import java.util.List;

/**
 * A PDML root or branch node
 */
public abstract class RootOrBranchNode implements PdmlNode {


    protected final @NotNull String name;

    /**
     * Get the node name
     *
     * @return node name
     */
    public @NotNull String getName() { return name; }

    protected final @NotNull List<ChildNode> childNodes;

    /**
     * Get the child nodes
     *
     * @return child nodes
     */
    public @NotNull List<ChildNode> getChildNodes() { return childNodes; }


    protected RootOrBranchNode (
        @NotNull String name,
        @NotNull List<ChildNode> childNodes ) {

        this.name = name;
        this.childNodes = childNodes;
    }


    /**
     * Add a child node at the end of the child nodes
     *
     * @param childNode the child node to append
     */
    public void appendChild ( @NotNull ChildNode childNode ) {
        childNodes.add ( childNode );
    }

    /**
     * Add a branch node at the end of the child nodes
     *
     * @param branchName the name of the branch node to append
     * @param branchChildren the child nodes of the branch node to append
     */
    public void appendBranch (
        @NotNull String branchName,
        @NotNull List<ChildNode> branchChildren ) {
        appendChild ( new BranchNode ( branchName, branchChildren, this ) );
    }

    /**
     * Add a branch node with no children at the end of the child nodes
     *
     * @param branchName the name of the branch node to append
     */
    public void appendBranch (
        @NotNull String branchName ) {
        appendBranch ( branchName, new ArrayList<>() );
    }

    /**
     * Add a text node at the end of the child nodes
     *
     * @param text the text to be appended
     */
    public void appendText ( @NotNull String text ) {
        appendChild ( new TextNode ( text, this ) );
    }

    /**
     * Check if this node is empty
     *
     * @return <code>true</code> if this node is empty, otherwise <code>false</code>
     */
    public boolean isEmpty() {
        return childNodes.isEmpty();
    }

    /**
     * Check if this is a root node
     *
     * @return <code>true</code> if this is a root node, otherwise <code>false</code>
     */
    public abstract boolean isRoot();

    /**
     * Check if this is a branch node
     *
     * @return <code>true</code> if this is a branch node, otherwise <code>false</code>
     */
    public abstract boolean isBranch();

    /**
     * @return the list of names in the path, starting from the root node
     */
    public abstract @NotNull List<String> pathNames();

    /**
     * @return the path of names, starting from the root node, separated by a slash
     */
    public @NotNull String path () {
        return String.join ( String.valueOf ( PdmlConstants.PATH_SEPARATOR ), pathNames() );
    }
}

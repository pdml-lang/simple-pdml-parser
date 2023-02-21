package dev.pdml.simpleparser.utils;

import dev.pdml.simpleparser.annotations.NotNull;
import dev.pdml.simpleparser.node.PdmlNode;
import dev.pdml.simpleparser.node.branch.BranchNode;
import dev.pdml.simpleparser.node.branch.ChildNode;
import dev.pdml.simpleparser.node.branch.RootOrBranchNode;
import dev.pdml.simpleparser.node.leaf.TextNode;

import java.util.function.Consumer;

public class NodeUtils {

    /**
     * Traverse a PDML tree in depth-first manner, and call a consumer on each node
     * @param tree the tree to be traversed
     * @param nodeConsumer the consumer to be called for each node
     */
    public static void forEachNodeInTree (
        @NotNull RootOrBranchNode tree,
        @NotNull Consumer<PdmlNode> nodeConsumer ) {

        nodeConsumer.accept ( tree );

        for ( ChildNode child : tree.getChildNodes() ) {
            switch ( child ) {
                case BranchNode bn -> forEachNodeInTree ( bn, nodeConsumer );
                case TextNode tn -> nodeConsumer.accept ( tn );
                default -> throw new IllegalStateException ( "Unexpected value: " + child );
            }
        }
    }

    /**
     * Traverse a PDML tree in depth-first manner, and call a consumer on each text node
     * @param tree the tree to be traversed
     * @param textConsumer the consumer to be called for each text
     */
    public static void forEachTextInTree (
        @NotNull RootOrBranchNode tree,
        @NotNull Consumer<String> textConsumer ) {

        forEachNodeInTree ( tree, node -> {
            if ( node instanceof TextNode tn ) {
                textConsumer.accept ( tn.getText() );
            }
        } );
    }
}

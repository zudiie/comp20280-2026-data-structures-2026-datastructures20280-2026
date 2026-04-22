package project20280.tree;

import project20280.interfaces.Entry;
import project20280.interfaces.Position;

import java.util.Comparator;

/**
 * An implementation of a sorted map using an AVL (Adelson-Velsky and Landis) tree.
 *
 * An AVL tree is a self-balancing BST where the heights of the two child subtrees
 * of any node differ by at most 1. This balance guarantee ensures O(log n) time
 * for all operations (get, put, remove).
 *
 * This class extends TreeMap and overrides the rebalancing hooks
 * (rebalanceInsert, rebalanceDelete) to enforce AVL balance after every update.
 *
 * Balance is maintained using the aux field inherited from BSTNode (via TreeMap),
 * which stores the height of each node.
 */
public class AVLTreeMap<K, V> extends TreeMap<K, V> {

    /**
     * Constructs an empty AVL map using the natural ordering of keys.
     */
    public AVLTreeMap() {
        super();
    }

    /**
     * Constructs an empty AVL map using the given comparator to order keys.
     *
     * @param comp comparator defining the order of keys in the map
     */
    public AVLTreeMap(Comparator<K> comp) {
        super(comp);
    }

    /**
     * Returns the AVL height stored at position p.
     *
     * Height is stored in the aux field of BSTNode:
     * - Sentinel leaf nodes have aux=0 (height 0)
     * - Internal nodes have aux = 1 + max(height(left), height(right))
     *
     * @param p a position in the tree
     * @return the height stored at p
     */
    protected int height(Position<Entry<K, V>> p) {
        return tree.getAux(p);
    }

    /**
     * Recomputes and updates the height of position p based on its children.
     *
     * Height of a node = 1 + max(height of left child, height of right child).
     * Sentinel leaves have height 0, so an internal node with two sentinel
     * children gets height 1 + max(0, 0) = 1.
     *
     * This must be called bottom-up after any rotation or structural change.
     *
     * @param p the position whose height needs to be updated
     */
    protected void recomputeHeight(Position<Entry<K, V>> p) {
        tree.setAux(p, 1 + Math.max(height(left(p)), height(right(p))));
    }

    /**
     * Returns true if position p satisfies the AVL balance property.
     *
     * A node is balanced if the absolute difference between the heights
     * of its left and right subtrees is at most 1:
     *   |height(left) - height(right)| <= 1
     *
     * @param p the position to check
     * @return true if balanced, false if imbalanced (balance factor > 1)
     */
    protected boolean isBalanced(Position<Entry<K, V>> p) {
        return Math.abs(height(left(p)) - height(right(p))) <= 1;
    }

    /**
     * Returns the taller child of position p (used to guide restructuring).
     *
     * When heights differ, returns the child with greater height.
     * When heights are equal, breaks the tie by matching the orientation
     * of p relative to its own parent (to avoid zig-zag cases):
     * - If p is a left child, return left child (zig-zig preference)
     * - If p is a right child, return right child (zig-zig preference)
     * - If p is root, either child works — left is returned by default
     *
     * @param p the position whose taller child we want
     * @return the child of p with the greater height
     */
    protected Position<Entry<K, V>> tallerChild(Position<Entry<K, V>> p) {
        if (height(left(p)) > height(right(p))) return left(p);  // left is clearly taller
        if (height(left(p)) < height(right(p))) return right(p); // right is clearly taller

        // heights are equal — break tie by matching p's own orientation
        if (isRoot(p)) return left(p);                // p is root, choice doesn't matter
        if (p == left(parent(p))) return left(p);     // p is left child → prefer left
        else return right(p);                         // p is right child → prefer right
    }

    /**
     * Rebalances the tree by walking upward from position p toward the root.
     *
     * At each node, it:
     * 1. Checks if the node is unbalanced (balance factor > 1)
     * 2. If so, performs a trinode restructure using the taller grandchild,
     *    then recomputes heights of the new subtree's children and root
     * 3. Recomputes the height of the current node
     * 4. Stops early if the height hasn't changed (no further rebalancing needed)
     *
     * Called by both rebalanceInsert and rebalanceDelete.
     *
     * @param p the position to start rebalancing from (bottom of the changed path)
     */
    protected void rebalance(Position<Entry<K, V>> p) {
        int oldHeight, newHeight;
        do {
            oldHeight = height(p); // snapshot height before any changes

            if (!isBalanced(p)) {
                // find the taller grandchild and restructure around it
                // restructure returns the new root of this subtree
                p = restructure(tallerChild(tallerChild(p)));

                // after restructure, recompute heights of the two new children
                recomputeHeight(left(p));
                recomputeHeight(right(p));
            }

            // update height of current node (whether or not we restructured)
            recomputeHeight(p);
            newHeight = height(p);

            p = parent(p); // move up toward root
        } while (oldHeight != newHeight && p != null);
        // stop if height unchanged (ancestors unaffected) or we've reached the top
    }

    /**
     * Hook called by TreeMap.put() after a new node is inserted.
     *
     * Starts rebalancing from the newly inserted node upward,
     * since the insertion may have violated AVL balance on the path to the root.
     *
     * @param p the position of the newly inserted node
     */
    @Override
    protected void rebalanceInsert(Position<Entry<K, V>> p) {
        rebalance(p);
    }

    /**
     * Hook called by TreeMap.remove() after a node is deleted.
     *
     * The parameter p is the sibling of the removed leaf (the node that
     * was promoted to fill the gap). We start rebalancing from p's parent
     * upward, since that is the lowest point that could now be unbalanced.
     *
     * We skip rebalancing if p is the root (no parent to rebalance from).
     *
     * @param p the sibling of the removed leaf (promoted node)
     */
    @Override
    protected void rebalanceDelete(Position<Entry<K, V>> p) {
        if (!isRoot(p))
            rebalance(parent(p));
    }

    /**
     * Validates the AVL structure of the entire tree (for debugging only).
     *
     * Checks every internal node to ensure:
     * 1. It has a non-null entry
     * 2. Its stored height equals 1 + max(height of children)
     *
     * Prints a violation message and dumps the tree if any check fails.
     *
     * @return true if the tree is a valid AVL tree, false otherwise
     */
    private boolean sanityCheck() {
        for (Position<Entry<K, V>> p : tree.positions()) {
            if (isInternal(p)) {
                if (p.getElement() == null)
                    System.out.println("VIOLATION: Internal node has null entry");
                else if (height(p) != 1 + Math.max(height(left(p)), height(right(p)))) {
                    System.out.println("VIOLATION: AVL unbalanced node with key " + p.getElement().getKey());
                    dump();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a formatted string representation of the AVL tree structure.
     * Useful for visualising the tree shape during debugging.
     */
    public String toBinaryTreeString() {
        BinaryTreePrinter<Entry<K, V>> btp = new BinaryTreePrinter<>(this.tree);
        return btp.print();
    }

    /**
     * Quick manual test: inserts a sequence of integers into the AVL tree,
     * prints the tree after all insertions, then removes one element and
     * prints again to verify rebalancing after deletion.
     */
    public static void main(String[] args) {
        AVLTreeMap avl = new AVLTreeMap<>();

        Integer[] arr = new Integer[]{5, 3, 10, 2, 4, 7, 11, 1, 6, 9, 12, 8};

        for (Integer i : arr) {
            if (i != null) avl.put(i, i);
            System.out.println("root " + avl.root());
        }
        System.out.println(avl.toBinaryTreeString());

        avl.remove(5);
        System.out.println(avl.toBinaryTreeString());
    }
}
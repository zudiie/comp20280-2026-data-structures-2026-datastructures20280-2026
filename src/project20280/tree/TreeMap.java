package project20280.tree;

import project20280.interfaces.Entry;
import project20280.interfaces.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;

/**
 * An implementation of a sorted map using a Binary Search Tree (BST).
 *
 * Keys are stored in BST order: for any node, all keys in its left subtree
 * are smaller, and all keys in its right subtree are larger.
 *
 * The tree uses SENTINEL LEAVES — every empty spot is represented by a real
 * node with a null element (rather than a null pointer). This simplifies
 * insertion and deletion logic, as we never need to handle null nodes directly.
 *
 * Structure for n entries:
 *   - n internal nodes (hold real key-value entries)
 *   - n+1 external sentinel nodes (null elements, no children)
 *   - total tree size = 2n + 1
 *
 * This class also serves as the base for AVLTreeMap and TreapMap, which
 * override the rebalancing hooks (rebalanceInsert, rebalanceDelete) to
 * enforce their own balance properties after updates.
 */
public class TreeMap<K, V> extends AbstractSortedMap<K, V> {

    // ---------------- nested BalanceableBinaryTree class ----------------

    /**
     * A specialised extension of LinkedBinaryTree that adds:
     * 1. BSTNode — a node subclass with an auxiliary integer field (aux),
     *    used by subclasses to store balancing data (e.g. height in AVL,
     *    priority in Treap).
     * 2. rotate() — single rotation for rebalancing.
     * 3. restructure() — trinode restructure (AVL-style, handles all 4 cases).
     */
    protected static class BalanceableBinaryTree<K, V> extends LinkedBinaryTree<Entry<K, V>> {

        // -------------- nested BSTNode class --------------
        /**
         * Extends the basic Node with an auxiliary integer field.
         *
         * Usage by subclasses:
         * - AVLTreeMap: aux stores the HEIGHT of the node
         * - TreapMap:   aux stores the random PRIORITY of the node
         *
         * Default value is 0 (correct initial height for a sentinel leaf).
         */
        protected static class BSTNode<E> extends Node<E> {
            int aux = 0;

            BSTNode(E e, Node<E> parent, Node<E> leftChild, Node<E> rightChild) {
                super(e, parent, leftChild, rightChild);
            }

            public int getAux() { return aux; }
            public void setAux(int value) { aux = value; }
        }
        // --------- end of nested BSTNode class ---------

        /** Returns the aux value at position p (cast to BSTNode to access the field). */
        public int getAux(Position<Entry<K, V>> p) {
            return ((BSTNode<Entry<K, V>>) p).getAux();
        }

        /** Sets the aux value at position p. */
        public void setAux(Position<Entry<K, V>> p, int value) {
            ((BSTNode<Entry<K, V>>) p).setAux(value);
        }

        /**
         * Overrides the node factory so every node created in this tree
         * is a BSTNode (with aux field) rather than a plain Node.
         * Called internally by addRoot, addLeft, addRight.
         */
        @Override
        protected Node<Entry<K, V>> createNode(Entry<K, V> e, Node<Entry<K, V>> parent,
                                               Node<Entry<K, V>> left, Node<Entry<K, V>> right) {
            return new BSTNode<>(e, parent, left, right);
        }

        /**
         * Low-level helper that sets child's parent pointer and attaches
         * child as either the left or right child of parent.
         *
         * @param parent        the parent node
         * @param child         the child node to attach
         * @param makeLeftChild if true, attach as left child; otherwise right
         */
        private void relink(Node<Entry<K, V>> parent, Node<Entry<K, V>> child, boolean makeLeftChild) {
            child.setParent(parent);
            if (makeLeftChild)
                parent.setLeft(child);
            else
                parent.setRight(child);
        }

        /**
         * Rotates position p upward above its parent.
         *
         * Before (p=a is left child):        After:
         *        b                              a
         *       / \                            / \
         *      a  t2          =>             t0   b
         *     / \                                / \
         *    t0  t1                             t1  t2
         *
         * The mirror case (p is right child) works symmetrically.
         *
         * Steps:
         * 1. Detach p from its parent, attach p directly under grandparent
         * 2. Transfer p's inner subtree (t1) to the old parent
         * 3. Make old parent a child of p
         *
         * Used by: restructure() for AVL, and TreapMap's bubble-up/bubble-down.
         */
        public void rotate(Position<Entry<K, V>> p) {
            Node<Entry<K, V>> x = validate(p);   // node being rotated up
            Node<Entry<K, V>> y = x.getParent(); // x's current parent
            Node<Entry<K, V>> z = y.getParent(); // x's grandparent (null if y is root)

            if (z == null) {
                // y was the root — x becomes the new root of the entire tree
                root = x;
                x.setParent(null);
            } else {
                // attach x directly under z, in same position y occupied
                relink(z, x, y == z.getLeft());
            }

            // transfer the middle subtree and swap x with y
            if (x == y.getLeft()) {
                // x was left child: move x's right subtree to y's left
                relink(y, x.getRight(), true);  // t1 becomes y's left child
                relink(x, y, false);            // y becomes x's right child
            } else {
                // x was right child: move x's left subtree to y's right
                relink(y, x.getLeft(), false);  // t1 becomes y's right child
                relink(x, y, true);             // y becomes x's left child
            }
        }

        /**
         * Performs a trinode restructure involving x, its parent y, and
         * grandparent z. Handles all four imbalance cases (LL, RR, LR, RL).
         *
         * Four possible input configurations (z=grandparent, y=parent, x=child):
         *
         *   LL: z=a, y=b (left of z), x=c (left of y)  → rotate y up (1 rotation)
         *   RR: z=c, y=b (right of z), x=a (right of y) → rotate y up (1 rotation)
         *   LR: z=a, y=c (right of z), x=b (left of y)  → rotate x up twice
         *   RL: z=c, y=a (left of z), x=b (right of y)  → rotate x up twice
         *
         * Result is always a balanced subtree with the middle-value node as root:
         *          b
         *        /   \
         *       a     c
         *      / \   / \
         *     t0 t1 t2 t3
         *
         * @param x the lowest node in the unbalanced trio (child of y, grandchild of z)
         * @return the new root of the restructured subtree
         */
        public Position<Entry<K, V>> restructure(Position<Entry<K, V>> x) {
            Position<Entry<K, V>> y = parent(x);
            Position<Entry<K, V>> z = parent(y);

            if ((x == right(y)) == (y == right(z))) {
                // zig-zig: x and y are on the same side — single rotation of y
                rotate(y);
                return y; // y is now the subtree root
            } else {
                // zig-zag: x and y are on opposite sides — double rotation of x
                rotate(x);
                rotate(x);
                return x; // x is now the subtree root
            }
        }
    }
    // ----------- end of nested BalanceableBinaryTree class -----------

    /**
     * The underlying tree. All structural operations go through this object.
     * Using BalanceableBinaryTree gives subclasses access to aux and rotations.
     */
    protected BalanceableBinaryTree<K, V> tree = new BalanceableBinaryTree<>();

    /**
     * Constructs an empty map using the natural ordering of keys.
     * Adds a sentinel null root so treeSearch always has a node to start from,
     * even on an empty map.
     */
    public TreeMap() {
        super();
        tree.addRoot(null); // sentinel root: real node, null element, no children
    }

    /**
     * Constructs an empty map using the given comparator to order keys.
     */
    public TreeMap(Comparator<K> comp) {
        super(comp);
        tree.addRoot(null); // sentinel root
    }

    /**
     * Returns the number of key-value entries in the map.
     *
     * The tree has n internal nodes (entries) and n+1 sentinel leaves,
     * so total tree.size() = 2n+1, giving n = (size-1)/2.
     */
    @Override
    public int size() {
        return (tree.size() - 1) / 2;
    }

    /** Delegates trinode restructure to the underlying BalanceableBinaryTree. */
    protected Position<Entry<K, V>> restructure(Position<Entry<K, V>> x) {
        return tree.restructure(x);
    }

    /**
     * Hook called by put() after a new node is inserted.
     * Empty in base TreeMap — overridden by AVLTreeMap and TreapMap
     * to trigger their respective rebalancing logic.
     *
     * @param p the position of the newly inserted node
     */
    protected void rebalanceInsert(Position<Entry<K, V>> p) { }

    /**
     * Hook called by remove() after a node is deleted.
     * Empty in base TreeMap — overridden by AVLTreeMap and TreapMap.
     *
     * @param p the sibling of the removed leaf (the node promoted in its place)
     */
    protected void rebalanceDelete(Position<Entry<K, V>> p) { }

    /**
     * Hook called by get() after a node is accessed.
     * Empty in base TreeMap — could be overridden by a Splay tree subclass
     * to splay the accessed node to the root.
     *
     * @param p the position that was accessed (may be a sentinel leaf)
     */
    protected void rebalanceAccess(Position<Entry<K, V>> p) { }

    /**
     * Converts a sentinel leaf p into an internal node holding the given entry.
     *
     * Before: p is a leaf (null element, no children)
     * After:  p holds the entry, with two new sentinel leaves as children
     *
     * This is the only way new entries are added to the tree.
     *
     * @param p     a sentinel leaf position where the new entry will be placed
     * @param entry the key-value entry to store at p
     */
    private void expandExternal(Position<Entry<K, V>> p, Entry<K, V> entry) {
        tree.set(p, entry);         // store the entry at p
        tree.addLeft(p, null);      // add left sentinel leaf
        tree.addRight(p, null);     // add right sentinel leaf
    }

    // Convenience wrappers — delegate to the underlying tree for brevity

    protected Position<Entry<K, V>> root() { return tree.root(); }
    protected Position<Entry<K, V>> parent(Position<Entry<K, V>> p) { return tree.parent(p); }
    protected Position<Entry<K, V>> left(Position<Entry<K, V>> p) { return tree.left(p); }
    protected Position<Entry<K, V>> right(Position<Entry<K, V>> p) { return tree.right(p); }
    protected Position<Entry<K, V>> sibling(Position<Entry<K, V>> p) { return tree.sibling(p); }
    protected boolean isRoot(Position<Entry<K, V>> p) { return tree.isRoot(p); }
    protected boolean isExternal(Position<Entry<K, V>> p) { return tree.isExternal(p); }
    protected boolean isInternal(Position<Entry<K, V>> p) { return tree.isInternal(p); }
    protected void set(Position<Entry<K, V>> p, Entry<K, V> e) { tree.set(p, e); }
    protected Entry<K, V> remove(Position<Entry<K, V>> p) { return tree.remove(p); }

    /**
     * Searches the subtree rooted at p for the given key.
     *
     * Follows standard BST search:
     * - If we reach a sentinel leaf, the key is not in the tree — return the leaf.
     *   (The caller uses this leaf position as the insertion point.)
     * - If key matches current node, return that node.
     * - If key < current node's key, recurse left.
     * - If key > current node's key, recurse right.
     *
     * @param p   root of the subtree to search
     * @param key the key to search for
     * @return the position holding the key, or the sentinel leaf where it would go
     */
    private Position<Entry<K, V>> treeSearch(Position<Entry<K, V>> p, K key) {
        if (isExternal(p)) {
            return p; // sentinel leaf: key not found, return insertion point
        }
        int comp = compare(key, p.getElement());
        if (comp == 0) {
            return p;                        // found exact match
        } else if (comp < 0) {
            return treeSearch(left(p), key); // key is smaller, go left
        } else {
            return treeSearch(right(p), key); // key is larger, go right
        }
    }

    /**
     * Returns the position with the smallest key in the subtree rooted at p.
     * Walks left as far as possible (leftmost internal node = minimum).
     *
     * @param p root of the subtree
     * @return position of minimum key, or null if subtree is empty
     */
    protected Position<Entry<K, V>> treeMin(Position<Entry<K, V>> p) {
        if (isExternal(p)) return null;
        Position<Entry<K, V>> walk = p;
        while (isInternal(left(walk))) {
            walk = left(walk); // keep going left
        }
        return walk;
    }

    /**
     * Returns the position with the largest key in the subtree rooted at p.
     * Walks right as far as possible (rightmost internal node = maximum).
     *
     * @param p root of the subtree
     * @return position of maximum key, or null if subtree is empty
     */
    protected Position<Entry<K, V>> treeMax(Position<Entry<K, V>> p) {
        if (isExternal(p)) return null;
        Position<Entry<K, V>> walk = p;
        while (isInternal(right(walk))) {
            walk = right(walk); // keep going right
        }
        return walk;
    }

    /**
     * Returns the value associated with the given key, or null if not found.
     *
     * Searches the BST for the key. If found at an internal node, returns
     * its value. If search ends at a sentinel leaf, the key is absent.
     *
     * @param key the key to look up
     * @return associated value, or null if key is not in the map
     */
    @Override
    public V get(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);
        rebalanceAccess(p); // hook for splay trees (no-op here)
        if (isExternal(p) || p.getElement() == null) {
            return null; // key not found
        }
        return p.getElement().getValue();
    }

    /**
     * Inserts or updates a key-value entry in the map.
     *
     * Case 1 — key is new: treeSearch returns a sentinel leaf.
     *   expandExternal converts it into an internal node, then
     *   rebalanceInsert is called for subclass balancing.
     *   Returns null (no previous value).
     *
     * Case 2 — key already exists: treeSearch returns the internal node.
     *   The existing entry is replaced in-place.
     *   Returns the old value.
     *
     * @param key   the key to insert or update
     * @param value the value to associate with the key
     * @return the old value if key existed, null if it was newly inserted
     */
    @Override
    public V put(K key, V value) throws IllegalArgumentException {
        checkKey(key);
        Entry<K, V> newEnt = new MapEntry<>(key, value);
        Position<Entry<K, V>> p = treeSearch(root(), key);

        if (isExternal(p)) {
            // key not found — insert at the sentinel leaf position
            expandExternal(p, newEnt);
            rebalanceInsert(p); // notify subclass (AVL, Treap) to rebalance
            return null;
        } else {
            // key already exists — update value in place
            V old = p.getElement().getValue();
            set(p, newEnt);
            rebalanceAccess(p);
            return old;
        }
    }

    /**
     * Removes the entry with the given key from the map, if present.
     *
     * Two cases based on the node's children:
     *
     * Case 1 — at most one internal child:
     *   Remove the sentinel leaf on the empty side, then remove the node itself.
     *   The remaining child (or sentinel) is promoted up automatically.
     *
     * Case 2 — both children are internal:
     *   Cannot remove directly. Instead, find the in-order predecessor
     *   (rightmost node of left subtree), copy its entry to p, then
     *   delete the predecessor (which has at most one internal child — Case 1).
     *
     * After removal, rebalanceDelete is called from the promoted sibling upward.
     *
     * @param key the key to remove
     * @return the value that was associated with the key, or null if not found
     */
    @Override
    public V remove(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);

        if (isExternal(p)) {
            // key not found
            rebalanceAccess(p);
            return null;
        } else {
            V old = p.getElement().getValue();

            if (isInternal(left(p)) && isInternal(right(p))) {
                // Case 2: both children internal — replace with in-order predecessor
                Position<Entry<K, V>> replacement = treeMax(left(p));
                set(p, replacement.getElement()); // overwrite p with predecessor's entry
                p = replacement;                  // now delete the predecessor instead
            }

            // Case 1: p now has at most one internal child
            // pick the sentinel leaf side to remove
            Position<Entry<K, V>> leaf = (isExternal(left(p)) ? left(p) : right(p));
            Position<Entry<K, V>> sib = sibling(leaf); // sib will be promoted
            remove(leaf); // remove the sentinel leaf
            remove(p);    // remove the node (sib takes its place)
            rebalanceDelete(sib); // notify subclass to rebalance from sib upward
            return old;
        }
    }

    /**
     * Returns the entry with the smallest key in the map, or null if empty.
     * Delegates to treeMin on the root.
     */
    @Override
    public Entry<K, V> firstEntry() {
        if (isEmpty()) return null;
        return treeMin(root()).getElement();
    }

    /**
     * Returns the entry with the largest key in the map, or null if empty.
     * Delegates to treeMax on the root.
     */
    @Override
    public Entry<K, V> lastEntry() {
        if (isEmpty()) return null;
        return treeMax(root()).getElement();
    }

    /**
     * Returns the entry with the least key >= given key.
     *
     * If treeSearch finds the key exactly, return it.
     * Otherwise search ended at a sentinel leaf — walk up the tree:
     * - If we came from a left child, the parent is the ceiling.
     * - If we came from a right child, keep walking up.
     * Returns null if no key >= given key exists.
     */
    @Override
    public Entry<K, V> ceilingEntry(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);
        if (isInternal(p)) return p.getElement(); // exact match
        while (!isRoot(p)) {
            if (p == left(parent(p))) {
                return parent(p).getElement(); // parent is smallest key > given key
            } else {
                p = parent(p); // came from right, keep going up
            }
        }
        return null;
    }

    /**
     * Returns the entry with the greatest key <= given key.
     *
     * Mirror of ceilingEntry:
     * - Exact match → return it.
     * - Walk up: if we came from a right child, parent is the floor.
     * - If we came from a left child, keep walking up.
     * Returns null if no key <= given key exists.
     */
    @Override
    public Entry<K, V> floorEntry(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);
        if (isInternal(p)) return p.getElement(); // exact match
        while (!isRoot(p)) {
            if (p == right(parent(p))) {
                return parent(p).getElement(); // parent is largest key < given key
            } else {
                p = parent(p);
            }
        }
        return null;
    }

    /**
     * Returns the entry with the greatest key strictly less than given key.
     *
     * If the found node has a left internal child, the answer is the maximum
     * of that left subtree. Otherwise walk up until we come from a right child.
     * Returns null if no strictly smaller key exists.
     */
    @Override
    public Entry<K, V> lowerEntry(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);
        if (isInternal(p) && isInternal(left(p))) {
            return treeMax(left(p)).getElement(); // predecessor is in left subtree
        }
        while (!isRoot(p)) {
            if (p == right(parent(p))) {
                return parent(p).getElement(); // first ancestor we came from the right
            }
            p = parent(p);
        }
        return null;
    }

    /**
     * Returns the entry with the least key strictly greater than given key.
     *
     * If the found node has a right internal child, the answer is the minimum
     * of that right subtree. Otherwise walk up until we come from a left child.
     * Returns null if no strictly greater key exists.
     */
    @Override
    public Entry<K, V> higherEntry(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);
        if (isInternal(p) && isInternal(right(p))) {
            return treeMin(right(p)).getElement(); // successor is in right subtree
        }
        while (!isRoot(p)) {
            if (p == left(parent(p))) {
                return parent(p).getElement(); // first ancestor we came from the left
            }
            p = parent(p);
        }
        return null;
    }

    /**
     * Returns all entries in the map in ascending key order.
     *
     * Uses in-order traversal of the tree, filtering out sentinel leaves
     * (external nodes with null elements) — only internal nodes have entries.
     */
    @Override
    public Iterable<Entry<K, V>> entrySet() {
        ArrayList<Entry<K, V>> buffer = new ArrayList<>(size());
        for (Position<Entry<K, V>> p : tree.inorder()) {
            if (isInternal(p)) {
                buffer.add(p.getElement()); // skip sentinel leaves
            }
        }
        return buffer;
    }

    /**
     * Returns a string of all keys in sorted order, e.g. "[1, 3, 5, 7]".
     * Delegates to keySet() which uses entrySet() internally.
     */
    @Override
    public String toString() {
        ArrayList<K> keys = new ArrayList<>();
        for (K key : keySet()) keys.add(key);
        return keys.toString();
    }

    /**
     * Returns all entries with keys in [fromKey, toKey).
     *
     * Uses a recursive helper that prunes subtrees outside the range,
     * visiting only nodes that could contain keys in range.
     * Results are added in ascending order.
     *
     * @param fromKey lower bound (inclusive)
     * @param toKey   upper bound (exclusive)
     * @return iterable of entries in range
     */
    @Override
    public Iterable<Entry<K, V>> subMap(K fromKey, K toKey) throws IllegalArgumentException {
        checkKey(fromKey);
        checkKey(toKey);
        ArrayList<Entry<K, V>> buffer = new ArrayList<>(size());
        if (compare(fromKey, toKey) < 0) {
            subMapRecursive(fromKey, toKey, root(), buffer);
        }
        return buffer;
    }

    /**
     * Recursive helper for subMap. Traverses only the relevant portion of the tree.
     *
     * - If p's key < fromKey: all qualifying keys must be in the right subtree.
     * - Otherwise: recurse left first, then add p if it's in range, then recurse right.
     *
     * This avoids visiting the entire tree when the range is narrow.
     */
    private void subMapRecursive(K fromKey, K toKey, Position<Entry<K, V>> p,
                                 ArrayList<Entry<K, V>> buffer) {
        if (isInternal(p)) {
            if (compare(p.getElement(), fromKey) < 0) {
                // p is below range — only right subtree can have qualifying keys
                subMapRecursive(fromKey, toKey, right(p), buffer);
            } else {
                subMapRecursive(fromKey, toKey, left(p), buffer); // check left first
                if (compare(p.getElement(), toKey) < 0) {         // p is in [from, to)
                    buffer.add(p.getElement());
                    subMapRecursive(fromKey, toKey, right(p), buffer); // check right
                }
                // if p >= toKey, right subtree is also out of range — stop
            }
        }
    }

    /** Delegates rotate to the underlying BalanceableBinaryTree. */
    protected void rotate(Position<Entry<K, V>> p) {
        tree.rotate(p);
    }

    // ---------- debug utilities ----------

    /**
     * Prints a text representation of the tree to stdout.
     * Shows each internal node's entry and marks leaves as "leaf".
     * Indentation reflects depth in the tree.
     */
    protected void dump() {
        dumpRecurse(root(), 0);
    }

    private void dumpRecurse(Position<Entry<K, V>> p, int depth) {
        String indent = (depth == 0 ? "" : String.format("%" + (2 * depth) + "s", ""));
        if (isExternal(p))
            System.out.println(indent + "leaf");
        else {
            System.out.println(indent + p.getElement());
            dumpRecurse(left(p), depth + 1);
            dumpRecurse(right(p), depth + 1);
        }
    }

    /**
     * Returns a formatted 2D string of the tree structure using BinaryTreePrinter.
     * Useful for visually verifying tree shape during debugging.
     */
    public String toBinaryTreeString() {
        BinaryTreePrinter<Entry<K, V>> btp = new BinaryTreePrinter<>(this.tree);
        return btp.print();
    }

    /**
     * Quick stress test: randomly inserts and removes integers, then
     * copies surviving entries into an AVLTreeMap to verify compatibility.
     */
    public static void main(String[] args) {
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();

        Random rnd = new Random();
        int n_max = 50;

        // randomly either insert or remove each number
        Consumer<Integer> modify = x -> {
            if (rnd.nextFloat() > 0.5)
                treeMap.put(x, 0);
            else
                treeMap.remove(x);
        };

        BinaryTreePrinter<Entry<Integer, Integer>> btp = new BinaryTreePrinter<>(treeMap.tree);
        System.out.println(btp.print()); // print empty tree

        rnd.ints(1, n_max).limit(10000000).boxed().forEach(modify);
        System.out.println(btp.print()); // print after stress test

        // copy surviving entries into an AVL tree to verify structure
        AVLTreeMap<Integer, Integer> avl = new AVLTreeMap<>();
        for (Position<Entry<Integer, Integer>> i : treeMap.tree.inorder()) {
            if (i.getElement() != null) {
                avl.put(i.getElement().getKey(), 0);
            }
        }
        System.out.println(avl.toBinaryTreeString());
    }
}
package project20280.tree;

import project20280.interfaces.Position;
import project20280.interfaces.Tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * An abstract base class providing some functionality of the Tree interface.
 * <p>
 * The following three methods remain abstract, and must be
 * implemented by a concrete subclass: root, parent, children. Other
 * methods implemented in this class may be overridden to provide a
 * more direct and efficient implementation.
 */
public abstract class AbstractTree<E> implements Tree<E> {

    /**
     * Returns true if Position p has one or more children.
     *
     * @param p A valid Position within the tree
     * @return true if p has at least one child, false otherwise
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     */
    @Override
    public boolean isInternal(Position<E> p) {
        // TODO
        return numChildren(p) > 0;
    }

    /**
     * Returns true if Position p does not have any children.
     *
     * @param p A valid Position within the tree
     * @return true if p has zero children, false otherwise
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     */
    @Override
    public boolean isExternal(Position<E> p) {
        // TODO
        return numChildren(p) == 0;
    }

    /**
     * Returns true if Position p represents the root of the tree.
     *
     * @param p A valid Position within the tree
     * @return true if p is the root of the tree, false otherwise
     */
    @Override
    public boolean isRoot(Position<E> p) {
        // TODO
        return p == root();
    }

    /**
     * Returns the number of children of Position p.
     *
     * @param p A valid Position within the tree
     * @return number of children of Position p
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     */
    @Override
    public int numChildren(Position<E> p) {
        // TODO
        int count = 0;
        for (Position<E> c : children(p)) count++;
        return count;
    }

    /**
     * Returns the number of nodes in the tree.
     *
     * @return number of nodes in the tree
     */
    @Override
    public int size() {
        int count = 0;
        for (Position p : positions()) count++;
        return count;
    }

    /**
     * Tests whether the tree is empty.
     *
     * @return true if the tree is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    //---------- support for computing depth of nodes and height of (sub)trees ----------

    /**
     * Returns the number of levels separating Position p from the root.
     *
     * @param p A valid Position within the tree
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     */
    public int depth(Position<E> p) throws IllegalArgumentException {
        // TODO
        if (isRoot(p)) return 0;
        return 1 + depth(parent(p));
    }

    /**
     * Returns the height of the tree.
     * <p>
     * Note: This implementation works, but runs in O(n^2) worst-case time.
     */
    private int heightBad() {             // works, but quadratic worst-case time
        int h = 0;
        for (Position<E> p : positions())
            if (isExternal(p))                // only consider leaf positions
                h = Math.max(h, depth(p));
        return h;
    }

    public int height_recursive(Position<E> p) {
        // TODO
        if (p == null || isExternal(p)) return 0;
        int h = 0;
        for (Position<E> c : children(p))
            h = Math.max(h, height_recursive(c));
        return 1 + h;
    }

    /**
     * Returns the height of the subtree rooted at Position p.
     *
     * @param p A valid Position within the tree
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     */
    public int height() throws IllegalArgumentException {
        return height_recursive(root());
    }

    //---------- support for various iterations of a tree ----------

    //---------------- nested ElementIterator class ----------------
    /* This class adapts the iteration produced by positions() to return elements. */
    private class ElementIterator implements Iterator<E> {
        Iterator<Position<E>> posIterator = positions().iterator();

        public boolean hasNext() {
            return posIterator.hasNext();
        }

        public E next() {
            return posIterator.next().getElement();
        } // return element!

        public void remove() {
            posIterator.remove();
        }
    }

    /**
     * Returns an iterator of the elements stored in the tree.
     *
     * @return iterator of the tree's elements
     */
    @Override
    public Iterator<E> iterator() {
        return new ElementIterator();
    }

    /**
     * Returns an iterable collection of the positions of the tree.
     *
     * @return iterable collection of the tree's positions
     */
    @Override
    public Iterable<Position<E>> positions() {
        return preorder();
    }

    /**
     * Adds positions of the subtree rooted at Position p to the given
     * snapshot using a preorder traversal
     *
     * @param p        Position serving as the root of a subtree
     * @param snapshot a list to which results are appended
     */
    private void preorderSubtree(Position<E> p, List<Position<E>> snapshot) {
        // TODO
    }

    /**
     * Returns an iterable collection of positions of the tree, reported in preorder.
     *
     * @return iterable collection of the tree's positions in preorder
     */
    public Iterable<Position<E>> preorder() {
        // TODO
        return null;
    }

    /**
     * Adds positions of the subtree rooted at Position p to the given
     * snapshot using a postorder traversal
     *
     * @param p        Position serving as the root of a subtree
     * @param snapshot a list to which results are appended
     */
    private void postorderSubtree(Position<E> p, List<Position<E>> snapshot) {
        // TODO
    }

    /**
     * Returns an iterable collection of positions of the tree, reported in postorder.
     *
     * @return iterable collection of the tree's positions in postorder
     */
    public Iterable<Position<E>> postorder() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty())
            postorderSubtree(root(), snapshot);   // fill the snapshot recursively
        return snapshot;
    }

    /**
     * Returns an iterable collection of positions of the tree in breadth-first order.
     *
     * @return iterable collection of the tree's positions in breadth-first order
     */
    public Iterable<Position<E>> breadthfirst() {
        // TODO
        return null;
    }
}
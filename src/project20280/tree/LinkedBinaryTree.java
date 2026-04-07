package project20280.tree;

import project20280.interfaces.Position;

import java.util.ArrayList;
//import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Concrete implementation of a binary tree using a node-based, linked
 * structure.
 */
public class LinkedBinaryTree<E> extends AbstractBinaryTree<E> {

    static java.util.Random rnd = new java.util.Random();
    /**
     * The root of the binary tree
     */
    protected Node<E> root = null; // root of the tree

    // LinkedBinaryTree instance variables
    /**
     * The number of nodes in the binary tree
     */
    private int size = 0; // number of nodes in the tree

    /**
     * Constructs an empty binary tree.
     */
    public LinkedBinaryTree() {
    } // constructs an empty binary tree

    // constructor

    public static LinkedBinaryTree<Integer> makeRandom(int n) {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<>();
        bt.root = randomTree(null, 1, n);
        bt.size = n;
        return bt;
    }

    // nonpublic utility

    public static <T extends Integer> Node<T> randomTree(Node<T> parent, Integer first, Integer last) {
        if (first > last) return null;
        else {
            Integer treeSize = last - first + 1;
            Integer leftCount = rnd.nextInt(treeSize);
            Integer rightCount = treeSize - leftCount - 1;
            Node<T> root = new Node<T>((T) ((Integer) (first + leftCount)), parent, null, null);
            root.setLeft(randomTree(root, first, first + leftCount - 1));
            root.setRight(randomTree(root, first + leftCount + 1, last));
            return root;
        }
    }

    // accessor methods

    public static void main(String [] args) {

        //String[] arr = { "A", "B", "C", "D", "E", null, "F", null, null, "G", "H", null, null, null, null };
//        bt.createLevelOrder(arr);
//        System.out.println(bt.toBinaryTreeString());

//        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<>();
//        Integer[] arr = new Integer[] {1,
//                 2,3,
//                 4,5,6,7,
//                 8,9,10,11,12, 13, 14, 15,
//                 16 ,17 ,18 ,19 ,20 ,21 ,22 ,23 ,24 ,25 ,26 ,27 ,28 ,29 ,30 ,31 ,
//                 null ,null ,null ,35};
//
//        bt.createLevelOrder(arr);
//        int h = bt.heightWithCount(bt.root());
//        System.out.println(h);
//        System.out.println("tree diameter:" + bt.diameter());
//        System.out.println(bt.toBinaryTreeString());
//        System.out.println(bt.numExternal());

//        // trees II Q2
//        LinkedBinaryTree<String> bt = new LinkedBinaryTree<>();
//        String[] arr = {"A", "B", "C", "D", "E", null, "F", null, null, "G", "H", null, null, null, null};
//        bt.createLevelOrder(arr);
//        System.out.println(bt.toBinaryTreeString());

        // trees II Q3
        Integer[] inorder = {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
                16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
                28, 29, 30
        };

        Integer[] preorder = {
                18, 2, 1, 14, 13, 12, 4, 3, 9, 6, 5, 8, 7, 10, 11,
                15, 16, 17, 28, 23, 19, 22, 20, 21, 24, 27, 26,
                25, 29, 30
        };

        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<>();
        bt.construct(inorder, preorder);

        System.out.println(bt.toBinaryTreeString());
        System.out.println(bt.diameter());

    }

    public ArrayList<ArrayList<E>> rootToLeafPaths() {
        ArrayList<ArrayList<E>> result = new ArrayList<>();
        if (root == null) return result;

        ArrayList<E> current = new ArrayList<>();
        rootToLeafPathsHelper(root, current, result);
        return result;
    }

    private void rootToLeafPathsHelper(Node<E> node,
                                       ArrayList<E> current,
                                       ArrayList<ArrayList<E>> result) {

        if (node == null) return;

        // add current node to path
        current.add(node.getElement());

        // if leaf, store a copy of the path
        if (node.getLeft() == null && node.getRight() == null) {
            result.add(new ArrayList<>(current));
        } else {
            rootToLeafPathsHelper(node.getLeft(), current, result);
            rootToLeafPathsHelper(node.getRight(), current, result);
        }

        // backtrack
        current.remove(current.size() - 1);
    }

    public int numExternal() {
        return numExternal(root);
    }

    private int numExternal(Position<E> p) {
        if (p == null) return 0;
        if (left(p) == null && right(p) == null) return 1;
        return numExternal(left(p)) + numExternal(right(p));
    }

    private int callCount = 0;

    public int getCallCount() {
        return callCount;
    }

    public int heightWithCount(Position<E> p) {
        callCount++; // Increment for every call made
        if (p == null) return -1;
        return 1 + Math.max(heightWithCount(left(p)), heightWithCount(right(p)));
    }

    // recursive function to find the diameter of the tree
    public int diameter() {
        return diameter(root);
    }

    // helper method that calculates diameter at a specific node
    private int diameter(Position<E> p) {
        // base case: if node is null, diameter is 0
        if (p == null) return 0;

        // get the height of left and right subtrees
        int leftheight = height(left(p));
        int rightheight = height(right(p));

        // get the diameter of left and right subtrees recursively
        int leftdiameter = diameter(left(p));
        int rightdiameter = diameter(right(p));

        // the diameter at this node is the max of:
        // 1. path through the current node (left height + right height + 2)
        // 2. the diameter of the left subtree
        // 3. the diameter of the right subtree
        return Math.max(leftheight + rightheight + 2,
                Math.max(leftdiameter, rightdiameter));
    }

    /**
     * Factory function to create a new node storing element e.
     */
    protected Node<E> createNode(E e, Node<E> parent, Node<E> left, Node<E> right) {
        return new Node<E>(e, parent, left, right);
    }

    /**
     * Verifies that a Position belongs to the appropriate class, and is not one
     * that has been previously removed. Note that our current implementation does
     * not actually verify that the position belongs to this particular list
     * instance.
     *
     * @param p a Position (that should belong to this tree)
     * @return the underlying Node instance for the position
     * @throws IllegalArgumentException if an invalid position is detected
     */
    protected Node<E> validate(Position<E> p) throws IllegalArgumentException {
        if (!(p instanceof Node)) throw new IllegalArgumentException("Not valid position type");
        Node<E> node = (Node<E>) p; // safe cast
        if (node.getParent() == node) // our convention for defunct node
            throw new IllegalArgumentException("p is no longer in the tree");
        return node;
    }

    /**
     * Returns the number of nodes in the tree.
     *
     * @return number of nodes in the tree
     */
    @Override
    public int size() {
        return size;
    }



    /**
     * Returns the root Position of the tree (or null if tree is empty).
     *
     * @return root Position of the tree (or null if tree is empty)
     */
    @Override
    public Position<E> root() {
        return root;
    }

    // update methods supported by this class

    /**
     * Returns the Position of p's parent (or null if p is root).
     *
     * @param p A valid Position within the tree
     * @return Position of p's parent (or null if p is root)
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     */
    @Override
    public Position<E> parent(Position<E> p) throws IllegalArgumentException {
        return ((Node<E>) p).getParent();
    }

    /**
     * Returns the Position of p's left child (or null if no child exists).
     *
     * @param p A valid Position within the tree
     * @return the Position of the left child (or null if no child exists)
     * @throws IllegalArgumentException if p is not a valid Position for this tree
     */
    @Override
    public Position<E> left(Position<E> p) throws IllegalArgumentException {
        return ((Node<E>) p).getLeft();
    }

    /**
     * Returns the Position of p's right child (or null if no child exists).
     *
     * @param p A valid Position within the tree
     * @return the Position of the right child (or null if no child exists)
     * @throws IllegalArgumentException if p is not a valid Position for this tree
     */
    @Override
    public Position<E> right(Position<E> p) throws IllegalArgumentException {
        return ((Node<E>) p).getRight();
    }

    /**
     * Places element e at the root of an empty tree and returns its new Position.
     *
     * @param e the new element
     * @return the Position of the new element
     * @throws IllegalStateException if the tree is not empty
     */
    public Position<E> addRoot(E e) throws IllegalStateException {
        if (size != 0) throw new IllegalStateException("Tree is not empty");
        root = createNode(e, null, null, null);
        size = 1;
        return root;
    }

    public void insert(E e) {
        if (size == 0) {
            addRoot(e);
        } else {
            addRecursive(root, e);
        }

    }

    // recursively add Nodes to binary tree in proper position
    private Node<E> addRecursive(Node<E> p, E e) {
        Comparable<E> comp = (Comparable<E>) e;
        if (comp.compareTo(p.getElement()) < 0) {
            if (p.getLeft() == null) {
                Node<E> node = createNode(e, p, null, null);
                p.setLeft(node);
                size++;
                return node;
            } else {
                return addRecursive(p.getLeft(), e);
            }
        } else {
            if (p.getRight() == null) {
                Node<E> node = createNode(e, p, null, null);
                p.setRight(node);
                size++;
                return node;
            } else {
                return addRecursive(p.getRight(), e);
            }
        }
    }

    /**
     * Creates a new left child of Position p storing element e and returns its
     * Position.
     *
     * @param p the Position to the left of which the new element is inserted
     * @param e the new element
     * @return the Position of the new element
     * @throws IllegalArgumentException if p is not a valid Position for this tree
     * @throws IllegalArgumentException if p already has a left child
     */
    public Position<E> addLeft(Position<E> p, E e) throws IllegalArgumentException {
        Node<E> parent = validate(p);
        if (parent.getLeft() != null) throw new IllegalArgumentException("p already has a left child");
        Node<E> child = createNode(e, parent, null, null);
        parent.setLeft(child);
        size++;
        return child;
    }

    /**
     * Creates a new right child of Position p storing element e and returns its
     * Position.
     *
     * @param p the Position to the right of which the new element is inserted
     * @param e the new element
     * @return the Position of the new element
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     * @throws IllegalArgumentException if p already has a right child
     */
    public Position<E> addRight(Position<E> p, E e) throws IllegalArgumentException {
        Node<E> parent = validate(p);
        if (parent.getRight() != null) throw new IllegalArgumentException("p already has a right child");
        Node<E> child = createNode(e, parent, null, null);
        parent.setRight(child);
        size++;
        return child;
    }

    /**
     * Replaces the element at Position p with element e and returns the replaced
     * element.
     *
     * @param p the relevant Position
     * @param e the new element
     * @return the replaced element
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     */
    public E set(Position<E> p, E e) throws IllegalArgumentException {
        Node<E> node = validate(p);
        E temp = node.getElement();
        node.setElement(e);
        return temp;
    }

    /**
     * Attaches trees t1 and t2, respectively, as the left and right subtree of the
     * leaf Position p. As a side effect, t1 and t2 are set to empty trees.
     *
     * @param p  a leaf of the tree
     * @param t1 an independent tree whose structure becomes the left child of p
     * @param t2 an independent tree whose structure becomes the right child of p
     * @throws IllegalArgumentException if p is not a valid Position for this tree
     * @throws IllegalArgumentException if p is not a leaf
     */
    public void attach(Position<E> p, LinkedBinaryTree<E> t1, LinkedBinaryTree<E> t2) throws IllegalArgumentException {
        Node<E> node = validate(p);
        if (node.getLeft() != null || node.getRight() != null)
            throw new IllegalArgumentException("p must be a leaf");

        size += t1.size() + t2.size();

        if (t1.size() > 0) {
            t1.root.setParent(node);
            node.setLeft(t1.root);
            t1.root = null;
            t1.size = 0;
        }
        if (t2.size() > 0) {
            t2.root.setParent(node);
            node.setRight(t2.root);
            t2.root = null;
            t2.size = 0;
        }
    }

    /**
     * Removes the node at Position p and replaces it with its child, if any.
     *
     * @param p the relevant Position
     * @return element that was removed
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     * @throws IllegalArgumentException if p has two children.
     */
    public E remove(Position<E> p) throws IllegalArgumentException {
        Node<E> node = validate(p);
        if (node.getLeft() != null && node.getRight() != null)
            throw new IllegalArgumentException("p has two children");

        Node<E> child = (node.getLeft() != null ? node.getLeft() : node.getRight());
        if (child != null)
            child.setParent(node.getParent());

        if (node == root)
            root = child;
        else {
            Node<E> parent = node.getParent();
            if (node == parent.getLeft())
                parent.setLeft(child);
            else
                parent.setRight(child);
        }

        size--;
        E temp = node.getElement();
        node.setElement(null);
        node.setLeft(null);
        node.setRight(null);
        node.setParent(node); // convention for defunct node
        return temp;
    }

    public String toString() {
        return positions().toString();
    }

    public void createLevelOrder(ArrayList<E> l) {
        root = createLevelOrderHelper(l, null, 0);
        size = countNodes(root);
    }

    private Node<E> createLevelOrderHelper(java.util.ArrayList<E> l, Node<E> p, int i) {
        if (i < l.size()) {
            if (l.get(i) == null) return null;
            Node<E> node = createNode(l.get(i), p, null, null);
            node.setLeft(createLevelOrderHelper(l, node, 2 * i + 1));
            node.setRight(createLevelOrderHelper(l, node, 2 * i + 2));
            return node;
        }
        return null;
    }

    public void createLevelOrder(E[] arr) {
        this.root = createLevelOrderHelper(arr, null, 0);
        this.size = countNodes(this.root);
    }

    private Node<E> createLevelOrderHelper(E[] arr, Node<E> p, int i) {
        if (i < arr.length) {
            if (arr[i] == null) return null;
            Node<E> node = createNode(arr[i], p, null, null);
            node.setLeft(createLevelOrderHelper(arr, node, 2 * i + 1));
            node.setRight(createLevelOrderHelper(arr, node, 2 * i + 2));
            return node;
        }
        return null;
    }

    public void construct(E[] inorder, E[] preorder){
        //check input is valid
        if (inorder == null || preorder == null || inorder.length != preorder.length)
            throw new IllegalArgumentException("Invalid traversal arrays");
        // return root of the new tree
        this.root = constructHelper(inorder, 0, inorder.length - 1,
                preorder, 0, preorder.length - 1,
                null);

        // size of the tree is the length of the array --> no of nodes
        this.size = inorder.length;
    }

    private Node<E> constructHelper(E[] inorder, int inStart, int inEnd,
                                    E[] preorder, int preStart, int preEnd,
                                    Node<E> parent) {

        if (inStart > inEnd || preStart > preEnd)
            return null;

        // first element in preorder is root
        E rootElement = preorder[preStart];
        Node<E> node = createNode(rootElement, parent, null, null);

        // find root in inorder
        int rootIndex = inStart;
        while (rootIndex <= inEnd && !inorder[rootIndex].equals(rootElement)) {
            rootIndex++;
        }

        int leftSize = rootIndex - inStart;

        // build left subtree
        node.setLeft(
                constructHelper(inorder, inStart, rootIndex - 1,
                        preorder, preStart + 1, preStart + leftSize,
                        node)
        );

        // build right subtree
        node.setRight(
                constructHelper(inorder, rootIndex + 1, inEnd,
                        preorder, preStart + leftSize + 1, preEnd,
                        node)
        );

        return node;
    }

    // helper to calculate size properly after array/list level-order injection
    private int countNodes(Node<E> node) {
        if (node == null) return 0;
        return 1 + countNodes(node.getLeft()) + countNodes(node.getRight());
    }

    public String toBinaryTreeString() {
        BinaryTreePrinter<E> btp = new BinaryTreePrinter<>(this);
        return btp.print();
    }

    public int height() {
        return height(root);
    }

    public Iterable<Position<E>> inorder() {
        ArrayList<Position<E>> snapshot = new ArrayList<>();
        if (root != null)
            inorderSubtree(root, snapshot);
        return snapshot;
    }

    private void inorderSubtree(Position<E> p, ArrayList<Position<E>> snapshot) {
        if (left(p) != null)
            inorderSubtree(left(p), snapshot);
        snapshot.add(p);
        if (right(p) != null)
            inorderSubtree(right(p), snapshot);
    }

    private int height(Position<E> p) {
        if (p == null) return -1; // base case for empty subtree
        return 1 + Math.max(height(left(p)), height(right(p)));
    }

    /**
     * Nested static class for a binary tree node.
     */
    public static class Node<E> implements Position<E> {
        private E element;
        private Node<E> left, right, parent;

        public Node(E e, Node<E> p, Node<E> l, Node<E> r) {
            element = e;
            left = l;
            right = r;
            parent = p;
        }



        // accessor
        public E getElement() {
            return element;
        }

        // modifiers
        public void setElement(E e) {
            element = e;
        }

        public Node<E> getLeft() {
            return left;
        }

        public void setLeft(Node<E> n) {
            left = n;
        }

        public Node<E> getRight() {
            return right;
        }

        public void setRight(Node<E> n) {
            right = n;
        }

        public Node<E> getParent() {
            return parent;
        }

        public void setParent(Node<E> n) {
            parent = n;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (element == null) {
                sb.append("\u29B0");
            } else {
                sb.append(element);
            }
            return sb.toString();
        }
    }
}

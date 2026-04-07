package project20280.tree;

import org.junit.jupiter.api.Test;
import project20280.interfaces.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkedBinaryTreeTest {

    @Test
    void testSize() {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<Integer>();
        Position<Integer> root = bt.addRoot(1);
        assertEquals(1, bt.size());

        Position<Integer> l = bt.addLeft(root, 2);

        bt.remove(bt.root());
        assertEquals(1, bt.size());
    }

    @Test
    void testAddRoot() {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<Integer>();

        Integer c = Integer.parseInt("0");
        bt.addRoot(c);
        assertEquals(c, bt.root().getElement(), "root not added correctly");
    }

    @Test
    void testAddLeft() {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<Integer>();

        Integer c = Integer.parseInt("0");
        bt.addRoot(c);
        bt.addLeft(bt.root(), 1);
        assertEquals(1, bt.left(bt.root()).getElement());
    }

    @Test
    void testAddRight() {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<Integer>();

        Integer c = Integer.parseInt("0");
        bt.addRoot(c);
        bt.addRight(bt.root(), 1);
        assertEquals(1, bt.right(bt.root()).getElement());
    }

    @Test
    void testRemove() {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<Integer>();

        Integer c = Integer.parseInt("0");
        bt.addRoot(c);
        bt.addRight(bt.root(), 1);
        Integer old = bt.remove(bt.right(bt.root()));
        assertEquals(old, 1);
        assertEquals(1, bt.size());
    }

    @Test
    void testToString() {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<Integer>();

        Integer[] arr = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        bt.createLevelOrder(arr);
        //System.out.println(bt.toString());
        assertEquals("[8, 4, 9, 2, 10, 5, 11, 1, 12, 6, 3, 7]", bt.toString());
    }

    @Test
    void testCreateLevelOrder() {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<Integer>();

        Integer[] arr = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        bt.createLevelOrder(arr);
        //System.out.println(bt.toString());
        assertEquals("[8, 4, 9, 2, 10, 5, 11, 1, 12, 6, 3, 7]", bt.toString());
    }

    @Test
    void testInorder() {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<Integer>();

        Integer[] arr = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        bt.createLevelOrder(arr);
        //System.out.println(bt.toString());
        assertEquals("[8, 4, 9, 2, 10, 5, 11, 1, 12, 6, 3, 7]", bt.inorder().toString());
    }

    @Test
    void testDepth() {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<Integer>();

        Integer[] arr = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        bt.createLevelOrder(arr);

        assertEquals(0, bt.depth(bt.root()));
    }

    @Test
    void testHeight() {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<Integer>();

        Integer[] arr = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        bt.createLevelOrder(arr);

        assertEquals(3, bt.height());
    }

    @Test
    void testRootToPaths() {

        Integer[] inorder = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        Integer[] preorder = {5, 1, 0, 4, 2, 3, 7, 6, 8};

        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<>();
        bt.construct(inorder, preorder);

        System.out.println(bt.toBinaryTreeString());

        assertEquals(
                "[[5, 1, 0], [5, 1, 4, 2, 3], [5, 7, 6], [5, 7, 8]]",
                bt.rootToLeafPaths().toString()
        );
    }

}

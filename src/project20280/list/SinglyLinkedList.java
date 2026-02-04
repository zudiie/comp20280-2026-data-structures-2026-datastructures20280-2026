package project20280.list;

import project20280.interfaces.List;

import java.util.Iterator;

public class SinglyLinkedList<E> implements List<E> {

    private static class Node<E> {

        private final E element;            // reference to the element stored at this node

        /**
         * A reference to the subsequent node in the list
         */
        private Node<E> next;         // reference to the subsequent node in the list

        /**
         * Creates a node with the given element and next node.
         *
         * @param e the element to be stored
         * @param n reference to a node that should follow the new node
         */
        public Node(E e, Node<E> n, E element) {
            this.element = e;
            this.next = n;
        }

        // Accessor methods

        /**
         * Returns the element stored at the node.
         *
         * @return the element stored at the node
         */
        public E getElement() {
            return element;
        }

        /**
         * Returns the node that follows this one (or null if no such node).
         *
         * @return the following node
         */
        public Node<E> getNext() {
            return next;
        }

        // Modifier methods

        /**
         * Sets the node's next reference to point to Node n.
         *
         * @param n the node that should follow this one
         */
        public void setNext(Node<E> n) {
            next = n;
        }
    } //----------- end of nested Node class -----------

    /**
     * The head node of the list
     */
    private Node<E> head = null;               // head node of the list (or null if empty)


    /**
     * Number of nodes in the list
     */
    private int size = 0;                      // number of nodes in the list

    public SinglyLinkedList() {
    }              // constructs an initially empty list

    //@Override
    public int size() {
        int size = 0;
        Node<E> walk = head;
        while (walk != null) {
            size++;
            walk = walk.getNext();
        }
        return size;
    }

    //@Override
    public boolean isEmpty() {
        if (head == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public E get(int position) {
        Node<E> walk = head;
        E e = null;
        for (int i = 0; i < position; i++) {
            walk = walk.getNext();
        }
        return walk.element;
    }

    @Override
    public void add(int position, E e) {
        Node<E> walk = head;
        Node<E> newest = new Node<E>(e, null, null);
        if (position == 0) {
            head = new Node<E>(e, head, null);;
            size++;
        } else {
            for (int i = 1; i < position; i++) {
                if (walk.getNext() == null) {
                    throw new IllegalArgumentException("No such position");
                } else {
                    walk = walk.getNext();
                }
            }
            newest.setNext(walk.getNext());
            walk.setNext(newest);
            size++;
        }
    }


    @Override
    public void addFirst(E e) {
        head = new Node<E>(e, head, null);
        size++;
    }

    @Override
    public void addLast(E e) {
        Node<E> newest =  new Node<E>(e, null, null);
        Node<E> last = head;
        if (last == null) {
            head = newest;
        } else {
            while (last.getNext() != null) {
                last = last.getNext();
            }
            last.setNext(newest);
        }
        size++;
    }

    @Override
    public E remove(int position) {
        Node<E> walk = head;
        Node<E> prev = null;
        E e = null;
        for (int i = 0; i < position; i++) {
            if (walk.getNext() == null){
                return null;
            }
            prev = walk;
            walk = walk.getNext();
            e = walk.element;
        }
        prev.setNext(walk.getNext());
        size--;
        return e;
    }

    @Override
    public E removeFirst() {
        E e = null;
        if (head == null) {
            return null;
        } else {
            e =  head.element;
            head = head.getNext();
        }
        size--;
        return e;
    }

    public void reverse() {
        Node<E> prev = null;
        Node<E> curr = head;
        Node<E> next;
        while (curr != null) {
            next = curr.getNext();
            curr.setNext(prev);
            prev = curr;
            curr = next;
        }
        head = prev;
    }

    @Override
    public E removeLast() {
        Node<E> walk = head;
        E e = null;
        Node<E> prev = null;
        if (head == null) {
            return null;
        } else if (head.getNext() == null) {
            e = head.element;
            head = null;
        } else {
            while (walk.getNext() != null) {
                prev = walk;
                walk = walk.getNext();
            }
            prev.setNext(null);
            e =  walk.element;
        }
        size--;
        return e;
    }

    //@Override
    public Iterator<E> iterator() {
        return new SinglyLinkedListIterator<E>();
    }

    private class SinglyLinkedListIterator<E> implements Iterator<E> {
        Node<E> curr = (Node<E>) head;

        @Override
        public boolean hasNext() {
            return curr != null;
        }

        @Override
        public E next() {
            E res = curr.getElement();
            curr = curr.next;
            return res;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> curr = head;
        while (curr != null) {
            sb.append(curr.getElement());
            if (curr.getNext() != null)
                sb.append(", ");
            curr = curr.getNext();
        }
        sb.append("]");
        return sb.toString();
    }


    public static void main(String[] args) {
        SinglyLinkedList<Integer> ll = new SinglyLinkedList<Integer>();
        System.out.println("ll " + ll + " isEmpty: " + ll.isEmpty());
        //LinkedList<Integer> ll = new LinkedList<Integer>();
        System.out.println(ll.isEmpty());
        ll.addFirst(0);
        ll.addFirst(1);
        ll.addFirst(2);
        ll.addFirst(3);
        ll.addFirst(4);
        ll.addLast(-1);
        ll.reverse();
        System.out.println(ll);
        ll.removeLast();
        ll.removeFirst();
        //System.out.println("I accept your apology");
        //ll.add(3, 2);
        System.out.println(ll);
        ll.remove(5);
        System.out.println(ll);
        System.out.println(ll.isEmpty());

    }
}

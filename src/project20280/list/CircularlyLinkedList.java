package project20280.list;

import project20280.interfaces.List;

import java.util.Iterator;

public class CircularlyLinkedList<E> implements List<E> {

    private class Node<T> {
        private final T data;
        private Node<T> next;

        public Node(T e, Node<T> n) {
            data = e;
            next = n;
        }

        public T getData() {
            return data;
        }

        public void setNext(Node<T> n) {
            next = n;
        }

        public Node<T> getNext() {
            return next;
        }
    }

    private Node<E> tail = null;
    private int size = 0;

    public CircularlyLinkedList() {

    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public E get(int i) {
        Node<E> walk = tail.getNext();
        for (int j = 0; j < i; j++) {
            walk = walk.getNext();
        }
        return walk.getData();
    }

    /**
     * Inserts the given element at the specified index of the list, shifting all
     * subsequent elements in the list one position further to make room.
     *
     * @param i the index at which the new element should be stored
     * @param e the new element to be stored
     */
    @Override
    public void add(int i, E e) {
        if (i < 0 || i > size) {
            return;
        }
        if (size == 0) {
            Node<E> newNode = new Node<>(e, null);
            newNode.setNext(newNode);
            tail = newNode;
        } else if (i == 0) {
            Node<E> newNode = new Node<>(e, tail.getNext());
            tail.setNext(newNode);
        } else {
            Node<E> curr = tail.getNext();
            for (int j = 0; j < i - 1; j++) {
                curr = curr.getNext();
            }
            Node<E> newNode = new Node<>(e, curr.getNext());
            curr.setNext(newNode);
            if (i == size) {
                tail = newNode;
            }
        }
        size++;
    }

    @Override
    public E remove(int i) {
        if  (size == 0 || i < 0 || i >= size) {
            return null;
        }

        E removed;
        if (size == 1) {
            removed = tail.getData();
            tail = null;
        } else if (i == 0) {
            Node<E> head = tail.getNext();
            removed = head.getData();
            tail.setNext(head.getNext());
        } else {
            Node<E> curr = tail.getNext();
            for (int j = 0; j < i - 1; j++) {
                curr = curr.getNext();
            }
            Node<E> target = curr.getNext();
            removed = target.getData();
            curr.setNext(target.getNext());
            if (target == tail) {
                tail = curr;
            }
        }
        size--;
        return removed;
    }

    public void rotate() {
        if (tail == null || size <= 1) {
            return;
        }
        tail = tail.getNext();
    }

    private class CircularlyLinkedListIterator<E> implements Iterator<E> {
        Node<E> curr = (Node<E>) tail;

        @Override
        public boolean hasNext() {
            return curr != tail;
        }

        @Override
        public E next() {
            E res = curr.data;
            curr = curr.next;
            return res;
        }

    }

    @Override
    public Iterator<E> iterator() {
        return new CircularlyLinkedListIterator<E>();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E removeFirst() {
        return remove(0);
    }

    @Override
    public E removeLast() {
        return remove(size-1);
    }

    @Override
    public void addFirst(E e) {
        add(0, e);
    }

    @Override
    public void addLast(E e) {
        add(size, e);
    }


    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> curr = tail;
        do {
            curr = curr.next;
            sb.append(curr.data);
            if (curr != tail) {
                sb.append(", ");
            }
        } while (curr != tail);
        sb.append("]");
        return sb.toString();
    }


    public static void main(String[] args) {
        CircularlyLinkedList<Integer> ll = new CircularlyLinkedList<Integer>();
        for (int i = 10; i < 20; ++i) {
            ll.addLast(i);
        }

        System.out.println(ll);

        ll.removeFirst();
        System.out.println(ll);

        ll.removeLast();
        System.out.println(ll);

        ll.rotate();
        System.out.println(ll);

        ll.removeFirst();
        ll.rotate();
        System.out.println(ll);

        ll.removeLast();
        ll.rotate();
        System.out.println(ll);

        for (Integer e : ll) {
            System.out.println("value: " + e);
        }
    }
}

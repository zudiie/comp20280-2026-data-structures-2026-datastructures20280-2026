package project20280.list;

import project20280.interfaces.List;

import java.util.Iterator;

public class DoublyLinkedList<E> implements List<E> {

    private static class Node<E> {
        private final E data;
        private Node<E> next;
        private Node<E> prev;

        public Node(E e, Node<E> p, Node<E> n) {
            data = e;
            prev = p;
            next = n;
        }

        public E getData() {
            return data;
        }

        public Node<E> getNext() {
            return next;
        }

        public Node<E> getPrev() {
            return prev;
        }

        public void setNext(Node<E> n) {
            next = n;
        }

        public void setPrev(Node<E> p) {
            prev = p;
        }
    }

    private final Node<E> head;
    private final Node<E> tail;
    private int size = 0;

    public DoublyLinkedList() {
        head = new Node<E>(null, null, null);
        tail = new Node<E>(null, head, null);
        head.next = tail;
    }

    private void addBetween(E e, Node<E> pred, Node<E> succ) {
        Node<E> newNode = new Node<>(e, pred, succ);
        pred.setNext(newNode);
        succ.setPrev(newNode);
        size++;
    }

    @Override
    public int size() {
        Node<E> curr = head.getNext();
        int count = 0;
        while (curr.getNext() != null) {
            count++;
            curr = curr.getNext();
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        if (head.next == tail) return true;
        return false;
    }

    @Override
    public E get(int position) {
        Node<E> walk = head.getNext();
        E e = null;
        for (int i = 0; i < position; i++) {
            walk = walk.getNext();
        }
        return walk.data;
    }

    @Override
    public void add(int i, E e) {
        Node<E> curr = head.getNext();
        for  (int j = 0; j < i; j++) {
            curr = curr.getNext();
        }
        addBetween(e, curr.getPrev(), curr);
    }

    @Override
    public E remove(int i) {
        Node<E> curr = head.getNext();

        for (int j  = 0; j < i; j++) {
            curr = curr.getNext();
        }
        remove(curr);
        size--;
        return curr.data;
    }

    private class DoublyLinkedListIterator<E> implements Iterator<E> {
        Node<E> curr = (Node<E>) head.next;

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
        return new DoublyLinkedListIterator<E>();
    }

    private E remove(Node<E> n) {
        Node<E> curr = head.getNext();

        while (curr.getNext() != null) {
            if (curr == n) {
                if (curr.getPrev() == null) {
                    head.setNext(curr.getNext());
                } else if (curr.getNext() == null) {
                    curr.getPrev().setNext(null);
                } else {
                    curr.getPrev().setNext(curr.getNext());
                }
                size--;
                return curr.data;
            }
            curr = curr.getNext();
        }
        return null;
    }

    public E first() {
        if (isEmpty()) {
            return null;
        }
        return head.next.getData();
    }

    public E last() {
        if (isEmpty()) {
            return null;
        }
        return tail.prev.getData();
    }

    @Override
    public E removeFirst() {
        if (isEmpty()) return null;
        return remove(head.getNext());
    }

    @Override
    public E removeLast() {
        if (isEmpty()) return null;
        return remove(tail.getPrev());
    }

    @Override
    public void addLast(E e) {
        addBetween(e, tail.getPrev(), tail);
    }

    @Override
    public void addFirst(E e) {
        addBetween(e, head, head.getNext());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> curr = head.next;
        while (curr != tail) {
            sb.append(curr.data);
            curr = curr.next;
            if (curr != tail) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        DoublyLinkedList<Integer> ll = new DoublyLinkedList<Integer>();
        ll.addFirst(0);
        ll.addFirst(1);
        ll.addFirst(2);
        ll.addLast(-1);
        System.out.println(ll);

        ll.removeFirst();
        System.out.println(ll);

        ll.removeLast();
        System.out.println(ll);

        for (Integer e : ll) {
            System.out.println("value: " + e);
        }
    }
}
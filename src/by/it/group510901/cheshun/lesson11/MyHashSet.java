package by.it.group510901.cheshun.lesson11;

import java.util.Set;
import java.util.Iterator;
import java.util.Collection;

public class MyHashSet<E> implements Set<E> {

    private Node<E>[] table;
    private int size;
    private int threshold;
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public MyHashSet() {
        table = new Node[INITIAL_CAPACITY];
        threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
    }

    private static class Node<E> {
        E value;
        Node<E> next;

        Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }
    }

    private int hash(Object key) {
        int h = key == null ? 0 : key.hashCode();
        h ^= (h >>> 16);
        return h & (table.length - 1);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        table = new Node[INITIAL_CAPACITY];
        size = 0;
        threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean add(E e) {
        if (size >= threshold) {
            resize();
        }
        int index = hash(e);
        Node<E> current = table[index];
        while (current != null) {
            if (equalsNullable(current.value, e)) {
                return false;
            }
            current = current.next;
        }
        table[index] = new Node<>(e, table[index]);
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = hash(o);
        Node<E> current = table[index];
        Node<E> prev = null;
        while (current != null) {
            if (equalsNullable(current.value, o)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        int index = hash(o);
        Node<E> current = table[index];
        while (current != null) {
            if (equalsNullable(current.value, o)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        int newCapacity = oldTable.length * 2;
        Node<E>[] newTable = new Node[newCapacity];
        for (Node<E> head : oldTable) {
            Node<E> current = head;
            while (current != null) {
                Node<E> next = current.next;
                int newIndex = (current.value == null ? 0 : current.value.hashCode());
                newIndex ^= (newIndex >>> 16);
                newIndex &= (newCapacity - 1);
                current.next = newTable[newIndex];
                newTable[newIndex] = current;
                current = next;
            }
        }
        table = newTable;
        threshold = (int) (newCapacity * LOAD_FACTOR);
    }

    private static boolean equalsNullable(Object a, Object b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Node<E> head : table) {
            Node<E> current = head;
            while (current != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(current.value);
                first = false;
                current = current.next;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // ===== Остальные методы интерфейса Set =====

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int tableIndex = 0;
            private Node<E> currentNode = null;
            private Node<E> nextNode = null;
            private Node<E> lastReturned = null;

            {
                advance();
            }

            private void advance() {
                if (currentNode != null && currentNode.next != null) {
                    nextNode = currentNode.next;
                    return;
                }
                while (tableIndex < table.length) {
                    if (table[tableIndex] != null) {
                        nextNode = table[tableIndex];
                        tableIndex++;
                        return;
                    }
                    tableIndex++;
                }
                nextNode = null;
            }

            @Override
            public boolean hasNext() {
                return nextNode != null;
            }

            @Override
            public E next() {
                if (nextNode == null) throw new java.util.NoSuchElementException();
                lastReturned = nextNode;
                currentNode = nextNode;
                advance();
                return lastReturned.value;
            }

            @Override
            public void remove() {
                if (lastReturned == null) throw new IllegalStateException();
                MyHashSet.this.remove(lastReturned.value);
                lastReturned = null;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (E e : this) {
            result[i++] = e;
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }
        int i = 0;
        for (E e : this) {
            a[i++] = (T) e;
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            while (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }
}
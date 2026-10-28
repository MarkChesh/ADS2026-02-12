package by.it.group510901.cheshun.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {

    // Узел односвязного списка для хранения элементов
    private static class Node<E> {
        E element;
        Node<E> next;      // ссылка на следующий элемент в бакете (коллизии)
        Node<E> orderNext; // ссылка на следующий элемент в порядке добавления

        Node(E element) {
            this.element = element;
        }
    }

    private Node<E>[] table;        // массив бакетов
    private int size = 0;           // количество элементов
    private Node<E> head;           // начало списка порядка добавления
    private Node<E> tail;           // конец списка порядка добавления
    private static final int DEFAULT_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        table = new Node[DEFAULT_CAPACITY];
    }

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet(int capacity) {
        if (capacity <= 0) capacity = DEFAULT_CAPACITY;
        table = new Node[capacity];
    }


    private int indexFor(Object obj, int length) {
        if (obj == null) return 0;
        int hash = obj.hashCode();
        return (hash & 0x7FFFFFFF) % length;
    }


    private Node<E> findInBucket(int index, Object obj) {
        Node<E> current = table[index];
        while (current != null) {
            if (obj == null ? current.element == null : obj.equals(current.element)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }


    private void addToBucket(int index, Node<E> node) {
        node.next = table[index];
        table[index] = node;
    }


    private void removeFromBucket(int index, Node<E> node) {
        if (table[index] == node) {
            table[index] = node.next;
            return;
        }
        Node<E> current = table[index];
        while (current != null && current.next != node) {
            current = current.next;
        }
        if (current != null) {
            current.next = node.next;
        }
    }

    // Добавление узла в конец списка порядка
    private void linkOrder(Node<E> node) {
        if (tail == null) {
            head = tail = node;
        } else {
            tail.orderNext = node;
            tail = node;
        }
    }

    // Удаление узла из списка порядка
    private void unlinkOrder(Node<E> node) {
        if (head == node) {
            head = node.orderNext;
            if (head == null) tail = null;
            return;
        }
        Node<E> current = head;
        while (current != null && current.orderNext != node) {
            current = current.orderNext;
        }
        if (current != null) {
            current.orderNext = node.orderNext;
            if (tail == node) tail = current;
        }
    }

    // Перехеширование при увеличении размера
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        table = new Node[oldTable.length * 2];

        Node<E> current = head;
        while (current != null) {
            int index = indexFor(current.element, table.length);
            current.next = table[index];
            table[index] = current;
            current = current.orderNext;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        head = tail = null;
        size = 0;
    }

    @Override
    public boolean add(E element) {
        int index = indexFor(element, table.length);
        if (findInBucket(index, element) != null) {
            return false;
        }

        Node<E> newNode = new Node<>(element);
        addToBucket(index, newNode);
        linkOrder(newNode);
        size++;

        // Если load factor > 0.75 - увеличиваем таблицу
        if (size > table.length * 0.75) {
            resize();
        }
        return true;
    }

    @Override
    public boolean remove(Object obj) {
        int index = indexFor(obj, table.length);
        Node<E> node = findInBucket(index, obj);
        if (node == null) {
            return false;
        }
        removeFromBucket(index, node);
        unlinkOrder(node);
        size--;
        return true;
    }

    @Override
    public boolean contains(Object obj) {
        int index = indexFor(obj, table.length);
        return findInBucket(index, obj) != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (!contains(obj)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E element : c) {
            if (add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        // Создаём временный массив элементов для безопасного удаления
        Object[] elements = new Object[size];
        Node<E> current = head;
        int i = 0;
        while (current != null) {
            elements[i++] = current.element;
            current = current.orderNext;
        }

        for (Object obj : elements) {
            for (Object target : c) {
                if (obj == null ? target == null : obj.equals(target)) {
                    if (remove(obj)) {
                        modified = true;
                    }
                    break;
                }
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Object[] elements = new Object[size];
        Node<E> current = head;
        int i = 0;
        while (current != null) {
            elements[i++] = current.element;
            current = current.orderNext;
        }

        for (Object obj : elements) {
            boolean found = false;
            for (Object target : c) {
                if (obj == null ? target == null : obj.equals(target)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                remove(obj);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> current = head;
        boolean first = true;
        while (current != null) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.element);
            first = false;
            current = current.orderNext;
        }
        sb.append("]");
        return sb.toString();
    }

    // ===== Остальные методы интерфейса Set (заглушки) =====

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                E element = current.element;
                current = current.orderNext;
                return element;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        Node<E> current = head;
        int i = 0;
        while (current != null) {
            result[i++] = current.element;
            current = current.orderNext;
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        }
        Node<E> current = head;
        int i = 0;
        while (current != null) {
            a[i++] = (T) current.element;
            current = current.orderNext;
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
}
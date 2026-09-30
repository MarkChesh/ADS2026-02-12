package by.it.group510901.cheshun.lesson10;


import java.util.*;

public class MyPriorityQueue<E> implements Queue<E> {

    private static final int DEFAULT_CAPACITY = 11;
    private E[] heap;
    private int size;
    private Comparator<? super E> comparator;

    @SuppressWarnings("unchecked")
    public MyPriorityQueue() {
        this.heap = (E[]) new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = null;
    }

    @SuppressWarnings("unchecked")
    public MyPriorityQueue(Comparator<? super E> comparator) {
        this.heap = (E[]) new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public MyPriorityQueue(Collection<? extends E> c) {
        this.heap = (E[]) new Object[Math.max(DEFAULT_CAPACITY, c.size())];
        this.size = 0;
        this.comparator = null;
        for (E e : c) {
            add(e);
        }
    }

    // ============== Вспомогательные методы ==============

    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<? super E>) a).compareTo(b);
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > heap.length) {
            int newCapacity = heap.length + (heap.length >> 1) + 1;
            if (newCapacity < minCapacity) newCapacity = minCapacity;
            E[] newHeap = (E[]) new Object[newCapacity];
            System.arraycopy(heap, 0, newHeap, 0, size);
            heap = newHeap;
        }
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) >>> 1;
            if (compare(heap[index], heap[parent]) >= 0) break;
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        int half = size >>> 1;
        while (index < half) {
            int left = (index << 1) + 1;
            int right = left + 1;
            int smallest = left;
            if (right < size && compare(heap[right], heap[left]) < 0) {
                smallest = right;
            }
            if (compare(heap[index], heap[smallest]) <= 0) break;
            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        E tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    // ============== Обязательные методы ==============

    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean add(E element) {
        if (element == null) throw new NullPointerException();
        ensureCapacity(size + 1);
        heap[size] = element;
        siftUp(size);
        size++;
        return true;
    }

    @Override
    public E remove() {
        if (size == 0) throw new NoSuchElementException();
        return poll();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        if (o == null) return false;
        for (int i = 0; i < size; i++) {
            if (o.equals(heap[i])) return true;
        }
        return false;
    }

    @Override
    public boolean offer(E element) {
        return add(element);
    }

    @Override
    public E poll() {
        if (size == 0) return null;
        E result = heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        if (size > 0) siftDown(0);
        return result;
    }

    @Override
    public E peek() {
        return size == 0 ? null : heap[0];
    }

    @Override
    public E element() {
        if (size == 0) throw new NoSuchElementException();
        return heap[0];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
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
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        for (E e : c) {
            if (add(e)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        // Собираем элементы, которые нужно оставить
        for (int i = size - 1; i >= 0; i--) {
            if (c.contains(heap[i])) {
                removeAt(i);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        for (int i = size - 1; i >= 0; i--) {
            if (!c.contains(heap[i])) {
                removeAt(i);
                modified = true;
            }
        }
        return modified;
    }

    private E removeAt(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        E removed = heap[index];
        size--;
        if (index == size) {
            heap[size] = null;
        } else {
            heap[index] = heap[size];
            heap[size] = null;
            siftDown(index);
            siftUp(index);
        }
        return removed;
    }

    // ============== Остальные методы Queue/Collection ==============

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        System.arraycopy(heap, 0, arr, 0, size);
        return arr;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        }
        System.arraycopy(heap, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        for (int i = 0; i < size; i++) {
            if (o.equals(heap[i])) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;
            private int lastRet = -1;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (cursor >= size) throw new NoSuchElementException();
                lastRet = cursor;
                return heap[cursor++];
            }

            @Override
            public void remove() {
                if (lastRet < 0) throw new IllegalStateException();
                removeAt(lastRet);
                cursor = lastRet;
                lastRet = -1;
            }
        };
    }
}
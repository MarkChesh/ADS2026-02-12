package by.it.group510901.cheshun.lesson11;

import java.util.*;

public class MyTreeSet<E> implements Set<E> {

    private Object[] data;
    private int size;
    private final Comparator<? super E> comparator;

    public MyTreeSet() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    public MyTreeSet(Comparator<? super E> comparator) {
        this.data = new Object[10];
        this.size = 0;
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<? super E>) a).compareTo(b);
    }


    @SuppressWarnings("unchecked")
    private int binarySearch(Object o) {
        int low = 0, high = size - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = compare((E) o, (E) data[mid]);
            if (cmp < 0) high = mid - 1;
            else if (cmp > 0) low = mid + 1;
            else return mid;
        }
        return -(low + 1);
    }

    private void ensureCapacity(int min) {
        if (min > data.length) {
            int newCap = data.length + (data.length >> 1) + 1;
            if (newCap < min) newCap = min;
            data = Arrays.copyOf(data, newCap);
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
        for (int i = 0; i < size; i++) data[i] = null;
        size = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean add(E e) {
        if (e == null) throw new NullPointerException("null not allowed");
        int idx = binarySearch(e);
        if (idx >= 0) return false;
        int insertAt = -(idx + 1);
        ensureCapacity(size + 1);
        System.arraycopy(data, insertAt, data, insertAt + 1, size - insertAt);
        data[insertAt] = e;
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        int idx;
        try {
            idx = binarySearch(o);
        } catch (ClassCastException ex) {
            return false;
        }
        if (idx < 0) return false;
        System.arraycopy(data, idx + 1, data, idx, size - idx - 1);
        data[--size] = null;
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) return false;
        try {
            return binarySearch(o) >= 0;
        } catch (ClassCastException ex) {
            return false;
        }
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
        boolean changed = false;
        for (E e : c) {
            if (add(e)) changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            // удаляем все вхождения, но у нас Set — максимум одно
            while (remove(o)) changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        int w = 0;
        for (int r = 0; r < size; r++) {
            if (c.contains(data[r])) {
                data[w++] = data[r];
            } else {
                changed = true;
            }
        }
        for (int i = w; i < size; i++) data[i] = null;
        size = w;
        return changed;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int cursor = 0;
            boolean canRemove = false;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (cursor >= size) throw new NoSuchElementException();
                canRemove = true;
                return (E) data[cursor++];
            }

            @Override
            public void remove() {
                if (!canRemove) throw new IllegalStateException();
                MyTreeSet.this.remove(data[cursor - 1]);
                cursor--;
                canRemove = false;
            }
        };
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) Arrays.copyOf(data, size, a.getClass());
        }
        System.arraycopy(data, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(data[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
package by.it.group510901.cheshun.lesson12;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class MySplayMap implements NavigableMap<Integer, String> {

    private static class Node {
        Integer key; String value; Node left, right, parent;
        Node(Integer k, String v) { key = k; value = v; }
    }

    private Node root;
    private int size;


    private void splay(Node x) {
        while (x != null && x.parent != null) {
            Node p = x.parent, g = p.parent;
            if (g == null) { if (x == p.left) rotR(p); else rotL(p); }
            else if (x == p.left && p == g.left) { rotR(g); rotR(p); }
            else if (x == p.right && p == g.right) { rotL(g); rotL(p); }
            else if (x == p.left) { rotR(p); rotL(g); }
            else { rotL(p); rotR(g); }
        }
        if (x != null) root = x;
    }

    private void rotL(Node x) {
        Node y = x.right; x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y; else x.parent.right = y;
        y.left = x; x.parent = y;
    }

    private void rotR(Node x) {
        Node y = x.left; x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.right) x.parent.right = y; else x.parent.left = y;
        y.right = x; x.parent = y;
    }

    private Node find(Integer key) {
        Node cur = root, last = null;
        while (cur != null) {
            last = cur;
            int c = key.compareTo(cur.key);
            if (c == 0) { splay(cur); return cur; }
            cur = c < 0 ? cur.left : cur.right;
        }
        if (last != null) splay(last);
        return null;
    }

    // ---------- map ----------
    @Override public String put(Integer key, String value) {
        if (root == null) { root = new Node(key, value); size = 1; return null; }
        Node cur = root, last = null;
        while (cur != null) {
            last = cur;
            int c = key.compareTo(cur.key);
            if (c == 0) { String o = cur.value; cur.value = value; splay(cur); return o; }
            cur = c < 0 ? cur.left : cur.right;
        }
        Node n = new Node(key, value); n.parent = last;
        if (key.compareTo(last.key) < 0) last.left = n; else last.right = n;
        size++; splay(n); return null;
    }

    @Override public String remove(Object k) {
        if (!(k instanceof Integer)) return null;
        Node n = find((Integer) k);
        if (n == null) return null;
        String old = n.value;
        Node L = n.left, R = n.right;
        if (L != null) L.parent = null;
        if (R != null) R.parent = null;
        if (L == null) root = R;
        else {
            root = L;
            Node m = L; while (m.right != null) m = m.right;
            splay(m);
            m.right = R;
            if (R != null) R.parent = m;
        }
        if (root != null) root.parent = null;
        size--; return old;
    }

    @Override public String get(Object k) {
        if (!(k instanceof Integer)) return null;
        Node n = find((Integer) k);
        return n == null ? null : n.value;
    }

    @Override public boolean containsKey(Object k) {
        return k instanceof Integer && find((Integer) k) != null;
    }

    @Override public boolean containsValue(Object v) { return cv(root, v); }
    private boolean cv(Node n, Object v) {
        if (n == null) return false;
        if (v == null ? n.value == null : v.equals(n.value)) return true;
        return cv(n.left, v) || cv(n.right, v);
    }

    @Override public int size() { return size; }
    @Override public void clear() { root = null; size = 0; }
    @Override public boolean isEmpty() { return size == 0; }

    // ---------- navigable ----------
    @Override public Integer firstKey() {
        if (root == null) throw new NoSuchElementException();
        Node c = root; while (c.left != null) c = c.left; splay(c); return c.key;
    }

    @Override public Integer lastKey() {
        if (root == null) throw new NoSuchElementException();
        Node c = root; while (c.right != null) c = c.right; splay(c); return c.key;
    }

    @Override public Integer lowerKey(Integer k) {
        Node c = root; Integer r = null;
        while (c != null) { if (k.compareTo(c.key) > 0) { r = c.key; c = c.right; } else c = c.left; }
        return r;
    }

    @Override public Integer floorKey(Integer k) {
        Node c = root; Integer r = null;
        while (c != null) {
            int x = k.compareTo(c.key);
            if (x == 0) { splay(c); return c.key; }
            if (x > 0) { r = c.key; c = c.right; } else c = c.left;
        }
        return r;
    }

    @Override public Integer ceilingKey(Integer k) {
        Node c = root; Integer r = null;
        while (c != null) {
            int x = k.compareTo(c.key);
            if (x == 0) { splay(c); return c.key; }
            if (x < 0) { r = c.key; c = c.left; } else c = c.right;
        }
        return r;
    }

    @Override public Integer higherKey(Integer k) {
        Node c = root; Integer r = null;
        while (c != null) { if (k.compareTo(c.key) < 0) { r = c.key; c = c.left; } else c = c.right; }
        return r;
    }

    // ---------- headMap / tailMap ----------
    @Override public SortedMap<Integer, String> headMap(Integer k) { return headMap(k, false); }
    @Override public SortedMap<Integer, String> tailMap(Integer k) { return tailMap(k, true); }

    @Override public NavigableMap<Integer, String> headMap(Integer k, boolean inc) {
        MySplayMap r = new MySplayMap(); hm(root, k, inc, r); return r;
    }
    private void hm(Node n, Integer k, boolean inc, MySplayMap r) {
        if (n == null) return;
        int c = n.key.compareTo(k);
        if (c < 0 || (inc && c == 0)) { hm(n.left, k, inc, r); r.put(n.key, n.value); hm(n.right, k, inc, r); }
        else hm(n.left, k, inc, r);
    }

    @Override public NavigableMap<Integer, String> tailMap(Integer k, boolean inc) {
        MySplayMap r = new MySplayMap(); tm(root, k, inc, r); return r;
    }
    private void tm(Node n, Integer k, boolean inc, MySplayMap r) {
        if (n == null) return;
        int c = n.key.compareTo(k);
        if (c > 0 || (inc && c == 0)) { tm(n.left, k, inc, r); r.put(n.key, n.value); tm(n.right, k, inc, r); }
        else tm(n.right, k, inc, r);
    }

    // ---------- toString ----------
    @Override public String toString() {
        StringBuilder sb = new StringBuilder("{");
        ts(root, sb, new boolean[]{true});
        return sb.append("}").toString();
    }
    private void ts(Node n, StringBuilder sb, boolean[] first) {
        if (n == null) return;
        ts(n.left, sb, first);
        if (!first[0]) sb.append(", ");
        sb.append(n.key).append("=").append(n.value);
        first[0] = false;
        ts(n.right, sb, first);
    }

    // ---------- remaining NavigableMap ----------
    private static class E implements Map.Entry<Integer, String> {
        Integer k; String v;
        E(Integer k, String v) { this.k = k; this.v = v; }
        public Integer getKey() { return k; }
        public String getValue() { return v; }
        public String setValue(String x) { String o = v; v = x; return o; }
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return (k == null ? e.getKey() == null : k.equals(e.getKey()))
                    && (v == null ? e.getValue() == null : v.equals(e.getValue()));
        }
        public int hashCode() { return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode()); }
        public String toString() { return k + "=" + v; }
    }

    @Override public Map.Entry<Integer, String> lowerEntry(Integer k) { Integer x = lowerKey(k); return x == null ? null : new E(x, get(x)); }
    @Override public Map.Entry<Integer, String> floorEntry(Integer k) { Integer x = floorKey(k); return x == null ? null : new E(x, get(x)); }
    @Override public Map.Entry<Integer, String> ceilingEntry(Integer k) { Integer x = ceilingKey(k); return x == null ? null : new E(x, get(x)); }
    @Override public Map.Entry<Integer, String> higherEntry(Integer k) { Integer x = higherKey(k); return x == null ? null : new E(x, get(x)); }
    @Override public Map.Entry<Integer, String> firstEntry() { return isEmpty() ? null : new E(firstKey(), get(firstKey())); }
    @Override public Map.Entry<Integer, String> lastEntry() { return isEmpty() ? null : new E(lastKey(), get(lastKey())); }
    @Override public Map.Entry<Integer, String> pollFirstEntry() { Map.Entry<Integer, String> e = firstEntry(); if (e != null) remove(e.getKey()); return e; }
    @Override public Map.Entry<Integer, String> pollLastEntry() { Map.Entry<Integer, String> e = lastEntry(); if (e != null) remove(e.getKey()); return e; }

    @Override public NavigableMap<Integer, String> descendingMap() {
        MySplayMap r = new MySplayMap(); dm(root, r); return r;
    }
    private void dm(Node n, MySplayMap r) { if (n == null) return; dm(n.right, r); r.put(n.key, n.value); dm(n.left, r); }

    @Override public NavigableMap<Integer, String> subMap(Integer f, boolean fi, Integer t, boolean ti) {
        MySplayMap r = new MySplayMap(); sm(root, f, fi, t, ti, r); return r;
    }
    private void sm(Node n, Integer f, boolean fi, Integer t, boolean ti, MySplayMap r) {
        if (n == null) return;
        boolean geF = n.key.compareTo(f) > 0 || (fi && n.key.compareTo(f) == 0);
        boolean leT = n.key.compareTo(t) < 0 || (ti && n.key.compareTo(t) == 0);
        if (geF) sm(n.left, f, fi, t, ti, r);
        if (geF && leT) r.put(n.key, n.value);
        if (leT) sm(n.right, f, fi, t, ti, r);
    }

    @Override public SortedMap<Integer, String> subMap(Integer f, Integer t) { return subMap(f, true, t, false); }
    @Override public Comparator<? super Integer> comparator() { return null; }

    @Override public Set<Map.Entry<Integer, String>> entrySet() {
        List<Map.Entry<Integer, String>> list = new ArrayList<>();
        es(root, list);
        return new LinkedHashSet<>(list);
    }
    private void es(Node n, List<Map.Entry<Integer, String>> l) { if (n == null) return; es(n.left, l); l.add(new E(n.key, n.value)); es(n.right, l); }

    @Override public Set<Integer> keySet() {
        List<Integer> l = new ArrayList<>(); ks(root, l); return new LinkedHashSet<>(l);
    }
    private void ks(Node n, List<Integer> l) { if (n == null) return; ks(n.left, l); l.add(n.key); ks(n.right, l); }

    @Override public Collection<String> values() {
        List<String> l = new ArrayList<>(); vs(root, l); return l;
    }
    private void vs(Node n, List<String> l) { if (n == null) return; vs(n.left, l); l.add(n.value); vs(n.right, l); }

    @Override public NavigableSet<Integer> navigableKeySet() { throw new UnsupportedOperationException(); }
    @Override public NavigableSet<Integer> descendingKeySet() { throw new UnsupportedOperationException(); }
    @Override public void putAll(Map<? extends Integer, ? extends String> m) {
        for (Map.Entry<? extends Integer, ? extends String> e : m.entrySet()) put(e.getKey(), e.getValue());
    }
}
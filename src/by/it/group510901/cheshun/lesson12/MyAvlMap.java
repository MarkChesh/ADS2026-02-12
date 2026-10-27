package by.it.group510901.cheshun.lesson12;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyAvlMap implements Map<Integer, String> {

    private class Node {
        Integer key;
        String value;
        Node left, right;
        int height;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private Node root;
    private int size;

    public MyAvlMap() {
        root = null;
        size = 0;
    }

    // ---------- AVL helpers ----------

    private int height(Node n) {
        return n == null ? 0 : n.height;
    }

    private void updateHeight(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    private int balanceFactor(Node n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t2 = x.right;
        x.right = y;
        y.left = t2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t2 = y.left;
        y.left = x;
        x.right = t2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    private Node balance(Node n) {
        updateHeight(n);
        int bf = balanceFactor(n);

        if (bf > 1) {
            if (balanceFactor(n.left) < 0) {
                n.left = rotateLeft(n.left);
            }
            return rotateRight(n);
        }
        if (bf < -1) {
            if (balanceFactor(n.right) > 0) {
                n.right = rotateRight(n.right);
            }
            return rotateLeft(n);
        }
        return n;
    }

    private Node insert(Node node, Integer key, String value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }
        if (key.compareTo(node.key) < 0) {
            node.left = insert(node.left, key, value);
        } else if (key.compareTo(node.key) > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }
        return balance(node);
    }

    private Node minNode(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private Node removeNode(Node node, Integer key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeNode(node.left, key);
        } else if (cmp > 0) {
            node.right = removeNode(node.right, key);
        } else {
            if (node.left == null || node.right == null) {
                Node child = (node.left != null) ? node.left : node.right;
                size--;
                return child;
            } else {
                Node successor = minNode(node.right);
                node.key = successor.key;
                node.value = successor.value;
                // удаляем successor, но не уменьшаем size повторно
                node.right = removeNodeWithoutSize(node.right, successor.key);
            }
        }
        return balance(node);
    }

    private Node removeNodeWithoutSize(Node node, Integer key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeNodeWithoutSize(node.left, key);
        } else if (cmp > 0) {
            node.right = removeNodeWithoutSize(node.right, key);
        } else {
            if (node.left == null || node.right == null) {
                return (node.left != null) ? node.left : node.right;
            } else {
                Node successor = minNode(node.right);
                node.key = successor.key;
                node.value = successor.value;
                node.right = removeNodeWithoutSize(node.right, successor.key);
            }
        }
        return balance(node);
    }

    private Node find(Node node, Integer key) {
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp == 0) return node;
            node = (cmp < 0) ? node.left : node.right;
        }
        return null;
    }

    // ---------- Map API ----------

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) return false;
        return find(root, (Integer) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValueHelper(root, value);
    }

    private boolean containsValueHelper(Node node, Object value) {
        if (node == null) return false;
        if (value == null ? node.value == null : value.equals(node.value)) return true;
        return containsValueHelper(node.left, value) || containsValueHelper(node.right, value);
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Node n = find(root, (Integer) key);
        return n == null ? null : n.value;
    }

    @Override
    public String put(Integer key, String value) {
        Node existing = find(root, key);
        String old = existing == null ? null : existing.value;
        root = insert(root, key, value);
        return old;
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Node n = find(root, (Integer) key);
        if (n == null) return null;
        String old = n.value;
        root = removeNode(root, (Integer) key);
        return old;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        for (Map.Entry<? extends Integer, ? extends String> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public Set<Integer> keySet() {
        return new AbstractSet<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    private final java.util.Stack<Node> stack = new java.util.Stack<>();
                    private Node current = root;

                    private void pushLeft(Node n) {
                        while (n != null) {
                            stack.push(n);
                            n = n.left;
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        if (current != null) {
                            pushLeft(current);
                            current = null;
                        }
                        return !stack.isEmpty();
                    }

                    @Override
                    public Integer next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        Node n = stack.pop();
                        current = n.right;
                        return n.key;
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public Collection<String> values() {
        java.util.List<String> list = new java.util.ArrayList<>();
        for (Integer k : keySet()) {
            list.add(get(k));
        }
        return list;
    }

    @Override
    public Set<Map.Entry<Integer, String>> entrySet() {
        return new AbstractSet<Map.Entry<Integer, String>>() {
            @Override
            public Iterator<Map.Entry<Integer, String>> iterator() {
                final Iterator<Integer> keys = keySet().iterator();
                return new Iterator<Map.Entry<Integer, String>>() {
                    @Override
                    public boolean hasNext() {
                        return keys.hasNext();
                    }

                    @Override
                    public Map.Entry<Integer, String> next() {
                        Integer k = keys.next();
                        final String v = get(k);
                        return new Map.Entry<Integer, String>() {
                            @Override
                            public Integer getKey() { return k; }
                            @Override
                            public String getValue() { return v; }
                            @Override
                            public String setValue(String value) {
                                return put(k, value);
                            }
                        };
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Integer k : keySet()) {
            if (!first) sb.append(", ");
            sb.append(k).append("=").append(get(k));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}

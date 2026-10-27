package by.it.group510901.cheshun.lesson12;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class MyRbMap implements SortedMap<Integer, String> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node {
        Integer key;
        String value;
        Node left, right, parent;
        boolean color;

        Node(Integer key, String value, boolean color, Node parent) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.parent = parent;
        }
    }

    private Node root;
    private int size;

    public MyRbMap() {
        this.size = 0;
    }

    // ==================== ОБЯЗАТЕЛЬНЫЕ МЕТОДЫ ====================

    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException("Key cannot be null");
        if (root == null) {
            root = new Node(key, value, BLACK, null);
            size = 1;
            return null;
        }

        Node current = root;
        Node parent = null;
        int cmp = 0;

        while (current != null) {
            parent = current;
            cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                String oldValue = current.value;
                current.value = value;
                return oldValue;
            }
        }

        Node newNode = new Node(key, value, RED, parent);
        if (cmp < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        size++;
        fixAfterInsert(newNode);
        return null;
    }

    @Override
    public String remove(Object key) {
        Node node = findNode((Integer) key);
        if (node == null) return null;
        String oldValue = node.value;
        deleteNode(node);
        size--;
        return oldValue;
    }

    @Override
    public String get(Object key) {
        Node node = findNode((Integer) key);
        return node == null ? null : node.value;
    }

    @Override
    public boolean containsKey(Object key) {
        return findNode((Integer) key) != null;
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
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Integer firstKey() {
        if (root == null) throw new NoSuchElementException();
        return minimum(root).key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) throw new NoSuchElementException();
        return maximum(root).key;
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        MyRbMap result = new MyRbMap();
        addLessThan(root, toKey, result);
        return result;
    }

    private void addLessThan(Node node, Integer toKey, MyRbMap result) {
        if (node == null) return;
        if (node.key.compareTo(toKey) < 0) {
            addLessThan(node.left, toKey, result);
            result.put(node.key, node.value);
            addLessThan(node.right, toKey, result);
        } else {
            addLessThan(node.left, toKey, result);
        }
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        MyRbMap result = new MyRbMap();
        addGreaterOrEqual(root, fromKey, result);
        return result;
    }

    private void addGreaterOrEqual(Node node, Integer fromKey, MyRbMap result) {
        if (node == null) return;
        if (node.key.compareTo(fromKey) >= 0) {
            addGreaterOrEqual(node.left, fromKey, result);
            result.put(node.key, node.value);
            addGreaterOrEqual(node.right, fromKey, result);
        } else {
            addGreaterOrEqual(node.right, fromKey, result);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        inOrder(root, sb);
        if (sb.length() > 1) sb.setLength(sb.length() - 2); // Убираем последнюю запятую и пробел
        sb.append("}");
        return sb.toString();
    }

    private void inOrder(Node node, StringBuilder sb) {
        if (node == null) return;
        inOrder(node.left, sb);
        sb.append(node.key).append("=").append(node.value).append(", ");
        inOrder(node.right, sb);
    }

    // ==================== ЗАГЛУШКИ ДЛЯ КОМПИЛЯЦИИ ====================
    // Требуются интерфейсом SortedMap, но не нужны по заданию.

    @Override
    public Set<Integer> keySet() {
        return new HashSet<>();
    }

    @Override
    public Collection<String> values() {
        return new ArrayList<>();
    }

    @Override
    public Set<Map.Entry<Integer, String>> entrySet() {
        return new HashSet<>();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        // Заглушка
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        return new MyRbMap(); // Заглушка
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЕРЕВА ====================

    private Node findNode(Integer key) {
        if (key == null) return null;
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) current = current.left;
            else if (cmp > 0) current = current.right;
            else return current;
        }
        return null;
    }

    private Node minimum(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private Node maximum(Node node) {
        while (node.right != null) node = node.right;
        return node;
    }

    private void fixAfterInsert(Node node) {
        while (node != root && node.parent.color == RED) {
            if (node.parent == node.parent.parent.left) {
                Node uncle = node.parent.parent.right;
                if (uncle != null && uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                Node uncle = node.parent.parent.left;
                if (uncle != null && uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    private void deleteNode(Node node) {
        if (node.left != null && node.right != null) {
            Node successor = minimum(node.right);
            node.key = successor.key;
            node.value = successor.value;
            node = successor;
        }

        Node child = (node.left != null) ? node.left : node.right;

        if (child != null) {
            child.parent = node.parent;
            if (node.parent == null) root = child;
            else if (node == node.parent.left) node.parent.left = child;
            else node.parent.right = child;

            if (node.color == BLACK) {
                fixAfterDelete(child);
            }
        } else if (node.parent == null) {
            root = null;
        } else {
            if (node.color == BLACK) {
                fixAfterDelete(node);
            }
            if (node == node.parent.left) node.parent.left = null;
            else node.parent.right = null;
            node.parent = null;
        }
    }

    private void fixAfterDelete(Node node) {
        while (node != root && colorOf(node) == BLACK) {
            if (node == node.parent.left) {
                Node sibling = node.parent.right;
                if (colorOf(sibling) == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateLeft(node.parent);
                    sibling = node.parent.right;
                }
                if (colorOf(sibling.left) == BLACK && colorOf(sibling.right) == BLACK) {
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    if (colorOf(sibling.right) == BLACK) {
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rotateRight(sibling);
                        sibling = node.parent.right;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    sibling.right.color = BLACK;
                    rotateLeft(node.parent);
                    node = root;
                }
            } else {
                Node sibling = node.parent.left;
                if (colorOf(sibling) == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateRight(node.parent);
                    sibling = node.parent.left;
                }
                if (colorOf(sibling.left) == BLACK && colorOf(sibling.right) == BLACK) {
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    if (colorOf(sibling.left) == BLACK) {
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        rotateLeft(sibling);
                        sibling = node.parent.left;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    sibling.left.color = BLACK;
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }
        node.color = BLACK;
    }

    private boolean colorOf(Node node) {
        return node == null ? BLACK : node.color;
    }

    private void rotateLeft(Node node) {
        Node right = node.right;
        node.right = right.left;
        if (right.left != null) right.left.parent = node;
        right.parent = node.parent;
        if (node.parent == null) root = right;
        else if (node == node.parent.left) node.parent.left = right;
        else node.parent.right = right;
        right.left = node;
        node.parent = right;
    }

    private void rotateRight(Node node) {
        Node left = node.left;
        node.left = left.right;
        if (left.right != null) left.right.parent = node;
        left.parent = node.parent;
        if (node.parent == null) root = left;
        else if (node == node.parent.right) node.parent.right = left;
        else node.parent.left = left;
        left.right = node;
        node.parent = left;
    }
}
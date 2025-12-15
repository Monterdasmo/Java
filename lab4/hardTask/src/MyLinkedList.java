/**
 * MyLinkedList - реалізація на основі двозв'язного списку
 * Hard Task - Завдання 1
 */
public class MyLinkedList implements MyList {
    
    private static class Node {
        Object data;
        Node next;
        Node prev;

        Node(Object data, Node prev, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node first;
    private Node last;
    private int size;

    public MyLinkedList() {
        this.first = null;
        this.last = null;
        this.size = 0;
    }

    @Override
    public void add(Object e) {
        linkLast(e);
    }

    @Override
    public void add(int index, Object element) {
        checkPositionIndex(index);
        if (index == size) {
            linkLast(element);
        } else {
            linkBefore(element, node(index));
        }
    }

    @Override
    public void addAll(Object[] c) {
        if (c == null || c.length == 0) return;
        for (Object element : c) {
            linkLast(element);
        }
    }

    @Override
    public void addAll(int index, Object[] c) {
        if (c == null || c.length == 0) return;
        checkPositionIndex(index);
        for (int i = 0; i < c.length; i++) {
            add(index + i, c[i]);
        }
    }

    @Override
    public Object get(int index) {
        checkElementIndex(index);
        return node(index).data;
    }

    @Override
    public Object remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }

    @Override
    public void set(int index, Object element) {
        checkElementIndex(index);
        node(index).data = element;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node x = first; x != null; x = x.next) {
                if (x.data == null) return index;
                index++;
            }
        } else {
            for (Node x = first; x != null; x = x.next) {
                if (o.equals(x.data)) return index;
                index++;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node x = first; x != null; x = x.next) {
            result[i++] = x.data;
        }
        return result;
    }

    private void linkLast(Object e) {
        final Node l = last;
        final Node newNode = new Node(e, l, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        size++;
    }

    private void linkBefore(Object e, Node succ) {
        final Node pred = succ.prev;
        final Node newNode = new Node(e, pred, succ);
        succ.prev = newNode;
        if (pred == null) {
            first = newNode;
        } else {
            pred.next = newNode;
        }
        size++;
    }

    private Object unlink(Node x) {
        final Object element = x.data;
        final Node next = x.next;
        final Node prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.data = null;
        size--;
        return element;
    }

    private Node node(int index) {
        if (index < (size >> 1)) {
            Node x = first;
            for (int i = 0; i < index; i++) {
                x = x.next;
            }
            return x;
        } else {
            Node x = last;
            for (int i = size - 1; i > index; i--) {
                x = x.prev;
            }
            return x;
        }
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        Node x = first;
        while (x != null) {
            sb.append(x.data);
            x = x.next;
            if (x != null) sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }
}

import java.util.Arrays;

/**
 * MyLinkedHashSet - зберігає елементи в порядку додавання.
 * Використовує хеш-таблицю + двозв'язний список.
 */
public class MyLinkedHashSet<E> {

    private static class Entry<E> {
        E value;
        Entry<E> next;     // bucket chain
        Entry<E> before;   // insertion order
        Entry<E> after;    // insertion order
        int hash;

        Entry(E value, int hash, Entry<E> next) {
            this.value = value;
            this.hash = hash;
            this.next = next;
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<E>[] table;
    private int size;
    private Entry<E> head;
    private Entry<E> tail;

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        table = new Entry[DEFAULT_CAPACITY];
        size = 0;
        head = null;
        tail = null;
    }

    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("MyLinkedHashSet does not support null values");
        }

        int hash = hash(value);
        int index = indexFor(hash, table.length);

        for (Entry<E> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && value.equals(e.value)) {
                return false;
            }
        }

        addEntry(value, hash, index);
        return true;
    }

    public boolean remove(Object value) {
        if (value == null) return false;

        int hash = hash(value);
        int index = indexFor(hash, table.length);

        Entry<E> prev = null;
        Entry<E> e = table[index];

        while (e != null) {
            Entry<E> next = e.next;
            if (e.hash == hash && value.equals(e.value)) {
                if (prev == null) {
                    table[index] = next;
                } else {
                    prev.next = next;
                }
                removeFromLinkedList(e);
                size--;
                return true;
            }
            prev = e;
            e = next;
        }

        return false;
    }

    public boolean contains(Object value) {
        if (value == null) return false;

        int hash = hash(value);
        int index = indexFor(hash, table.length);

        for (Entry<E> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && value.equals(e.value)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        Arrays.fill(table, null);
        head = null;
        tail = null;
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public E[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Entry<E> e = head; e != null; e = e.after) {
            result[i++] = e.value;
        }
        return (E[]) result;
    }

    private void addEntry(E value, int hash, int index) {
        if (size + 1 > (int) (table.length * LOAD_FACTOR)) {
            resize();
            index = indexFor(hash, table.length);
        }

        Entry<E> e = new Entry<>(value, hash, table[index]);
        table[index] = e;

        // append to insertion-order list
        if (tail == null) {
            head = tail = e;
        } else {
            tail.after = e;
            e.before = tail;
            tail = e;
        }

        size++;
    }

    private void removeFromLinkedList(Entry<E> e) {
        Entry<E> before = e.before;
        Entry<E> after = e.after;

        if (before == null) {
            head = after;
        } else {
            before.after = after;
            e.before = null;
        }

        if (after == null) {
            tail = before;
        } else {
            after.before = before;
            e.after = null;
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<E>[] oldTable = table;
        int newCapacity = oldTable.length * 2;
        Entry<E>[] newTable = new Entry[newCapacity];

        for (Entry<E> e = head; e != null; e = e.after) {
            int index = indexFor(e.hash, newCapacity);
            e.next = newTable[index];
            newTable[index] = e;
        }

        table = newTable;
    }

    private int hash(Object key) {
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (Entry<E> e = head; e != null; e = e.after) {
            sb.append(e.value);
            if (e.after != null) sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }
}

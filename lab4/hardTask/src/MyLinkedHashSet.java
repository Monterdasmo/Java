import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * MyLinkedHashSet з підтримкою Generics та Wildcards
 * Custom Collections 2.0 - Hard Task 2
 * 
 * Особливості:
 * - Generic типізація <E>
 * - Wildcards для гнучкості (? extends E, ? super E, ?)
 * - Збереження порядку додавання
 * - O(1) операції add/remove/contains
 */
public class MyLinkedHashSet<E> implements Iterable<E> {
    
    /**
     * Entry з generic типом
     */
    private static class Entry<E> {
        E value;
        Entry<E> next;     // для hash bucket
        Entry<E> before;   // для порядку
        Entry<E> after;    // для порядку
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

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        table = new Entry[initialCapacity];
        size = 0;
        head = null;
        tail = null;
    }

    /**
     * Додає елемент в set
     */
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("LinkedHashSet does not support null values");
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

    /**
     * ⭐ WILDCARD: ? extends E
     * Додає всі елементи з колекції
     * Приймає колекцію будь-якого типу, що є підтипом E
     * 
     * Приклад:
     * MyLinkedHashSet<Number> numbers = new MyLinkedHashSet<>();
     * List<Integer> integers = Arrays.asList(1, 2, 3);
     * numbers.addAll(integers); // Integer extends Number
     */
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }
        
        boolean modified = false;
        for (E element : c) {
            if (add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * ⭐ WILDCARD: ?
     * Перевіряє чи містить set всі елементи з колекції
     * Приймає колекцію будь-якого типу (unbounded wildcard)
     * 
     * Приклад:
     * MyLinkedHashSet<String> set = new MyLinkedHashSet<>();
     * List<Object> objects = Arrays.asList("test", 123);
     * set.containsAll(objects); // можна передати будь-яку колекцію
     */
    public boolean containsAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }
        
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * ⭐ WILDCARD: ?
     * Видаляє всі елементи, що містяться в колекції
     */
    public boolean removeAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }
        
        boolean modified = false;
        for (Object element : c) {
            if (remove(element)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * ⭐ WILDCARD: ?
     * Залишає тільки елементи, що містяться в колекції
     */
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }
        
        boolean modified = false;
        Entry<E> current = head;
        
        while (current != null) {
            Entry<E> next = current.after;
            if (!c.contains(current.value)) {
                remove(current.value);
                modified = true;
            }
            current = next;
        }
        
        return modified;
    }

    /**
     * ⭐ WILDCARD: ? super E
     * Копіює всі елементи в іншу колекцію
     * Приймає колекцію супертипу E
     * 
     * Приклад:
     * MyLinkedHashSet<Integer> integers = new MyLinkedHashSet<>();
     * Collection<Number> numbers = new ArrayList<>();
     * integers.copyTo(numbers); // Number - супертип Integer
     */
    public void copyTo(Collection<? super E> destination) {
        if (destination == null) {
            throw new NullPointerException("Destination cannot be null");
        }
        
        for (E element : this) {
            destination.add(element);
        }
    }

    /**
     * ⭐ WILDCARD: ? extends E (статичний метод)
     * Створює новий MyLinkedHashSet з елементів колекції
     */
    public static <T> MyLinkedHashSet<T> copyOf(Collection<? extends T> c) {
        MyLinkedHashSet<T> set = new MyLinkedHashSet<>();
        set.addAll(c);
        return set;
    }

    /**
     * ⭐ WILDCARD: ? extends E
     * Перетин множин - залишає тільки спільні елементи
     */
    public MyLinkedHashSet<E> intersection(MyLinkedHashSet<? extends E> other) {
        if (other == null) {
            throw new NullPointerException("Other set cannot be null");
        }
        
        MyLinkedHashSet<E> result = new MyLinkedHashSet<>();
        for (E element : this) {
            if (other.contains(element)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * ⭐ WILDCARD: ? extends E
     * Об'єднання множин
     */
    public MyLinkedHashSet<E> union(MyLinkedHashSet<? extends E> other) {
        if (other == null) {
            throw new NullPointerException("Other set cannot be null");
        }
        
        MyLinkedHashSet<E> result = new MyLinkedHashSet<>();
        result.addAll(this.toCollection());
        result.addAll(other.toCollection());
        return result;
    }

    /**
     * ⭐ WILDCARD: ? extends E
     * Різниця множин - елементи які є в this, але немає в other
     */
    public MyLinkedHashSet<E> difference(MyLinkedHashSet<? extends E> other) {
        if (other == null) {
            throw new NullPointerException("Other set cannot be null");
        }
        
        MyLinkedHashSet<E> result = new MyLinkedHashSet<>();
        for (E element : this) {
            if (!other.contains(element)) {
                result.add(element);
            }
        }
        return result;
    }

    // Базові методи (без змін)

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

    /**
     * Повертає колекцію для використання з wildcards
     */
    private Collection<E> toCollection() {
        java.util.ArrayList<E> list = new java.util.ArrayList<>(size);
        for (E element : this) {
            list.add(element);
        }
        return list;
    }

    // Iterator для Iterable
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Entry<E> current = head;
            private Entry<E> lastReturned = null;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                lastReturned = current;
                E value = current.value;
                current = current.after;
                return value;
            }

            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                MyLinkedHashSet.this.remove(lastReturned.value);
                lastReturned = null;
            }
        };
    }

    // Внутрішні методи

    private void addEntry(E value, int hash, int index) {
        if (size >= table.length * LOAD_FACTOR) {
            resize();
            index = indexFor(hash, table.length);
        }

        Entry<E> e = new Entry<>(value, hash, table[index]);
        table[index] = e;

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

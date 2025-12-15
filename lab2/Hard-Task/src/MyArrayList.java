import java.util.Arrays;
import java.util.RandomAccess;

public class MyArrayList<E> implements MyList<E>, RandomAccess {
    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;
    private int size;

    public MyArrayList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    @Override
    public void add(E e) {
        ensureCapacity(size + 1);
        elements[size++] = e;
    }

    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    @Override
    public void addAll(Iterable<? extends E> items) {
        if (items == null) return;
        for (E e : items) add(e);
    }

    @Override
    public void addAll(int index, Iterable<? extends E> items) {
        if (items == null) return;
        checkPositionIndex(index);

        Object[] tmp = toObjectArray(items);
        if (tmp.length == 0) return;

        ensureCapacity(size + tmp.length);
        System.arraycopy(elements, index, elements, index + tmp.length, size - index);
        System.arraycopy(tmp, 0, elements, index, tmp.length);
        size += tmp.length;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkElementIndex(index);
        return (E) elements[index];
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkElementIndex(index);
        E oldValue = (E) elements[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null;
        return oldValue;
    }

    @Override
    public void set(int index, E element) {
        checkElementIndex(index);
        elements[index] = element;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) if (elements[i] == null) return i;
        } else {
            for (int i = 0; i < size; i++) if (o.equals(elements[i])) return i;
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) newCapacity = minCapacity;
            elements = Arrays.copyOf(elements, newCapacity);
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

    private Object[] toObjectArray(Iterable<? extends E> items) {
        MyArrayList<E> buffer = new MyArrayList<>();
        for (E e : items) buffer.add(e);
        return buffer.toArray();
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }
}

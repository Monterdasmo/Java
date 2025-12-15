public interface MyList<E> {
    void add(E e);
    void add(int index, E element);

    void addAll(Iterable<? extends E> items);
    void addAll(int index, Iterable<? extends E> items);

    E get(int index);
    E remove(int index);
    void set(int index, E element);

    int indexOf(Object o);
    int size();

    Object[] toArray();
}

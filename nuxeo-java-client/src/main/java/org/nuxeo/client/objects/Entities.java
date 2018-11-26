package org.nuxeo.client.objects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Container holding a {@link List} of {@link Entity}.
 *
 * @param <E> The underlying entity type.
 * @since 3.2
 */
public class Entities<E> extends Entity implements Iterable<E> {

    protected List<E> entries = new ArrayList<>();

    protected Entities(String entityType) {
        super(entityType);
    }

    protected Entities(String entityType, List<? extends E> entries) {
        this(entityType);
        this.entries.addAll(entries);
    }

    public List<E> getEntries() {
        return entries;
    }

    public Stream<E> streamEntries() {
        return entries.stream();
    }

    public E getEntry(int index) {
        return entries.get(index);
    }

    @JsonIgnore
    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public boolean addEntry(E entry) {
        return entries.add(entry);
    }

    public void sort(Comparator<? super E> comparator) {
        entries.sort(comparator);
    }

    @Override
    public Iterator<E> iterator() {
        return entries.iterator();
    }
}

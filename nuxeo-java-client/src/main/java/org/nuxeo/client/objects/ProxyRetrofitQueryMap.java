package org.nuxeo.client.objects;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DON'T USE IT!
 * <p/>
 * This map exists to workaround retrofit's issue: https://github.com/square/retrofit/issues/1324
 * <p/>
 * Retrofit doesn't handle a multivalued map.
 */
public class ProxyRetrofitQueryMap extends HashMap<String, Serializable> {

    public ProxyRetrofitQueryMap(Map<String, Serializable> m) {
        super(m);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Entry<String, Serializable>> entrySet() {
        Set<Entry<String, Serializable>> originSet = super.entrySet();
        Set<Entry<String, Serializable>> newSet = new HashSet<>();

        for (Entry<String, Serializable> entry : originSet) {
            String entryKey = entry.getKey();
            if (entryKey == null) {
                throw new IllegalArgumentException("Query map contained null key.");
            }
            Object entryValue = entry.getValue();
            if (entryValue == null) {
                throw new IllegalArgumentException("Query map contained null value for key '" + entryKey + "'.");
            } else if (entryValue instanceof Iterable) {
                for (Serializable arrayValue : (Iterable<Serializable>) entryValue) {
                    if (arrayValue != null) { // Skip null values
                        Entry<String, Serializable> newEntry = new AbstractMap.SimpleEntry<>(entryKey, arrayValue);
                        newSet.add(newEntry);
                    }
                }
            } else {
                newSet.add(entry);
            }
        }
        return newSet;
    }
}
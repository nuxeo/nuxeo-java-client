package org.nuxeo.client.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0
 */
public class DataSet extends Document {

    public static final String FIELDS_PROPERTY = "ds:fields";


    public DataSet(String uid) {
        super(uid, "DataSet");
    }

    @SuppressWarnings("unchecked")
    public DataSet(Document document) {
        super(document);
        // convert to sub object as Jackson doesn't know our adapter during unmarshalling
        this.<List>getPropertyValue(FIELDS_PROPERTY).replaceAll(map -> new Field((Map<String, Object>) map));
    }

    public Field getField(int index) {
        List<Field> fields = getFields();
        if (fields == null) {
            return null;
        }
        return fields.get(index);
    }

    public List<Field> getFields() {
        return getPropertyValue(FIELDS_PROPERTY);
    }

    public void setFields(List<Field> fields) {
        setPropertyValue(FIELDS_PROPERTY, fields);
    }

    public boolean addField(Field field) {
        return getFieldsForUpdate().add(field);
    }

    protected List<Field> getFieldsForUpdate() {
        List<Field> fields = getFields();
        if (fields == null) {
            fields = new ArrayList<>();
        }
        setFields(fields);
        return fields;
    }
}

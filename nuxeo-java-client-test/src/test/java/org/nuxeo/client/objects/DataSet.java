package org.nuxeo.client.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0
 */
public class DataSet extends Document {

    protected List<Field> fields = new ArrayList<>();

    public DataSet(String file, String dataSet) {
        super(file, dataSet);
    }

    @SuppressWarnings("unchecked")
    public DataSet(Document document) {
        super(document);
        for (Map<String, Object> fieldProps : (List<Map<String, Object>>) document.getPropertyValue("ds:fields")) {
            fields.add(new Field(fieldProps));
        }
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}

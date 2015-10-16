package org.olu.jsondb;

import org.olu.jsondb.Storable;

/**
 * Created by ovidiu.latcu on 9/11/2015.
 */
public class DataObject implements Storable {
    private String id;
    private String name;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

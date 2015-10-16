package org.olu.jsondb;

/**
 * Interface that should be implemented by objects that are stored using {@link LightDB}.
 * CRUD operations are done based on this ID.
 * <p/>
 * Created by Ovy9086 on 7/11/2015.
 */
public interface Storable {

    String getId();

}

package org.olu.jsondb;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ovy9086 on 7/4/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class LightDbTest {

    @Rule
    public TemporaryFolder tempFolderRule = new TemporaryFolder();

    LightDB<DataObject> testedDb;
    File dbFile;

    @Before
    public void setup() throws IOException {
        tempFolderRule.create();
        dbFile = tempFolderRule.newFile("db.json");
        testedDb = new LightDB<>(DataObject.class, dbFile, new Gson());
    }

    @Test
    public void testPutWritesToFile() {
        testedDb.put(aDataObjectWithId("a"));
        assertTrue(dbFile.length() > 0);
    }

    @Test
    public void testDataObjectWrittenCorrectly() {
        String id = "abc";
        testedDb.put(aDataObjectWithId(id));
        assertEquals(id, testedDb.getAll().get(0).getId());
    }

    @Test
    public void testPutStoresAllData() {
        testedDb.put(aDataObjectWithId("a"));
        testedDb.put(aDataObjectWithId("b"));
        assertEquals(2, testedDb.getAll().size());
    }

    @Test
    public void testPutSameObjectUpdatesTheExistingOne() {
        testedDb.put(aDataObjectWithId("a"));
        testedDb.put(aDataObjectWithId("b"));
        testedDb.put(aDataObjectWithId("a"));
        testedDb.put(aDataObjectWithId("b"));
        assertEquals(2, testedDb.getAll().size());
    }

    @Test
    public void testRemoveReturnsTrueWhenItemRemoved() {
        testedDb.put(aDataObjectWithId("a"));
        testedDb.put(aDataObjectWithId("b"));
        assertTrue(testedDb.remove(aDataObjectWithId("a")));
        assertEquals(1, testedDb.getAll().size());
    }

    @Test
    public void testRemoveHasNoEffectIfItemDoesNotExist() {
        testedDb.put(aDataObjectWithId("a"));
        testedDb.put(aDataObjectWithId("b"));
        assertFalse(testedDb.remove(aDataObjectWithId("c")));
        assertEquals(2, testedDb.getAll().size());
    }

    @Test
    public void testGetByIdReturnsItemIfExists() {
        String itemId = "ITEM_ID";
        String name = "dummy_name";
        DataObject DataObject = aDataObjectWithId(itemId);
        DataObject.setName(name);
        testedDb.put(DataObject);
        DataObject fetchedDataObject = testedDb.getById(itemId);
        assertNotNull(fetchedDataObject);
        assertEquals(name, fetchedDataObject.getName());
    }

    @Test
    public void testGetByIdReturnsNullIfItemDoesNotExist() {
        String notFoundId = "not_found_id";
        DataObject DataObject = testedDb.getById(notFoundId);
        assertNull(DataObject);
    }

    @Test
    public void testRemoveObject() {
        String id = "ID";
        DataObject DataObject = aDataObjectWithId(id);
        testedDb.put(DataObject);
        testedDb.remove(DataObject);
        assertEquals(0, testedDb.getAll().size());
    }

    @Test
    public void testRemoveById() {
        String id = "ID";
        DataObject DataObject = aDataObjectWithId(id);
        testedDb.put(DataObject);
        testedDb.remove(id);
        assertEquals(0, testedDb.getAll().size());
    }

    @Test
    public void testRemoveAll() {
        testedDb.put(aDataObjectWithId("a"));
        testedDb.put(aDataObjectWithId("b"));
        testedDb.removeAll();
        assertEquals(0, testedDb.getAll().size());
    }

    public DataObject aDataObjectWithId(String id) {
        DataObject DataObject = new DataObject();
        DataObject.setId(id);
        return DataObject;
    }


}

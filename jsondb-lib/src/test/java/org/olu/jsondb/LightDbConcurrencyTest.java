package org.olu.jsondb;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.olu.jsondb.DataObject;
import org.olu.jsondb.LightDB;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ovy9086 on 7/4/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class LightDbConcurrencyTest {

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
    public void testMultipleReads() throws InterruptedException {
        testedDb.putAll(aListOfDataObjectsOfSize(100));

        Thread read1Thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 50; i++) {
                    testedDb.getAll();
                }
            }
        };

        Thread read2Thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 50; i++) {
                    testedDb.getAll();
                }
            }
        };

        read1Thread.start();
        read2Thread.start();

        read1Thread.join();
        read2Thread.join();
    }

    @Test
    public void testReadsWhileUpdatingItems() throws InterruptedException {
        testedDb.putAll(aListOfDataObjectsOfSize(100));

        Thread readTread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 100; i++) {
                    assertEquals(100, testedDb.getAll().size());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread updateThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Random random = new Random();
                for (int i = 0; i < 20; i++) {
                    DataObject dataObject = testedDb.getById(
                            "" + random.nextInt(50));
                    testedDb.put(dataObject);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        readTread.start();
        updateThread.start();

        readTread.join();
        updateThread.join();

    }

    @Test
    public void testMultipleWrites() throws InterruptedException {
        Thread writeThread1 = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 1; i <= 100; i++) {
                    DataObject dataObject = aDataObjectWithId(i + "");
                    testedDb.put(dataObject);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread writeThread2 = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 101; i <= 200; i++) {
                    DataObject dataObject = aDataObjectWithId(i + "");
                    testedDb.put(dataObject);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        writeThread1.start();
        writeThread2.start();

        writeThread1.join();
        writeThread2.join();

        assertEquals(200, testedDb.getAll().size());
        for (int i = 1; i <= 200; i++) {
            assertTrue("Object with id " + i + " not found in DB", testedDb.exists(i + ""));
        }
    }

    public DataObject aDataObjectWithId(String id) {
        DataObject dataObject = new DataObject();
        dataObject.setId(id);
        return dataObject;
    }

    public List<DataObject> aListOfDataObjectsOfSize(int size) {
        List<DataObject> dataObjectList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            dataObjectList.add(aDataObjectWithId("" + i));
        }
        return dataObjectList;
    }

}

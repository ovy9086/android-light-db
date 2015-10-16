package org.olu.jsondb;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Simple class persisting generic items in JSON format in a local file.
 * <p/>
 * Created by ovidiu.latcu on 7/3/2015.
 */
public class LightDB<T extends Storable> {

    private final Class<T> dataType;
    private final File file;
    private final Gson gson;

    /**
     * Static lock to be shared across instances of this "db". Will prevent multiple threads
     * to read the file while others are writting it.
     */
    private final Object fileLock = new Object();

    public LightDB(Class<T> dataType, File file, Gson gson) {
        this.dataType = dataType;
        this.file = file;
        this.gson = gson;
    }

    @NonNull
    public List<T> getAll() {
        synchronized (fileLock) {
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(file);
                JsonParser jsonParser = new JsonParser();
                JsonElement root = jsonParser.parse(new JsonReader(fileReader));
                List<T> data = new ArrayList<>();
                if (root != null && root.isJsonArray()) {
                    JsonArray rootArray = root.getAsJsonArray();
                    for (JsonElement json : rootArray) {
                        try {
                            data.add(gson.fromJson(json, dataType));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return data;
                }
                return new ArrayList<>();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSilently(fileReader);
            }
            return new ArrayList<>();
        }
    }

    public void put(@NonNull T object) {
        synchronized (fileLock) {
            List<T> all = getAll();
            boolean found = false;
            for (int i = 0; i < all.size(); i++) {
                if (object.getId().equals(all.get(i).getId())) {
                    all.set(i, object);
                    found = true;
                    break;
                }
            }
            if (!found) {
                all.add(object);
            }
            writeToFile(all);
        }
    }

    public void putAsync(@NonNull final T object) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                put(object);
            }
        }).start();
    }

    public boolean remove(@NonNull T object) {
        return removeItemWithId(object.getId());
    }

    public boolean remove(@NonNull String id) {
        return removeItemWithId(id);
    }

    public void removeAll() {
        writeToFile(new ArrayList<T>());
    }

    @Nullable
    public T getById(String id) {
        List<T> all = getAll();
        for (T item : all) {
            if (id.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    public boolean exists(@NonNull String id) {
        return getById(id) != null;
    }

    private boolean removeItemWithId(@NonNull String id) {
        List<T> list = getAll();
        Iterator<T> iterator = list.iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(id)) {
                iterator.remove();
                found = true;
            }
        }
        writeToFile(list);
        return found;
    }

    public void putAll(@NonNull List<T> all) {
        writeToFile(all);
    }

    public void putAllAsync(@NonNull final List<T> all) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeToFile(all);
            }
        }).start();
    }

    private void writeToFile(List<T> data) {
        synchronized (fileLock) {
            Type listType = new TypeToken<List<T>>() {
            }.getType();
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
                gson.toJson(data, listType, new JsonWriter(fileWriter));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSilently(fileWriter);
            }
        }
    }

    private static void closeSilently(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void closeSilently(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package org.olu.jsondb;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ovidiu.latcu on 7/9/2015.
 */
public class LightDBFactory {

    public static <T extends Storable> LightDB<T> get(Class<T> forType, Context context) {
        return new LightDB<>(forType, createFileFor(forType, context), new Gson());
    }

    public static <T extends Storable> File createFileFor(Class<T> type, Context context) {
        File filesDir = context.getFilesDir();
        File file = new File(filesDir, type.getSimpleName());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static <T extends Storable>Date getLastDateModified(Class<T> type, Context context) {
        return new Date(createFileFor(type, context).lastModified());
    }
}

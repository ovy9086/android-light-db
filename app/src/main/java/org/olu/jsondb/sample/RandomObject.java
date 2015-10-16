package org.olu.jsondb.sample;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Ovy9086 on 10/13/2015.
 */
public class RandomObject {

    private String s1;
    private String s2;
    private String s3;
    private float f1;
    private float f2;
    private float f3;
    private long l1;
    private long l2;
    private long l3;

    private RandomObject() {
    }

    public static RandomObject newObject() {
        Random random = new Random();
        RandomObject object = new RandomObject();
        object.s1 = UUID.randomUUID().toString();
        object.s2 = UUID.randomUUID().toString();
        object.s3 = UUID.randomUUID().toString();
        object.f1 = random.nextFloat();
        object.f2 = random.nextFloat();
        object.f3 = random.nextFloat();
        object.l1 = random.nextLong();
        object.l2 = random.nextLong();
        object.l3 = random.nextLong();
        return object;
    }

}

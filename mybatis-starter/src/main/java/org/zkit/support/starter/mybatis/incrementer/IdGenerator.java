package org.zkit.support.starter.mybatis.incrementer;

import lombok.Getter;

import java.util.Date;
import java.util.UUID;

/**
 * compressed id generator, result id not great than 53bits before 2318-06-04.
 */
public class IdGenerator {

    @Getter
    private static IdGenerator instance = new IdGenerator(0);

    public static IdGenerator initDefaultInstance(int machineId) {
        instance = new IdGenerator(machineId);
        return instance;
    }

    public static long generateId() {
        return instance.nextId();
    }

    // total bits=53(max 2^53-1：9007199254740992-1)

    // private final static long TIME_BIT = 40; // max: 2318-06-04
    private final static long MACHINE_BIT = 5; // max 31
    private final static long SEQUENCE_BIT = 8; // 256/10ms

    /**
     * mask/max value
     */
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIMESTAMP_LEFT = MACHINE_BIT + SEQUENCE_BIT;

    private final long machineId;
    private long sequence = 0L;
    private long lastStmp = -1L;

    private IdGenerator(long machineId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException(
                    "machineId can't be greater than " + MAX_MACHINE_NUM + " or less than 0");
        }
        this.machineId = machineId;
    }

    /**
     * generate new ID
     *
     */
    public synchronized long nextId() {
        long currStmp = getTimestamp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                currStmp = getNextTimestamp();
            }
        } else {
            sequence = 0L;
        }

        lastStmp = currStmp;

        return currStmp << TIMESTAMP_LEFT //
                | machineId << MACHINE_LEFT //
                | sequence;
    }

    private long getNextTimestamp() {
        long mill = getTimestamp();
        while (mill <= lastStmp) {
            mill = getTimestamp();
        }
        return mill;
    }

    private long getTimestamp() {
        // per 10ms
        return System.currentTimeMillis() / 10;// 10ms
    }

    public static Date parseIdTimestamp(long id) {
        return new Date((id >>> TIMESTAMP_LEFT) * 10);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}

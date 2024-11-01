package org.zkit.support.starter.boot.utils;

import java.time.Duration;

public class TimeUtils {

    public static Duration of(String time) {
        // 去除所有空格
        time = time.replaceAll("\\s", "");

        // 提取数字和单位
        long number = Long.parseLong(time.replaceAll("[^0-9]", ""));
        String unit = time.replaceAll("[0-9]", "");

        // 根据单位创建 Duration 对象并返回毫秒数
        return switch (unit) {
            case "ms", "millis" -> Duration.ofMillis(number);
            case "s", "sec", "second", "seconds" -> Duration.ofSeconds(number);
            case "min", "minutes" -> Duration.ofMinutes(number);
            case "h", "hour", "hours" -> Duration.ofHours(number);
            case "d", "day", "days" -> Duration.ofDays(number);
            default -> throw new IllegalArgumentException("Unsupported time unit: " + unit);
        };
    }

}

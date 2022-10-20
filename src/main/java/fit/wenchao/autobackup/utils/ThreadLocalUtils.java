package fit.wenchao.autobackup.utils;

import fit.wenchao.autobackup.exception.BackendException;
import fit.wenchao.autobackup.model.RespCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ThreadLocalUtils {

    private static final ThreadLocal<Map<String, String>> threadInfoHolder = new ThreadLocal<>();

    public static void setThreadInfo(Map<String, String> threadInfo) {
        threadInfoHolder.set(threadInfo);
    }

    public static void putThreadInfo(String key, String value) {
        Map<String, String> stringStringMap = threadInfoHolder.get();
        if(stringStringMap == null) {
            stringStringMap = new HashMap<>();
        }
        setThreadInfo(stringStringMap);
        stringStringMap.put(key, value);
    }

    public static String getThreadInfo(String key) {
        return Optional.ofNullable(threadInfoHolder.get()).map((map) -> map.get(key)).orElse(null);
    }
    public static void removeThreadInfo() {
        threadInfoHolder.remove();
    }
}
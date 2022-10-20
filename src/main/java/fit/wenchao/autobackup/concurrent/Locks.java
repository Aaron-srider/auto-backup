package fit.wenchao.autobackup.concurrent;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Locks {

    private final Object fileLock = new Object();

    private final Object newTaskLock = new Object();

    private final Object cancelTaskLock = new Object();

    private final Object addTemplateArtLinkLock = new Object();

    private final Object createArtLock = new Object();


    public static String getStringLock(String ... args) {
        StringBuilder lockBuilder = new StringBuilder();
        lockBuilder.append("lockString");
        for (String arg : args) {
            lockBuilder.append("-");
            lockBuilder.append(arg);
        }
        lockBuilder.append("-");
        lockBuilder.append("lockString");
        return lockBuilder.toString().intern();
    }



}
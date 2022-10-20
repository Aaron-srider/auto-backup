package fit.wenchao.autobackup.utils;

public class ExceptionUtils {
    public static Throwable getRootCause(Throwable throwable) {
        Throwable cur = throwable;
        Throwable par = throwable.getCause();
        while(par!=null) {
            cur = par;
            par = par.getCause();
        }
        return cur;
    }
}

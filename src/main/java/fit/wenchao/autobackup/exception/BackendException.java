package fit.wenchao.autobackup.exception;

import fit.wenchao.autobackup.model.RespCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BackendException extends RuntimeException{
    private Object data;
    private String code;
    private String msg;

    public BackendException (Object data, RespCode respCode) {
        this.data = data;
        this.code = respCode.name();
        this.msg = respCode.getMsg();
    }


    public BackendException (Throwable cause, Object data, RespCode respCode) {
        super(cause);
        this.data = data;
        this.code = respCode.name();
        this.msg = respCode.getMsg();
    }
}
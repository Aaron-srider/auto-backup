package fit.wenchao.autobackup.exception;

import fit.wenchao.autobackup.model.JsonResult;
import fit.wenchao.autobackup.model.ParameterCheckResult;
import fit.wenchao.autobackup.model.RespCode;
import fit.wenchao.autobackup.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

@ControllerAdvice
@Slf4j
//@CrossOrigin
public class GlobalExceptionHandler {
    public String buildErrorMsg(Throwable ex) {
        if(ex == null) {
            return "null";
        }
        StringBuilder errMsgBuilder = new StringBuilder();
        errMsgBuilder.append("Error Class: \n")
                .append("\t").append(ex.getClass().getTypeName())
                .append("\n")
                .append("Error Msg: \n")
                .append("\t").append(ex.getMessage()).append("\n");
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if(ex.getStackTrace()!=null) {
            errMsgBuilder .append("Error StackTrace: \n");
            for (StackTraceElement stackTraceElement : stackTrace) {
                errMsgBuilder.append("\t[").append(stackTraceElement.toString()).append("]\n");
            }
        }
        return errMsgBuilder.toString();
    }
    public static void main(String[] args) {
        try {
            Files.deleteIfExists(Paths.get("/Users/cw/test/dir"));
            Files.createDirectories(Paths.get("/Users/cw/test/dir"));
        } catch(Exception ex) {
            StringBuilder errMsgBuilder = new StringBuilder();
            errMsgBuilder.append("Error Class: \n")
                    .append("\t").append(ex.getClass().getTypeName())
                    .append("\n")
                    .append("Error Msg: \n")
                    .append("\t").append(ex.getMessage()).append("\n");
            StackTraceElement[] stackTrace = ex.getStackTrace();
            if(ex.getStackTrace()!=null) {
                errMsgBuilder .append("Error StackTrace: \n");
                for (StackTraceElement stackTraceElement : stackTrace) {
                    errMsgBuilder.append("\t[").append(stackTraceElement.toString()).append("]\n");
                }
            }

            System.out.println(errMsgBuilder.toString());

        }
    }

    /**
     * 统一处理错误返回值
     */
    @ExceptionHandler({BackendException.class})
    @ResponseBody
    public JsonResult errorCodeException(HttpServletResponse req, BackendException ex) {
        //cors(req);
        JsonResult jsonResult = new JsonResult();
        jsonResult.setData(ex.getData());
        jsonResult.setCode(ex.getCode());
        String msg = ex.getMsg();
        if(ex.getCause() !=null) {
            msg = buildErrorMsg(ex.getCause());
        }
        jsonResult.setMsg(msg);
        log.error("Error:{}", jsonResult);
        return jsonResult;
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public JsonResult paramValidateException(Exception ex) {
        BindingResult bindingResult = null;
        try {
            bindingResult = ClassUtils.getFieldValue(ex, "bindingResult", BindingResult.class);
        } catch (NoSuchFieldException e) {
            log.error("获取参数校验信息失败");
            JsonResult jsonResult = JsonResult.of(null, RespCode.FRONT_END_PARAMS_ERROR);
            return jsonResult;
        }
        ParameterCheckResult parameterCheckResult = bindingResultPackager(bindingResult);
        JsonResult jsonResult = JsonResult.of(parameterCheckResult, RespCode.FRONT_END_PARAMS_ERROR);
        log.error("Error:{}", jsonResult);
        return jsonResult;
    }

    /**
     * 统一处理参数校验异常
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public JsonResult constraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        ParameterCheckResult parameterCheckResult = new ParameterCheckResult();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            parameterCheckResult.putResult(
                    getLastPathNode(constraintViolation.getPropertyPath()),
                    constraintViolation.getMessage()
            );
        }

        return JsonResult.of(parameterCheckResult, RespCode.FRONT_END_PARAMS_ERROR);
    }

    private static String getLastPathNode(Path path) {
        String wholePath = path.toString();
        int i = wholePath.lastIndexOf(".");
        if (i != -1) {
            String substring = wholePath.substring(i + 1, wholePath.length());
            return substring;
        }
        return wholePath;
    }

    private ParameterCheckResult bindingResultPackager(BindingResult bindingResult) {
        ParameterCheckResult parameterCheckResult = new ParameterCheckResult();
        for (ObjectError objectError : bindingResult.getAllErrors()) {
            FieldError fieldError = (FieldError) objectError;
            parameterCheckResult.putResult(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return parameterCheckResult;
    }




    /**
     * 统一处理其他后端异常
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public JsonResult otherException(HttpServletResponse req, Exception ex) {
        log.error("Server Exception-Name:{}，Server Exception-Msg:{}", ex.getClass().getTypeName(),ex.getMessage());
        if(ex instanceof HttpMessageNotReadableException || ex instanceof MissingServletRequestParameterException) {
            return this.errorCodeException(req, new BackendException(null, RespCode.FRONT_END_PARAMS_ERROR));
        }

        ex.printStackTrace();
        JsonResult jsonResult = new JsonResult();
        jsonResult.setData(null);
        jsonResult.setCode("500");
        jsonResult.setMsg("Server internal error occurred --> [ " + ex.getClass().getTypeName() + "-->" + ex.getMessage()
         + " ]");
        return jsonResult;
    }

}
package fit.wenchao.autobackup.model;

import fit.wenchao.autobackup.model.RespCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "GlobalApiResponse", description = "后端接口统一的返回结构")
public class JsonResult {
    @ApiModelProperty(value = "返回的有效载荷，可以是一个对象，也可以是一个列表，或者二者相互嵌套。随接口而异。")

    private Object data;
    @ApiModelProperty(value = "返回代码，一个数字字符串。可以是错误代码，也可以是正确代码")

    private String code;
    @ApiModelProperty(value = "返回的可读（readable）信息，对返回的载荷和代码作简单解释。")
    private String msg;

    public static JsonResult of(Object data, RespCode respCode) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.data = data;
        jsonResult.code = respCode.name();
        jsonResult.msg = respCode.getMsg();
        return jsonResult;
    }

    public static JsonResult of(Object data, String code, String msg) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.data = data;
        jsonResult.code = code;
        jsonResult.msg = msg;
        return jsonResult;
    }

    public static JsonResult ok() {
        return new JsonResult(null, RespCode.SUCCESS.name(), RespCode.SUCCESS.getMsg());
    }

    public static JsonResult ok(Object data) {
        return new JsonResult(data, RespCode.SUCCESS.name(), RespCode.SUCCESS.getMsg());
    }
}
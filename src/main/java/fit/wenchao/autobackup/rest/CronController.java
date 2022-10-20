package fit.wenchao.autobackup.rest;


import cn.hutool.core.codec.Base64;
import fit.wenchao.autobackup.exception.BackendException;
import fit.wenchao.autobackup.model.JsonResult;
import fit.wenchao.autobackup.model.RespCode;
import fit.wenchao.autobackup.utils.ResponseEntityUtils;
import fit.wenchao.utils.dateUtils.DateUtils;
import org.apache.ibatis.io.ResolverUtil;
import org.quartz.CronExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.Date;

@RestController
@Validated
public class CronController {

    @GetMapping("/cron/validation")
    public ResponseEntity cronValid(@NotNull @NotEmpty String cron) {
        cron = Base64.decodeStr(cron);
        try {
            CronUtils.getNextExecuteTime(cron, new Date());
        }catch (IllegalArgumentException ex) {
            return ResponseEntityUtils.ok(JsonResult.ok( RespCode.INVALID_CRON.toString()));
        }

        return ResponseEntityUtils.ok(JsonResult.ok(RespCode.SUCCESS.toString()));
    }

    public static void main(String[] args) {
        String encode = Base64.encode("0 0 1 * * ?");
        System.out.println(encode);
    }
}

 class CronUtils {

    /**
     * 根据 Cron表达式和开始时间，得到下次执行时间
     *
     * @param cron
     * @param startDate
     * @return
     */
    public static Date getNextExecuteTime(String cron, Date startDate) {
        try {
            CronExpression cronExpression = new CronExpression(cron);
            return cronExpression.getNextValidTimeAfter(startDate == null ? new Date() : startDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("无效的cron表达式:" + cron, e);
        }
    }

     public static void main(String[] args) {
         Date nextExecuteTime = getNextExecuteTime("0 0 0 * * ?g", new Date());
         String s = DateUtils.formatDate(nextExecuteTime);
         System.out.println(s);
     }

}

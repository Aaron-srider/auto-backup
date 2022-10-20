package fit.wenchao.autobackup.rest;


import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import fit.wenchao.autobackup.dao.po.DbConnPO;
import fit.wenchao.autobackup.dao.repo.DbConnDao;
import fit.wenchao.autobackup.exception.BackendException;
import fit.wenchao.autobackup.model.JsonResult;
import fit.wenchao.autobackup.model.RespCode;
import fit.wenchao.autobackup.utils.JDBCUtils;
import fit.wenchao.autobackup.utils.ResponseEntityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;

@RestController
@Slf4j
@Validated
public class DbConnController {
    @Data
    @Accessors(chain = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateConn {
        @NotNull
        @NotEmpty
        String host;
        @NotNull
        Integer port;
        @NotNull
        @NotEmpty
        String username;
        @NotNull
        @NotEmpty
        String passwd;
        @NotNull
        @NotEmpty
        String name;
    }

    @Autowired
    DbConnDao dbConnDao;

    @Autowired
    JDBCUtils jdbcUtils;

    @Data
    static class DbConnValidation {
        String result;
        String code;
    }

    public static DbConnValidation ready(String ip, Integer port, String dbname, String username, String pwd) {
        String code;
        String result;
        DbConnValidation dbConnValidation = new DbConnValidation();
        try {
            Connection connection = null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            String ipport=ip +":" + port;

            String url="jdbc:mysql:// " +ipport+"/"+dbname+"?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
            connection = DriverManager.getConnection(url,username ,pwd);
            result = "Mysql 连接成功";
            code = "success";
            dbConnValidation.setCode(code);
            dbConnValidation.setResult(result);
            log.info(result);
            return dbConnValidation;
        } catch (CommunicationsException ex) {
            result = ex.getMessage();
            log.error(result);
        } catch (SQLSyntaxErrorException ex) {
            result = "目标数据库不存在";
            log.error(result);
        }catch (SQLException ex) {
            result = ex.getMessage();
            log.error(result);
        } catch (Exception e) {
            result = "数据库连接未知错误";
            log.error(result);
        }

        dbConnValidation.setCode("fail");
        dbConnValidation.setResult(result);
        return dbConnValidation;
    }

    public static void main(String[] args) {
        DbConnValidation ready = ready("49.232.155.160", 33068, "wordpress", "root", "wc123456");
        System.out.println(ready);
    }


    @GetMapping("/db-conn/valid")
    public ResponseEntity<JsonResult> dbConnValid(@NotNull Long connId, @NotEmpty @NotNull String dbname) {
        DbConnPO dbConnPO = dbConnDao.getById(connId);
        if(dbConnPO == null) {
            throw new BackendException(null, RespCode.NO_DBCONN);
        }
        DbConnValidation ready = ready(dbConnPO.getHost(), dbConnPO.getPort(), dbname, dbConnPO.getUsername(), dbConnPO.getPasswd());
        return ResponseEntityUtils.ok(JsonResult.ok(ready));
    }

    @PostMapping("/db_conn")
    public ResponseEntity<JsonResult> createDbConn(@NotNull @Validated CreateConn createConn) {

        DbConnPO dbConn = dbConnDao.getOneByMap("name", createConn.getName());
        if (dbConn != null) {
            throw new BackendException(null, RespCode.CONN_NAME_DUPLICATED);
        }

        dbConn = new DbConnPO();
        BeanUtils.copyProperties(createConn, dbConn);
        dbConnDao.save(dbConn);
        return ResponseEntityUtils.ok(JsonResult.ok());
    }
    @GetMapping("/db_conns")
    public ResponseEntity<JsonResult> getDbConns() {
        List<DbConnPO> list = dbConnDao.list();
        return ResponseEntityUtils.ok(JsonResult.ok(list));
    }
}

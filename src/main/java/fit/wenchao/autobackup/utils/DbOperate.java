package fit.wenchao.autobackup.utils;

import fit.wenchao.autobackup.exception.BackendException;
import fit.wenchao.autobackup.model.RespCode;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DbOperate {
    public static String testDBConnection(String driverClassName, String jdbcURL, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName(driverClassName);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(jdbcURL, username, password);
        } catch (Exception e) {
            Throwable parent = e.getCause();
            Throwable cur = e;
            while (parent != null) {
                cur = parent;
                parent = parent.getCause();
            }

            System.out.println(cur);
            if (cur.getClass().equals(ConnectException.class)) {
                return "CONNECTION_REFUSED";
            } else if (cur.getClass().equals(SQLException.class)) {
                return "ACCESS_DENIED";
            } else if (cur.getClass().equals(SQLSyntaxErrorException.class)) {
                return "UNKNOWN_DB";
            } else if (cur.getClass().equals(NoRouteToHostException.class)) {
                return "HOST_UNREACHABLE";
            } else if (cur.getClass().equals(SocketTimeoutException.class)) {
                return "HOST_CONNECTION_TIMEOUT";
            } else {
                return "UNKNOWN_ERROR";
            }
        }
        assert conn != null;
        DatabaseMetaData metaData = conn.getMetaData();
        System.out.println("连接的数据库为：" + metaData.getDatabaseProductName() + "-" + metaData.getDatabaseProductVersion());
        return "Success";
    }

    /**
     * 备份数据库db
     */
    public static String dbBackUp(String username,
                                String pwd,
                                String host,
                                Integer port,
                                String dbName,
                                String sqlPath
    ) throws Exception {
        File sqlFile = new File(sqlPath);

        // 构建备份sql文件的父目录
        File sqlDirFile = sqlFile.getParentFile();
        if (!sqlDirFile.exists()) {
            try {
                Files.createDirectories(sqlDirFile.toPath());
            } catch (Exception ex) {
                // 创建目录失败抛出异常
                Throwable rootCause = ExceptionUtils.getRootCause(ex);
                throw new BackendException(null,
                        RespCode.BACKUP_GENERAL_ERROR.name(), "创建文件失败，原因：" + rootCause.getMessage());
            }
        }

        // create backup sql file
        if (!sqlFile.exists()) {
            sqlFile.createNewFile();
        } else {
            Files.deleteIfExists(sqlFile.toPath());
        }


        Runtime runtime = Runtime.getRuntime();

        // get mysqldump path
        Process whichMysqldump = runtime
                .exec(new String[]{"sh", "-c", "which mysqldump"});
        String procConsoleResult = getProcConsoleResult(whichMysqldump);
        String mysqlDumpPath = procConsoleResult.trim();

        //mysqldump -hhostname -uusername -ppwd database-name > sql-path
        StringBuilder backupCommandBuilder;
        backupCommandBuilder = new StringBuilder();
        backupCommandBuilder.append(mysqlDumpPath).append(" --column-statistics=0 ");
        //sb.append("/Users/cw/test --column-statistics=0 ");
        backupCommandBuilder.append(" -h").append(host);
        backupCommandBuilder.append(" -P").append(port);
        backupCommandBuilder.append(" -u").append(username);
        backupCommandBuilder.append(" -p").append(pwd);
        backupCommandBuilder.append(" ").append(dbName).append(" >");
        backupCommandBuilder.append(sqlPath);
        log.debug("cmd命令为：" + backupCommandBuilder.toString());
        System.out.println("cmd命令为：" + backupCommandBuilder.toString());

        log.debug("开始备份：" + dbName);
        System.out.println("开始备份：" + dbName);
        Process process = runtime.exec(new String[]{"/bin/sh", "-c", backupCommandBuilder.toString()});

        int rt; // shell return value
        rt = process.waitFor();
        log.debug("备份完成，返回值：{}", getProcReadableRt(rt));
        System.out.println("备份完成，返回值：" + getProcReadableRt(rt));
        procConsoleResult = getProcConsoleResult(process);
        log.debug("命令行结果：" + procConsoleResult);
        System.out.println("命令行结果：" + procConsoleResult);

        String backupResultMsg = procConsoleResult;
        // dump failed
        if(rt != 0) {
            backupResultMsg = procConsoleResult;
            // mysql do not support column-statistics arg
            if(backupResultMsg.toLowerCase().contains("unknown variable 'column-statistics=0'")) {
                System.out.println("mysql不支持变量column-statistics，去掉参数column-statistics后重新执行备份命令");
                // rebuild dump cmd, remove column-statistics arg
                backupCommandBuilder = new StringBuilder();
                backupCommandBuilder.append(mysqlDumpPath);
                backupCommandBuilder.append(" -h").append(host);
                backupCommandBuilder.append(" -P").append(port);
                backupCommandBuilder.append(" -u").append(username);
                backupCommandBuilder.append(" -p").append(pwd);
                backupCommandBuilder.append(" ").append(dbName).append(" >");
                backupCommandBuilder.append(sqlPath);
                // rerun dump cmd
                process = runtime.exec(new String[]{"/bin/sh", "-c", backupCommandBuilder.toString()});
                rt = process.waitFor();
                log.debug("备份完成，返回值：{}", getProcReadableRt(rt));
                System.out.println("备份完成，返回值：" + getProcReadableRt(rt));
                procConsoleResult = getProcConsoleResult(process);
                log.debug("命令行结果：" + procConsoleResult);
                System.out.println("命令行结果：" + procConsoleResult);
                backupResultMsg = procConsoleResult;
                if(rt  != 0) {
                    throw new BackendException(null,  RespCode.BACKUP_GENERAL_ERROR.name(), backupResultMsg);
                }
                return backupResultMsg;
            } else {
                throw new BackendException(null,  RespCode.BACKUP_GENERAL_ERROR.name(), backupResultMsg);
            }
        }
        return backupResultMsg;
    }


    public static String getProcConsoleResult(Process process) throws IOException {
        //try(BufferedReader input =new BufferedReader(new InputStreamReader( new BufferedInputStream( Files.newInputStream(Paths.get("hello")))))) {
        //
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}
        //BufferedReader input =
        //        new BufferedReader(new InputStreamReader(new BufferedInputStream(Files.newInputStream(Paths.get("hello")))));
        //BufferedReader inputStreamReader =
        //        new BufferedReader(new InputStreamReader(process.getInputStream()));
        try (BufferedReader inputStreamReader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errorReader =
                     new BufferedReader(new InputStreamReader(process.getErrorStream()));
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ) {

            String line = null;
            try {
                while ((line = inputStreamReader.readLine()) != null) {
                    byteArrayOutputStream.write(line.getBytes());
                    byteArrayOutputStream.write('\n');
                }
            } catch
            (IOException ex) {

            }
            try {
                while ((line = errorReader.readLine()) != null) {
                    byteArrayOutputStream.write(line.getBytes());
                    byteArrayOutputStream.write('\n');
                }
            } catch
            (IOException ex) {

            }
            return byteArrayOutputStream.toString();
        }


    }

    public static String getProcReadableRt(int rt) {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "Success");
        map.put(1, "Catchall for general errors");
        map.put(2, "Misuse of shell builtins (according to Bash documentation)");
        map.put(126, "Command invoked cannot execute");
        map.put(127, "Command not found");
        map.put(128, "Invalid argument to exit");
        return map.get(rt);
    }

    /**
     * 恢复数据库
     *
     * @param root
     * @param pwd
     * @param dbName
     * @param filePath mysql -hlocalhost -uroot -p123456 db < /home/back.sql
     */
    public static void dbRestore(String username,
                                 String pwd,
                                 String host,
                                 Integer port,
                                 String dbName,
                                 String pathSql) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("mysql");
        sb.append(" -h" + host);
        sb.append(" -P" + port);
        sb.append(" -u" + username);
        sb.append(" -p" + pwd);
        sb.append(" " + dbName + " <");
        sb.append(pathSql);
        log.debug("cmd命令为：" + sb.toString());
        Runtime runtime = Runtime.getRuntime();
        log.debug("开始还原数据");
        Process process = runtime.exec(new String[]{"/bin/sh", "-c", sb.toString()});
        String procConsoleResult = getProcConsoleResult(process);
        log.debug("还原完成，返回值：{}", procConsoleResult);
        if (procConsoleResult != null && procConsoleResult.contains("Unknown database")) {
            throw new RuntimeException("Unknown database: " + dbName);
        }
        log.debug("还原成功！");
    }


    public static void main(String[] args) throws Exception {
        //System.out.println(testDBConnection(
        //        "com.mysql.cj.jdbc.Driver",
        //        "jdbc:mysql://111.111.111.111:30088/totp_admn?useUnicode=true&characterEncoding=UTF-8&connectTimeout=30000&useSSL=false&socketTimeout=30000",
        //        "root",
        //        "wc123456"));


        System.out.println("test\n");
        //String backName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".sql";
        //DbOperate.dbBackUp("root",
        //        "wc123456",
        //        "localhost",
        //        3306,
        //        "totp_admin",
        //        "/Users/cw/test/db-backup/" + backName
        //);
        //dbRestore("root",
        //        "wc123456",
        //        "49.232.155.160",
        //        30088,
        //        "totp_admin",
        //        "/Users/cw/test/db-backup/" + backName);


    }

}
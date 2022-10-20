package fit.wenchao.autobackup.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component("JDBCUtils")
public class JDBCUtils {

    @Autowired
    DataSourceProperties dataSourceProperties;

    public Connection getConnection() throws Exception {
        Connection connection = null;
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(dataSourceProperties.getUrl(), dataSourceProperties.getUsername()
                ,dataSourceProperties.getPassword());
        return connection;
    }

}
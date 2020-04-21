/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package database;

import com.alibaba.druid.pool.DruidDataSource;
import constants.ServerProperties.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Frz
 * @author BlackRabbit
 */
public class Database {

    //private static final ThreadLocal<Connection> con = new DatabaseConfig.ThreadLocalConnection();
    public static final int CLOSE_CURRENT_RESULT = 1;
    /**
     * The constant indicating that the current <code>ResultSet</code> object
     * should not be closed when calling <code>getMoreResults</code>.
     *
     * @since 1.4
     */
    public static final int KEEP_CURRENT_RESULT = 2;
    /**
     * The constant indicating that all <code>ResultSet</code> objects that have
     * previously been kept open should be closed when calling
     * <code>getMoreResults</code>.
     *
     * @since 1.4
     */
    public static final int CLOSE_ALL_RESULTS = 3;
    /**
     * The constant indicating that a batch statement executed successfully but
     * that no count of the number of rows it affected is available.
     *
     * @since 1.4
     */
    public static final int SUCCESS_NO_INFO = -2;
    /**
     * The constant indicating that an error occured while executing a batch
     * statement.
     *
     * @since 1.4
     */
    public static final int EXECUTE_FAILED = -3;
    /**
     * The constant indicating that generated keys should be made available for
     * retrieval.
     *
     * @since 1.4
     */
    public static final int RETURN_GENERATED_KEYS = 1;
    /**
     * The constant indicating that generated keys should not be made available
     * for retrieval.
     *
     * @since 1.4
     */
    public static final int NO_GENERATED_KEYS = 2;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver"); // touch the mysql driver
        } catch (ClassNotFoundException e) {
            System.out.println("SQL Driver Not Found. Consider death by clams.");
            e.printStackTrace();
        }
    }

    private final DruidDataSource dSrc;

    private Database() {
        dSrc = new DruidDataSource();
        dSrc.setName("mysql_pool");
        dSrc.setDriverClassName("com.mysql.jdbc.Driver");
        dSrc.setUrl(DatabaseConfig.DB_URL);
        dSrc.setUsername(DatabaseConfig.DB_USER);
        dSrc.setPassword(DatabaseConfig.DB_PASS);
        dSrc.setInitialSize(0);
        dSrc.setMaxActive(20000);
        dSrc.setMaxWait(60000);
        dSrc.setMinIdle(0);
        dSrc.setTimeBetweenEvictionRunsMillis(60000);
        dSrc.setMinEvictableIdleTimeMillis(25200000);
        dSrc.setValidationQuery("SELECT `x`");
        dSrc.setTestWhileIdle(true);
        dSrc.setTestOnBorrow(false);
        dSrc.setTestOnReturn(false);
        dSrc.setPoolPreparedStatements(true);
        dSrc.setMaxPoolPreparedStatementPerConnectionSize(3000);
        dSrc.setUseUnfairLock(true);  
    }

    public Connection get() {
        try {
            return dSrc.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public DruidDataSource getDruidDataSource() {
        return dSrc;
    }

    public static Database createDatabase() {
        return new Database();
    }

    public static void cleanUP(ResultSet rs, PreparedStatement ps, Connection con) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
            }
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {

        }
    }
}

package cn.itcast.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

    /**
     * 使用线程启动JDBC连接数据库并操作mysql数据库
     */
public class Mysql_JDBC extends Thread {
    protected static String user_name;
    protected static String user_password;

    //构造函数
    static void Mysql_sql(String user_name1, String user_password1){
        user_name = user_name1;
        user_password =user_password1;
    }

    // 获取数据库连接
    protected static Connection myConnection() throws Exception {
        Connection conn;
        String database_user = "root";  //连接数据库的用户名
        String database_password = "123456";  //连接数据库的密码
        String url = "jdbc:mysql://112.74.180.95:3306/q";// 数据库连接串
        String driverClass = "com.mysql.jdbc.Driver" ;
        ResultSet resultSet = null;
        Statement statement = null;

        try{
            // 1、加载数据库驱动（ 成功加载后，会将Driver类的实例注册到DriverManager类中）
            Class.forName(driverClass);
            // 2、获取数据库连接
            conn = DriverManager.getConnection(url,database_user,database_password);
            //反馈信息
            System.out.println("数据库连接成功！");
        }catch(Exception e){
            System.out.println("数据库连接失败！");
            conn = null;
            e.printStackTrace();
        }
        return conn;
    }

    //登录验证
    protected boolean log_in(){
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            // 1. 获得Connection
            conn = myConnection();
            // 2. 获取Statement
            statement = conn.createStatement();
            // 3. 准备SQL
            String sql = "SELECT * FROM user\n" +
                    "WHERE user_name = '" + user_name + "' AND user_password = '" + user_password + "';";
            // 4. 执行查询，得到ResultSet
            resultSet = statement.executeQuery(sql);
            // 5. 处理ResultSet
            while(resultSet.next()){
                System.out.println("账号登录成功！");
                return true;//System.out.println("此账号已注册！");
            }
            System.out.println("该用户未注册！");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("该用户未注册！");
        }finally{
            releaseDB(resultSet,statement,conn);
        }
        return false;//System.out.println("此账号未注册！");
    }

    //执行查询，得到ResultSet
    protected boolean testResultSet(String sql){
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            // 1. 获得Connection
            conn = myConnection();
            // 2. 获取Statement
            statement = conn.createStatement();
            // 3. 准备SQL
            // 4. 执行查询，得到ResultSet
            resultSet = statement.executeQuery(sql);
            System.out.println("数据库执行查询！");
            // 5. 处理ResultSet
//            while(resultSet.next()){
//                int A_Type = resultSet.getInt(5);
//                int M_Type = resultSet.getInt(4);
//                System.out.println(A_Type+" "+M_Type);
//            }
            /**
             * ResultSet表示select语句的查询结果集。ResultSet对象具有指向其当前数据行的指针，
             * 最初，指针被置于第一行记录之前，通过next()方法可以将指针移动到下一行记录。
             * 需要注意的是，不管查询出来有没有结果，ResultSet的值却不是null。
             * 因此判断ResultSet的结果集是否为空可以用next()方法
             */
            if(resultSet.next()){
                //System.out.println("结果集不为空!");
                return true;
            }
            else{
                //System.out.println("结果集为空!");
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("数据库查找失败！");
        }finally{
            releaseDB(null,statement,conn);
        }
        return false;
    }

    // 可以执行的sql：update、insert、delete
    protected void myStatement(String sql) throws SQLException {
        Connection conn = null;
        Statement statement = null;
        try {
            // 1. 获取数据库连接
            conn = myConnection();
            // 2. 准备执行的SQL
            // 3. 执行SQL（注意执行的SQL可以是INSERT、UPDATE或DELETE。但不能是SELECT）
            // 1）获取操作SQL语句的Statement对象
            // 通过调用Connection的createStatement()方法来获取
            statement = conn.createStatement();
            // 2）调用Statement对象的executeUpdate(sql)执行SQL语句进行插入
            statement.executeUpdate(sql);
            //反馈信息
            System.out.println("数据库更新成功！");
        } catch (Exception e) {
            e.printStackTrace();
            //反馈信息
            System.out.println("数据库更新失败！");
        } finally {
            releaseDB(null,statement,conn);
        }
    }

    // 关闭数据库资源(注意关闭要从里到外)
    private void releaseDB(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

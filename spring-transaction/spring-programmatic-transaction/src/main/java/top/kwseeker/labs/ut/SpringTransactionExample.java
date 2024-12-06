package top.kwseeker.labs.ut;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 编程式事务
 * 灵活但是代码耦合度高，每个事务都要重新实现doInTransaction()
 */
public class SpringTransactionExample {

    private static String url = "jdbc:mysql://localhost:3306/spring-tx?useSSL=false";
    private static String user = "root";
    private static String password = "123456";

    public static void main(String[] args) {
        //获取数据源
        final DataSource ds = new DriverManagerDataSource(url, user, password);
        final TransactionTemplate template = new TransactionTemplate();
        //设置事务管理器
        template.setTransactionManager(new DataSourceTransactionManager(ds));
        //其他配置
        //template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

        template.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //获取连接
                Connection conn = DataSourceUtils.getConnection(ds);
                Object savePoint = null;
                try {
                    //执行事务: 包含一个插入一个更新
                    {
                        PreparedStatement prepare = conn.prepareStatement("insert into account (accountName, user, money) values (?, ?, ?)");
                        prepare.setString(1, "111");
                        prepare.setString(2, "aaa");
                        prepare.setInt(3, 1000);
                        prepare.executeUpdate();
                    }
                    //设置保存点
                    savePoint = status.createSavepoint();
                    {
                        PreparedStatement prepare = conn.prepareStatement("update account set money=money+100 where user=?");
                        prepare.setString(1, "aaa");
                        prepare.executeUpdate();
                        //模拟运行时异常
                        throw new RuntimeException("test");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                //} catch (Exception e) {
                //    e.printStackTrace();
                //    System.out.println("更新失败");
                //    if(savePoint != null) {
                //        //回退到保存点
                //        System.out.println("回退到保存点");
                //        status.rollbackToSavepoint(savePoint);
                //    } else {
                //        //完全回退
                //        System.out.println("完全回退");
                //        status.setRollbackOnly();
                //    }
                }
                //return null;
            }
        });
    }

}

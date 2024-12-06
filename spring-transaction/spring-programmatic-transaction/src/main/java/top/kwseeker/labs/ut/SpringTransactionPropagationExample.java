package top.kwseeker.labs.ut;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 测试事务传播行为
 */
public class SpringTransactionPropagationExample {

    private static String url = "jdbc:mysql://localhost:3306/spring-tx?useSSL=false";
    private static String user = "root";
    private static String password = "123456";

    public static void main(String[] args) {
        //获取数据源
        final DataSource ds = new DriverManagerDataSource(url, user, password);
        final TransactionTemplate template = new TransactionTemplate();
        //设置事务管理器
        template.setTransactionManager(new DataSourceTransactionManager(ds));
        //其他事务配置
        //template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        template.setTimeout(30);
        //template.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        //template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        //template.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);

        //外层事务
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //这里jdbc操作推荐替换为jdbcTemplate操作　TODO
                Connection conn = DataSourceUtils.getConnection(ds);
                try {
                    //执行一个插入
                    PreparedStatement prepare = conn.prepareStatement("insert into account (accountName, user, money) values (?, ?, ?)");
                    prepare.setString(1, "111");
                    prepare.setString(2, "aaa");
                    prepare.setInt(3, 1000);
                    prepare.executeUpdate();
                    System.out.println("outer update done");
                    //执行内层事务
                    doInnerTransaction(template, ds);
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    //内层事务
    private static void doInnerTransaction(TransactionTemplate template, DataSource ds) {
        int prevPropagationBehavior = template.getPropagationBehavior();
        try {
            //template.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
            template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            template.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Connection conn = DataSourceUtils.getConnection(ds);    //不用担心这里取的连接不是同一个
                    try {
                        int i = 1 / 0;
                        PreparedStatement prepare = conn.prepareStatement("insert into account (accountName, user, money) values (?, ?, ?)");
                        prepare.setString(1, "222");
                        prepare.setString(2, "bbb");
                        prepare.setInt(3, 500);
                        prepare.executeUpdate();
                        System.out.println("inner update done");
                        //模拟异常触发回滚, 也可以通过 status.setRollbackOnly(); 设置回滚标志触发后续回滚
                        //throw new RuntimeException("模拟异常");
                        //status.setRollbackOnly();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            });
        } finally {
            System.out.println("reset previous behavior");
            template.setPropagationBehavior(prevPropagationBehavior);
        }
    }
}

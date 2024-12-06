package top.kwseeker.labs.ut;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * 事务中异步操作需要注意的问题
 */
public class SpringTransactionAsyncExample {

    private static String url = "jdbc:mysql://localhost:3306/spring-tx?useSSL=false";
    private static String user = "root";
    private static String password = "123456";

    public static void main(String[] args) {
        final DataSource ds = new DriverManagerDataSource(url, user, password);
        final TransactionTemplate template = new TransactionTemplate();
        template.setTransactionManager(new DataSourceTransactionManager(ds));
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

        template.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //1 首先下面操作有什么问题？
                //问题：异步查询返回0
                //原因：事务执行完毕后才会真正提交，而下面异步查询已经不属于同一个事务了是看不到还没提交刷表的操作结果的
                jdbcTemplate.update("insert into account (accountName, user, money) values (?, ?, ?)", "111", "aaa", 1000);

                final String query = "select count(1) from account where accountName = '111'";
                Integer count1 = jdbcTemplate.queryForObject(query, Integer.class);
                System.out.println("同步查询：" + count1);

                //2 错误写法
                //new Thread(() -> {
                //    //为什么说这里就不是同一个事务了？
                //    // 跟下源码就知道了，执行查询前肯定要获取连接，里面获取的连接也是通过 Connection con = DataSourceUtils.getConnection(obtainDataSource()); 获取的
                //    // 而这个方法原理是查 resources NamedThreadLocal 这里不是同一个线程，所以会获取连接失败，从而新建一个连接执行查询，然后就属于跨事务了，当然看不到还没提交的操作
                //    Integer count2 = jdbcTemplate.queryForObject(query, Integer.class);
                //    System.out.println("异步查询：" + count2);
                //}).start();
                //3 问题修正，添加事务同步器，在事务提交（数据刷盘）后执行查询操作
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        new Thread(() -> {
                            Integer count2 = jdbcTemplate.queryForObject(query, Integer.class);
                            System.out.println("异步查询：" + count2);
                        }).start();
                    }
                });

                try {
                    TimeUnit.SECONDS.sleep(1);  //延迟提交，确保异步查询在提交之前执行
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "success";
            }
        });
    }
}

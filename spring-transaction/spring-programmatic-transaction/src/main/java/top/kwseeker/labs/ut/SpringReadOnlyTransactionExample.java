package top.kwseeker.labs.ut;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * 只读事务对比于读写事务只是执行效率上的优化。
 * 实际中使用只读事务更多的是强调“事务”而不是“只读”，比如有些场景，希望多次查询过程中不要受到第三方事务更新影响，就可以为这个“多次查询”添加上只读事务。
 */
public class SpringReadOnlyTransactionExample {

    private static String url = "jdbc:mysql://localhost:3306/spring-tx?useSSL=false";
    private static String user = "root";
    private static String password = "123456";

    public static void main(String[] args) throws InterruptedException {
        final DataSource ds = new DriverManagerDataSource(url, user, password);
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //DML即使不显示地添加事务控制，也是在事务中执行的
            jdbcTemplate.update("insert into account (accountName, user, money) values (?, ?, ?)", "1111", "xxx", 1000);
        }).start();

        //不在事务中执行
        {
            String query = "select count(1) from account where accountName = '1111'";

            //第1次查询, 0
            Integer count1 = jdbcTemplate.queryForObject(query, Integer.class);
            System.out.println("count1:" + count1);

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //第2次查询, 1, 但是有些场景可能并不希望受到外部更新操作影响，即这里不想看到外部更新的数据，可以像下面添加事务
            Integer count2 = jdbcTemplate.queryForObject(query, Integer.class);
            System.out.println("count2:" + count2);
        }

        ////在事务中执行
        //final TransactionTemplate template = new TransactionTemplate();
        //template.setTransactionManager(new DataSourceTransactionManager(ds));
        ////设置只读事务
        //template.setReadOnly(true);
        //template.execute(new TransactionCallbackWithoutResult() {
        //    @Override
        //    protected void doInTransactionWithoutResult(TransactionStatus status) {
        //        String query = "select count(1) from account where accountName = '1111'";
        //
        //        //第1次查询
        //        Integer count1 = jdbcTemplate.queryForObject(query, Integer.class);
        //        System.out.println("count1:" + count1);
        //
        //        try {
        //            TimeUnit.SECONDS.sleep(2);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
        //
        //        //第2次查询
        //        Integer count2 = jdbcTemplate.queryForObject(query, Integer.class);
        //        System.out.println("count2:" + count2);
        //    }
        //});
    }
}

package top.kwseeker.labs;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.List;

public class ActivitiSqlMonitorTest {

    /**
     * 想监听Activiti部署执行流程等操作中都执行了哪些sql操作
     * 本来有个命令可以直接监听binlog日志的，但是忘记了
     * 这里使用Canal监听
     */
    @Test
    public void testMonitorActivitiSqlOperation() {
        String canalServerHost = "192.168.1.5";
        int canalServerPort = 11111;
        String destination = "apipe";
        //canal server 没有配置连接用户名密码
        String username = null;
        String password = null;

        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(canalServerHost, canalServerPort),
                destination, username, password);
        try {
            connector.connect();
            //connector.subscribe("ry-activiti\\..*,boot-activiti\\..*"); //这里的配置会覆盖 instance.properties 中的定义
            connector.subscribe("boot-activiti\\..*");
            //回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拿
            connector.rollback();
            for (; ; ) {
                Message message = connector.getWithoutAck(10);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId != -1 && size > 0) {    //有数据
                    // 处理数据变更事件
                    System.out.printf("Received %d entries\n", size);
                    printEntry(message.getEntries());
                }
                connector.ack(batchId);
            }
        } finally {
            connector.disconnect();
        }
    }

    private static void printEntry(List<Entry> entries) {
        for (Entry entry : entries) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                //开启/关闭事务的实体类型，跳过
                continue;
            }
            //RowChange对象，包含了一行数据变化的所有特征
            //比如isDdl 是否是ddl变更操作 sql 具体的ddl sql beforeColumns afterColumns 变更前后的数据字段等等
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry, e);
            }
            //获取操作类型：insert/update/delete类型
            EventType eventType = rowChange.getEventType();
            //打印Header信息
            System.out.printf("================》; binlog[%s:%s] , name[%s.%s] , eventType : %s%n",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType);
            //判断是否是DDL语句
            if (rowChange.getIsDdl()) {
                System.out.println("================》; isDdl: true, sql:" + rowChange.getSql());
            }
            //获取RowChange对象里的每一行数据，打印出来
            for (RowData rowData : rowChange.getRowDatasList()) {
                //如果是删除语句
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                    //如果是新增语句
                } else if (eventType == EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                    //如果是更新的语句
                } else {
                    //变更前的数据
                    System.out.println("------->; before");
                    printColumn(rowData.getBeforeColumnsList());
                    //变更后的数据
                    System.out.println("------->; after");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private static void printColumn(List<Column> columns) {
        for (Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}

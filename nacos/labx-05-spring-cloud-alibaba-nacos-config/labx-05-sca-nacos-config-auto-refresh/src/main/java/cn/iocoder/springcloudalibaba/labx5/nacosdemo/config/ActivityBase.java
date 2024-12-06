package cn.iocoder.springcloudalibaba.labx5.nacosdemo.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class ActivityBase implements Activity {

    protected String id;
    //活动名称
    protected String name;
    //活动开始时间 yyyy-MM-dd HH:mm:ss
    protected String startTime;
    //活动结束时间
    protected String endTime;

    @Override
    public ActivityState state() {
        return null;
    }

    @Override
    public String info() {
        return this.toString();
    }
}

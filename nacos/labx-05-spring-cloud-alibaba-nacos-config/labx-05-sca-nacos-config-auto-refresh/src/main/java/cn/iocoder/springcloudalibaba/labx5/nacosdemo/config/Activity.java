package cn.iocoder.springcloudalibaba.labx5.nacosdemo.config;

public interface Activity {

    String getId();

    String getName();

    String getStartTime();

    String getEndTime();

    ActivityState state();

    String info();
}

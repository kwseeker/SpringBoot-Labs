package cn.iocoder.springcloudalibaba.labx5.nacosdemo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动配置信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySetting {
    // 是否启用 0不起用1启用
    private boolean enable;
    private String activityJson;
    // 活动配置的 Activity Class 实际类型，将活动配置 JSON 反序列化时用
    private String activityClassName;
}

package cn.iocoder.springcloudalibaba.labx5.nacosdemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 使用 Nacos 配置中心存储活动配置，使用起来和本地配置文件一样简单
 * application.yml 中配置启用 Nacos 配置中心之后，Spring Boot 应用在启动时，会预加载来自 Nacos 配置 (作为 PropertySource)
 * 所以直接使用 @ConfigurationProperties 注解即可
 */
@Component
@ConfigurationProperties(prefix = "msa") // 使用这个注解的Nacos配置会自动刷新
public class ActivitySettingProperties {

    /**
     * 发布到Nacos中的配置示例：
     * msa:
     *   activities:
     *     MID_AUTUMN_FESTIVAL:
     *       enable: true
     *       activityJson: >-
     *         {
     *           "id": "MID_AUTUMN_FESTIVAL",
     *           "name": "喜迎中秋",
     *           "startTime": "2023-09-01 00:00:00",
     *           "endTime": "2023-09-07 23:59:59"
     *         }
     *       activityClassName: top.kwseeker.msa.action.webbdcc.domain.activity.model.ClassicActivity
     */
    private Map<String, ActivitySetting> activities;

    public Map<String, ActivitySetting> getActivities() {
        return activities;
    }

    public void setActivities(Map<String, ActivitySetting> activities) {
        this.activities = activities;
    }
}

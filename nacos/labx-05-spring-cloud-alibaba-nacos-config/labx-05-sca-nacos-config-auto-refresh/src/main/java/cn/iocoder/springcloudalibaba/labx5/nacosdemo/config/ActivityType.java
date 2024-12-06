package cn.iocoder.springcloudalibaba.labx5.nacosdemo.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 这个类不是必需的，只是为了方便通过代码查看设置过的活动类型
 */
@AllArgsConstructor
@Getter
public enum ActivityType {
    // 枚举名作为活动ID
    MID_AUTUMN_FESTIVAL("喜迎中秋", "/mid-autumn-festival")
    ;

    private final String name;
    private final String zkNodeSubPath;
}

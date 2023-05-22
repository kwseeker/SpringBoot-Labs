package top.kwseeker.springboot.lab03.springsecurity.user.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import top.kwseeker.springboot.lab03.springsecurity.common.page.PageParam;

import java.time.LocalDateTime;

import static top.kwseeker.springboot.lab03.springsecurity.common.util.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPageReqVO extends PageParam {

    private String username;

    private String mobile;

    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    private Long deptId;

}
package top.kwseeker.labs.spring.transaction.entity;

import lombok.Data;

import java.util.Date;

/**
CREATE TABLE `test`.`transfer_record` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `from_user_id` VARCHAR(45) NOT NULL,
  `to_user_id` VARCHAR(45) NOT NULL,
  `amount` INT NOT NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`)
 );
 */
@Data
public class TransferRecord {

    private Integer id;
    private String fromUserId;
    private String toUserId;
    private Integer amount;
    private Date createTime;
    private Date updateTime;
}

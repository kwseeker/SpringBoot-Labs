package top.kwseeker.labs.spring.transaction.entity;

import lombok.Data;

import java.util.Date;

/**
 * CREATE TABLE `test`.`user_account` (
 *   `id` INT NOT NULL,
 *   `user_id` VARCHAR(45) NOT NULL,
 *   `balance` INT NOT NULL,
 *   `create_time` DATETIME NULL,
 *   `update_time` DATETIME NULL,
 *   PRIMARY KEY (`id`),
 *   UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC));
 */
@Data
public class UserAccount {

    private Integer id;
    private String userId;
    private Integer balance;
    private Date createTime;
    private Date updateTime;
}

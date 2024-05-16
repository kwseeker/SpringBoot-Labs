/**
 * 测试主要来源于redisson源码的测试代码,
 * 原测试代码通过 Docker Java API 创建了 Redis Docker 容器，基于与此容器建立连接进行测试，启动很耗时
 * 这里选择修改为直接连接本地的Redis测试容器进行测试
 */
package top.kwseeker.lab.redisson;
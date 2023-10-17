# Nacos 

## 通用功能

+ **监控端点**

  参考 [nacos-config-spring-boot-actuator](https://github.com/nacos-group/nacos-spring-boot-project/tree/master/nacos-config-spring-boot-actuator) 子项目。提供了 Nacos 作为 Spring Boot 配置中心时的监控端点

  `actuator/health`端点拓展了 `nacosConfig`信息；

  新增 `actuator/nacos-config`端点，包含`nacosConfigMetadata`和`nacosConfigGlobalProperties`信息，参考[NacosConfigEndpoint](https://github.com/nacos-group/nacos-spring-boot-project/blob/master/nacos-config-spring-boot-actuator/src/main/java/com/alibaba/boot/nacos/actuate/endpoint/NacosConfigEndpoint.java)。



## 配置中心

+ **配置属性**

  ```java
  // NacosConfigProperties
  private String serverAddr = "127.0.0.1:8848";
  private String contextPath;
  private String encode;
  private String endpoint;
  private String namespace;
  private String accessKey;
  private String secretKey;
  private String ramRoleName;
  private boolean autoRefresh = false;
  private String dataId;
  private String dataIds;
  private String group = "DEFAULT_GROUP";
  private ConfigType type;
  private String maxRetry;
  private String configLongPollTimeout;
  private String configRetryTime;
  private boolean enableRemoteSyncConfig = false;
  @JSONField(
      serialize = false
  )
  private List<Config> extConfig = new ArrayList();
  @NestedConfigurationProperty
  private Bootstrap bootstrap = new Bootstrap();
  
  // Bootstrap
  private boolean enable;
  private boolean logEnable;
  ```

+ **配置持久化**

  执行nacos安装目录 conf 下 nacos-mysql.sql，然后修改 application.properties 配置DB数据源。如：

  ```properties
  spring.datasource.platform=mysql 
  db.num=1
  db.url.0=jdbc:mysql://localhost:3306/nacos_config?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
  db.user=root
  db.password=123456
  ```

+ **配置预加载**

  可以使用 SpringBoot 自身的注解加载 Nacos 配置，无需和 Nacos 有耦合与依赖。

+ **配置自动刷新**

  对 Nacos 的配置进行修改后，Spring Boot 应用并不会自动刷新本地的配置。如果要实现自动刷新需要在应用中借助 Nacos 的注解，比如 @NacosConfigListener 定义一个配置监听器监听配置变更并执行配置变更后的处理。

  相关注解

  ```java
  @NacosValue							// autoRefreshed = true
  @NacosConfigurationProperties		// autoRefreshed = true
  // 配置监听器
  @NacosConfigListener				// 如果要刷新某些框架的配置，比如日志，配置更新了但是日志框架没有重新加载，就无法起效，这时需要用配置监听器通过接口刷新下日志框架配置
  ```

  但是使用上面注解，项目代码将和 Nacos 耦合， Spring Cloud 为解决这个问题提供了 `org.springframework.cloud.context.config.annotation.RefreshScope` 注解，使用 **@RefreshScope** 注解可以使被注解的内内部注入的属性值自动刷新。

  不过 @RefreshScope  也只是实现了属性值的自动刷新；涉及一些业务还需要重新使用新的属性值重新执行初始化，比如数据库连接重建、日志实例配置更新等，还是需要写代码处理，这时可以通过监听  **[EnvironmentChangeEvent](https://github.com/spring-cloud/spring-cloud-commons/blob/master/spring-cloud-context/src/main/java/org/springframework/cloud/context/environment/EnvironmentChangeEvent.java)** 事件，执行自定义的逻辑处理。

  比如日志级别（logging.level）修改了，可以监听  EnvironmentChangeEvent 事件，从中查到 `logging.level` 的配置，然后从容器获取 LoggingSystem Bean，通过 setLogLevel() 方法更新日志数据级别。 

  应用场景：

  + 配置热更新
  + 线上排查问题日志级别修改，可以搭配白名单避免打印的日志太多

+ **多配置源优先级顺序**

  Nacos可以通过 `ext-config` 拓展Nacos配置服务器以及配置集。

  配置源优先级顺序：

  `SpringBoot application.properties > SpringBoot application.yaml > Nacos data-id/data-ids > Nacos ext-config:data-id/data-ids`

  > data-id 与 data-ids 两者只能二选一。

  具体可以看 Environment 对象的 **PropertySources** 列表，索引越小优先级越高：

  ```java
  ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
  Environment environment = context.getEnvironment();
  ```

+ **TODO**

  + 使用docker创建的nacos容器端口映射 58848:8848, 然后使用58848 访问失败，什么原因？

  + Spring Boot 能否支持 Nacos 不使用 NacosConfigurationProperties 等注解实现配置自动刷新？



## 注册中心

+ 配置属性

  ```java
  // NacosDiscoveryProperties
  private String serverAddr = "127.0.0.1:8848";
  private String contextPath;
  private String clusterName;
  private String endpoint;
  private String namespace;
  private String accessKey;
  private String secretKey;
  private boolean autoRegister = false;
  @NestedConfigurationProperty
  private Register register = new Register();
  
  // Register extends Instance
  private String groupName = "DEFAULT_GROUP";
  
  // Instance 
  private String instanceId;
  private String ip;
  private int port;
  private double weight = 1.0;
  private boolean healthy = true;
  private boolean enabled = true;
  private boolean ephemeral = true;
  private String clusterName;
  private String serviceName;
  private Map<String, String> metadata = new HashMap();	
  ```

  


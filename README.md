# basic-module 基础jar

#### 项目介绍

iSOOPS 项目基本jar包
基于spring-boot 2.2.2 / mybatis-plus 3.2

#### 软件架构

目录结构
- basicmodule
- - classes [框架形库]
- - - annotation [自定义注解库,包含日志打印/请求校验/防暴力请求等]
- - - basicmodel [Controller Request/Response封装]
- - - sign[签名库]
- - common[基于第三方封装库的2次封装]
- - - easypoi[基于easypoi二次封装http://easypoi.mydoc.io]
- - - redis[基于springboot-redis RedisUtil https://github.com/whvcse/RedisUtil RedisLock的二次封装]
- - source [各种封装,包括加密/对象处理/会计日计算/http/StringUtils扩展等]

#### 安装教程

由于本人太懒，就没有发布到https://mvnrepository.com
如果需要最新的jar的小伙伴可以通过以下链接
http://ssl-api.isoops.com/repository/maven-public/com/isoops/basic-module/2.0.0/basic-module-2.0.0.jar

导入jar或者导入源代码后，在spring-boot框架中需要scan对应的component，如下:

```java
@ComponentScan(basePackages = {"com.isoops.basicmodule"})
@SpringBootApplication
public class ExcelToolApplication {
		public static void main(String[] args) {
				SpringApplication.run(ExcelToolApplication.class, args);
		}
}
```

#### 使用说明

```java
//注解签名校验使用的是@Controller拦截条件，此处不可使用RestController
@Controller
@RequestMapping("/user")
@Api(description = "user")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private SRedis sRedis;

    //打印Request/Response日志,包含基本信息与接口执行时间
    @Logger
    //防止暴力请求
    @ControllerLock
    //签名校验
    @CheckRequest(level = CheckGradeEnum.signCheck)
    @ApiOperation(value = "登录", notes = "登录")
    @PostMapping("login")
    //封装Request了基础结构
    //封装了ResponseEntity结构,支持recome/export方法,分别为对象json response和文件下载
    public GenericResult<String> login(@RequestBody @Valid Request<Login> bean) {
        return GenericResult.recome(
                service.login(
                        bean.getObject().getAccount(),
                        bean.getObject().getPasswd()
                ));
    }

    @ControllerLock(seconds = 1,maxCount = 10)
    @Logger(msg = "Excel文件下载")
    @CheckRequest(level = CheckGradeEnum.signCheck)
    @PostMapping("downloadExcel")
    @ApiOperation(value = "Excel文件下载", notes = "Excel文件下载")
    public GenericResult visitorOpenDevice(@RequestBody @Valid Request bean) {

        sRedis.lockKey("user",10);
        sRedis.set("user",bean.getUserSignal());
        UserDetail userDetail = sRedis.get(bean.getUserSignal()+"_DETAIL",UserDetail.class);

        byte[] bytes = SEasypoi.exportExcelByte(
                Arrays.asList(userDetail),
                "title",
                "sheet",
                UserDetail.class);
        return (GenericResult) GenericResult.export(bytes,"test.xls");
    }
}
```

#### 参与贡献

期待你的参与



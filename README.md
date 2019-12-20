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
如果有多个需要Scan的Component包，则：
```java
@ComponentScan(basePackages = {"com.isoops.basicmodule","你的包路径"})
```
#### 使用说明

###### 注解@Logger/@ControllerLock/@CheckRequest/@CheckRequestUrl使用
@Logger 接口日志打印（包含基本request参数/请求参数/返回Response参数/接口运行时长）

@ControllerLock 防止暴力请求 如下每米可请求次数为10次 超过则自动拦截并报错（默认为每秒1次请求）

@CheckRequest 基于Request/Response封装组件的签名校验

@CheckRequestUrl 基于url带参的签名校验，可自定义校验参数名称

```html
    window.local.herf = "localhost:8080/uploadExcel?upCode='111'&upSign='222'&userId='name'";
```

```java
    @Logger(msg = "Excel文件上传")
    //防止暴力请求 如下每米可请求次数为10次 超过则自动拦截并报错（默认为每秒1次请求）
    @ControllerLock(seconds = 1,maxCount = 10)
    @CheckRequestUrl(codeKey = "upCode",signKey = "upSign",userSignalKey = "userId",level = CheckGradeEnum.signCheck)
    @ApiOperation(value = "Excel文件上传", notes = "Excel文件上传")
    @PostMapping("/uploadExcel")
    public GenericResult<Integer> uploadCustomProperty(@RequestParam("file") List<MultipartFile> files){
        return GenericResult.recome( service.uploadCustomProperty(files));
    }
```

###### 基础Controller使用

1.常规接口请求，遵循Requset/Response基本数据结构

2.封装了ResponseEntity结构GenericResult,支持recome/export方法,分别对应对象json-response 和 文件(file)下载

Request请求对象
```java
    @ApiModelProperty(value = "加密签名key码", required = true)
    @NotBlank(message = ErrorTemp.NOT_NULL)
    private String code;

    @ApiModelProperty(value = "加密签名", required = true)
    @NotBlank(message = "sign不能为空")
    private String sign;

    @ApiModelProperty(value = "用户标示", required = true)
    @NotBlank(message = ErrorTemp.NOT_NULL)
    private String userSignal;

    @ApiModelProperty(value = "数据对象", required = true)
    @Valid
    private T object;
```
Response返回对象
```java
    @ApiModelProperty(value = "状态")
    private Boolean state;
    @ApiModelProperty(value = "状态描述")
    private String msg;
    @ApiModelProperty(value = "状态码")
    private Integer stateCode;
    @ApiModelProperty(value = "是否有下一页")
    private Boolean haveNext;
    @ApiModelProperty(value = "分页总数量")
    private Long pageCount;
    @ApiModelProperty(value = "返回对象")
    private T object;
```
Controller书写
```java
//注解签名校验使用的是@Controller拦截条件，此处不可使用RestController
@Controller
@RequestMapping("/user")
@Api(description = "user")
public class UserController {

    @Autowired
    private UserService service;
    
    @Logger(msg = "登录")
    @ControllerLock
    @ApiOperation(value = "登录", notes = "登录")
    @PostMapping("login")
    public GenericResult<LoginResponseBean> login(@RequestBody @Valid Request<Login> bean) {
        return GenericResult.recome(
                service.login(
                        bean.getObject().getAccount(),
                        bean.getObject().getPasswd()
                ));
    }

    @CheckRequestUrl
    @ControllerLock(seconds = 1,maxCount = 10)
    @GetMapping("/download")
    public ResponseEntity download(@RequestParam("local_date")  String localDate,
                                   @RequestParam("method")  Integer method) throws InterceptorException {
        byte[] bytes = service.getUserListExcel(localDate,method);
        HttpHeaders headers = service.getHeaders();
        return GenericResult.export(bytes,headers);
    }

}
```


#### 参与贡献

期待你的参与



# SketchSrv scf服务调用



## 1.在服务管理平台申请scf key

**服务名：SketchSrv**

## 2.添加maven工程依赖

```xml
    <!--服务contract依赖-->
    <dependency>
        <groupId>com.bj58.ecdata.sketch</groupId>
        <artifactId>scf-sketch-contract</artifactId>
        <version>1.0.0</version>
    </dependency>
```

## 3.方法说明

获取一组标签对应的值

```java
    /**
     * 获取一组标签对应的值
     *
     * @param type   id类型，目前支持："imei",后续加入 "userid"
     * @param key    id值
     * @param keyFieldsArray 标签数组，请参考用户画像标签编码 http://iwiki.58corp.com/shorturl/1UBRYwtvgN
     * @return FieldsResult 标签查询结果
     * @see FieldsResult
     */
     public FieldsResult getInfo(String type, String key, String[] keyFieldsArray);
```

返回errCode说明

```java
    /**
     * 错误码： 
     * 0，成功；
     * -1，请求key类型无效；
     * -2，请求ID无效；
     * -3,请求WTable失败
     */

int errCode;
```



## 4.测试代码示例

```java
public class QueryTest {

    public static void main(String[] args) {

        String url = "tcp://SketchSrv/ISketchService";
        ISketchService sketchService = ProxyFactory.create(ISketchService.class, url, false);
        //通过scf key初始化scf对象
        SCFInit.initScfKeyByValue("you scf key");
        FieldsResult fieldsResult = sketchService.getInfo("imei", "0000272a38ac1d25", new String[]{"TL_all_estate_usual","notExistsKey"});
        System.out.println(JSON.toJSONString(fieldsResult));
        //输出为{"errCode":0,"key":"0000272a38ac1d25","kvMap":{"TL_all_estate_usual":"{\"imei\":\"0000272a38ac1d25_imei\",\"value\":{\"l\":\"103007059:23.2276\",\"d\":\"2020-07-27\"}}"}}
    }
}
```


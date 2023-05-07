# 部落发帖NLP scf服务调用



## 1.在服务管理平台申请scf key

**服务名：BuluoNLP**

## 2.添加maven工程依赖

```xml
    <!--服务contract依赖-->
    <dependency>
        <groupId>com.bj58.ecdata.nlp</groupId>
        <artifactId>scf-nlp-contract</artifactId>
        <version>1.0.3</version>
    </dependency>
```

## 3.测试代码示例

方法输出为label和label对应的概率，label有两个：```房产、其它```

```java
public class QueryTest {

    public static void main(String[] args) {

        String url = "tcp://BuluoNLP/IClassifyService";
        IClassifyService classifyService = ProxyFactory.create(IClassifyService.class, url, false);
        //通过scf key初始化scf对象
        SCFInit.initScfKeyByValue("you scf key");
        Map<String, Object> map = classifyService.predictFangLabel1("今天从外面回来发现楼道了有好多东西应该是隔壁新搬过来的邻居吧把东西都堆在门口楼道里还没有人楼道还好有监控不然东西丢了咋办");
        System.out.println(map);
        //输出为{房产=0.9538703, 其它=0.04612975}
    }
}
```


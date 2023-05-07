## IBM导入工具使用

克隆

```sh
git clone https://github.com/IBM/janusgraph-utils.git
cd janusgraph-utils/
```

### 修改

由于janusgraph-utils依赖的janusgraph版本过低，且有部分不满足我们需求，所以做了部分修改

#### pom修改

修改janusgraph-all版本，和httpclient版本

```xml
<dependency>
      <groupId>org.janusgraph</groupId>
      <artifactId>janusgraph-all</artifactId>
      <version>0.3.1</version>
      <exclusions>
        <exclusion>
          <artifactId>httpclient</artifactId>
          <groupId>org.apache.httpcomponents</groupId>
        </exclusion>
      </exclusions>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient -->

    <dependency>
      <groupId>org.apache.httpcomponents</groupId>
      <artifactId>httpclient</artifactId>
      <version>4.5.5</version>
    </dependency>
```

#### 修改代码com.ibm.janusgraph.utils.generator.GSONUtil 第104行

将node_id类型设置为String（原来为Intger）

> node_id唯一，为我们的节点全局唯一id，建立了唯一索引，导入边的时候需要通过这个node_id查询出节点，为了加快查询速度

```java
        //manually add node_id as a unique propertyKey and index
        PropertyKeyBean nodeIdKey = new PropertyKeyBean("node_id", "String");
```

#### 有几个jce的jar包可能下不下来，需要找能下下来的机器下载到本地仓库

### 编译

```sh
cd janusgraph-utils/
mvn package
```

### 使用

#### 编辑配置文件生成schema

```
vim csv-conf/user.json
```

```json
{
  "VertexTypes": [
    {
      "name": "User",
      "columns": {
        "userid": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "biz_name": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "areaname": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "nick_name": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "level": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "user_type": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "alleffect": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "degree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "indegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "outdegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "comment_degree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "comment_indegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "comment_outdegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "like_degree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "like_indegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "like_outdegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "follow_degree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "follow_indegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "follow_outdegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "betweenness": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "closeness": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "shuafen_card": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "xifen_card": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "zhongjie_card": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "jiazhi_card": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "dt": {"dataType":"String",  "composit":true, "mixedIndex":"search"}
      },
      "row": 10
    }
  ],
  "EdgeTypes": [
    {
      "name": "Follow",
      "multiplicity": "SIMPLE",
      "selfRef": false,
      "columns": {
        "values": {
          "dataType":"Float","composit":true
        }
      },
      "relations": [
        {"left": "User", "right": "User", "row": 10, "selfRef":false }
      ]
    },
    {
      "name": "Comment",
      "multiplicity": "SIMPLE",
      "columns": {
        "values": {
          "dataType":"Float","composit":true
        }
      },
      "relations": [
        {"left": "User", "right": "User", "row": 10 }
      ]
    },
    {
      "name": "Like",
      "multiplicity": "SIMPLE",
      "columns": {
        "values": {
          "dataType":"Float","composit":true
        }
      },
      "relations": [
        {"left": "User", "right": "User", "row": 10 }
      ]
    },
    {
      "name": "All",
      "multiplicity": "SIMPLE",
      "columns": {
        "values": {
          "dataType":"Float","composit":true
        }
      },
      "relations": [
        {"left": "User", "right": "User", "row": 10 }
      ]
    }
  ]
}
```

#### 生成CSV文件模板

```
mkdir ./user
sh run.sh gencsv csv-conf/user.json ./user
```

#### 查看模板并将CSV文件替换为要导入的文件，保留json文件

```sh
(base) [work(lisenmiao)@tjtx-87-74 janusgraph-utils]$ ls ./user
datamapper.json  schema.json  User_All_User_edges.csv  User_Comment_User_edges.csv  User.csv  User_Follow_User_edges.csv  User_Like_User_edges.csv

```

#### 导入CSV文件

> 这里指定刚刚生成的文件夹和json配置文件
>
> 同时指定了online-janusgraph-hbase-es.properties配置文件位置

```sh
sh run.sh import /opt/soft/janusgraph-0.3.1-hadoop2/conf/gremlin-server/online-janusgraph-hbase-es.properties \
    ./user ./user/schema.json ./user/datamapper.json
```
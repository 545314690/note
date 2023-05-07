# 用户画像data_usergroup.t_person_imei_total表清洗脚本

> step1:清空t_person_imei_total_yesterday_tmp_final>>>
>
> step2:计算last2_date字段并插入final表t_person_imei_total_yesterday_tmp_final>>>
>
> step3:删除total表中与final表的重复数据>>>
>
> step4:插入final表数据到total表t_person_imei_total>>>

## Python版本

### 安装clickhouse_driver

参考：https://pypi.org/project/clickhouse-driver/

```python
pip install clickhouse-driver
```

### python脚本

> 注意：
>
> 1.python版本需大于等于2.7
>
> 2.这里使用的clickhouse 端口: tcp *9000* 而不是 http *8123*
>
> Java jdbc使用的是8123

```python
from clickhouse_driver import Client
import sys
import datetime
clickhouse_host = '10.126.89.77'
clickhouse_database = 'data_usergroup'
clickhouse_port = 9000

api_interface_sql = "select count(*) from data_usergroup.t_person_imei_total"

# 清空final表
step1_sql = 'truncate table data_usergroup.t_person_imei_total_yesterday_tmp_final_shard on cluster cluster2_4shards_0replicas;'
# 插入final表
step2_sql = '''insert into data_usergroup.t_person_imei_total_yesterday_tmp_final
SELECT b.*,a.last_date as last2_date
FROM 
(
    SELECT last_date,imei
    FROM data_usergroup.t_person_imei_total
) AS a 
RIGHT JOIN data_usergroup.t_person_imei_total_yesterday_tmp AS b USING (imei);'''

# 删除total表中与final表的重复数据,注意这里是total2_shard(确认什么时候删除完毕，再进行下一步写入)
step3_sql = '''ALTER TABLE data_usergroup.t_person_imei_total2_shard ON cluster cluster2_2shards_2replicas DELETE 
WHERE
	imei IN ( SELECT imei FROM data_usergroup.t_person_imei_total_yesterday_tmp );'''

# 插入final表数据到total表
step4_sql = '''INSERT INTO data_usergroup.t_person_imei_total SELECT
* 
FROM
	data_usergroup.t_person_imei_total_yesterday_tmp_final'''

start_time = datetime.datetime.now()
end_time = datetime.datetime.now()

def print_cost_time():
    # 开始记录耗时
    end_time = datetime.datetime.now()
    time_cost = end_time - start_time
    print('总耗时>>>'+str(time_cost).split('.')[0])

def main():
    try:
        print('ck建立连接>>>')
        client = Client(host=clickhouse_host, database=clickhouse_database, port=clickhouse_port)
        print('step1:清空t_person_imei_total_yesterday_tmp_final>>>')
        result = client.execute(step1_sql)
        print(result)
        print_cost_time()
        print('step2:插入final表t_person_imei_total_yesterday_tmp_final>>>')
        result = client.execute(step2_sql)
        print(result)
        print_cost_time()
        print('step3:删除total表中与final表的重复数据>>>')
        result = client.execute(step3_sql)
        print(result)
        print_cost_time()
        print('step4:插入final表数据到total表t_person_imei_total>>>')
        result = client.execute(step4_sql)
        print(result)
        print_cost_time()
        print('全部执行成功')
        sys.exit(0)
    except Exception as e:
        print('发生异常')
        print(e)
        sys.exit(1)
    finally:
        client.disconnect()
if __name__ == '__main__':
    print('开始执行')
    main()

```

## Java版本

发现服务器python版本过低，又写了个Java的

```java
package com.bj58.userportrait.main;

import com.bj58.userportrait.constant.CoreConstant;
import com.bj58.userportrait.entity.Link;
import com.bj58.userportrait.utils.JdbcUtils;

/**
 * 处理Last2Date字段
 *
 * @Author lisenmiao
 * @Date 2019/10/16 18:16
 */
public class ProcessingLast2Date {
    String clickhouse_host = "userportrait-ckcluster.58corp.com";
    String clickhouse_database = "data_usergroup";
    String clickhouse_port = "80";


    //   # 清空final表
    String step1_sql = "truncate table data_usergroup.t_person_imei_total_yesterday_tmp_final_shard on cluster cluster2_4shards_0replicas;";
    //# 插入final表
    String step2_sql = "insert into data_usergroup.t_person_imei_total_yesterday_tmp_final SELECT b.*,a.last_date as last2_date FROM(SELECT last_date,imei FROM data_usergroup.t_person_imei_total) AS a RIGHT JOIN data_usergroup.t_person_imei_total_yesterday_tmp AS b USING (imei);";

    //   # 删除total表中与final表的重复数据,注意这里是total2_shard(确认什么时候删除完毕，再进行下一步写入)
    String step3_sql = "ALTER TABLE data_usergroup.t_person_imei_total2_shard ON cluster cluster2_2shards_2replicas DELETE  WHERE  imei IN ( SELECT imei FROM data_usergroup.t_person_imei_total_yesterday_tmp );";

    //   # 插入final表数据到total表
    String step4_sql = "INSERT INTO data_usergroup.t_person_imei_total SELECT  *    FROM    data_usergroup.t_person_imei_total_yesterday_tmp_final";

    private void process() throws Exception {

        Link link = new Link();
        link.setIp(clickhouse_host);
        link.setDb(clickhouse_database);
        link.setPort(clickhouse_port);
        link.setUsername("");
        link.setPassword("");
        link.setType(CoreConstant.DataType.CLICKHOUSE);
        JdbcUtils jdbcUtils = new JdbcUtils(link);
        System.out.println("开始执行>>>");
        System.out.println("step1:清空t_person_imei_total_yesterday_tmp_final>>>");
        long startTime = System.currentTimeMillis();
        boolean execute = jdbcUtils.execute(step1_sql, null);
        System.out.println(execute);
        printTime(startTime);
        System.out.println("step2:插入final表t_person_imei_total_yesterday_tmp_final>>>");
        execute = jdbcUtils.execute(step2_sql, null);
        System.out.println(execute);
        printTime(startTime);
        System.out.println("step3:删除total表中与final表的重复数据>>>");
        execute = jdbcUtils.execute(step3_sql, null);
        System.out.println(execute);
        printTime(startTime);
        System.out.println("step4:插入final表数据到total表t_person_imei_total>>>");
        execute = jdbcUtils.execute(step4_sql, null);
        System.out.println(execute);
        printTime(startTime);
    }

    private void printTime(long startTime) {
        long endTime = System.currentTimeMillis();
        System.out.println("花费总秒数》》》" + (endTime - startTime) / 1000);
    }

    public static void main(String[] args) {
        ProcessingLast2Date processingLast2Date = new ProcessingLast2Date();
        try {
            processingLast2Date.process();
            System.out.println("执行成功>>>");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("执行失败>>>");
            System.exit(1);
        }
    }
}

```

### pom文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.bj58</groupId>
    <artifactId>user-portrait-data-processing</artifactId>
    <version>1.0</version>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <fastjson.version>1.2.60</fastjson.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>ru.yandex.clickhouse</groupId>
            <artifactId>clickhouse-jdbc</artifactId>
            <version>0.1.53</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.5.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```


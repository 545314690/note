

## 1.数据表

- 用户processlist表 

  hdp_fin_ba_defaultdb.o_ba_active_user_processlist_data（安卓设备可获取），包含packagename，上报时间等，详细信息见：http://iwiki.58corp.com/shorturl/1fUTHYAZLU

- packgename 和appname对应字典表 hdp_fin_ba_defaultdb.t_ba_packages_connection

- hdp_lbg_ecdata_dw_defaultdb.t_dict_app_tag  常用app 以及标签标注表，tag标注规则见下表:



| 业务     | 招聘 | 招聘      | 求职      | 招聘企业版（B） | 找工作    |      |      |               |          |      |      |      |
| -------- | ---- | --------- | --------- | --------------- | --------- | ---- | ---- | ------------- | -------- | ---- | ---- | ---- |
| 房产     | 房产 | 租房      | 二手房    | 新房            | 房东（B） |      |      |               |          |      |      |      |
| 车       | 汽车 | 司机（B） | 交警（B） | 加油（B）       | 租车      |      |      |               |          |      |      |      |
| 黄页     | 搬家 | 保姆      | 保洁      | 家务            | 维修      | 装修 | 物流 | 招商加盟（B） | 婚庆摄影 | 二手 | 宠物 |      |
| 交友     | 交友 | 相亲      |           |                 |           |      |      |               |          |      |      |      |
| 身份     | B端  |           |           |                 |           |      |      |               |          |      |      |      |
|          | C端  |           |           |                 |           |      |      |               |          |      |      |      |
| 性别     | 男   | 男性      |           |                 |           |      |      |               |          |      |      |      |
|          | 女   | 女性      |           |                 |           |      |      |               |          |      |      |      |
| 价值用户 |      | 投资理财  | 贷款      | 金融            |           |      |      |               |          |      |      |      |
|          |      |           |           |                 |           |      |      |               |          |      |      |      |

新增标签关键词

| 标签类型 | 一级标签 | 二级标签 | 关键词                                                       |
| -------- | -------- | -------- | ------------------------------------------------------------ |
| 身份     | 宝妈     |          | 宝妈、宝宝、早教、睡前故事、玩具                             |
| 业务     | 黄页     | 宠物     | 宠物、狗粮、猫粮、宠物医院                                   |
| 业务     | 交友     | 娱乐     | 视频、小视频、段子、搞笑、直播                               |
| 业务     | 房产     | 租房     | 自如、公寓、租房、短租                                       |
| 业务     | 黄页     | 司机     | 物流、搬家                                                   |
| 业务     | 黄页     | 普工     | 蓝领、民工、普工、电工、油漆工、焊工、水暖工、钢筋工 、水泥工、瓦工 |
| 价值用户 |          | 晒工资   | 理财、贷款、金融、投资                                       |

hdp_lbg_ecdata_dw_defaultdb.t_dict_app_tag表结构:

```sql
CREATE TABLE `hdp_lbg_ecdata_dw_defaultdb.t_dict_app_tag`(
  `tag_biz` string COMMENT '业务线标签：招聘、房产、车、黄页、交友', 
  `tag_identity` string COMMENT '身份标签：B端、C端', 
  `tag_gender` string COMMENT '性别标签：男、女', 
  `tag_valuable` string COMMENT '价值用户标签：0、1', 
  `keyword` string COMMENT '关键词', 
  `app_or_game` string COMMENT '应用/游戏', 
  `app_name` string COMMENT 'app name', 
  `icon` string COMMENT 'icon', 
  `type` string COMMENT 'app 分类', 
  `platform` string COMMENT '平台', 
  `descripton` string COMMENT 'app描述', 
  `full_name` string COMMENT 'app fullname', 
  `company` string COMMENT '公司', 
  `device` string COMMENT '设备')
COMMENT 'App标签字典表'
ROW FORMAT DELIMITED 
  FIELDS TERMINATED BY '\t' 
  LINES TERMINATED BY '\n' ;

```

标注完后使用hive命令load进表：

```sql
hive>  LOAD DATA LOCAL INPATH '/home/hdp_lbg_ecdata_dw/lisenmiao/dict_app_tag.txt' OVERWRITE INTO TABLE hdp_lbg_ecdata_dw_defaultdb.t_dict_app_tag;
```



生成tag表

```sql
insert into t_dict_app_tag_v3 (tag_biz,tag_identity,tag_gender,tag_valuable,keyword,app_or_game,app_name,icon,type,platform,descripton,full_name,company,device,tag_lv2,packagename)
select a.*,b.packagename from t_dict_app_tag_20201204 as  a join t_ba_packages_connection as b on a.app_name=b.name
```



## 2.牛顿冷却定律

使用牛顿冷却定律公式进行app processlist访问分数冷却

本期温度 = 上一期温度 x exp(-(冷却系数) x 间隔的小时数)

将这个公式用在"排名算法"，就相当于(假定本期没有增加净赞成票)

本期得分 = 上一期得分 x exp(-(冷却系数) x 间隔的小时数)

其中，"冷却系数"是一个你自己决定的值。如果假定一篇新文章的初始分数是100分，24小时之后"冷却"为1分，那么可以计算得到"冷却系数"约等于0.192。如果你想放慢"热文排名"的更新率，"冷却系数"就取一个较小的值，否则就取一个较大的值。

![img](https://img-blog.csdn.net/20160107150700540?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

我们假设：用户访问一次app计1分，访问app 在60天之后将score冷却为0.1分，间隔单位为天，则：

0.1=1x exp(-(冷却系数) x 60)，计算出冷却系数为：0.076753，则一个app被访问N天后的分数为：

1x exp(-(0.076753) x N)

## 3.计算规则

- 将app访问按每小时（频次可调整）进行去重，比如一个imei在一小时内被上报了20次访问过58同城app，我们只计算为1次
- 由于当日上报的数据有可能为几天前的数据，所以应该按照访问时间跟当天日期天数差进行带入冷却公式进行冷却
- 计算分为当日上报数据app访问分数和历史访问分数，最终分数为两个分数的加和



## 4.每日得分计算

```sql
 use hdp_lbg_ecdata_dw_defaultdb;
CREATE TABLE
IF
	NOT EXISTS hdp_lbg_ecdata_dw_defaultdb.t_app_processlist_score_1d (
	 all_score        string comment 'all_score',
	 imei        string comment 'imei',
	 packagename        string comment 'packagename',
	 app_name           string comment 'app name',
	 tag_biz        string comment '业务线标签：招聘、房产、车、黄页、交友',
	 tag_identity   string comment '身份标签：B端、C端',
	 tag_gender     string comment '性别标签：男、女',
	 tag_valuable   string comment '价值用户标签：0、1',
	 keyword        string comment '关键词'
	) COMMENT 'App标签临时表'  PARTITIONED BY (`dt` string) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n' STORED AS TEXTFILE;
	
INSERT OVERWRITE TABLE hdp_lbg_ecdata_dw_defaultdb.t_app_processlist_score_1d PARTITION (dt=${startDate})
select t1.all_score,t1.imei,t2.packagename,t2.app_name,t2.tag_biz,t2.tag_identity,t2.tag_gender,t2.tag_valuable,t2.keyword from 
(
select sum(score) as all_score,imei,name from 
(
select *, datediff(${startDate},ttime) as tdiff, if(datediff(${startDate},ttime)<60,exp(-0.076753*datediff(${startDate},ttime)),0.1) as score from (
	select count(1) as cnt,imei,from_unixtime(timestamp/1000,'yyyy-MM-dd HH') as ttime,name 
     from  hdp_fin_ba_defaultdb.o_ba_active_user_processlist_data lateral view explode(split(processlist,'\\|')) aa  as name 
     where dt=${dateSuffix} and length(name)>0
	 group by ttime,imei,name	
)
) GROUP by imei,name
)
t1 
JOIN (
select * from 
	hdp_lbg_ecdata_dw_defaultdb.t_dict_app_tag t3
	join 
	hdp_fin_ba_defaultdb.t_ba_packages_connection t2 on t3.app_name = t2.name	
)	t2 on t1.name = t2.packagename	
where (length(tag_biz)>0 or tag_valuable='1' or length(tag_gender)>0 or tag_identity='B端')
```



### 挖掘标签结果表：

| task id | 表名                                                         | 描述                 | 分区字段 | 数据库                      |
| ------- | ------------------------------------------------------------ | -------------------- | -------- | --------------------------- |
| 168372  | [t_app_processlist_score_identity_1d](http://dp.58corp.com/data-develop/task-list/task-detail/168372) | 用户身份表(一日)     | dt       | hdp_lbg_ecdata_dw_defaultdb |
| 168342  | [t_app_processlist_score_gender_1d](http://dp.58corp.com/data-develop/task-list/task-detail/168342) | 用户性别表(一日)     | dt       | hdp_lbg_ecdata_dw_defaultdb |
| 168336  | [t_app_processlist_score_valuable_1d](http://dp.58corp.com/data-develop/task-list/task-detail/168336) | 是否价值用户(一日)   | dt       | hdp_lbg_ecdata_dw_defaultdb |
| 168328  | [t_app_processlist_score_biz_1d](http://dp.58corp.com/data-develop/task-list/task-detail/168328) | 用户业务分类表(一日) | dt       | hdp_lbg_ecdata_dw_defaultdb |
| 168301  | [t_app_processlist_score_history](http://dp.58corp.com/data-develop/task-list/task-detail/168301) | app分数明细(历史)    | dt       | hdp_lbg_ecdata_dw_defaultdb |
| 168113  | [t_app_processlist_score_1d](http://dp.58corp.com/data-develop/task-list/task-detail/168113) | app分数明细(一日)    | dt       | hdp_lbg_ecdata_dw_defaultdb |

### 表说明

#### hdp_lbg_ecdata_dw_defaultdb.t_app_processlist_score_valuable_1d

| #    | 字段名           | 字段中文名         | 字段类型 | 不允许为空 | 分区字段 | 字段解释 |
| :--- | :--------------- | :----------------- | :------- | :--------- | :------- | :------- |
| 1    | **all_score**    | all_score          | string   |            |          |          |
| 2    | **imei**         | imei               | string   |            |          |          |
| 3    | **tag_valuable** | 价值用户标签：0、1 | string   |            |          |          |
| 4    | **dt**           |                    | string   |            | PTN      |          |

#### hdp_lbg_ecdata_dw_defaultdb.t_app_processlist_score_identity_1d

| #    | 字段名           | 字段中文名    | 字段类型 | 不允许为空 | 分区字段 | 字段解释 |
| :--- | :--------------- | :------------ | :------- | :--------- | :------- | :------- |
| 1    | **all_score**    | all_score     | string   |            |          |          |
| 2    | **imei**         | imei          | string   |            |          |          |
| 3    | **tag_identity** | 用户身份：B端 | string   |            |          |          |
| 4    | **dt**           |               | string   |            | PTN      |          |

#### hdp_lbg_ecdata_dw_defaultdb.t_app_processlist_score_history

| #    | 字段名           | 字段中文名                             | 字段类型 | 不允许为空 | 分区字段 | 字段解释 |
| :--- | :--------------- | :------------------------------------- | :------- | :--------- | :------- | :------- |
| 1    | **all_score**    | all_score                              | string   |            |          |          |
| 2    | **imei**         | imei                                   | string   |            |          |          |
| 3    | **packagename**  | packagename                            | string   |            |          |          |
| 4    | **app_name**     | app name                               | string   |            |          |          |
| 5    | **tag_biz**      | 业务线标签：招聘、房产、车、黄页、交友 | string   |            |          |          |
| 6    | **tag_identity** | 身份标签：B端、C端                     | string   |            |          |          |
| 7    | **tag_gender**   | 性别标签：男、女                       | string   |            |          |          |
| 8    | **tag_valuable** | 价值用户标签：0、1                     | string   |            |          |          |
| 9    | **keyword**      | 关键词                                 | string   |            |          |          |
| 10   | **dt**           |                                        | string   |            | PTN      |          |

#### hdp_lbg_ecdata_dw_defaultdb.t_app_processlist_score_gender_1d

| #    | 字段名         | 字段中文名       | 字段类型 | 不允许为空 | 分区字段 | 字段解释 |
| :--- | :------------- | :--------------- | :------- | :--------- | :------- | :------- |
| 1    | **all_score**  | all_score        | string   |            |          |          |
| 2    | **imei**       | imei             | string   |            |          |          |
| 3    | **tag_gender** | 性别标签：男，女 | string   |            |          |          |
| 4    | **dt**         |                  | string   |            | PTN      |          |

#### hdp_lbg_ecdata_dw_defaultdb.t_app_processlist_score_biz_1d

| #    | 字段名        | 字段中文名                             | 字段类型 | 不允许为空 | 分区字段 | 字段解释 |
| :--- | :------------ | :------------------------------------- | :------- | :--------- | :------- | :------- |
| 1    | **all_score** | all_score                              | string   |            |          |          |
| 2    | **imei**      | imei                                   | string   |            |          |          |
| 3    | **prob**      | 概率                                   | float    |            |          |          |
| 4    | **tag_biz**   | 业务线标签：招聘、房产、车、黄页、交友 | string   |            |          |          |
| 5    | **dt**        |                                        | string   |            | PTN      |          |

#### **hdp_lbg_ecdata_dw_defaultdb.t_app_processlist_score_1d**

|      |                  |                                        |          |            |          |          |
| :--- | :--------------- | :------------------------------------- | :------- | :--------- | :------- | :------- |
| #    | 字段名           | 字段中文名                             | 字段类型 | 不允许为空 | 分区字段 | 字段解释 |
| 1    | **all_score**    | all_score                              | string   |            |          |          |
| 2    | **imei**         | imei                                   | string   |            |          |          |
| 3    | **packagename**  | packagename                            | string   |            |          |          |
| 4    | **app_name**     | app name                               | string   |            |          |          |
| 5    | **tag_biz**      | 业务线标签：招聘、房产、车、黄页、交友 | string   |            |          |          |
| 6    | **tag_identity** | 身份标签：B端、C端                     | string   |            |          |          |
| 7    | **tag_gender**   | 性别标签：男、女                       | string   |            |          |          |
| 8    | **tag_valuable** | 价值用户标签：0、1                     | string   |            |          |          |
| 9    | **keyword**      | 关键词                                 | string   |            |          |          |
| 10   | **dt**           |                                        |          |            |          |          |
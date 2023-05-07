# Excel读写工具

## Excel最大行列数问题

### Excel的最大行数和列数分别是多少

版本不同，excel的最大行数和列数不同。2003版最大行数是65536行，最大列数是256列。文件后缀.xls

Excel2007及以后的版本最大行数是1048576行，最大列数是16384列。文件后缀.xlsx

### 查看最大行列数

2007最大行数：1048567，查看方法：Ctrl+下方向键(↓)
2007最大列名为：XFD，查看方法：Ctrl+右方向键(→)

### 写入行列数大于最大值的时候poi报错

> 当Excel写入超大行数的时候，建议分多个Sheet页，每页最多写入100W行

```java
java.lang.IllegalArgumentException: Invalid row number (1048576) outside allowable range (0..1048575)

	at org.apache.poi.xssf.streaming.SXSSFSheet.createRow(SXSSFSheet.java:123)
	at org.apache.poi.xssf.streaming.SXSSFSheet.createRow(SXSSFSheet.java:65)
```



## 使用Easy-Excel 操作Excel文件（极力推荐）

> EasyExcel是一个基于Java的简单、省内存的读写Excel的开源项目。在尽可能节约内存的情况下支持读写百M的Excel
>
> git 地址：https://github.com/alibaba/easyexcel
>
> 文档地址：https://alibaba-easyexcel.github.io/index.html

由于文档很详细这里不在赘述，具体文档示例可以通过clone  git代码到本地，直接可以再test包下面找到并运行

## 使用Freemarker模板引擎导出复杂格式的Excel

> 对于复杂样式，要是用 Apache poi, jxl, 阿里 EasyExcel 去实现，不可避免的，代码肯定会非常繁琐。
>
> 该方案原理是使用Freemarker模板引擎，该方案同时适用与导出Html、XML、word等复杂格式文件。
>
> 要注意的是：通过模板引擎以字符串替换的方式写入，写入速度会更快，但是写完的文件比poi写入的大得多

具体实现可参考

- [如何通过 Freemark 优雅地生成那些花里胡哨的复杂样式 Excel 文件？](https://juejin.im/post/5cfb6650e51d45775d516f4f)
- [freemarker生成excel、word、html、xml实例教程](https://blog.csdn.net/u010722643/article/details/41732607)
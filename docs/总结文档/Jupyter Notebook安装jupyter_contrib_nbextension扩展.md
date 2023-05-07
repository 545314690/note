## Jupyter Notebook安装jupyter_contrib_nbextension扩展

> 官方安装文档地址：
> https://github.com/ipython-contrib/jupyter_contrib_nbextensions/blob/master/README.md

### 安装nbextensions

```
pip install jupyter_contrib_nbextensions

```

### 安装 javascript and css files

```sh
jupyter contrib nbextension install --user
```

### 安装configurator

```
pip install jupyter_nbextensions_configurator
```

### 重新启动Jupyter Notebook

就会发现已经有Nbextensions标签了

![1568881294286](D:\58工作内容\总结文档\1568881294286.png)

### 常用扩展功能

勾选上即可：
最常用功能说明，建议勾选上：
Collapsible headings 折叠标题
Notify 通知机制，跑一些耗时较久的任务，完成后通知
Codefolding 折叠代码
Zen mode extension 隐藏活动状态栏，方便注意代码
Execute time extension 显示运行的时间
2to3 Converter 把python2.x代码转换为python3.x
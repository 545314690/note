

# Windows10上安装fasttext

### 无法通过conda install 安装fasttext

## 1.直接 pip install fasttext 

```
报错：`Microsoft Visual C++ 14.0 is required. Get it with "Microsoft Visual C++ Build Tools":`
```

### 安装Microsoft Visual C++ Build Tools

> 下载链接 https://download.microsoft.com/download/5/f/7/5f7acaeb-8363-451f-9425-68a90f98b238/visualcppbuildtools_full.exe



![1568715595866](D:\58工作内容\总结文档\1568715595866.png)

自定义选中win10 SDK，下一步 

![1568722670668](D:\58工作内容\总结文档\1568722670668.png)

### 激活conda环境

> ### python3.6是你的环境名字

```
conda activate python3.6
```

### pip安装

```sh
pip install fasttext
```

## 2.通过whl文件安装

> 下载链接 https://www.lfd.uci.edu/~gohlke/pythonlibs/#fasttext

我的python是3.6，所以下载3.6版本对应的



![1568723957133](D:\58工作内容\总结文档\1568723957133.png)

### 同样要先激活的你环境

```sh
conda activate python3.6
```

### 安装whl文件

```sh
pip install fasttext-0.9.1-cp36-cp36m-win_amd64.whl
```

## 测试

```python
(python3.6) E:\>python
Python 3.6.8 |Anaconda, Inc.| (default, Feb 21 2019, 18:30:04) [MSC v.1916 64 bit (AMD64)] on win32
Type "help", "copyright", "credits" or "license" for more information.
>>> import fasttext
>>>

```

## whl文件是什么

![1568773234812](D:\58工作内容\总结文档\1568773234812.png)

## 总结

### 推荐whl安装

第二种方式安装不需要安装c++ build tool成功
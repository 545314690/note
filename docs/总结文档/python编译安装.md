# Python3.6编译安装

> 下载地址 https://www.python.org/ftp/python/3.6.9/Python-3.6.9.tgz

## 环境ubuntu

```shell
apt-get install build-essential
apt-get install gcc automake autoconf libtool make
```

## 下载

```shell
wget https://www.python.org/ftp/python/3.6.9/Python-3.6.9.tgz
tar -zxvf Python-3.6.9.tgz
cd Python-3.6.9
```

## 安装

```shell
./configure 
make && make install

```



#### 报错1

```
 File "/data/soft/Python-3.6.9/Lib/ensurepip/__init__.py", line 117, in _bootstrap
    return _run_pip(args + [p[0] for p in _PROJECTS], additional_paths)
  File "/data/soft/Python-3.6.9/Lib/ensurepip/__init__.py", line 27, in _run_pip
    import pip._internal
zipimport.ZipImportError: can't decompress data; zlib not available
Makefile:1102: recipe for target 'install' failed
make: *** [install] Error 1

```

#### 解决1

```sh
apt-get install zlibc zlib1g-dev
```

## 验证

```shell
# 建立软连接
ln -s /usr/local/bin/python3 /usr/bin/python
python -V
Python 3.6.9

```



## 安装pip

```sh
wget https://bootstrap.pypa.io/get-pip.py
python get-pip
# 建立软连接
ln -s /usr/local/bin/pip3 /usr/bin/pip
```

## 验证

```sh
pip -V
pip 18.1 from /usr/local/lib/python3.6/site-packages/pip (python 3.6)
```


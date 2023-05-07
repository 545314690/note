# Fasttext安装

> 参考
>
>  https://fasttext.cc/docs/en/supervised-tutorial.html
>
> https://github.com/facebookresearch/fastText

## 环境ubuntu

## Requirements

- (g++-4.7.2 or newer) or (clang-3.3 or newer)  【C++11 support】
- Python 2.6 or newer
- NumPy & SciPy
- For the python bindings (see the subdirectory python) you will need:
  - Python version 2.7 or >=3.4
  - NumPy & SciPy
  - [pybind11](https://github.com/pybind/pybind11)

```shell
apt-get install build-essential
apt-get install gcc automake autoconf libtool make
```

## 下载

```shell
wget https://github.com/facebookresearch/fastText/archive/v0.9.1.zip
unzip v0.9.1.zip
```

## 安装

```sql
$ cd fastText-0.9.1
# for command line tool :
$ make
# for python bindings :
$ pip install .
```

### 报错

```sh
       ----------------------------------------
Command "python setup.py egg_info" failed with error code 1 in /tmp/pip-req-build-t_oux7sk/
pip is configured with locations that require TLS/SSL, however the ssl module in Python is not available.
Could not fetch URL https://pypi.org/simple/pip/: There was a problem confirming the ssl certificate: HTTPSConnectionPool(host='pypi.org', port=443): Max retries exceeded with url: /simple/pip/ (Caused by SSLError("Can't connect to HTTPS URL because the SSL module is not available.",)) - skipping

```

### 解决：使用阿里云pip源

```sh
#下载源修改为国内镜像源，大幅加快下载速度：
mkdir ~/.pip

vim ~/.pip/pip.conf

[global]

index-url = http://mirrors.aliyun.com/pypi/simple/

[install]

trusted-host = mirrors.aliyun.com
```

### 再次pip install

```sh
pip install .
```

### 建立软连接

```sh
ln -s /data/soft/fasttext/fastText-0.9.1/fasttext /usr/bin/fasttext
```

## 测试

### Shell命令测试

```sh
root@hdfs-01:/data/soft/fasttext/fastText-0.9.1# ./fasttext
usage: fasttext <command> <args>

The commands supported by fasttext are:

  supervised              train a supervised classifier
  quantize                quantize a model to reduce the memory usage
  test                    evaluate a supervised classifier
  test-label              print labels with precision and recall scores
  predict                 predict most likely labels
  predict-prob            predict most likely labels with probabilities
  skipgram                train a skipgram model
  cbow                    train a cbow model
  print-word-vectors      print word vectors given a trained model
  print-sentence-vectors  print sentence vectors given a trained model
  print-ngrams            print ngrams given a trained model and word
  nn                      query for nearest neighbors
  analogies               query for analogies
  dump                    dump arguments,dictionary,input/output vectors

```

### Python使用测试

```python
root@hdfs-01:/data/soft/fasttext/fastText-0.9.1# python
Python 3.6.9 (default, Sep 17 2019, 15:17:42) 
[GCC 5.4.0 20160609] on linux
Type "help", "copyright", "credits" or "license" for more information.
>>> import fasttext
>>> help(fasttext.FastText)

Help on module fasttext.FastText in fasttext:

NAME
    fasttext.FastText

DESCRIPTION
    # Copyright (c) 2017-present, Facebook, Inc.
    # All rights reserved.
    #
    # This source code is licensed under the MIT license found in the
    # LICENSE file in the root directory of this source tree.

FUNCTIONS
    cbow(*kargs, **kwargs)
    
    eprint(cls, *args, **kwargs)
    
    load_model(path)
        Load a model given a filepath and return a model object.
    
    read_args(arg_list, arg_dict, arg_names, default_values)
    
    skipgram(*kargs, **kwargs)
    
    supervised(*kargs, **kwargs)
    
    tokenize(text)
        Given a string of text, tokenize it and return a list of tokens
    
    train_supervised(*kargs, **kwargs)
        Train a supervised model and return a model object.
        
        input must be a filepath. The input text does not need to be tokenized
        as per the tokenize function, but it must be preprocessed and encoded
        as UTF-8. You might want to consult standard preprocessing scripts such
        as tokenizer.perl mentioned here: http://www.statmt.org/wmt07/baseline.html
        
        The input file must must contain at least one label per line. For an
        example consult the example datasets which are part of the fastText
        repository such as the dataset pulled by classification-example.sh.
    
    train_unsupervised(*kargs, **kwargs)
        Train an unsupervised model and return a model object.
        
        input must be a filepath. The input text does not need to be tokenized
        as per the tokenize function, but it must be preprocessed and encoded
        as UTF-8. You might want to consult standard preprocessing scripts such
        as tokenizer.perl mentioned here: http://www.statmt.org/wmt07/baseline.html
        
        The input field must not contain any labels or use the specified label prefix
        unless it is ok for those words to be ignored. For an example consult the
        dataset pulled by the example script word-vector-example.sh, which is
:


```


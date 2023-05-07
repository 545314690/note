## bert安装

官网：https://github.com/google-research/bert

### 安装：

```shell
cd  /data
git clone https://github.com/google-research/bert
```

### 下载谷歌中文预训练模型

下载并解压到`/data/bert_test`：https://storage.googleapis.com/bert_models/2018_11_03/chinese_L-12_H-768_A-12.zip

```shell
unzip chinese_L-12_H-768_A-12.zip -d /data/bert_test
```

## 准备样本数据

准备样本数据到`/data/bert_test/data_wishes/`：

| 样本                | 数据集 |      |
| ------------------- | ------ | ---- |
| bert_data_train.csv | 训练集 |      |
| bert_data_dev.csv   | 验证集 |      |
| bert_data_test.csv  | 预测集 |      |

​    数据格式：

```
text,label
昨天的保姆新闻事件大家应该都看到了 选保姆还是找正规的家政公司 ,中立
保证金及运费无法提现和退保联系分公司客服永远打不通的电话,负面
58太好了,正面
```

## 编辑bert训练代码

编辑`bert/run_classifier.py`

### 1.添加数据处理器：

```python
class IMDBProcessor(DataProcessor):
    """
    IMDB data processor
    """
    def _read_csv(self, data_dir, file_name):
        with tf.gfile.Open(data_dir + file_name, "r") as f:
            reader = csv.reader(f, delimiter=",", quotechar=None)
            lines = []
            for line in reader:
                lines.append(line)

        return lines

    def get_train_examples(self, data_dir):
        lines = self._read_csv(data_dir, "bert_data_train.csv")

        examples = []
        for (i, line) in enumerate(lines):
            if i == 0:
                continue
            guid = "train-%d" % (i)
            text_a = tokenization.convert_to_unicode(line[0])
            label = tokenization.convert_to_unicode(line[1])
            examples.append(
                InputExample(guid=guid, text_a=text_a, label=label))
        return examples

    def get_dev_examples(self, data_dir):
        lines = self._read_csv(data_dir, "bert_data_dev.csv")

        examples = []
        for (i, line) in enumerate(lines):
            if i == 0:
                continue
            guid = "dev-%d" % (i)
            text_a = tokenization.convert_to_unicode(line[0])
            label = tokenization.convert_to_unicode(line[1])
            examples.append(
                InputExample(guid=guid, text_a=text_a, label=label))
        return examples

    def get_test_examples(self, data_dir):
        lines = self._read_csv(data_dir, "bert_data_test.csv")

        examples = []
        for (i, line) in enumerate(lines):
            if i == 0:
                continue
            guid = "test-%d" % (i)
            text_a = tokenization.convert_to_unicode(line[0])
            label = tokenization.convert_to_unicode(line[1])
            examples.append(
                InputExample(guid=guid, text_a=text_a, label=label))
        return examples

    def get_labels(self):
        return ["负面", "正面", "中立"]
```

### 2.注册数据处理器`"imdb": IMDBProcessor`

```python
  processors = {
      "cola": ColaProcessor,
      "mnli": MnliProcessor,
      "mrpc": MrpcProcessor,
      "xnli": XnliProcessor,
      "imdb": IMDBProcessor
  }
```

### 3.修改`metric_fn`,使验证时可以看到召回率和精确度

```python
  def metric_fn(per_example_loss, label_ids, logits, is_real_example):
    predictions = tf.argmax(logits, axis=-1, output_type=tf.int32)
    accuracy = tf.compat.v1.metrics.accuracy(
        labels=label_ids, predictions=predictions, weights=is_real_example)
    loss = tf.compat.v1.metrics.mean(values=per_example_loss, weights=is_real_example)
    precision = tf.compat.v1.metrics.precision(labels=label_ids,predictions=predictions,weights=is_real_example)
    # auc = tf.compat.v1.metrics.auc(labels=label_ids,predictions=predictions,weights=is_real_example)
    recall = tf.compat.v1.metrics.recall(labels=label_ids,predictions=predictions,weights=is_real_example)
    # f1_score = tf.contrib.metrics.f1_score(labels=label_ids, predictions=predictions)
    return {
        "eval_accuracy": accuracy,
        "eval_loss": loss,
        "eval_recall": recall,
        # "eval_auc": auc,
        "eval_precision": precision,
        # "eval_f1_score": f1_score
    }
```

### 4.预测时将标签写入预测结果中

```python
    with tf.gfile.GFile(output_predict_file, "w") as writer:
      num_written_lines = 0
      tf.logging.info("***** Predict results *****")
      for (i, prediction) in enumerate(result):
        probabilities = prediction["probabilities"]
        # 求出概率最大值：label index
        prob_list = probabilities.tolist()
        label_index = prob_list.index(max(prob_list))
        label = label_list[label_index]
        if i >= num_actual_predict_examples:
          break
        output_line = "\t".join(
            str(class_probability)
            for class_probability in probabilities)
        # 将标签写入预测结果中
        output_line += "\t" + str(label_index) + "\t" + label + "\n"
        writer.write(output_line)
        num_written_lines += 1
```

## 训练模型

使用我们注册的`imdb`，指定`do_train=true`，`do_eval=true`

```shell
export BERT_BASE_DIR=/data/bert_test/chinese_L-12_H-768_A-12
export MY_DATASET_WISHES=/data/bert_test/data_wishes/
python /data/bert/run_classifier.py \
  --data_dir=$MY_DATASET_WISHES \
  --task_name=imdb \
  --vocab_file=$BERT_BASE_DIR/vocab.txt \
  --bert_config_file=$BERT_BASE_DIR/bert_config.json \
  --output_dir=/data/bert_test/output_wishes/ \
  --do_train=true \
  --do_eval=true \
  --init_checkpoint=$BERT_BASE_DIR/bert_model.ckpt \
  --max_seq_length=200 \
  --train_batch_size=128 \
  --learning_rate=5e-5 \
  --num_train_epochs=2.0
```

在`/data/bert_test/output_wishes/` 可以看到模型输出

cat eval_results.txt 

```shell

eval_accuracy = 0.8856
eval_loss = 0.36825505
eval_precision = 0.9229607
eval_recall = 0.9174174
global_step = 175
loss = 0.36659196

```



## 预测模型

指定`do_predict=true`

```shell
export BERT_BASE_DIR=/data/bert_test/chinese_L-12_H-768_A-12
export MY_DATASET_WISHES=/data/bert_test/data_wishes/
python /data/bert/run_classifier.py \
  --data_dir=$MY_DATASET_WISHES \
  --task_name=imdb \
  --vocab_file=$BERT_BASE_DIR/vocab.txt \
  --bert_config_file=$BERT_BASE_DIR/bert_config.json \
  --output_dir=/data/bert_test/output_wishes/ \
  --do_train=false \
  --do_eval=false \
  --do_predict=true \
  --init_checkpoint=$BERT_BASE_DIR/bert_model.ckpt \
  --max_seq_length=200 \
  --predict_batch_size=1000
```





## 定时任务

``/data/bert_test/data_wishes`下有几个脚本

| 脚本                           | 说明                               | 使用方法                                                    |
| ------------------------------ | ---------------------------------- | ----------------------------------------------------------- |
| predict_and_2mysql.sh          | 预测某天并写入库，不写日期默认昨天 | sh predict_and_2mysql.sh 2020-05-03                         |
| webhdfs.py                     | 下载hdfs文件                       | 被predict_and_2mysql.sh调用                                 |
| proccess_and_2mysql.py         | 合并预测结果并入库                 | 被predict_and_2mysql.sh调用                                 |
| proccess_and_2mysql_history.py | 重跑历史                           | python proccess_and_2mysql_history.py 2020-05-01 2020-05-03 |
| predict_and_2mysql.log         | 日志                               |                                                             |

```shell
crontab -e
```

早上6点6分执行脚本`/data/bert_test/data_wishes/predict_and_2mysql.sh`写入

```shell
06 6 * * * /bin/sh /data/bert_test/data_wishes/predict_and_2mysql.sh > /data/bert_test/data_wishes/predict_and_2mysql.log 2>&1
```


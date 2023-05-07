## bert安装

官网：https://github.com/google-research/bert

可以直接使用修改过的`http://igit.58corp.com/ecdata_ai/bert.git`

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

### 2.注册数据处理器`"fangchan": IMDBProcessor`

```python
  processors = {
      "cola": ColaProcessor,
      "mnli": MnliProcessor,
      "mrpc": MrpcProcessor,
      "xnli": XnliProcessor,
      "fangchan": FangChanProcessor
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

使用我们注册的`fangchan`，指定`do_train=true`，`do_eval=true`

> 数据集：
>
> label
> 其它    28608
> 房产    20493
>
> 训练、测试集：
>
> 12277 /data/bert_test/data_fangchan/bert_data_test.csv
> 36826 /data/bert_test/data_fangchan/bert_data_train.csv
>
> 耗时：CPU ， 5小时 
> 2020-06-15 20:15:30  开始
>
> 2020-06-16 01:10:30  结束

```shell
export BERT_BASE_DIR=/data/bert_test/chinese_L-12_H-768_A-12
export MY_DATASET_FANGCHAN=/data/bert_test/data_fangchan/
python /data/bert/run_classifier.py \
  --data_dir=$MY_DATASET_FANGCHAN \
  --task_name=fangchan \
  --vocab_file=$BERT_BASE_DIR/vocab.txt \
  --bert_config_file=$BERT_BASE_DIR/bert_config.json \
  --output_dir=/data/bert_test/output_fangchan_label1/ \
  --do_train=true \
  --do_eval=true \
  --init_checkpoint=$BERT_BASE_DIR/bert_model.ckpt \
  --max_seq_length=200 \
  --train_batch_size=128 \
  --learning_rate=5e-5 \
  --num_train_epochs=2.0
```

在`/data/bert_test/output_fangchan_label1/` 可以看到模型输出

cat /data/bert_test/output_fangchan_label1/eval_results.txt 

```shell
eval_accuracy = 0.9916911
eval_loss = 0.034399934
eval_precision = 0.99283504
eval_recall = 0.99283504
global_step = 575
loss = 0.03438964
```

训练模型label2

```shell
export BERT_BASE_DIR=/data/bert_test/chinese_L-12_H-768_A-12
export MY_DATASET_FANGCHAN2=/data/bert_test/data_fangchan_label2/
python /data/bert/run_classifier.py \
  --data_dir=$MY_DATASET_FANGCHAN2 \
  --task_name=fangchan2 \
  --vocab_file=$BERT_BASE_DIR/vocab.txt \
  --bert_config_file=$BERT_BASE_DIR/bert_config.json \
  --output_dir=/data/bert_test/output_fangchan_label2/ \
  --do_train=true \
  --do_eval=true \
  --init_checkpoint=$BERT_BASE_DIR/bert_model.ckpt \
  --max_seq_length=200 \
  --train_batch_size=128 \
  --learning_rate=5e-5 \
  --num_train_epochs=2.0
```

在`/data/bert_test/output_fangchan_label2/` 可以看到模型输出

cat /data/bert_test/output_fangchan_label2/eval_results.txt 

```shell
eval_accuracy = 0.9744483
eval_loss = 0.11937177
eval_precision = 0.9926108
eval_recall = 0.99179655
global_step = 80
loss = 0.11897847

```

训练模型label3

```shell
export BERT_BASE_DIR=/data/bert_test/chinese_L-12_H-768_A-12
export MY_DATASET_FANGCHAN3=/data/bert_test/data_fangchan_label3/
python /data/bert/run_classifier.py \
  --data_dir=$MY_DATASET_FANGCHAN3 \
  --task_name=fangchan3 \
  --vocab_file=$BERT_BASE_DIR/vocab.txt \
  --bert_config_file=$BERT_BASE_DIR/bert_config.json \
  --output_dir=/data/bert_test/output_fangchan_label3/ \
  --do_train=true \
  --do_eval=true \
  --init_checkpoint=$BERT_BASE_DIR/bert_model.ckpt \
  --max_seq_length=200 \
  --train_batch_size=128 \
  --learning_rate=5e-5 \
  --num_train_epochs=2.0
```

在`/data/bert_test/output_fangchan_label3/` 可以看到模型输出

cat /data/bert_test/output_fangchan_label3/eval_results.txt 

```shell
eval_accuracy = 0.9504132
eval_loss = 0.16478045
eval_precision = 0.94259816
eval_recall = 0.9842271
global_step = 22
loss = 0.16882081
```

训练模型label3 1个月数据

```shell
export BERT_BASE_DIR=/data/bert_test/chinese_L-12_H-768_A-12
export MY_DATASET_FANGCHAN3=/data/bert_test/data_fangchan_label3_1month/
python /data/bert/run_classifier.py \
  --data_dir=$MY_DATASET_FANGCHAN3 \
  --task_name=fangchan3 \
  --vocab_file=$BERT_BASE_DIR/vocab.txt \
  --bert_config_file=$BERT_BASE_DIR/bert_config.json \
  --output_dir=/data/bert_test/output_fangchan_label3_1month/ \
  --do_train=true \
  --do_eval=true \
  --init_checkpoint=$BERT_BASE_DIR/bert_model.ckpt \
  --max_seq_length=200 \
  --train_batch_size=128 \
  --learning_rate=5e-5 \
  --num_train_epochs=2.0
```

在`/data/bert_test/output_fangchan_label3_1month/` 可以看到模型输出

cat /data/bert_test/output_fangchan_label3_1month/eval_results.txt 

```
eval_accuracy = 0.97624433
eval_loss = 0.09298542
eval_precision = 0.97748727
eval_recall = 0.99703705
global_step = 82
loss = 0.09298542
```



## 预测模型

指定`do_predict=true`

```shell
export BERT_BASE_DIR=/data/bert_test/chinese_L-12_H-768_A-12
export MY_DATASET_FANGCHAN=/data/bert_test/data_fangchan_predict/
python /data/bert/run_classifier.py \
  --data_dir=$MY_DATASET_FANGCHAN \
  --task_name=fangchan \
  --vocab_file=$BERT_BASE_DIR/vocab.txt \
  --bert_config_file=$BERT_BASE_DIR/bert_config.json \
  --output_dir=/data/bert_test/output_fangchan_label1/ \
  --do_train=false \
  --do_eval=false \
  --do_predict=true \
  --init_checkpoint=$BERT_BASE_DIR/bert_model.ckpt \
  --max_seq_length=200 \
  --predict_batch_size=1000
```



label3

`

```shell
export BERT_BASE_DIR=/data/bert_test/chinese_L-12_H-768_A-12
export MY_DATASET_FANGCHAN3=/data/bert_test/data_fangchan_label3_1month/
python /data/bert/run_classifier.py \
  --data_dir=$MY_DATASET_FANGCHAN3 \
  --task_name=fangchan3 \
  --vocab_file=$BERT_BASE_DIR/vocab.txt \
  --bert_config_file=$BERT_BASE_DIR/bert_config.json \
  --output_dir=/data/bert_test/output_fangchan_label3_1month/ \
  --do_train=false \
  --do_eval=false \
  --do_predict=true \
  --init_checkpoint=$BERT_BASE_DIR/bert_model.ckpt \
  --max_seq_length=200 \
  --predict_batch_size=1000
```

 ni
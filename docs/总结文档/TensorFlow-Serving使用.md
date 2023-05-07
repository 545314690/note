## 使用TensorFlow-serving部署模型及访问

> 参考：https://www.tensorflow.org/tfx/serving/docker
>
> https://www.kesci.com/home/project/5ea7f8ca564b12002c09f610
>
> https://zhuanlan.zhihu.com/p/96917543

### 模型保存

```python
## 将模型保存成pb格式文件
export_path = "/data/user_growth_model/"
version = "1"       #后续可以通过版本号进行模型版本迭代与管理
model.save(export_path+version, save_format="tf")
```

### 查看模型参数:

saved_model_cli show --dir /data/user_growth_model/1/ --all

> 注意三个值：
>
> signature_def：serving_default
>
> inputs：dense_input
>
> outputs：dense_3

```shell
saved_model_cli show --dir /data/user_growth_model/1/ --all

2020-09-10 18:14:19.141354: W tensorflow/stream_executor/platform/default/dso_loader.cc:59] Could not load dynamic library 'libcudart.so.10.1'; dlerror: libcudart.so.10.1: cannot open shared object file: No such file or directory; LD_LIBRARY_PATH: /home/work/anaconda3/lib/:/opt/rh/devtoolset-9/root/usr/lib64:/opt/rh/devtoolset-9/root/usr/lib:/opt/rh/devtoolset-9/root/usr/lib64/dyninst:/opt/rh/devtoolset-9/root/usr/lib/dyninst:/opt/rh/devtoolset-9/root/usr/lib64:/opt/rh/devtoolset-9/root/usr/lib:/opt/rh/devtoolset-9/root/usr/lib64:/opt/rh/devtoolset-9/root/usr/lib:/opt/rh/devtoolset-9/root/usr/lib64/dyninst:/opt/rh/devtoolset-9/root/usr/lib/dyninst:/opt/rh/devtoolset-9/root/usr/lib64:/opt/rh/devtoolset-9/root/usr/lib
2020-09-10 18:14:19.141416: I tensorflow/stream_executor/cuda/cudart_stub.cc:29] Ignore above cudart dlerror if you do not have a GPU set up on your machine.

MetaGraphDef with tag-set: 'serve' contains the following SignatureDefs:

signature_def['__saved_model_init_op']:
  The given SavedModel SignatureDef contains the following input(s):
  The given SavedModel SignatureDef contains the following output(s):
    outputs['__saved_model_init_op'] tensor_info:
        dtype: DT_INVALID
        shape: unknown_rank
        name: NoOp
  Method name is: 

signature_def['serving_default']:
  The given SavedModel SignatureDef contains the following input(s):
    inputs['dense_input'] tensor_info:
        dtype: DT_FLOAT
        shape: (-1, 39)
        name: serving_default_dense_input:0
  The given SavedModel SignatureDef contains the following output(s):
    outputs['dense_3'] tensor_info:
        dtype: DT_FLOAT
        shape: (-1, 1)
        name: StatefulPartitionedCall:0
  Method name is: tensorflow/serving/predict

```



### 使用docker启动TensorFlow-Serving

内部端口: 8501是http访问端口，8500是rpc访问端口

```shell
docker run -t --rm --name tfserving -p 8585:8501 -p 8580:8500 \
-v /data/user_growth_model/data/:/models \
-e MODEL_NAME=user_growth_model \
tensorflow/serving & >server.log 2>&1
```

### 访问服务

#### 1，curl访问

```shell
curl -d '{"instances": [[0.00984358, 0.081081, 0, 0, 0,1, 0, 0, 0, 0,0, 0, 1, 0, 0,1, 0, 0, 0, 0,0, 0, 0, 1, 0,0, 0, 0, 0, 0,0, 0, 0, 0, 0,0, 0, 0, 1]]}'  -X POST http://10.126.87.72:8585/v1/models/user_growth_model:predict
```

#### 2.python访问http服务

```python
import requests
def request_http():
    # x = X_test.head(10).values.tolist()
    x = [
        [0.00984358, 0.081081, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
         0, 0, 0, 0, 0, 0, 1]]
    data = json.dumps({"signature_name": "serving_default", "instances": x})
    headers = {"content-type": "application/json"}
    json_response = requests.post('http://10.126.87.72:8585/v1/models/user_growth_model:predict',
                                  data=data, headers=headers)
    predictions = json.loads(json_response.text)["predictions"]
    print(predictions)
```

#### 3.访问rpc

安装依赖包

```shell
pip install tensorflow-serving-api
```

访问rpc实例

```python
from tensorflow_serving.apis import predict_pb2
from tensorflow_serving.apis import prediction_service_pb2_grpc
import json, requests
import grpc
import tensorflow as tf
import numpy as np
def request_server(x, server_url):
    '''
    用于向TensorFlow Serving服务请求推理结果的函数。
    :param img_resized: 经过预处理的待推理图片数组，numpy array，shape：(h, w, 3)
    :param server_url: TensorFlow Serving的地址加端口，str，如：'0.0.0.0:8500'
    :return: 模型返回的结果数组，numpy array
    '''
    # Request.
    channel = grpc.insecure_channel(server_url)
    stub = prediction_service_pb2_grpc.PredictionServiceStub(channel)
    request = predict_pb2.PredictRequest()
    request.model_spec.name = "user_growth_model"  # 模型名称，启动容器命令的model_name参数
    request.model_spec.signature_name = "serving_default"  # 签名名称，刚才叫你记下来的
    # "dense_input"是你导出模型时设置的输入名称，刚才叫你记下来的
    request.inputs["dense_input"].CopyFrom(
        tf.make_tensor_proto(x, shape=[len(x),len(x[0])]))  #这个shape是(数据量，数据维度)
    print(request)
    response = stub.Predict(request, 5.0)  # 5 secs timeout
    return np.asarray(response.outputs["dense_3"].float_val) # dense_3为输出名称，刚才叫你记下来的

def request_rpc():
    # grpc地址及端口，为你容器所在机器的ip + 容器启动命令里面设置的port
    server_url = '10.126.87.72:8580'
    # x=X_test.head(10).values.tolist()
    x=[[0.00984358, 0.081081, 0, 0, 0,1, 0, 0, 0, 0,0, 0, 1, 0, 0,1, 0, 0, 0, 0,0, 0, 0, 1, 0,0, 0, 0, 0, 0,0, 0, 0, 0, 0,0, 0, 0, 1]]
    request_server(x, server_url)
```




## 接口名称

### 1) 请求地址

>https://blog.imyzc.com/api/v1/classes

### 2) 调用方式：HTTP get

### 3) 接口描述：

* 获取所有分类接口

### 4) 请求参数:

#### GET参数:
|字段名称       |字段说明         |类型            |必填            |备注     |
| -------------|:--------------:|:--------------:|:--------------:| ------:|
|maxCount|限制获取数据最大条数|int|N|-|



### 5) 请求返回结果:

```
{
    "success": true,
    "code": "200",
    "message": "成功",
    "data": {
        "list": [
            {
                "id": 1,
                "title": "测试分类",
                "status": 1,
                "count": 0,
                "urlName": "test",
                "previewText": "测试分类说明文字测试分类说明文字x2",
                "previewImage": "f17dc33d5f5db99d78ac3db0a7d37a46"
            },
            ...
        ],
        "allCount": 4
    }
}
```


### 6) 请求返回结果参数说明:
|字段名称       |字段说明         |类型            |必填            |备注     |
| -------------|:--------------:|:--------------:|:--------------:| ------:|
|list|文章分类结果数组|array|Y|-|
|id|文章分类ID|int|Y|-|
|title|文章分类标题|string|Y|-|
|count|该分类下有多少篇文章|int|Y|-|
|urlName|文章分类URL名字|string|Y|-|
|previewText|文章分类说明文字|string|Y|-|
|previewImage|文章分类缩略图URL|string|Y|-|
|allCount|返回所有文章分类数量|int|Y|-|


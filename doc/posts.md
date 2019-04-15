

## 接口名称

### 1) 请求地址

>https://blog.imyzc.com/api/v1/posts

### 2) 调用方式：HTTP get

### 3) 接口描述：

* 通用文章简略信息获取接口

### 4) 请求参数:

#### GET参数:
|字段名称       |字段说明         |类型            |必填            |备注     |
| -------------|:--------------:|:--------------:|:--------------:| ------:|
|maxCount|限制获取数据最大条数|int|N|-|
|sortBy|指定返回数据排序方式|string|N|可选值：浏览量 view、时间 date、文章名称 name |
|onlyTag|指定筛选目标标签文章|string|N|标签ID，可提交多个，每个用 - 分隔，例如 2、2-9-6|
|byDate|指定筛选目标日期文章|string|N|例如 2019-02|
|byClass|指定筛选目标分类文章|string|N|分类的 ID 或者 URL名字|
|byUser|指定筛选目标用户文章|int|N|用户ID|
|byStatus|指定筛选目标状态文章|string|N|可选值：公开 public、草稿 draft、私有 private |

### 5) 请求返回结果:

```
{
    "success": true,
    "code": "200",
    "message": "成功",
    "data": [
        {
            "id": 36,
            "urlName": "own-web",
            "postDate": "2018-12-14 13:41:48",
            "tags": "-3-2-12-",
            "title": "如何搭建一个属于自己的网站",
            "previewText": "教你如何搭建一个属于自己的网站，装逼专用！",
            "previewImage": "e3eb48776bdab2536c7a023fdfca3922",
            "status": 1,
            "postPrefix": 1,
            "viewCount": 37,
            "commentCount": 0,
            "authorId": 1
        },
        ...
    ]
}
```

### 6) 请求返回结果参数说明:
|字段名称       |字段说明         |类型            |必填            |备注     |
| -------------|:--------------:|:--------------:|:--------------:| ------:|
|id|文章ID|string|Y|-|
|urlName|文章URL名字|string|Y|-|
|postDate|文章发表时间|string|Y|-|
|postClass|文章分类|string|N|格式："分类URL名字:分类标题"|
|tags|文章标签|string|N|格式："-3-2-12-"，数字为标签 ID |
|title|文章标题|string|Y|-|
|previewText|文章说明文字|string|N|-|
|previewImage|文章预览小图URL|string|N|-|
|status|文章状态|string|Y|-|
|postPrefix|文章前缀|string|Y|-|
|viewCount|文章查看数|int|Y|-|
|commentCount|文章评论数|int|Y|-|
|authorId|作者ID|int|Y|-|
|author|作者名字|string|N|-|


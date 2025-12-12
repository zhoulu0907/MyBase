# 附件 API 示例

## 上传附件（Multipart）

- 路径：`POST /runtime/metadata/{tableName}/attachment/upload`
- Content-Type：`multipart/form-data`

示例（HTTP）：

```http
POST /runtime/metadata/customer/attachment/upload HTTP/1.1
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="file"; filename="contract.pdf"
Content-Type: application/pdf

<binary content>
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="name"

contract.pdf
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="directory"

contracts
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="type"

application/pdf
------WebKitFormBoundary7MA4YWxkTrZu0gW--
```

示例（curl）：

```bash
curl -X POST \
  -F "file=@contract.pdf" \
  -F "name=contract.pdf" \
  -F "directory=contracts" \
  -F "type=application/pdf" \
  http://localhost:8080/runtime/metadata/customer/attachment/upload
```

成功返回：`CommonResult<String>` 文件路径/标识

## 下载附件（权限校验）

- 路径：`GET /runtime/metadata/{tableName}/attachment/download`
- 参数：`menuId`、`id`、`fieldName`、`fileId`

示例：

```http
GET /runtime/metadata/customer/attachment/download?menuId=47012574606491648&id=167556724686127104&fieldName=contract_files&fileId=123456
X-Trace-Id: abc-xyz-001
```

- 校验逻辑：
  - 加载权限上下文并校验页面/数据权限
  - 在详情数据的主表或任意连接器中匹配 `fieldName` 且类型为附件（FILE/IMAGE）
  - 解析附件 ID 列表，包含 `fileId` 则允许下载

失败场景：

- 数据不存在或不可见：`DATA_NOT_EXISTS`
- 字段不为附件类型或无匹配附件 ID：`DATA_NOT_EXISTS`

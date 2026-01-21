# OneBase OCR Plugin

## 简介
`onebase-plugin-ocr` 是 OneBase 平台的 OCR 识别插件,基于百度 AI 开放平台实现。
插件提供身份证、港澳台通行证及护照的图片文字识别能力。

## 功能特性
- **身份证识别**：支持身份证正反面识别。
- **港澳台通行证识别**：支持多种类型的通行证识别（如港澳通行证、台湾通行证等）。
- **护照识别**：支持护照信息页识别。
- **配置化**：支持通过平台动态配置百度 API Key 和 Secret。

## 接口说明

所有接口的基础路径为 `/plugin/onebase-plugin-ocr`。

### 1. 身份证识别

**接口地址**：`POST /id-card`

**请求参数**：
| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| frontFile | File | 否 | 身份证正面图片文件 |
| backFile | File | 否 | 身份证反面图片文件 |

**说明**：`frontFile` 和 `backFile` 至少需要传入一个。

**响应示例**：
```json
{
    "code": 0,
    "msg": "success",
    "data": {
        "front": { ...百度API返回结果... },
        "back": { ...百度API返回结果... }
    }
}
```

### 2. 港澳台通行证识别

**接口地址**：`POST /exitentrypermit`

**请求参数**：
| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| frontFile | File | 否 | 正面图片文件 |
| backFile | File | 否 | 反面图片文件 |
| exitentrypermitType | String | 是 | 通行证类型 |

**支持的通行证类型 (`exitentrypermitType`)**：
- `hk_mc_passport`: 港澳通行证 (Front/Back)
- `tw_passport`: 台湾通行证 (Front/Back)
- `tw_return_passport`: 台湾居民来往大陆通行证 (台胞证) (Front/Back)
- `hk_mc_return_passport`: 港澳居民来往内地通行证 (回乡证) (Front/Back)

**响应示例**：
```json
{
    "code": 0,
    "msg": "success",
    "data": {
        "front": { ... },
        "back": { ... }
    }
}
```

### 3. 护照识别

**接口地址**：`POST /passport`

**请求参数**：
| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| file | File | 是 | 护照图片文件 |

**响应示例**：
```json
{
    "code": 0,
    "msg": "success",
    "data": { ... }
}
```

## 配置说明

插件需要在宿主环境中配置以下参数(通过 `PluginContext`):

| 配置键 | 说明 | 是否必填 | 默认值 |
| :--- | :--- | :--- | :--- |
| `client-id` | OCR 服务商 Client ID<br/>百度: API Key<br/>阿里: Access Key ID<br/>腾讯: Secret ID | 是 | 无 |
| `client-secret` | OCR 服务商 Client Secret<br/>百度: Secret Key<br/>阿里: Access Key Secret<br/>腾讯: Secret Key | 是 | 无 |
| `endpoint` | OCR 服务商 API 接入点<br/>百度: `https://aip.baidubce.com`<br/>阿里: `https://ocr.cn-shanghai.aliyuncs.com`<br/>腾讯: `https://ocr.tencentcloudapi.com` | 否 | `https://aip.baidubce.com` |

> **说明**: 配置键采用通用命名,支持接入百度/阿里/腾讯等多种 OCR 服务商。当前实现基于百度 OCR API,如需接入其他服务商,需扩展 `BaiduOcrService` 或创建对应的服务类。

### 开发环境配置

在 `onebase-plugin-host-simulator` 中,编辑 `src/main/resources/plugin-context.yml`:

```yaml
plugins:
  onebase-plugin-ocr:
    client-id: "your-api-key"           # 服务商 API Key
    client-secret: "your-secret-key"    # 服务商 Secret Key
    endpoint: "https://aip.baidubce.com"  # API 接入点 (可选)
```

### 生产环境配置

在 `onebase-server-runtime` 中,配置存储在数据库表 `plugin_config_info`:

```sql
-- 插入配置 (详见 sql/plugin_config_insert.sql)
INSERT INTO plugin_config_info (plugin_id, config_key, config_value, description, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES 
  ('onebase-plugin-ocr', 'client-id', 'your-api-key', 'OCR 服务商 Client ID', 'admin', NOW(), 'admin', NOW(), 0, 1),
  ('onebase-plugin-ocr', 'client-secret', 'your-secret-key', 'OCR 服务商 Client Secret', 'admin', NOW(), 'admin', NOW(), 0, 1),
  ('onebase-plugin-ocr', 'endpoint', 'https://aip.baidubce.com', 'OCR 服务商 API 接入点', 'admin', NOW(), 'admin', NOW(), 0, 1);
```

**注意**: 
- 请替换 `your-api-key` 和 `your-secret-key` 为真实的服务商密钥
- 百度 OCR 密钥获取: [百度AI开放平台](https://ai.baidu.com/tech/ocr)
- 阿里云 OCR: [阿里云OCR](https://www.aliyun.com/product/ocr)
- 腾讯云 OCR: [腾讯云OCR](https://cloud.tencent.com/product/ocr)
- 支持多租户配置,为不同 `tenant_id` 插入独立配置记录

## 依赖说明
本插件依赖以下第三方库（已打包在插件中）：
- Kong Unirest Java (HTTP Client)
- Google Guava (Cache)
- Apache Commons Lang3
- Apache Commons Collections4

## 开发指南
- 源码位置：`onebase-plugin/plugin-ocr`
- 构建命令：`mvn clean install`

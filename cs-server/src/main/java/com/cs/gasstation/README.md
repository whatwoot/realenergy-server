# GasStation Service 使用指南

## 概述

GasStation Service 是一个集成 GasStation API 的 Java Spring Boot 服务，提供查询账户余额和购买资源创建订单的功能。

## 核心功能

### 1. 查询账户余额

```java
BalanceResponse response = gasStationService.queryBalance();
// 返回：币种、余额、充值地址
```

### 2. 购买资源创建订单

支持两种购买方式：

#### 方式一：客户指定数量（buy_type=0）

```java
// 方法一：使用便捷方法
CreateOrderResponse response = gasStationService.createOrderByAmount(
    "request-id-001",                    // 业务方ID
    "TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN",  // 资源接收地址
    "30001",                             // 租赁周期code: 10010(10分钟), 20001(1小时), 30001(1天)
    64400,                               // 购买能量数量(最小值64000，可选)
    null                                 // 购买带宽数量(最小值5000，可选)
);
```

#### 方式二：系统预估（buy_type=1）

注意：系统预估只能购买能量，不支持购买带宽

```java
CreateOrderResponse response = gasStationService.createOrderByEstimate(
    "request-id-002",                    // 业务方ID
    "TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN",  // 资源接收地址
    "30001",                             // 租赏周期code
    "TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN",  // 转账到账地址(用于预估矿工费)
    "TLBz527x3WqYYEYUwNgsfqeWHSDC1tHvZd"   // 合约地址(用于预估矿工费)
);
```

#### 方式三：完整自定义参数

```java
CreateOrderRequest request = new CreateOrderRequest();
request.setRequestId("request-id-003");
request.setReceiveAddress("TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN");
request.setBuyType(0);
request.setServiceChargeType("30001");
request.setEnergyNum(100000);
request.setNetNum(null);

CreateOrderResponse response = gasStationService.createOrder(request);
```

## 配置说明

### 1. 添加依赖

在 `pom.xml` 中已自动添加：

```xml
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-java</artifactId>
    <version>3.14.5</version>
</dependency>
```

### 2. 配置应用

在 `application.yml` 中配置 GasStation 参数：

```yaml
gasstation:
  domain: https://openapi.gasstation.ai/  # API域名
  app_id: your_app_id                     # 替换为你的App ID
  secret: your_secret_key                 # 替换为你的Secret Key
```

或通过环境变量设置：

```bash
export GASSTATION_DOMAIN=https://openapi.gasstation.ai/
export GASSTATION_APP_ID=your_app_id
export GASSTATION_SECRET=your_secret_key
```

### 3. 获取 App ID 和 Secret

1. 访问 GasStation 官方网站
2. 创建 API Key
3. 复制并设置 `app_id` 和 `secret`

## API 端点

### REST API 示例

如果需要通过 HTTP 调用：

#### 查询余额

```bash
curl -X GET http://localhost:8080/api/gasstation/balance
```

响应示例：
```json
{
  "symbol": "trx",
  "balance": "92.4792050000000000",
  "depositAddress": "TFEWqCSUFs6tthfyLWFjpUepSqWPWKDZQx"
}
```

#### 创建订单

```bash
curl -X POST http://localhost:8080/api/gasstation/order \
  -H "Content-Type: application/json" \
  -d '{
    "request_id": "request-001",
    "receive_address": "TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN",
    "buy_type": 0,
    "service_charge_type": "30001",
    "energy_num": 64400
  }'
```

响应示例：
```json
{
  "tradeNo": "20240209001234567890"
}
```

## 技术细节

### AES 加密

- 运算模式：ECB
- 填充模式：PKCS5
- 输出格式：Base64 UrlSafe

所有请求数据都通过 AES 加密处理，Service 内部自动处理加密/解密。

### 错误处理

Service 方法会抛出 `Exception`，你可以在调用时使用 try-catch 处理：

```java
try {
    BalanceResponse response = gasStationService.queryBalance();
} catch (Exception e) {
    log.error("查询余额失败", e);
    // 处理错误
}
```

### 日志

所有操作都有详细的日志记录，配置日志级别为 DEBUG 可以查看请求/响应详情：

```yaml
logging:
  level:
    com.cs.gasstation: DEBUG
```

## 最小购买限制

- **能量**：最小购买数量 64,400
- **带宽**：最小购买数量 5,000

## 租赁周期代码

| Code  | 时长   |
|-------|--------|
| 10010 | 10分钟 |
| 20001 | 1小时  |
| 30001 | 1天    |

## 常见问题

### 1. 加密失败

检查 `secret` 是否正确配置。

### 2. 响应码非 0

参考 GasStation 官方文档获取错误码的含义。

### 3. 连接超时

检查网络连接和 API 域名是否可访问。

## 示例代码

完整的测试示例见 `GasStationServiceTest.java`。

## 依赖关系

- Spring Boot 2.7.18+
- Unirest 3.14.5
- Jackson (for JSON processing)
- Lombok (for code generation)

## 相关文件

```
com.cs.gasstation/
├── config/
│   └── GasStationConfig.java          # 配置类
├── controller/
│   └── GasStationController.java      # REST API 控制器
├── dto/
│   ├── BalanceRequest.java            # 余额查询请求
│   ├── BalanceResponse.java           # 余额查询响应
│   ├── CreateOrderRequest.java        # 创建订单请求
│   ├── CreateOrderResponse.java       # 创建订单响应
│   └── ApiResponse.java               # 通用API响应
├── service/
│   └── GasStationService.java         # 核心业务逻辑
└── util/
    └── AesUtil.java                   # AES加密工具
```

## 许可证

根据项目整体许可证。

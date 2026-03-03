package com.cs.gasstation.service;

import com.cs.gasstation.dto.ApiResponse;
import com.cs.gasstation.dto.BalanceRequest;
import com.cs.gasstation.dto.BalanceResponse;
import com.cs.gasstation.dto.CreateOrderRequest;
import com.cs.gasstation.dto.CreateOrderResponse;
import com.cs.gasstation.dto.QueryRecordRequest;
import com.cs.gasstation.dto.QueryRecordResponse;
import com.cs.gasstation.util.AesUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * GasStation服务类
 * 提供查询余额和购买资源创建订单的功能
 */
@Slf4j
@Service
public class GasStationService {

    @Value("${gasstation.domain:https://openapi.gasstation.ai/}")
    private String domain;

    @Value("${gasstation.app_id}")
    private String appId;

    @Value("${gasstation.secret}")
    private String secret;

    private final ObjectMapper objectMapper;

    public GasStationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 查询账户余额
     *
     * @return 余额信息
     * @throws Exception 请求异常
     */
    public BalanceResponse queryBalance() throws Exception {
        log.info("开始查询GasStation余额");

        // 构建请求参数
        BalanceRequest request = new BalanceRequest();
        request.setTime(System.currentTimeMillis() / 1000);

        // AES加密
        String requestData = objectMapper.writeValueAsString(request);
        log.debug("请求数据: {}", requestData);

        String encryptedData = AesUtil.encrypt(requestData, secret);
        log.debug("加密数据: {}", encryptedData);

        // 构建请求URL
        String url = domain + "api/tron/gas/balance?app_id=" + appId + "&data=" + encryptedData;

        try {
            // 发送GET请求
            HttpResponse<String> response = Unirest.get(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("accept", "application/json;charset=utf-8")
                    .asString();

            log.debug("响应状态码: {}", response.getStatus());
            log.debug("响应内容: {}", response.getBody());

            // 解析响应
            ApiResponse<BalanceResponse> apiResponse = objectMapper.readValue(
                    response.getBody(),
                    objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, BalanceResponse.class)
            );

            // 检查响应状态
            if ("0".equals(apiResponse.getCode())) {
                log.info("查询余额成功: {}", apiResponse.getData());
                return apiResponse.getData();
            } else {
                log.error("查询余额失败, 错误码: {}, 错误信息: {}", apiResponse.getCode(), apiResponse.getMsg());
                throw new Exception("查询余额失败: " + apiResponse.getMsg());
            }
        } catch (Exception e) {
            log.error("查询余额异常", e);
            throw e;
        }
    }

    /**
     * 购买资源创建订单
     *
     * @param orderRequest 订单请求参数
     * @return 订单响应信息
     * @throws Exception 请求异常
     */
    public CreateOrderResponse createOrder(CreateOrderRequest orderRequest) throws Exception {
        log.info("开始创建购买资源订单, 请求ID: {}", orderRequest.getRequestId());

        // AES加密
        String requestData = objectMapper.writeValueAsString(orderRequest);
        log.debug("请求数据: {}", requestData);

        String encryptedData = AesUtil.encrypt(requestData, secret);
        log.debug("加密数据: {}", encryptedData);

        // 构建请求URL
        String url = domain + "api/tron/gas/create_order?app_id=" + appId + "&data=" + encryptedData;

        try {
            // 发送POST请求
            HttpResponse<String> response = Unirest.post(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .asString();

            log.debug("响应状态码: {}", response.getStatus());
            log.debug("响应内容: {}", response.getBody());

            // 解析响应
            ApiResponse<CreateOrderResponse> apiResponse = objectMapper.readValue(
                    response.getBody(),
                    objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, CreateOrderResponse.class)
            );

            // 检查响应状态
            if ("0".equals(apiResponse.getCode())) {
                log.info("创建订单成功: 订单ID={}", apiResponse.getData().getTradeNo());
                return apiResponse.getData();
            } else {
                log.error("创建订单失败, 错误码: {}, 错误信息: {}", apiResponse.getCode(), apiResponse.getMsg());
                throw new Exception("创建订单失败: " + apiResponse.getMsg());
            }
        } catch (Exception e) {
            log.error("创建订单异常", e);
            throw e;
        }
    }

    /**
     * 创建客户指定数量的购买订单 (buy_type=0)
     *
     * @param requestId      业务方ID
     * @param receiveAddress 资源接收地址
     * @param serviceChargeType 租赁周期code
     * @param energyNum      购买能量数量(可选)
     * @param netNum         购买带宽数量(可选)
     * @return 订单响应
     * @throws Exception 请求异常
     */
    public CreateOrderResponse createOrderByAmount(String requestId, String receiveAddress,
                                                    String serviceChargeType, Integer energyNum, Integer netNum) throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setRequestId(requestId);
        request.setReceiveAddress(receiveAddress);
        request.setBuyType(0);
        request.setServiceChargeType(serviceChargeType);
        request.setEnergyNum(energyNum);
        request.setNetNum(netNum);

        return createOrder(request);
    }

    /**
     * 创建系统预估的购买订单 (buy_type=1)
     * 注意：系统预估只能购买能量，不支持购买带宽
     *
     * @param requestId      业务方ID
     * @param receiveAddress 资源接收地址
     * @param serviceChargeType 租赁周期code
     * @param addressTo      转账到账地址
     * @param contractAddress 合约地址
     * @return 订单响应
     * @throws Exception 请求异常
     */
    public CreateOrderResponse createOrderByEstimate(String requestId, String receiveAddress,
                                                      String serviceChargeType, String addressTo, String contractAddress) throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setRequestId(requestId);
        request.setReceiveAddress(receiveAddress);
        request.setBuyType(1);
        request.setServiceChargeType(serviceChargeType);
        request.setAddressTo(addressTo);
        request.setContractAddress(contractAddress);

        return createOrder(request);
    }

    /**
     * 查询订单状态
     * 通过request_id查询特定的代理记录
     *
     * @param requestIds 多个request_id的字符串，英文逗号分割，例:123,345
     * @return 订单记录列表
     * @throws Exception 请求异常
     */
    public List<QueryRecordResponse> queryOrderStatus(String requestIds) throws Exception {
        log.info("开始查询订单状态, request_ids: {}", requestIds);

        // 构建请求参数
        QueryRecordRequest request = new QueryRecordRequest();
        request.setRequestIds(requestIds);

        // AES加密
        String requestData = objectMapper.writeValueAsString(request);
        log.debug("请求数据: {}", requestData);

        String encryptedData = AesUtil.encrypt(requestData, secret);
        log.debug("加密数据: {}", encryptedData);

        // 构建请求URL
        String url = domain + "api/tron/gas/record/list?app_id=" + appId + "&data=" + encryptedData;

        try {
            // 发送GET请求
            HttpResponse<String> response = Unirest.get(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("accept", "application/json;charset=utf-8")
                    .asString();

            log.debug("响应状态码: {}", response.getStatus());
            log.debug("响应内容: {}", response.getBody());

            // 解析响应
            ApiResponse<List<QueryRecordResponse>> apiResponse = objectMapper.readValue(
                    response.getBody(),
                    objectMapper.getTypeFactory().constructParametricType(ApiResponse.class,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, QueryRecordResponse.class))
            );

            // 检查响应状态
            if ("0".equals(apiResponse.getCode())) {
                log.info("查询订单状态成功, 结果数量: {}", apiResponse.getData().size());
                return apiResponse.getData();
            } else {
                log.error("查询订单状态失败, 错误码: {}, 错误信息: {}", apiResponse.getCode(), apiResponse.getMsg());
                throw new Exception("查询订单状态失败: " + apiResponse.getMsg());
            }
        } catch (Exception e) {
            log.error("查询订单状态异常", e);
            throw e;
        }
    }

    /**
     * 查询单个订单状态
     *
     * @param requestId 业务方ID
     * @return 订单记录，如果不存在则返回null
     * @throws Exception 请求异常
     */
    public QueryRecordResponse querySingleOrderStatus(String requestId) throws Exception {
        List<QueryRecordResponse> records = queryOrderStatus(requestId);
        if (records != null && !records.isEmpty()) {
            return records.get(0);
        }
        return null;
    }
}

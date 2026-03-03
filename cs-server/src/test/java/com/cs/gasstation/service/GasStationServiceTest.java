package com.cs.gasstation.service;

import com.cs.gasstation.dto.BalanceResponse;
import com.cs.gasstation.dto.CreateOrderRequest;
import com.cs.gasstation.dto.CreateOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * GasStation Service 测试类
 */
@Slf4j
@SpringBootTest
public class GasStationServiceTest {

    @Autowired
    private GasStationService gasStationService;

    /**
     * 测试查询余额
     */
    @Test
    public void testQueryBalance() throws Exception {
        BalanceResponse response = gasStationService.queryBalance();
        log.info("查询余额成功: {}", response);
        assert response != null;
        assert response.getBalance() != null;
    }

    /**
     * 测试创建客户指定数量订单
     */
    @Test
    public void testCreateOrderByAmount() throws Exception {
        CreateOrderResponse response = gasStationService.createOrderByAmount(
                "test-request-001",  // requestId
                "TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN",  // receiveAddress
                "30001",  // serviceChargeType 1天
                64400,  // energyNum 最小值
                null  // netNum
        );
        log.info("创建订单成功: {}", response);
        assert response != null;
        assert response.getTradeNo() != null;
    }

    /**
     * 测试创建系统预估订单
     */
    @Test
    public void testCreateOrderByEstimate() throws Exception {
        CreateOrderResponse response = gasStationService.createOrderByEstimate(
                "test-request-002",  // requestId
                "TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN",  // receiveAddress
                "30001",  // serviceChargeType 1天
                "TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN",  // addressTo
                "TLBz527x3WqYYEYUwNgsfqeWHSDC1tHvZd"  // contractAddress
        );
        log.info("创建订单成功: {}", response);
        assert response != null;
        assert response.getTradeNo() != null;
    }

    /**
     * 测试创建自定义参数订单
     */
    @Test
    public void testCreateCustomOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setRequestId("test-request-003");
        request.setReceiveAddress("TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN");
        request.setBuyType(0);
        request.setServiceChargeType("20001");  // 1小时
        request.setEnergyNum(100000);  // 购买更多能量

        CreateOrderResponse response = gasStationService.createOrder(request);
        log.info("创建订单成功: {}", response);
        assert response != null;
        assert response.getTradeNo() != null;
    }
}

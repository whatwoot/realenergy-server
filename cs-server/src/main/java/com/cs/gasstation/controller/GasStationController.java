package com.cs.gasstation.controller;

import com.cs.gasstation.dto.BalanceResponse;
import com.cs.gasstation.dto.CreateOrderRequest;
import com.cs.gasstation.dto.CreateOrderResponse;
import com.cs.gasstation.service.GasStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * GasStation API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/gasstation")
public class GasStationController {

    @Autowired
    private GasStationService gasStationService;

    /**
     * 查询账户余额
     *
     * @return 余额信息
     */
    @GetMapping("/balance")
    public BalanceResponse queryBalance() throws Exception {
        return gasStationService.queryBalance();
    }

    /**
     * 创建购买资源订单
     *
     * @param orderRequest 订单请求参数
     * @return 订单信息
     */
    @PostMapping("/order")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest orderRequest) throws Exception {
        return gasStationService.createOrder(orderRequest);
    }

    /**
     * 客户指定数量购买资源订单
     *
     * @param requestId      业务方ID
     * @param receiveAddress 资源接收地址
     * @param serviceChargeType 租赁周期code
     * @param energyNum      购买能量数量(可选)
     * @param netNum         购买带宽数量(可选)
     * @return 订单信息
     */
    @PostMapping("/order/amount")
    public CreateOrderResponse createOrderByAmount(
            @RequestParam String requestId,
            @RequestParam String receiveAddress,
            @RequestParam String serviceChargeType,
            @RequestParam(required = false) Integer energyNum,
            @RequestParam(required = false) Integer netNum) throws Exception {
        return gasStationService.createOrderByAmount(requestId, receiveAddress, serviceChargeType, energyNum, netNum);
    }

    /**
     * 系统预估购买资源订单
     *
     * @param requestId      业务方ID
     * @param receiveAddress 资源接收地址
     * @param serviceChargeType 租赁周期code
     * @param addressTo      转账到账地址
     * @param contractAddress 合约地址
     * @return 订单信息
     */
    @PostMapping("/order/estimate")
    public CreateOrderResponse createOrderByEstimate(
            @RequestParam String requestId,
            @RequestParam String receiveAddress,
            @RequestParam String serviceChargeType,
            @RequestParam String addressTo,
            @RequestParam String contractAddress) throws Exception {
        return gasStationService.createOrderByEstimate(requestId, receiveAddress, serviceChargeType, addressTo, contractAddress);
    }
}

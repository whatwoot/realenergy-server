package com.cs.copy.system.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author fiona
 * @date 2024/11/23 01:13
 */
@Data
@Schema(description = "充值地址列表")
public class RechargeAddrAddRequest extends BaseRequest {
    private List<String> addrs;
}

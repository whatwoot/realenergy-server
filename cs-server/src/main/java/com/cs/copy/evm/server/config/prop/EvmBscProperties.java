package com.cs.copy.evm.server.config.prop;

import com.cs.sp.common.base.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author fiona
 * @date 2024/5/29 20:11
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvmBscProperties extends BaseVO {
    private Long chainId;
    private String rpcUrls;
}

package com.cs.energy.evm.server.config.prop;

import com.cs.sp.common.base.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fiona
 * @date 2024/5/29 20:11
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("evm.config")
public class EvmProperties extends BaseVO {
    private String keystorePath;
    private String keyPwd;
}

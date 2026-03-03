package com.cs.energy.asset.api.dto;

import com.cs.sp.common.base.BaseDTO;
import lombok.Data;

/**
 * @author fiona
 * @date 2025/2/18 21:22
 */
@Data
public class TxV3SimpleDTO extends BaseDTO {
    @Data
    public static class Transaction {
        private Long now;
        private Long mcBlockSeqno;

        public Long getMcBlockSeqno() {
            return mcBlockSeqno;
        }

        public void setMcBlockSeqno(Long mcBlockSeqno) {
            this.mcBlockSeqno = mcBlockSeqno;
        }
    }
}

package com.cs.energy.evm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author fiona
 * @date 2024/5/27 20:45
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScanDTO implements Serializable {
    private Long start;
    private Long end;
}

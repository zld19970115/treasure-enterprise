package io.treasure.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * sharing_and_distribution_params
 * @author 
 */
@Data
public class SharingAndDistributionParams implements Serializable {
    private Integer id;

    private Integer helpedTimes;

    /**
     * 月数
     */
    private Integer months;

    private static final long serialVersionUID = 1L;
}
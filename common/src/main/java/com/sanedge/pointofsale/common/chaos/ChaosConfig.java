package com.sanedge.pointofsale.common.chaos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Root of the {@code chaos.yaml} configuration.
 */
@Data
public class ChaosConfig {

    private String warmupDuration = "0s";
    private boolean enableDefaultIgnored = true;
    private List<ChaosPolicy> policies = new ArrayList<>();
}

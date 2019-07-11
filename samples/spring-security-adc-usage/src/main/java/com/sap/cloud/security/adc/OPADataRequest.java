package com.sap.cloud.security.adc;

import java.util.Map;

/**
 * TODO: extract as library
 */
public class OPADataRequest {

    private Map<String, Object> input;

    public OPADataRequest(Map<String, Object> input) {
        this.input = input;
    }

    public Map<String, Object> getInput() {
        return this.input;
    }

}
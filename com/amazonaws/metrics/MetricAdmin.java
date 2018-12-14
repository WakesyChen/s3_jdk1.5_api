/*
 * Copyright 2011-2018 Amazon Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.amazonaws.regions.Regions;

/**
 *  Administration of AwsSdkMetrics as an MBean.
 */
public class MetricAdmin implements MetricAdminMBean {
    
    public boolean enableDefaultMetrics() {
        return AwsSdkMetrics.enableDefaultMetrics();
    }
    
    public void disableMetrics() {
        AwsSdkMetrics.disableMetrics();
    }
    
    public String getRequestMetricCollector() {
        MetricCollector mc = AwsSdkMetrics.getInternalMetricCollector();
        RequestMetricCollector rmc = mc == null ? null : mc.getRequestMetricCollector();
        return mc == null || rmc == RequestMetricCollector.NONE
             ? "NONE"
             : rmc.getClass().getName()
             ;
    }
    
    public String getServiceMetricCollector() {
        MetricCollector mc = AwsSdkMetrics.getInternalMetricCollector();
        ServiceMetricCollector smc = mc == null ? null : mc.getServiceMetricCollector();
        return mc == null || smc == ServiceMetricCollector.NONE
             ? "NONE"
             : smc.getClass().getName()
             ;
    }
    
    public boolean isMetricsEnabled() {
        return AwsSdkMetrics.isMetricsEnabled();
    }

    
    public boolean isMachineMetricsExcluded() {
        return AwsSdkMetrics.isMachineMetricExcluded();
    }
    
    public void setMachineMetricsExcluded(boolean excludeJvmMetrics) {
        AwsSdkMetrics.setMachineMetricsExcluded(excludeJvmMetrics);
    }
    
    public String getRegion() {
        return AwsSdkMetrics.getRegionName();
    }
    
    public void setRegion(String region) {
        AwsSdkMetrics.setRegion(region);
    }
    
    public Integer getMetricQueueSize() {
        return AwsSdkMetrics.getMetricQueueSize();
    }
    
    public void setMetricQueueSize(Integer metricQueueSize) {
        AwsSdkMetrics.setMetricQueueSize(metricQueueSize);

    }
    
    public Integer getQueuePollTimeoutMilli() {
        Long queuePollTimeoutMilli = AwsSdkMetrics.getQueuePollTimeoutMilli();
        return queuePollTimeoutMilli == null ? null : queuePollTimeoutMilli.intValue();
    }
    
    public void setQueuePollTimeoutMilli(Integer timeoutMilli) {
        AwsSdkMetrics.setQueuePollTimeoutMilli(timeoutMilli == null ? null : timeoutMilli.longValue());
    }
    
    public String getMetricNameSpace() {
        return AwsSdkMetrics.getMetricNameSpace();
    }
    
    public void setMetricNameSpace(String metricNameSpace) {
        AwsSdkMetrics.setMetricNameSpace(metricNameSpace);
    }

    
    public boolean isPerHostMetricsIncluded() {
        return AwsSdkMetrics.isPerHostMetricIncluded();
    }
    
    public void setPerHostMetricsIncluded(boolean includePerHostMetrics) {
        AwsSdkMetrics.setPerHostMetricsIncluded(includePerHostMetrics);
    }
    
    public String getJvmMetricName() {
        return AwsSdkMetrics.getJvmMetricName();
    }
    
    public void setJvmMetricName(String jvmMetricName) {
        AwsSdkMetrics.setJvmMetricName(jvmMetricName);
    }
    
    public String getHostMetricName() {
        return AwsSdkMetrics.getHostMetricName();
    }
    
    public void setHostMetricName(String hostMetricName) {
        AwsSdkMetrics.setHostMetricName(hostMetricName);
    }
    
    public String getCredentialFile() {
        return AwsSdkMetrics.getCredentialFile();
    }

    
    public void setCredentialFile(String filepath)
            throws FileNotFoundException, IOException {
        AwsSdkMetrics.setCredentialFile(filepath);
    }
    
    public boolean isSingleMetricNamespace() {
        return AwsSdkMetrics.isSingleMetricNamespace();
    }
    
    public void setSingleMetricNamespace(boolean singleMetricNamespace) {
        AwsSdkMetrics.setSingleMetricNamespace(singleMetricNamespace);
    }
}

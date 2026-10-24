package com.assignment.billing;

import java.math.BigDecimal;

public class ServicePlan {

    private String serviceId;
    private String serviceName;
    private BigDecimal firstHourPrice;
    private BigDecimal additionalHourPrice;

    public ServicePlan(
            String serviceId,
            String serviceName,
            BigDecimal firstHourPrice,
            BigDecimal additionalHourPrice) {

        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.firstHourPrice = firstHourPrice;
        this.additionalHourPrice = additionalHourPrice;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public BigDecimal getFirstHourPrice() {
        return firstHourPrice;
    }

    public BigDecimal getAdditionalHourPrice() {
        return additionalHourPrice;
    }

    public void displayService() {
        System.out.println("Service ID: " + serviceId);
        System.out.println("Service Name: " + serviceName);
        System.out.println("First Hour Price: Rs. " + firstHourPrice);
        System.out.println("Additional Hour Price: Rs. "
                + additionalHourPrice);
    }
}
package com.assignment.billing;

import java.util.LinkedHashMap;
import java.util.Map;

public class Resource {

    private String resourceId;
    private String resourceName;
    private int capacity;

    private Map<String, ServicePlan> services = new LinkedHashMap<>();

    public Resource(String resourceId, String resourceName, int capacity) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.capacity = capacity;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public int getCapacity() {
        return capacity;
    }

    public Map<String, ServicePlan> getServices() {
        return services;
    }

    // Add a service to this resource.
    public void addService(ServicePlan service) {
        services.put(service.getServiceId(), service);
    }

    // Find a service by its ID.
    public ServicePlan getService(String serviceId) {
        return services.get(serviceId);
    }

    public void displayResource() {
        System.out.println("Resource ID: " + resourceId);
        System.out.println("Resource Name: " + resourceName);
        System.out.println("Capacity: " + capacity);

        System.out.println("Available Services:");

        for (ServicePlan service : services.values()) {
            service.displayService();
            System.out.println("-------------------------");
        }
    }
}
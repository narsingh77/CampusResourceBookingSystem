package com.vityarthi.booking.service;

import com.vityarthi.booking.model.CampusResource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages catalog of facilities, venues, and specialized labs.
 */
public class ResourceService {
    private final Map<String, CampusResource> resourceCatalog = new ConcurrentHashMap<>();

    public void addResource(CampusResource resource) {
        resourceCatalog.put(resource.getResourceId().toUpperCase(), resource);
    }

    public Optional<CampusResource> getResource(String resourceId) {
        if (resourceId == null) return Optional.empty();
        return Optional.ofNullable(resourceCatalog.get(resourceId.toUpperCase()));
    }

    public Collection<CampusResource> getAllResources() {
        return resourceCatalog.values();
    }

    public List<CampusResource> findResourcesByCapacity(int minCapacity) {
        return resourceCatalog.values().stream()
                .filter(r -> r.isOperational() && r.getCapacity() >= minCapacity)
                .collect(Collectors.toList());
    }

    public List<CampusResource> findResourcesByType(String type) {
        return resourceCatalog.values().stream()
                .filter(r -> r.isOperational() && r.getResourceType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }
}

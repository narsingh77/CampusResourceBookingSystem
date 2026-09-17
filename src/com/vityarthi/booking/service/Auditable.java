package com.vityarthi.booking.service;

import java.util.List;

/**
 * Interface for system logging and audit trails.
 */
public interface Auditable {
    void logAction(String action, String performedBy, String details);
    List<String> getAuditLogs();
}

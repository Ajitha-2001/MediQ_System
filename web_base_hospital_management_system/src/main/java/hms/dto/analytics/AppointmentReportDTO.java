package hms.dto.analytics;

import java.util.Map;

public class AppointmentReportDTO {
    private long total;
    private long approved;
    private long canceled;
    private long pending;
    private Map<String, Double> dailyStats;


    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public long getApproved() { return approved; }
    public void setApproved(long approved) { this.approved = approved; }
    public long getCanceled() { return canceled; }
    public void setCanceled(long canceled) { this.canceled = canceled; }
    public long getPending() { return pending; }
    public void setPending(long pending) { this.pending = pending; }
    public Map<String, Double> getDailyStats() { return dailyStats; }
    public void setDailyStats(Map<String, Double> dailyStats) { this.dailyStats = dailyStats; }
}
package hms.dto.analytics;

import java.util.Map;

public class PatientReportDTO {
    private long totalPatients;
    private long activePatients;
    private long newPatientsThisMonth;
    private Map<String, Long> genderDistribution;
    private Map<String, Long> ageGroups;

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getActivePatients() { return activePatients; }
    public void setActivePatients(long activePatients) { this.activePatients = activePatients; }

    public long getNewPatientsThisMonth() { return newPatientsThisMonth; }
    public void setNewPatientsThisMonth(long newPatientsThisMonth) { this.newPatientsThisMonth = newPatientsThisMonth; }

    public Map<String, Long> getGenderDistribution() { return genderDistribution; }
    public void setGenderDistribution(Map<String, Long> genderDistribution) { this.genderDistribution = genderDistribution; }

    public Map<String, Long> getAgeGroups() { return ageGroups; }
    public void setAgeGroups(Map<String, Long> ageGroups) { this.ageGroups = ageGroups; }
}

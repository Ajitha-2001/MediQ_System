package hms.dto.analytics;

import java.util.List;
import java.util.Map;

public class CustomReportDTO {
    private String title;
    private String period;
    private Map<String, Long> summaryStats;
    private Map<String, Double> calculatedStats;
    private List<String> chartLabels;
    private List<? extends Number> chartData;


    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Map<String, Long> getSummaryStats() { return summaryStats; }
    public void setSummaryStats(Map<String, Long> summaryStats) { this.summaryStats = summaryStats; }
    public Map<String, Double> getCalculatedStats() { return calculatedStats; }
    public void setCalculatedStats(Map<String, Double> calculatedStats) { this.calculatedStats = calculatedStats; }
    public List<String> getChartLabels() { return chartLabels; }
    public void setChartLabels(List<String> chartLabels) { this.chartLabels = chartLabels; }
    public List<? extends Number> getChartData() { return chartData; }
    public void setChartData(List<? extends Number> chartData) { this.chartData = chartData; }
}
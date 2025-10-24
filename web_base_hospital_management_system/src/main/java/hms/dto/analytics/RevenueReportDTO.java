package hms.dto.analytics;

public class RevenueReportDTO {
    private double totalRevenue;
    private double averageInvoiceAmount;
    private double maxInvoice;
    private double minInvoice;


    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public double getAverageInvoiceAmount() { return averageInvoiceAmount; }
    public void setAverageInvoiceAmount(double averageInvoiceAmount) { this.averageInvoiceAmount = averageInvoiceAmount; }
    public double getMaxInvoice() { return maxInvoice; }
    public void setMaxInvoice(double maxInvoice) { this.maxInvoice = maxInvoice; }
    public double getMinInvoice() { return minInvoice; }
    public void setMinInvoice(double minInvoice) { this.minInvoice = minInvoice; }
}
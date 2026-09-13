package com.ecommerce.dto;

public class DashboardResponse {

    // =========================================================
    // BASIC STATISTICS
    // =========================================================

    private long totalUsers;
    private long totalProducts;
    private long totalOrders;

    private double totalRevenue;


    // =========================================================
    // ADDITIONAL STATISTICS
    // =========================================================

    private long customerCount;
    private long lowStockProducts;

    private long todayOrders;
    private double todayRevenue;

    private double monthlyRevenue;


    // =========================================================
    // ORDER STATUS STATISTICS
    // =========================================================

    private long pendingOrders;
    private long confirmedOrders;
    private long processingOrders;
    private long shippedOrders;
    private long deliveredOrders;
    private long cancelledOrders;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public DashboardResponse() {
    }


    // =========================================================
    // FULL CONSTRUCTOR
    // =========================================================

    public DashboardResponse(
            long totalUsers,
            long totalProducts,
            long totalOrders,
            double totalRevenue,
            long customerCount,
            long lowStockProducts,
            long todayOrders,
            double todayRevenue,
            double monthlyRevenue,
            long pendingOrders,
            long confirmedOrders,
            long processingOrders,
            long shippedOrders,
            long deliveredOrders,
            long cancelledOrders) {

        this.totalUsers = totalUsers;
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;

        this.customerCount = customerCount;
        this.lowStockProducts = lowStockProducts;

        this.todayOrders = todayOrders;
        this.todayRevenue = todayRevenue;
        this.monthlyRevenue = monthlyRevenue;

        this.pendingOrders = pendingOrders;
        this.confirmedOrders = confirmedOrders;
        this.processingOrders = processingOrders;
        this.shippedOrders = shippedOrders;
        this.deliveredOrders = deliveredOrders;
        this.cancelledOrders = cancelledOrders;
    }


    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }


    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }


    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }


    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }


    public long getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(long customerCount) {
        this.customerCount = customerCount;
    }


    public long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(long lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }


    public long getTodayOrders() {
        return todayOrders;
    }

    public void setTodayOrders(long todayOrders) {
        this.todayOrders = todayOrders;
    }


    public double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }


    public double getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(double monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }


    public long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }


    public long getConfirmedOrders() {
        return confirmedOrders;
    }

    public void setConfirmedOrders(long confirmedOrders) {
        this.confirmedOrders = confirmedOrders;
    }


    public long getProcessingOrders() {
        return processingOrders;
    }

    public void setProcessingOrders(long processingOrders) {
        this.processingOrders = processingOrders;
    }


    public long getShippedOrders() {
        return shippedOrders;
    }

    public void setShippedOrders(long shippedOrders) {
        this.shippedOrders = shippedOrders;
    }


    public long getDeliveredOrders() {
        return deliveredOrders;
    }

    public void setDeliveredOrders(long deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
    }


    public long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }
}
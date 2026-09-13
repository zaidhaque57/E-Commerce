package com.ecommerce.dto;

public class RevenueResponse {

    private String date;
    private long orders;
    private double revenue;

    public RevenueResponse() {
    }

    public RevenueResponse(
            String date,
            long orders,
            double revenue) {

        this.date = date;
        this.orders = orders;
        this.revenue = revenue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getOrders() {
        return orders;
    }

    public void setOrders(long orders) {
        this.orders = orders;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}
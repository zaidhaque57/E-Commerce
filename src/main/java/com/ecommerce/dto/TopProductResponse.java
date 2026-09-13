package com.ecommerce.dto;

public class TopProductResponse {

    private Long productId;
    private String productName;
    private long quantitySold;
    private double revenue;

    public TopProductResponse() {
    }

    public TopProductResponse(
            Long productId,
            String productName,
            long quantitySold,
            double revenue) {

        this.productId = productId;
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.revenue = revenue;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(long quantitySold) {
        this.quantitySold = quantitySold;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}
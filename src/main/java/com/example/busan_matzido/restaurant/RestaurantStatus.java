package com.example.busan_matzido.restaurant;

public enum RestaurantStatus {
    PENDING("승인 대기"),
    APPROVED("공개"),
    REJECTED("반려");

    private final String label;

    RestaurantStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

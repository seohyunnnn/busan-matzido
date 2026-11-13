package com.example.busan_matzido.restaurant;

public enum Category {
    PORK_SOUP("돼지국밥"),
    MILMYEON("밀면"),
    SEAFOOD("해산물·회"),
    KOREAN("한식"),
    WESTERN("양식"),
    JAPANESE("일식"),
    CHINESE("중식"),
    SNACK("분식·길거리"),
    CAFE("카페·디저트"),
    PUB("술집·포차"),
    ETC("기타");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

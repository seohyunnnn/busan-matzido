package com.example.busan_matzido.restaurant;

/** 부산광역시 15개 구 + 1개 군 */
public enum District {
    GANGSEO("강서구"),
    GEUMJEONG("금정구"),
    GIJANG("기장군"),
    NAM("남구"),
    DONG("동구"),
    DONGNAE("동래구"),
    BUSANJIN("부산진구"),
    BUK("북구"),
    SASANG("사상구"),
    SAHA("사하구"),
    SEO("서구"),
    SUYEONG("수영구"),
    YEONJE("연제구"),
    YEONGDO("영도구"),
    JUNG("중구"),
    HAEUNDAE("해운대구");

    private final String label;

    District(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

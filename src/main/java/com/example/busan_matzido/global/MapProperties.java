package com.example.busan_matzido.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 템플릿에서 ${@mapProperties.enabled} / ${@mapProperties.key} 로 사용 */
@Component("mapProperties")
public class MapProperties {

    private final String key;

    public MapProperties(@Value("${app.kakao.map-key:}") String key) {
        this.key = key == null ? "" : key.trim();
    }

    public String getKey() {
        return key;
    }

    public boolean isEnabled() {
        return !key.isEmpty();
    }
}

package com.example.Login_System2.application.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizer {

    private static final Safelist SAFE_LIST = Safelist.basicWithImages();

    public String sanitize(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, SAFE_LIST);
    }
    
}

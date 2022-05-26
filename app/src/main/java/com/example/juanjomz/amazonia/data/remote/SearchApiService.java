package com.example.juanjomz.amazonia.data.remote;

import com.bumptech.glide.Glide;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchApiService {

    public static List<String> getImage(String plantName) {
        String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
        String url = "https://www.google.com/search?site=imghp&tbm=isch&source=hp&q="+plantName+" plant&gws_rd=cr";
        List<String> resultUrls = new ArrayList();
        try {
            Document doc = Jsoup.connect(url).userAgent(userAgent).referrer("https://www.google.com/").get();
            Elements elements =  doc.getElementsByTag("img");
            for (Element element : elements) {
                resultUrls.add(element.attr("src"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultUrls;
    }
}

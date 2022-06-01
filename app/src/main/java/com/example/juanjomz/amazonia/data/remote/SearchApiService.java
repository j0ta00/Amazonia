package com.example.juanjomz.amazonia.data.remote;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.juanjomz.amazonia.domain.PlantBO;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> getImages(List<PlantBO> species){
        List<String> results=new LinkedList<String>();
        species.forEach(specie->results.add(getImage(specie.getCommonName()==null?specie.getScientificName():specie.getCommonName()).get(1)));
        return results;
    }
}

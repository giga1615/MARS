package com.mars.pushhandle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.UriEncoder;

@Service
public class AndroidPushPeriodicNotifications {

    public String PeriodicNotificationJson(List<String> tokens) throws JSONException, UnsupportedEncodingException {
        LocalDate localDate = LocalDate.now();

        JSONObject body = new JSONObject();
        JSONArray array = new JSONArray();

        for(int i=0; i<tokens.size(); i++) {
            array.put(tokens.get(i));
        }

        body.put("registration_ids",array);

        body.put("priority", "high");
        JSONObject notification = new JSONObject();
        notification.put("title","MARS");
        //String content = "공유된 캡슐이 있습니다.";
        notification.put("body",URLEncoder.encode("공유된 캡슐이 있습니다.", "UTF-8"));
        
        body.put("notification", notification);
        System.out.println("##########body.toString()#########" +body.toString());
        //System.out.println(body.toString());

        return body.toString();
    }
}
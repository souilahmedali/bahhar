package com.msouilah.navionics;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

@RestController
public class TileController {


    @GetMapping("/ping")
    public String getPing(){
        return new Date().toString();
    }


    @GetMapping(value = "/navionics/tile/{z}/{x}/{y}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getTile(@PathVariable String z, @PathVariable String x, @PathVariable String y) {
        byte[] media = "error".getBytes();
        Calendar calendar = Calendar.getInstance();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", "https://webapp.navionics.com#13#10");
        headers.add("Referer", "https://webapp.navionics.com");
        HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> responseToken = restTemplate.exchange("https://backend.navionics.com/tile/get_key/NAVIONICS_WEBAPP_P01/webapp.navionics.com?_={t}",
                HttpMethod.GET, httpEntity, String.class, Collections.singletonMap("t", calendar.getTimeInMillis()));

        if (responseToken.getStatusCode().is2xxSuccessful()) {
            HashMap<String, String> uriVariables = new HashMap<>();
            uriVariables.put("z",z);
            uriVariables.put("x",x);
            uriVariables.put("y",y);
            uriVariables.put("t",responseToken.getBody());
            ResponseEntity<byte[]> responseTile = restTemplate.exchange("https://backend.navionics.com/tile/{z}/{x}/{y}?LAYERS=config_1_10.00_1&TRANSPARENT=FALSE&UGC=TRUE&navtoken={t}",
                    HttpMethod.GET, httpEntity, byte[].class, uriVariables);
            if(responseTile.getStatusCode().is2xxSuccessful())
                return responseTile.getBody();
        }
        return media;
    }
}

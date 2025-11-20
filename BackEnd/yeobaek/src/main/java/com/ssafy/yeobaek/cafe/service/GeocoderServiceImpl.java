package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.response.Coordinates;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;

@Slf4j
@Service
public class GeocoderServiceImpl implements GeocoderService {

    @Value("${vworld.api.key}")
    private String apiKey;

    @Value("${vworld.api.search-type}")
    private String searchType;

    @Value("${vworld.api.epsg}")
    private String epsg;

    @Value("${vworld.api.url}")
    private String url;

    @Override
    public Coordinates getCoordinates(String address) {
        log.info("주소 좌표 변환 시작 - 주소: '{}'", address);

        StringBuilder sb = new StringBuilder(url);
        sb.append("?service=address");
        sb.append("&request=getCoord");
        sb.append("&format=json");
        sb.append("&crs=").append(epsg);
        sb.append("&key=").append(apiKey);
        sb.append("&type=").append(searchType);
        sb.append("&address=").append(URLEncoder.encode(address, StandardCharsets.UTF_8));

        try {
            URL url = new URL(sb.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

            JSONParser jspa = new JSONParser();
            JSONObject jsob = (JSONObject) jspa.parse(reader);
            JSONObject jsrs = (JSONObject) jsob.get("response");

            // API 응답 상태 확인
            String status = (String) jsrs.get("status");
            if (!"OK".equals(status)) {
                if ("NOT_FOUND".equals(status)) {
                    log.warn("주소 좌표 변환 실패 - 주소를 찾을 수 없음: '{}'", address);
                    throw new CustomException(ResponseCode.CAFE_INVALID_ADDRESS);
                } else if ("ERROR".equals(status)) {
                    JSONObject jsError = (JSONObject) jsrs.get("error");
                    String errorCode = (String) jsError.get("code");
                    String errorText = (String) jsError.get("text");
                    log.error("VWorld Geocoding API 오류 발생. 코드: {}, 메시지: {}", errorCode, errorText);
                    throw new CustomException(ResponseCode.GEOCODING_API_ERROR);
                }
                log.error("VWorld Geocoding API에서 예상치 못한 상태 반환: {}", status);
                throw new CustomException(ResponseCode.GEOCODING_API_ERROR);
            }

            JSONObject jsResult = (JSONObject) jsrs.get("result");
            JSONObject jsPoint = (JSONObject) jsResult.get("point");

            double longitude = Double.parseDouble((String) jsPoint.get("x"));
            double latitude = Double.parseDouble((String) jsPoint.get("y"));

            log.info("주소 좌표 변환 성공 - 위도: {}, 경도: {}", latitude, longitude);
            return new Coordinates(longitude, latitude);

        } catch (IOException | ParseException | NullPointerException e) {
            log.error("주소 좌표 변환 중 예외 발생 - 주소: '{}'", address, e);
            throw new CustomException(ResponseCode.GEOCODING_API_ERROR);
        }
    }
}

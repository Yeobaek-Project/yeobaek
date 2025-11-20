package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.response.Coordinates;

public interface GeocoderService {
    Coordinates getCoordinates(String address);
}

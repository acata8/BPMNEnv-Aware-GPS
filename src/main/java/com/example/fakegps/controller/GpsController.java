package com.example.fakegps.controller;

import com.example.fakegps.service.GpsSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gps")
public class GpsController {

    @Autowired
    private GpsSenderService gpsSenderService;


    @PostMapping("/start")
    public Map<String, String> startSending() {
        gpsSenderService.startSending();
        return Map.of("status", "started");
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentPosition() {
        Map<String, Object> position = new HashMap<>();
        position.put("lat", gpsSenderService.getCurrentLat());
        position.put("lon", gpsSenderService.getCurrentLon());
        position.put("inside", gpsSenderService.isInside());
        return ResponseEntity.ok(position);
    }

}

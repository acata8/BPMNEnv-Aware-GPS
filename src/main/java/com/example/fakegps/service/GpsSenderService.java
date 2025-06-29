package com.example.fakegps.service;

import com.example.fakegps.models.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
public class GpsSenderService {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String LAST_PID_ENDPOINT = "/api/process/last";
    private static final String GPS_ENDPOINT = "/api/movement/gps";

    private final HttpClient client = HttpClient.newHttpClient();
    private final Random random = new Random();

    private double currentLat = 0.0;
    private double currentLon = 0.0;
    private boolean inside = false;

    public synchronized double getCurrentLat() {
        return currentLat;
    }

    public synchronized double getCurrentLon() {
        return currentLon;
    }

    public synchronized boolean isInside() {
        return inside;
    }

    private synchronized void updatePosition(double lat, double lon, boolean inside) {
        this.currentLat = lat;
        this.currentLon = lon;
        this.inside = inside;
    }

    public void startSending() {
        new Thread(() -> {
            try {

                updatePosition(0,0,false);

                HttpRequest pidRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + LAST_PID_ENDPOINT))
                        .GET()
                        .build();



                HttpResponse<String> pidResponse = client.send(pidRequest, HttpResponse.BodyHandlers.ofString());

                if (pidResponse.statusCode() != 200) {
                    System.out.println("PID not found! Response: " + pidResponse.body());
                    return;
                }

                String pid = pidResponse.body().split(":")[1].replaceAll("[\"}]", "").trim();
                System.out.println("[GPS] Get pid: " + pid);

                User user = new User("Participant_0ie2wap", "Activity_1stc37q");

                // Lista delle coordinate
                List<double[]> coordinates = List.of(
                        new double[]{43.143023, 13.077067},
                        new double[]{43.14347, 13.07354},
                        new double[]{43.14333, 13.07199},
                        new double[]{43.14316, 13.06959},
                        new double[]{43.14255, 13.06837},
                        new double[]{43.141744, 13.068269},
                        new double[]{43.141192, 13.068693},
                        new double[]{43.140460, 13.069358},
                        new double[]{43.139876, 13.069069},
                        new double[]{43.139612, 13.068766} // Coordinata valida
                );

                for (int i = 0; i < coordinates.size(); i++) {
                    double lat = coordinates.get(i)[0];
                    double lon = coordinates.get(i)[1];
                    boolean inside = (i == coordinates.size() - 1);

                    sendGps(pid, lat, lon, user.participantId, user.activityId, inside);
                    Thread.sleep(1000);
                }

                System.out.println("[GPS] Done");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendGps(String pid, double lat, double lon, String participantId, String activityId, Boolean inside) throws IOException, InterruptedException {
        String url = String.format(Locale.US, "%s%s?pid=%s&lat=%.6f&lon=%.6f&participantId=%s&activityId=%s",
                BASE_URL, GPS_ENDPOINT, pid, lat, lon, participantId, activityId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        updatePosition(lat, lon, inside);

        System.out.printf("Sent: (lat: %.2f, lon: %.2f) → Status: %d, Response: %s%n",
                lat, lon, response.statusCode(), response.body());
    }

}

package com.example.fakegps.models;

import java.time.Instant;

public class User {

    public final String participantId;
    public final String activityId;
    public final String name;


    public User(String participantId, String activityId) {
        this.participantId = participantId;
        this.activityId = activityId;
        this.name = "Andrea Cataluffi";
    }
}

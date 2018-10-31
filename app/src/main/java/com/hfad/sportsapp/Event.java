package com.hfad.sportsapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@IgnoreExtraProperties
public class Event implements Serializable {

    private String id;
    private String description;
    private SportsCategory sportsCategory;
    private Date date;
    private MyLatLng position;
    private String organizerUid;
    private List<String> participants = new ArrayList<>();

    public Event() {

    }

    public Event(String description, SportsCategory sportsCategory, Date date, MyLatLng position, String organizerUid) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.sportsCategory = sportsCategory;
        this.date = date;
        this.position = position;
        this.organizerUid = organizerUid;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public SportsCategory getSportsCategory() {
        return sportsCategory;
    }

    public Date getDate() {
        return date;
    }

    public String getOrganizerUid() {
        return organizerUid;
    }

    public MyLatLng getPosition() {
        return position;
    }

    public List<String> getParticipants() {
        return participants;
    }
}

// TODO 4. list of participant of event;
// TODO 5. add myself to event
// TODO 6. integration with Jak dojade ???
// TODO 7. push notification
// TODO 8. settings (kind of map, what categories does user want to see, change language etc.)

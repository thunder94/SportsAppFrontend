package com.hfad.sportsapp;

import java.util.HashMap;
import java.util.Map;

public enum SportsCategory {
    AIR_SPORTS(0, "Air sports"), ARCHERY(1, "Archery"), BALL_OVER_NET_GAMES(2, "Ball-over-net games"),
    BASKETBALL_FAMILY(3, "Basketball family"), BAT_AND_BALL(4, "Bat-and-ball"), ACRO_SPORTS(5, "Acro sports"),
    BOARD_SPORTS(6, "Board sports"), CLIMBING(7, "Climbing"), CYCLING(8, "Cycling"), COMBAT_SPORTS(9, "Combat sports"),
    CUE_SPORTS(10, "Cue sports"), EQUINE_SPORTS(11, "Equine sports"), FISHING(12, "Fishing"),
    FLYING_DISC_SPORTS(13, "Flying disc sports"), FOOTBALL(14, "Football"), GOLF(15, "Golf"), GYMNASTICS(16, "Gymnastics"),
    HANDBALL_FAMILY(17, "Handball family"), HUNTING(18, "Hunting"), ICE_SPORTS(19, "Ice sports"),
    KITE_SPORTS(20, "Kite sports"), RACQUET_SPORTS(21, "Racquet sports"), REMOTE_CONTROL(22, "Remote control"),
    RUNNING(23, "Running"), SAILING(24, "Sailing"), SNOW_SPORTS(25, "Snow sports"), SHOOTING_SPORTS(26, "Shooting sports"),
    STICK_AND_BALL_GAMES(27, "Stick and ball games"), TAG_GAMES(28, "Tag games"), WALKING(29, "Walking"),
    WEIGHTLIFTING(30, "Weightlifting"), MOTORIZED_SPORTS(31, "Motorized sports"), MARKER_SPORTS(32, "Marker sports"),
    OTHER(33, "Other");

    private final int id;
    private final String name;

    private static final Map<Integer, SportsCategory> byId = new HashMap<>();
    static {
        for (SportsCategory e : SportsCategory.values()) {
            if (byId.put(e.getId(), e) != null) {
                throw new IllegalArgumentException("duplicate id: " + e.getId());
            }
        }
    }

    SportsCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static SportsCategory getById(int id) {
        return byId.get(id);
    }
}

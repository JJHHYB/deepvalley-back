package jjhhyb.deepvalley.place;

import lombok.Getter;

@Getter
public enum PlaceSortType {
    POST_COUNT("postCount"), AVG_RATING("avgRating");

    private final String name;

    PlaceSortType(String name) {
        this.name = name;
    }
}

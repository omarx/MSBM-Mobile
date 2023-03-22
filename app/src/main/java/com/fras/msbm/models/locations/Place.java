package com.fras.msbm.models.locations;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Shane on 7/7/2016.
 */
@Builder
@Setter @Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class Place {
    private String name;
    private Coordinates coordinates;

    public static List<Place> getBookingLocations() {
        List<Place> places = new ArrayList<>();
        places.add(Place.builder().name("Gazebo 1").coordinates(new Coordinates(18.008155, -76.747830)).build());
        places.add(Place.builder().name("Gazebo 2").coordinates(new Coordinates(18.008217, -76.747577)).build());
        places.add(Place.builder().name("Gazebo 3").coordinates(new Coordinates(18.008224, -76.747442)).build());
        places.add(Place.builder().name("Gazebo 4").coordinates(new Coordinates(18.008260, -76.746979)).build());
        places.add(Place.builder().name("Gazebo 5").coordinates(new Coordinates(18.0071707, -76.7483646)).build());
        places.add(Place.builder().name("Classroom 1").coordinates(new Coordinates(18.007921, -76.747649)).build());
        places.add(Place.builder().name("Classroom 2").coordinates(new Coordinates(18.007991, -76.747191)).build());
        return places;
    }
}

package com.fras.msbm.models.locations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Shane on 7/7/2016.
 */
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class Coordinates {
    private double latitude;
    private double longitude;
}

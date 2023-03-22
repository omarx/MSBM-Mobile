package com.fras.msbm.models.locations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Shane on 7/30/2016.
 */
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class OpeningHours {
    private String title;
    private String start;
    private String end;
}
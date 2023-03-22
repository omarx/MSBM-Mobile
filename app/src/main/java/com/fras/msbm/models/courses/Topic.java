package com.fras.msbm.models.courses;

import com.fras.msbm.models.courses.Module;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Shane on 7/2/2016.
 */
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    private int id;
    private String name;
    private int visible;
    private String summary;
    private int summaryFormat;
    private List<Module> modules;
}

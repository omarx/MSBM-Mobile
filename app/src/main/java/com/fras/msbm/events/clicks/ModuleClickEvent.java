package com.fras.msbm.events.clicks;

import com.fras.msbm.models.courses.Module;

/**
 * Created by Shane on 7/4/2016.
 */
public class ModuleClickEvent {
    private final Module module;

    public ModuleClickEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }
}

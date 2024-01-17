package com.game.model;

import java.util.List;

public enum Location {
    HOME {},
    OUTSIDE {},
    ANY {
        @Override
        public List<Location> getOptions() {
            return List.of(HOME, OUTSIDE);
        }
    };

    public List<Location> getOptions() {
        return List.of(this);
    }
}

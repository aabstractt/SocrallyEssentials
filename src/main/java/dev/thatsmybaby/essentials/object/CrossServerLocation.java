package dev.thatsmybaby.essentials.object;

import cn.nukkit.level.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public final class CrossServerLocation {

    private String name;
    private String locationSerialized;
    private Location location;
}
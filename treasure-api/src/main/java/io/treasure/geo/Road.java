package io.treasure.geo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Road  implements Serializable {

    String id;
    String name;
    String distance;
    String direction;
    String location;

}

package io.treasure.geo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Poi  implements Serializable {

    String id;
    String name;
    String type;
    String tel;
    String distance;
    String direction;
    String address;
    String location;
    String businessarea;

}

package io.treasure.geo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Aoi  implements Serializable {

    String id;
    String name;
    String adcode;
    String area;
    String distance;

}

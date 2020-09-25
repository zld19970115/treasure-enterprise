package io.treasure.geo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Roadinter  implements Serializable {

    String distance;
    String direction;
    String location;
    String first_id;
    String first_name;
    String second_id;
    String second_name;

}

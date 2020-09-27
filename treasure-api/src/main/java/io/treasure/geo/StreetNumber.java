package io.treasure.geo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StreetNumber  implements Serializable {

    private String number;
    private String location;
    private String direction;
    private String distance;
    private String street;
}

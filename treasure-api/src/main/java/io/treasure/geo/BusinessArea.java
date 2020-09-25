package io.treasure.geo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BusinessArea  implements Serializable {

    private String businessArea;
    private String location;
    private String name;
    private String id;
}

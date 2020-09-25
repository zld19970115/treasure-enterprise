package io.treasure.geo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Regeocodes  implements Serializable {

    private String formatted_address;
    private AddressComponent addressComponent;
    //private List<Road> roads;//道路信息列表
    //private List<Roadinter> roadinters;//道路交叉列表
    private List<Poi> pois;
    private List<Aoi> aois;

}

package io.treasure.geo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AddressComponent  implements Serializable {

    private String province;
    private String city;
    private String citycode;
    private String district;
    private String adcode;
    private String township;
    private String towncode;
    private String country;
    //private List<Neighborthood> neighborthood;//
    //private Building building;//
    private StreetNumber streetNumber;
    private List<BusinessArea> businessAreas;

}

package io.treasure.geo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Neighborthood implements Serializable {

    private List<String> name;
    private List<String> type;

}

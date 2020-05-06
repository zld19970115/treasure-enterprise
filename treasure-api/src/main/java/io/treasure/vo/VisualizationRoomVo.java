package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("查询可视化包房VO")
public class VisualizationRoomVo {

    private String roomName;

    private String icon;

    private Integer numLow;

    private Integer numHigh;

    private String oneTime;

    private String twoTime;

    private String threeTime;

    private String fourTime;

    private Integer oneState;

    private Integer twoState;

    private Integer threeState;

    private Integer fourState;

}

package io.treasure.vo;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("查询可视化包房VO")
public class VisualizationRoomVo {

    private Long id;

    private Long oneCid;

    private Long twoCid;

    private Long threeCid;

    private Long fourCid;

    private int type;

    private Long oneCreator;

    private Long twoCreator;

    private Long threeCreator;

    private Long fourCreator;

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

    private Integer flag = 0;

}

package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.dao.*;

import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.SharingActivityDTO;
import io.treasure.entity.*;
import io.treasure.vo.PagePlus;
import io.treasure.vo.SharingActivityComboVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/sharing_manager")
@Api(tags="活动助力管理")
public class SharingForManagerController {


    @Autowired(required = false)
    private SharingActivityLogDao sharingActivityLogDao;

    @Autowired(required = false)
    private SharingActivityDao sharingActivityDao;
    @Autowired(required = false)
    private SharingActivityForDtoDao sharingActivityForDtoDao;
    @Autowired(required = false)
    private SharingActivityExtendsDao sharingActivityExtendsDao;

    @Autowired(required = false)//关系表
    private DistributionRewardDao distributionRewardDao;
    @Autowired(required = false)
    private DistributionRewardLogDao distributionRewardLogDao;


    @CrossOrigin
    @Login
    @GetMapping("/helper_records")
    @ApiOperation(value = "助力日志表",tags = "查询所有助力日本志信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="sal_id",value = "id",dataType = "Long",paramType = "query",required = false),
            @ApiImplicitParam(name = "activity_id",value="活动id",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name ="initiator_id",value = "发起者的clientId",dataType = "Long",paramType = "query",required = false),
            @ApiImplicitParam(name="ps_no",value = "发起序号号",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="helper_mobile",value = "助力者手机号",dataType = "string",paramType = "query",required = false),
            @ApiImplicitParam(name="startTime",value = "时间范围开始",dataType = "Date",paramType = "query",required = false),
            @ApiImplicitParam(name="stopTime",value = "时间范围结束",dataType = "Date",paramType = "query",required=false),
            @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false)
    })
    public Result requireItems(Long sal_id, Integer activity_id,Long initiator_id,Integer ps_no,
                               String helper_mobile,Date startTime,Date stopTime,int index, int itemNum) throws ParseException {

        QueryWrapper<SharingActivityLogEntity> saqw = new QueryWrapper<>();


        if(sal_id != null)
            saqw.eq("sal_id",sal_id);
        if(activity_id != null)
            saqw.eq("activity_id",activity_id);
        if(initiator_id != null)
            saqw.eq("initiator_id",initiator_id);
        if(ps_no != null)
            saqw.eq("propose_sequeue_no",ps_no);
        if(helper_mobile != null)
            saqw.eq("helper_mobile",helper_mobile);
        //时间处理
        if(startTime != null && stopTime != null){
            saqw.between("create_pmt",startTime,stopTime);
        }else if(startTime != null){
            saqw.ge("create_pmt",startTime);//大于
        }else if(stopTime != null){
            saqw.le("create_pmt",stopTime);
        }

        Page<SharingActivityLogEntity> record = new Page<SharingActivityLogEntity>(index,itemNum);
        IPage<SharingActivityLogEntity> sharingRecords = sharingActivityLogDao.selectPage(record, saqw);

        if(sharingRecords == null)
            return new Result().error("nothing");

        return new Result().ok(sharingRecords);
        //return new Result().ok(merchantWithdrawEntityIPage.getRecords());
    }


    //查询助力参数

    @CrossOrigin
    @Login
    @GetMapping("/sharing_item")
    @ApiOperation(value = "助力查询",tags = "指定助力查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="sa_id",value = "id（非空表示查一项）",dataType = "Integer",paramType = "query",required = false),
    })
    public Result getSharingList(Integer sa_id) throws ParseException {
        QueryWrapper<SharingActivityEntity> saqw = new QueryWrapper<>();
        if(sa_id != null)
            saqw.eq("sa_id",sa_id);
        return new Result().ok(sharingActivityDao.selectOne(saqw));
    }

    @CrossOrigin
    @Login
    @GetMapping("/sharing_list")
    @ApiOperation(value = "助力查询",tags = "指定助力查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="sa_id",value = "id（非空表示查一项）",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name = "subject",value="活动名称模糊",dataType = "String",paramType = "query",required = false),
            @ApiImplicitParam(name="reward_type",value = "奖励类型",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="reward_amount",value = "奖励数量",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="compare_method",value = "奖励数量1:大于，2小于,0等于",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="creator",value = "发起者",dataType = "Long",paramType = "query",required = false),
            @ApiImplicitParam(name="startTime",value = "时间范围开始",dataType = "Date",paramType = "query",required = false),
            @ApiImplicitParam(name="stopTime",value = "时间范围结束",dataType = "Date",paramType = "query",required=false),
            @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false)
    })
    public Result getSharingList(Integer sa_id, String subject,Integer reward_type,Integer reward_amount,Integer compare_method,
                                 Long creator,Date startTime,Date stopTime,int index, int itemNum) throws ParseException {

        QueryWrapper<SharingActivityEntity> saqw = new QueryWrapper<>();

        if(sa_id != null)
            saqw.eq("sa_id",sa_id);
        if(subject != null)
            saqw.like("subject",subject);
        if(reward_type != null)
            saqw.eq("reward_type",reward_type);
        //奖励数量设置
        if(reward_amount != null){
            if(compare_method == 1){
                saqw.ge("reward_amount",reward_amount);
            }else if(compare_method == 2){
                saqw.le("reward_amount",reward_amount);
            }else{
                saqw.eq("reward_amount",reward_amount);
            }
        }

        if(creator != null)
            saqw.eq("creator",creator);

        //时间处理
        if(startTime != null && stopTime != null){
            saqw.le("open_date",startTime);
            saqw.ge("close_date",stopTime);
        }else if(startTime != null){
            saqw.le("open_date",startTime);
        }else if(stopTime != null){
            saqw.ge("close_date",stopTime);
        }

        Page<SharingActivityEntity> record = new Page<SharingActivityEntity>(index,itemNum);
        IPage<SharingActivityEntity> sharingRecords = sharingActivityDao.selectPage(record, saqw);
        if(sharingRecords == null)
            return new Result().error("nothing");

        return new Result().ok(sharingRecords);
    }

    @Login
    @PostMapping("/sharing_item")
    @ApiOperation("更新活动")
    public Result updateSharingItem(@RequestBody SharingActivityEntity entity){
        int saId = entity.getSaId()==null?0:entity.getSaId();
        QueryWrapper<SharingActivityEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sa_id",saId);
        SharingActivityEntity sharingActivityEntity = sharingActivityDao.selectOne(queryWrapper);
        if(sharingActivityEntity == null)
            return new Result().error("null object or params error");
        sharingActivityDao.updateById(entity);
        return new Result().ok("has been updated");
    }

    @Login
    @PutMapping("/sharing_item")
    @ApiOperation("插入新活动")
    public Result insertSharingItem(@RequestBody SharingActivityDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        sharingActivityForDtoDao.insert(dto);

        /*
        SharingActivityExtendsEntity sharingActivityExtendsEntity = new SharingActivityExtendsEntity();
        if(dto.getSaId() != null){
            sharingActivityExtendsEntity.setSaeId(dto.getSaId());
        }else{

        }
        sharingActivityExtendsDao.insert(sharingActivityExtendsEntity);
        */

        return new Result().ok("added new record");
    }
    @Login
    @PutMapping("/sharing_combo_item")
    @ApiOperation("插入新活动")
    public Result insertSharingComboItem(@RequestBody SharingActivityComboVo vo){
        //效验数据
        ValidatorUtils.validateEntity(vo.getSharingActivityDto(), AddGroup.class, DefaultGroup.class);

        sharingActivityForDtoDao.insert(vo.getSharingActivityDto());
        sharingActivityExtendsDao.insert(vo.getSharingActivityExtendsEntity());

        return new Result().ok("added new record");
    }




    @CrossOrigin
    @Login
    @GetMapping("/sharing_extends_item")
    @ApiOperation(value = "助力续表信息查询",tags = "指定续表信息助力查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="sa_id",value = "id（非空表示查一项）",dataType = "Integer",paramType = "query",required = false),
    })
    public Result getSharingExtendsList(Integer sae_id) throws ParseException {
        QueryWrapper<SharingActivityExtendsEntity> saqw = new QueryWrapper<>();
        if(sae_id != null)
            saqw.eq("sae_id",sae_id);
        return new Result().ok(sharingActivityExtendsDao.selectOne(saqw));
    }

    @Login
    @PostMapping("/sharing_extends_item")
    @ApiOperation("更新活动续表")
    public Result updateSharingExtendsItem(@RequestBody SharingActivityExtendsEntity entity){
        int saeId = entity.getSaeId()==null?0:entity.getSaeId();
        QueryWrapper<SharingActivityExtendsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saeId",saeId);
        SharingActivityExtendsEntity saeEntity = sharingActivityExtendsDao.selectOne(queryWrapper);
        if(saeEntity == null)
            return new Result().error("null object or params error");
        sharingActivityExtendsDao.updateById(saeEntity);
        return new Result().ok("has been updated");
    }

    @CrossOrigin
    @Login
    @GetMapping("/distribution_relation_list")
    @ApiOperation(value = "分销关系记录表",tags = "分销关系表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="id",value = "id（非空表示查一项）",dataType = "Long",paramType = "query",required = false),
                @ApiImplicitParam(name = "mobile_master",value="团队组长电话",dataType = "String",paramType = "query",required = false),
            @ApiImplicitParam(name="mobile_slaver",value = "成员电话",dataType = "String",paramType = "query",required = false),
            @ApiImplicitParam(name="status",value = "1有效，其它失效",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="sa_id",value = "活动编号",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="startTime",value = "时间范围开始",dataType = "Date",paramType = "query",required = false),
            @ApiImplicitParam(name="stopTime",value = "时间范围结束",dataType = "Date",paramType = "query",required=false),
            @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false)
    })
    public Result getDistributionRelationList(Long id, String mobile_master,String mobile_slaver,Integer status,Integer sa_id,
                                 Date startTime,Date stopTime,int index, int itemNum) throws ParseException {

        QueryWrapper<DistributionRelationshipEntity> saqw = new QueryWrapper<>();

        if(id != null)
            saqw.eq("id",id);
        if(mobile_master != null)
            saqw.like("mobile_master",mobile_master);
        if(mobile_slaver != null)
            saqw.like("mobile_slaver",mobile_slaver);
        if(status != null)
            saqw.eq("status",status);
        if(sa_id != null)
            saqw.eq("sa_id",sa_id);
        //时间处理
        if(startTime != null && stopTime != null){
            saqw.ge("union_start_time",startTime);
            saqw.le("union_start_time",stopTime);
        }else if(startTime != null){
            saqw.ge("union_start_time",startTime);
        }else if(stopTime != null){
            saqw.le("union_start_time",stopTime);
        }

        Page<DistributionRelationshipEntity> record = new Page<DistributionRelationshipEntity>(index,itemNum);
        IPage<DistributionRelationshipEntity> sharingRecords = distributionRewardDao.selectPage(record, saqw);
        if(sharingRecords == null)
            return new Result().error("nothing");

        return new Result().ok(sharingRecords);
    }


    @CrossOrigin
    @Login
    @GetMapping("/distribution_reward_list")
    @ApiOperation(value = "分销关系记录表",tags = "分销关系表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="id",value = "id（非空表示查一项）",dataType = "Long",paramType = "query",required = false),
            @ApiImplicitParam(name = "mobile_master",value="团队组长电话",dataType = "String",paramType = "query",required = false),
            @ApiImplicitParam(name="mobile_slaver",value = "成员电话",dataType = "String",paramType = "query",required = false),
            @ApiImplicitParam(name="reward_type",value = "1有效，其它失效",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="references_total",value = "活动编号",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="reward_amount",value = "活动编号",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="startTime",value = "时间范围开始",dataType = "Date",paramType = "query",required = false),
            @ApiImplicitParam(name="stopTime",value = "时间范围结束",dataType = "Date",paramType = "query",required=false),
            @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false)
    })
    public Result distributionRewardList(Long id, String mobile_master,String mobile_slaver,Integer reward_type,Integer references_total,
                                 Integer reward_amount,
                                 Date startTime,Date stopTime,int index, int itemNum) throws ParseException {

        QueryWrapper<DistributionRewardLogEntity> saqw = new QueryWrapper<>();

        if(id != null)
            saqw.eq("id",id);
        if(mobile_master != null)
            saqw.like("mobile_master",mobile_master);
        if(mobile_slaver != null)
            saqw.like("mobile_slaver",mobile_slaver);
        if(reward_type != null)
            saqw.eq("reward_type",reward_type);
        if(references_total != null)
            saqw.eq("references_total",references_total);
        if(reward_amount != null)
            saqw.eq("reward_amount",reward_amount);
        //时间处理
        if(startTime != null && stopTime != null){
            saqw.ge("consume_time",startTime);
            saqw.le("consume_time",stopTime);
        }else if(startTime != null){
            saqw.ge("consume_time",startTime);
        }else if(stopTime != null){
            saqw.le("consume_time",stopTime);
        }

        Page<DistributionRewardLogEntity> record = new Page<DistributionRewardLogEntity>(index,itemNum);
        IPage<DistributionRewardLogEntity> sharingRecords = distributionRewardLogDao.selectPage(record, saqw);
        if(sharingRecords == null)
            return new Result().error("nothing");

        return new Result().ok(sharingRecords);
    }

    @Autowired(required = false)
    private DistributionParamsDao distributionParamsDao;

    @GetMapping("/dp_item")
    @ApiOperation(value = "获取分销参数列表",tags = "获取分销参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="id",value = "id（非空表示查一项）",dataType = "Long",paramType = "query",required = false),
    })
    public Result getDistributionParams(Long id){
        QueryWrapper<DistributionParamsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sa_id",id);

        List<DistributionParamsEntity> distributionParamsEntities = distributionParamsDao.selectList(queryWrapper);
        return new Result().ok(distributionParamsEntities);
    }


    @Login
    @PostMapping("/dp_item")
    @ApiOperation("更新分销系统参数")
    public Result updateDistributionParamsById(@RequestBody DistributionParamsEntity entity){

        distributionParamsDao.updateById(entity);
        return new Result().ok("update complete");
    }




}

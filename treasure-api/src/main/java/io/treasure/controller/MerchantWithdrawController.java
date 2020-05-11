package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.gson.Gson;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dao.MerchantWithdrawDao;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.QueryWithdrawDto;
import io.treasure.enm.Common;
import io.treasure.enm.WithdrawEnm;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantWithdrawEntity;
import io.treasure.service.MerchantWithdrawService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.service.impl.MerchantServiceImpl;
import io.treasure.utils.RegularUtil;
import io.treasure.vo.PagePlus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 提现表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-20
 */
@RestController
@RequestMapping("/merchantwithdraw")
@Api(tags="提现管理")
public class MerchantWithdrawController {
    @Autowired
    private MerchantWithdrawService merchantWithdrawService;
    @Autowired
    private MerchantServiceImpl merchantService;
    @Autowired(required = false)
    private MerchantWithdrawDao merchantWithdrawDao;
    @CrossOrigin
    @Login
    @GetMapping("allPage")
    @ApiOperation("全部列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "long")

    })
    public Result<PageData<MerchantWithdrawDTO>> allPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.page(params);
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("agreePage")
    @ApiOperation("同意提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantWithdrawDTO>> agreePage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_AGREE_YES.getStatus()+"");
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.listPage(params);
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("agreeNoPage")
    @ApiOperation("拒绝提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantWithdrawDTO>> agreeNoPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_AGREE_NO.getStatus()+"");
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.listPage(params);
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("applPage")
    @ApiOperation("拒绝提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantWithdrawDTO>> applPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_NO.getStatus()+"");
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.listPage(params);
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("getInfo")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="Long")
    })
    public Result<MerchantWithdrawDTO> get(@RequestParam Long id){
        MerchantWithdrawDTO data = merchantWithdrawService.get(id);
        return new Result<MerchantWithdrawDTO>().ok(data);
    }

    @Login
    @PostMapping("save")
    @ApiOperation("申请提现")
    public Result save(@RequestBody MerchantWithdrawDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        MerchantEntity merchantEntity = merchantService.selectById(dto.getMerchantId());
        Double notCash = merchantEntity.getNotCash();
        List<MerchantWithdrawDTO> merchantWithdrawDTOS = merchantWithdrawService.selectByMartIdAndStasus(dto.getMerchantId());
      if (merchantWithdrawDTOS.size()!=0) {
          return new Result().error("提现处理中，请稍后再试");
      }
        double money = dto.getMoney();
        if (money>notCash){
            return new Result().error("提现金额不足");
        }
        if(money<1 || money >5000){
            return new Result().error("提现范围在1~5000元");
        }

//
//        if((int)money != money){
//            return new Result().error("请输入整数");
//        }

        dto.setCreateDate(new Date());
        dto.setStatus(Common.STATUS_ON.getStatus());
        dto.setVerifyState(WithdrawEnm.STATUS_NO.getStatus());
        dto.setWay(WithdrawEnm.WAY_HAND.getStatus());
        merchantWithdrawService. save(dto);
        return new Result();
    }
    @CrossOrigin
    @Login
    @DeleteMapping("remove")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long")
    })
    public Result delete(@RequestParam long id){
        merchantWithdrawService.updateStatusById(id,Common.STATUS_DELETE.getStatus());
        return new Result();
    }


    @CrossOrigin
    @Login
    @GetMapping("/selectCath")
    @ApiOperation("查询提现金额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "编号", paramType = "query", required = true, dataType="Long")
    })
    public Result selectCath(@RequestParam Long martId) {

        List<MasterOrderEntity>  masterOrderEntity = merchantWithdrawService.selectOrderByMartID(martId);
        MerchantEntity merchantEntity = merchantService.selectById(martId);
        Double wartCash = merchantWithdrawService.selectWaitByMartId(martId);
      if (wartCash==null){
    wartCash=0.00;
}
        if (masterOrderEntity==null){
            if(null!=merchantEntity){
                BigDecimal wartcashZore = new BigDecimal("0.00");
                merchantEntity.setTotalCash(0.00);
                merchantEntity.setAlreadyCash(0.00);
                merchantEntity.setNotCash(0.00);
                merchantEntity.setPointMoney(0.00);
                merchantEntity.setWartCash(wartcashZore);
                merchantService.updateById(merchantEntity);
                Map map = new HashMap();
                map.put("alead_cash", 0.00);
                map.put("not_cash", 0.00);
                map.put("wart_cash",wartcashZore);
                return new Result().ok(map);
            }else{
                return new Result().error("无法获取店铺信息!");
            }
        }
        List<MerchantWithdrawEntity> merchantWithdrawEntities = merchantWithdrawService.selectPoByMartID(martId);
        if (merchantWithdrawEntities.size()==0){
            BigDecimal bigDecimal = merchantWithdrawService.selectTotalCath(martId);
            BigDecimal bigDecimal1 = merchantWithdrawService.selectPointMoney(martId);

            BigDecimal wartcashZore = new BigDecimal("0.00");
            if (null==bigDecimal ){

                if(null!=merchantEntity){
                    if (bigDecimal1==null){  bigDecimal1 = new BigDecimal("0.00");}
                    merchantEntity.setTotalCash(0.00);
                    merchantEntity.setAlreadyCash(0.00);
                    merchantEntity.setNotCash(0.00);
                    merchantEntity.setPointMoney(bigDecimal1.doubleValue());
                    merchantEntity.setWartCash(wartcashZore);
                    merchantService.updateById(merchantEntity);
                    Map map = new HashMap();

                    map.put("alead_cash", 0.00);
                    map.put("not_cash", 0.00);
                    map.put("wart_cash",wartcashZore);
                    return new Result().ok(map);
                }else{
                    return new Result().error("无法获取店铺信息!");
                }
            }
            BigDecimal totalCash =  bigDecimal.add(bigDecimal1);
            merchantEntity.setTotalCash(totalCash.doubleValue());
            merchantEntity.setAlreadyCash(0.00);
            merchantEntity.setNotCash(bigDecimal.doubleValue());
            merchantEntity.setPointMoney(bigDecimal1.doubleValue());
            merchantEntity.setWartCash(wartcashZore);
            merchantService.updateById(merchantEntity);
            Map map = new HashMap();
            map.put("alead_cash", 0.00);
            map.put("not_cash", bigDecimal.doubleValue());
            map.put("wart_cash",wartcashZore);

            return new Result().ok(map);
        }
        if (merchantWithdrawEntities.size() != 0) {
            BigDecimal wartcash = new BigDecimal(String.valueOf(wartCash));
            BigDecimal bigDecimal = merchantWithdrawService.selectTotalCath(martId);//查询总额
            BigDecimal bigDecimal1 = merchantWithdrawService.selectPointMoney(martId);//查询扣点总额
            if (bigDecimal1==null){
                bigDecimal1 = new BigDecimal("0.00");
            }
            if (bigDecimal==null){
                bigDecimal = new BigDecimal("0.00");
            }
            Double aDouble = merchantWithdrawService.selectAlreadyCash(martId); //查询已提现总额
            if (aDouble==null){
                aDouble=0.00;
            }
            String allMoney = String.valueOf(merchantWithdrawService.selectByMartId(martId));
            BigDecimal v = new BigDecimal(0);
            if(allMoney!="null"){
                v = new BigDecimal(allMoney);
            }

            BigDecimal a = bigDecimal.subtract(v);
            double c = a.doubleValue();
            BigDecimal totalCash =  bigDecimal.add(bigDecimal1);
            merchantEntity.setTotalCash(totalCash.doubleValue());
            merchantEntity.setAlreadyCash(aDouble);
            merchantEntity.setNotCash(c);
            merchantEntity.setPointMoney(bigDecimal1.doubleValue());
            merchantEntity.setWartCash(wartcash);
            merchantService.updateById(merchantEntity);
            Map map = new HashMap();

            map.put("alead_cash", aDouble);//已提现
            map.put("not_cash", c);//未体现
            map.put("wart_cash",wartcash);//审核中
            return new Result().ok(map);
    }
        return new Result();
}
    @CrossOrigin
    @Login
    @PutMapping("agreeYes")
    @ApiOperation("同意提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "审核人", paramType = "query", required = true, dataType="long")
    })
    public Result agreeYes(@RequestParam long id,@RequestParam long verify){
        merchantWithdrawService.verify(id,verify,WithdrawEnm.STATUS_AGREE_YES.getStatus(),"同意提现",new Date());
        return new Result().ok("提现成功");
    }
    @CrossOrigin
    @Login
    @PutMapping("agreeNo")
    @ApiOperation("拒绝提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verifyReason", value = "拒绝原因", paramType = "query", required = true, dataType="String"),
            @ApiImplicitParam(name = "verify", value = "审核人", paramType = "query", required = true, dataType="long")
    })
    public Result agreeNo(@RequestParam long id,@RequestParam long verify,@RequestParam String verifyReason){
        merchantWithdrawService.verify(id,verify,WithdrawEnm.STATUS_AGREE_NO.getStatus(),verifyReason,new Date());
        return new Result().ok("提现成功");
    }


    @CrossOrigin
    @Login
    @PutMapping("selectWithStatus")
    @ApiOperation("查询可提现状态")
    public Result selectWithStatus(){
        String s = merchantWithdrawService.selectWithStatus();

        return new Result().ok(s);
    }

    public Date paseYMD(Date date) throws ParseException {
        SimpleDateFormat ymdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

        return ymd.parse(ymd.format(date)+" 00:00:00");
    }

    /**
     * 根据 条件查询所有提现信息列表
     * @return
     */
    @CrossOrigin
    @Login
    @GetMapping("/list_sum")
    @ApiOperation(value = "分类提现列表与汇总",tags = "按不同方式显示提现记录和汇总信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="merchantId",value = "商户id",dataType = "long",defaultValue = "0",paramType = "query",required = false),
            @ApiImplicitParam(name = "startTime",value="开始时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name ="stopTime",value = "结束时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name="type",value = "1微信;2支付宝;3银行卡",dataType = "int",paramType = "query",required = false),
            @ApiImplicitParam(name="timeMode",value = "默认精确;1精度(天)",dataType = "int",paramType = "query",required=false),
            @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false)
    })
    public Result requireItems(Long merchantId,Date startTime,Date stopTime,
                               Integer type,Integer timeMode,Integer index,Integer itemNum) throws ParseException {

        QueryWrapper<MerchantWithdrawEntity> mweqw = new QueryWrapper<>();
        if(timeMode != null){
            if(timeMode == 1){
                if(startTime != null)
                    startTime = paseYMD(startTime);//ymdt转ymd

                if(stopTime != null)
                    stopTime = paseYMD(stopTime);//ymdt转ymd
            }
        }

        if(merchantId != null)
            mweqw.eq("merchant_id",merchantId);
        if(startTime != null && stopTime != null){
            mweqw.between("create_date",startTime,stopTime);
        }else if(startTime != null){
            mweqw.gt("create_date",startTime);//大于
        }else if(stopTime != null){

            mweqw.lt("create_date",stopTime);
        }
        if(type != null)
            mweqw.eq("type",type);

        PagePlus<MerchantWithdrawEntity> map = new PagePlus<MerchantWithdrawEntity>(index,itemNum);
        map.setCurrent(index);
        map.setSize(itemNum);
        IPage<MerchantWithdrawEntity> merchantWithdrawEntityIPage = merchantWithdrawDao.selectPage(map, mweqw);
        //merchantWithdrawEntityIPage.getRecords().forEach(System.out::println);

        //汇总
        if(merchantWithdrawEntityIPage == null)
            return null;

        //更新附加内容
        List<MerchantWithdrawEntity> records = merchantWithdrawEntityIPage.getRecords();
        Double wxSumMoney = 0d;
        Double  aliSumMoney = 0d;
        Double cardSumMoney = 0d;

        for(int i=0;i<records.size();i++){
            MerchantWithdrawEntity item = records.get(i);
            if(item.getType() != null){
                if(item.getType()== 1){
                    wxSumMoney = wxSumMoney+item.getMoney();
                }else if(item.getType()== 2){
                    aliSumMoney = aliSumMoney+item.getMoney();
                }else if(item.getType() == 3){
                    cardSumMoney = cardSumMoney+item.getMoney();
                }
            }
        }
        //System.out.println("总页数"+map.getTotal());
        map.setAliMoney(aliSumMoney);
        map.setCardMoney(cardSumMoney);
        map.setWxMoney(wxSumMoney);

        return new Result().ok(merchantWithdrawEntityIPage);
        //return new Result().ok(merchantWithdrawEntityIPage.getRecords());
    }

}
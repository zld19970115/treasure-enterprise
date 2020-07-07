package io.treasure.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.dao.MerchantDao;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantUserDTO;
import io.treasure.enm.Audit;
import io.treasure.enm.Common;
import io.treasure.entity.MerchantEntity;
import io.treasure.service.CategoryService;
import io.treasure.service.MerchantService;
import io.treasure.service.MerchantUserService;
import io.treasure.utils.SendSMSUtil;
import io.treasure.vo.PagePlus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.util.*;


/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@RestController
@RequestMapping("/merchant")
@Api(tags="商户表")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantUserService merchantUserService;
    //店铺分类
    @Autowired
    private CategoryService categoryService;
    @Autowired(required = false)
    private MerchantDao merchantDao;
    @Autowired
    private SMSConfig smsConfig;
    @CrossOrigin
    //@Login
    @GetMapping("page")
    @ApiOperation("列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name ="merchantId", value = "id", paramType = "query", dataType="String"),
            @ApiImplicitParam(name ="name", value = "店铺名称", paramType = "query", dataType="String"),
            @ApiImplicitParam(name ="mobile", value = "手机号码", paramType = "query", dataType="String"),
            @ApiImplicitParam(name ="categoryId", value = "经营类别", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Common.STATUS_DELETE.getStatus()+"");
        PageData<MerchantDTO> page = merchantService.page(params);
        return new Result<PageData<MerchantDTO>>().ok(page);
    }
    @CrossOrigin
    //@Login
    @GetMapping("getById")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result<MerchantDTO> get(@RequestParam  Long id){
        MerchantDTO data = merchantService.get(id);
        //查询经营类型
        String categoryId=data.getCategoryid();
        List<Long> cateId=new ArrayList<Long>();
        if(StringUtils.isNotBlank(categoryId)){
            String[] cateIds=categoryId.split(",");
            for(int i=0;i<cateIds.length;i++){
                cateId.add(Long.parseLong(cateIds[i]));
            }
            data.setCategoryList(categoryService.getListById(cateId));
        }

        //查询二级分类
        String categoryTwoId=data.getCategoryidtwo();
        List<Long> cateTwoId=new ArrayList<Long>();
        if(StringUtils.isNotBlank(categoryTwoId)){
            String[] cateIds=categoryTwoId.split(",");
            for(int i=0;i<cateIds.length;i++){
                cateTwoId.add(Long.parseLong(cateIds[i]));
            }
            data.setCategoryTwoList(categoryService.getListById(cateTwoId));
        }
//        MerchantQrCodeEntity merchantQrCodeEntity = merchantQrCodeService.getMerchantQrCodeByMerchantId(Long.parseLong(id));
//        if(merchantQrCodeEntity!=null){
//            data.setQrUrl(merchantQrCodeEntity.getQrUrl());
//        }


        return new Result<MerchantDTO>().ok(data);
    }
    @CrossOrigin
    @Login
    @PostMapping("save")
    @ApiOperation("保存")
    public Result save(@RequestBody MerchantDTO dto){
        //效验数据
       // ValidatorUtils.validateEntity(dto);
        //根据商户名称、身份证号查询商户信息
        MerchantEntity flag = merchantService.getByName(dto.getName(),9);
        if(null!=flag){
            return new Result().error("该商户您已经注册过了！");
        }

        dto.setStatus(Common.STATUS_CLOSE.getStatus());
        dto.setCreateDate(new Date());
        dto.setAuditstatus(Audit.STATUS_NO.getStatus());
        merchantService.save(dto);
        //修改创建者的商户信息
        MerchantUserDTO user=merchantUserService.get(dto.getCreator());
        //根据商户名称、身份证号查询商户信息
        MerchantEntity  entity= merchantService.getByName(dto.getName(),9);
        String merchantId=user.getMerchantid();
        user.setMerchantid(String.valueOf(entity.getId()));
        merchantUserService.update(user);
        String mobile = merchantService.selectOfficialMobile();
        SendSMSUtil.MerchantsSettlement(mobile, dto.getName(), smsConfig);
        return new Result().ok(entity);
    }
    @CrossOrigin
    @Login
    @PutMapping("edit")
    @ApiOperation("修改")
    public Result update(@RequestBody MerchantDTO dto){
        //效验数据s
      //  ValidatorUtils.validateEntity(dto);
        MerchantDTO entity=merchantService.get(dto.getId());
        if(!entity.getName().equals(dto.getName())){
            //根据修改的名称和身份账号查询
            MerchantEntity  merchant= merchantService.getByName(dto.getName(),Common.STATUS_DELETE.getStatus());
            if(null!=merchant){
                return new Result().error("该商户您已经注册过了！");
            }
        }
//        if(!entity.getCards().equals(dto.getCards())){
//            //根据修改的名称和身份账号查询
//            MerchantEntity  merchant= merchantService.getByCards(dto.getCards(),Common.STATUS_DELETE.getStatus());
//            if(null!=merchant){
//                return new Result().error("该商户您已经注册过了！");
//            }
//        }
        dto.setStatus(Common.STATUS_ON.getStatus());
        dto.setUpdateDate(new Date());
        dto.setAuditstatus(Audit.STATUS_NO.getStatus());
        merchantService.update(dto);
        return new Result();
    }
    @CrossOrigin
    @Login
    @PutMapping("updateBasic")
    @ApiOperation("修改店铺名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long"),
            @ApiImplicitParam(name = "headurl", value ="店铺头像", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "name", value = "店铺名称", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "brief", value = "店铺简介", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "log", value = "店铺经度", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "lat", value = "店铺纬度", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "address", value = "店铺地址", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "cards", value = "身份证号码", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "businesslicense", value = "营业执照", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "monetary", value = "平均消费", paramType = "query", required = true, dataType = "double"),
            @ApiImplicitParam(name = "updater", value = "修改者", paramType = "query", required = true, dataType = "long"),
            @ApiImplicitParam(name = "idcardFrontImg", value = "身份证正面照", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "idcardBackImg", value = "身份证反面照", paramType = "query", required = true, dataType = "String")
    })
    public Result updateBasic(@ApiIgnore @RequestParam Map<String, Object> params){
        String id=(String)params.get("id");
        String name=(String)params.get("name");
        MerchantDTO entity=merchantService.get(Long.parseLong(id));
        if(null!=entity){
            if(!entity.getName().equals(name)){
                //根据修改的名称和身份账号查询
                MerchantEntity  merchant= merchantService.getByName(name,Common.STATUS_DELETE.getStatus());
                if(null!=merchant){
                    return new Result().error("该商户您已经注册过了！");
                }
            }
            String headurl=(String)params.get("headurl");
            String brief=(String)params.get("brief");
            String log=(String)params.get("log");
            String lat=(String)params.get("lat");
            String address=(String)params.get("address");

            String businesslicense=(String)params.get("businesslicense");
            String cards=(String)params.get("cards");
            String monetary=(String) params.get("monetary");
            String updater=(String)params.get("updater");
            String idcardBackImg=(String)params.get("idcardBackImg");
            String idcardFrontImg=(String)params.get("idcardFrontImg");

            entity.setHeadurl(headurl);
            entity.setName(name);
            entity.setBrief(brief);
            entity.setLog(log);
            entity.setLat(lat);
            entity.setAddress(address);
            entity.setCards(cards);
            entity.setBusinesslicense(businesslicense);
            entity.setMonetary(Double.parseDouble(monetary));
            entity.setUpdateDate(new Date());
            entity.setUpdater(Long.parseLong(updater));
            entity.setIdcardBackImg(idcardBackImg);
            entity.setIdcardFrontImg(idcardFrontImg);
            entity.setStatus(1);
            merchantService.update(entity);
            return new Result();
        }else{
            return new Result().error("记录不存在！");
        }
    }
    @CrossOrigin
    @Login
    @PutMapping("updateHourse")
    @ApiOperation("修改店铺开店、闭店时间和联系电话")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long"),
            @ApiImplicitParam(name = "businesshours", value = "营业时间", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "closeshophours", value = "关店时间", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "tel", value = "联系电话", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "updater", value = "修改者", paramType = "query", required = true, dataType = "long"),
            @ApiImplicitParam(name = "depost", value = "押金", paramType = "query", required = true, dataType = "double")
    })
    public Result updateHourse(@RequestParam  long id,@RequestParam String businesshours,@RequestParam String closeshophours,
                               @RequestParam String tel,@RequestParam long updater,@RequestParam double depost){
        MerchantDTO entity=merchantService.get(id);
        entity.setBusinesshours(businesshours);
        entity.setCloseshophours(closeshophours);
        entity.setTel(tel);
        entity.setUpdateDate(new Date());
        entity.setUpdater(updater);
        entity.setDepost(depost);
        merchantService.update(entity);
        return new Result();
    }
    @CrossOrigin
    @Login
    @PutMapping("updateCategory")
    @ApiOperation("修改店铺类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long"),
            @ApiImplicitParam(name = "categoryid", value = "店铺一级分类", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "categoryidtwo", value = "店铺二级分类", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "updater", value = "修改者", paramType = "query", required = true, dataType = "long")
    })
    public Result updateCategory(@RequestParam long id,@RequestParam String categoryid,@RequestParam String categoryidtwo,@RequestParam long updater){
        MerchantDTO entity=merchantService.get(id);
        entity.setCategoryid(categoryid);
        entity.setCategoryidtwo(categoryidtwo);
        entity.setUpdateDate(new Date());
        entity.setUpdater(updater);
        merchantService.update(entity);
        return new Result();
    }
    @CrossOrigin
    @Login
    @DeleteMapping
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result delete(@RequestParam Long id){
        merchantService.remove(id,Common.STATUS_DELETE.getStatus());
        return new Result();
    }
    @CrossOrigin
    @Login
    @PutMapping("closeShop")
    @ApiOperation("闭店")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result closeShop(@RequestParam Long id){
        merchantService.closeShop(id,Common.STATUS_CLOSE.getStatus());
        return new Result();
    }


    public boolean isAllowOpening(MerchantEntity merchantEntity){

        String name = merchantEntity.getName()+"xx";
        String headurl = merchantEntity.getHeadurl()+"xx";
        String log = merchantEntity.getLog()+"xx";
        String lat = merchantEntity.getLat()+"xx";
        String businesshours = merchantEntity.getBusinesshours()+"xx";
        String tel = merchantEntity.getTel()+"xx";
        String categoryid = merchantEntity.getCategoryid()+"xx";

        if(name.length()<4||name.equals("nullxx")
                || headurl.length()<4||headurl.equals("nullxx")
                ||log.equals("nullxx")|| log.length()<4
                || lat.equals("nullxx")||lat.length()<4
                ||businesshours.length()<4||businesshours.equals("nullxx")
                || tel.length()<4 ||tel.equals("nullxx")
                || categoryid.length()<4||categoryid.equals("nullxx"))
            return false;
        return true;
    }
    public List<String>  nullFields(MerchantEntity merchantEntity){
        String name = merchantEntity.getName()+"xx";
        String headurl = merchantEntity.getHeadurl()+"xx";
        String log = merchantEntity.getLog()+"xx";
        String lat = merchantEntity.getLat()+"xx";
        String businesshours = merchantEntity.getBusinesshours()+"xx";
        String tel = merchantEntity.getTel()+"xx";
        String categoryid = merchantEntity.getCategoryid()+"xx";
        //Double monetary = merchantEntity.getMonetary();

        List<String> res = new ArrayList<>();
        //name.equals("") ||headurl.equals("")|| log.equals("")||lat.equals("")||businesshours.equals("")||tel.equals("")|| categoryid.equals("")||
        if(name.length()<4 ||name.equals("nullxx"))
            res.add("店铺名称");
        if(headurl.length()<4||headurl.equals("nullxx"))
            res.add("店铺logo");
        if( log.length()<4||log.equals("nullxx")|| lat.length()<4||lat.equals("nullxx"))
            res.add("经纬度");
        if(businesshours.length()<4||businesshours.equals("nullxx"))
            res.add("营业时间");
        if(tel.length()<4||tel.equals("nullxx"))
            res.add("联系电话");
        if(categoryid.length()<4||categoryid.equals("nullxx"))
            res.add("所属类别一级");

        return res;

    }

    @CrossOrigin
    @Login
    @PutMapping("setUpShop")
    @ApiOperation("营业中")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result setUpShop(@RequestParam Long id){
            Map<String,String> nullFieldMap = new HashMap<>();

            //检查商户信息必填项是否为空
            MerchantEntity merchantEntity = merchantService.selectById(id);
            if(merchantEntity != null){
                if(isAllowOpening(merchantEntity)){
                    merchantService.closeShop(id,Common.STATUS_ON.getStatus());
                    return new Result();
                }else{
                    //有非空字段
                    List<String> res = new ArrayList<>();
                    res = nullFields(merchantEntity);
                    Result result = new Result().error("部分字段不能为空");
                    result.setData(res);
                    return result;
                }

            }else{
                merchantService.closeShop(id,Common.STATUS_ON.getStatus());
                return new Result();
            }
    }
    @CrossOrigin
    @Login
    @PutMapping("inserZFB")
    @ApiOperation("绑定商户支付宝")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "商户id", paramType = "query", required = true, dataType = "string"),
            @ApiImplicitParam(name = "ali_account_number", value = "收款支付宝账户", paramType = "query", required = false, dataType = "string"),
            @ApiImplicitParam(name = "ali_account_realname", value = "支付宝收款人真实姓名", paramType = "query", required = false, dataType = "string")
    })
    public Result inserZFB(@ApiIgnore @RequestParam Map<String, Object> params){
        String martId = (String) params.get("martId");
        MerchantEntity merchantEntity = merchantService.selectById(martId);
        if (merchantEntity==null){
            return new Result().ok("没有该商户");
        }
        if (merchantEntity.getAliAccountNumber()!=null||merchantEntity.getAliAccountRealname()!=null){
            return new Result().ok("1");//已绑定支付宝
        }
       String ali_account_number = (String) params.get("ali_account_number");
        String ali_account_realname = (String) params.get("ali_account_realname");
        if (ali_account_number!=null&&ali_account_realname!=null){
            merchantEntity.setAliAccountNumber(ali_account_number);
            merchantEntity.setAliAccountRealname(ali_account_realname);
            merchantService.updateById(merchantEntity);
            return new Result().ok("绑定成功");
        }


        return new Result().ok("0");//未绑定支付宝
    }
    @CrossOrigin
    @Login
    @PutMapping("inserWX")
    @ApiOperation("绑定商户微信")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "商户id", paramType = "query", required = true, dataType = "string"),
            @ApiImplicitParam(name = "wx_account_openid", value = "收款微信openid", paramType = "query", required = false, dataType = "string"),
            @ApiImplicitParam(name = "status", value = "APP/VX", paramType = "query", required = true, dataType = "string")
    })
    public Result inserWX(@ApiIgnore @RequestParam Map<String, Object> params){
        String martId = (String) params.get("martId");
        MerchantEntity merchantEntity = merchantService.selectById(martId);
        if (merchantEntity==null){
            return new Result().ok("没有该商户");
        }
        if (StringUtils.isNotBlank(merchantEntity.getWxAccountOpenid())){
            return new Result().ok("1");//已绑定微信
        }
        String wx_account_openid = (String) params.get("wx_account_openid");
        String status = (String) params.get("status");
        if (wx_account_openid!=null){
            if ("VX".equals(status) || status == "VX"){
                merchantEntity.setWxStatus(1);
            }else {
                merchantEntity.setWxStatus(2);
            }
            merchantEntity.setWxAccountOpenid(wx_account_openid);
            merchantService.updateById(merchantEntity);
            return new Result().ok(merchantEntity.getMobile());
        }
        return new Result().ok("0");//未绑定微信
    }
    @CrossOrigin
    @Login
    @PutMapping("deleltWX")
    @ApiOperation("解除绑定商户微信")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "商户id", paramType = "query", required = true, dataType = "string")
    })
    public Result deleltWX(@ApiIgnore @RequestParam Map<String, Object> params){
        String martId = (String) params.get("martId");
        MerchantEntity merchantEntity = merchantService.selectById(martId);
        if (merchantEntity==null){
            return new Result().error("没有该商户");
        }
        merchantService.updateWX(martId);
        return new Result().ok("解绑成功");//未绑定微信
    }
    @Login
    @GetMapping("deleltWXByCode")
    @ApiOperation("商户解绑验证码(返回验证码)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tel", value = "手机号", required = true, paramType = "query")
    })
    public Result register(String tel) {
        Result result = SendSMSUtil.sendCodeFordeletWx(tel, smsConfig);
        return result;
    }

    @CrossOrigin
    @Login
    @PutMapping("selectWx")
    @ApiOperation("查询商家是否绑定微信提现openid")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "商户id", paramType = "query", required = true, dataType = "string")
    })
    public Result selectWx(@ApiIgnore @RequestParam Map<String, Object> params){
        String martId = (String) params.get("martId");
        MerchantEntity merchantEntity = merchantService.selectById(martId);
        Map map = new HashMap();
        if (merchantEntity==null){
            return new Result().error("没有该商户");
        }
      if (merchantEntity.getWxAccountOpenid()!=null){
          map.put("status",1);
          map.put("martId",merchantEntity.getMobile());
          return new Result().ok(map);//绑定微信
      }else {
          map.put("status",2);
          return new Result().ok(map);//未绑定微信
      }

    }


    @GetMapping("getMerchantByCategoryId")
    @ApiOperation("查询某分类下店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "longitude", value = "顾客的经度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "latitude", value = "顾客的纬度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "categoryId", value = "分类ID", paramType = "query",required=true, dataType="String")
    })
    public Result<PageData<MerchantDTO>> queryClassifyMerchant(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantDTO> page = merchantService.getMerchantByCategoryId(params);
        return new Result<PageData<MerchantDTO>>().ok(page);
    }
    @GetMapping("merchantSorting")
    @ApiOperation("商户排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
    })
    public Result<PageData<MerchantDTO>> sorting(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantDTO> page = merchantService.merchantSortingPage(params);
        return new Result<PageData<MerchantDTO>>().ok(page);
    }
    /**
     * 根据 条件查询所有提现信息列表
     * @return
     */
    @CrossOrigin
    //@Login
    @GetMapping("/list_sum")
    @ApiOperation(value = "分类提现列表与汇总",tags = "按不同方式显示提现记录和汇总信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="id",value = "商户id",dataType = "long",defaultValue = "0",paramType = "query",required = false),
            @ApiImplicitParam(name = "startTime",value="开始时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name ="stopTime",value = "结束时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name="total_cash",value = "总收入大于",dataType = "int",paramType = "query",required = false),
            @ApiImplicitParam(name="already_cash",value = "已提现",dataType = "int",paramType = "query",required = false),
            @ApiImplicitParam(name="wart_cash",value = "审核中",dataType = "int",paramType = "query",required = false),
            @ApiImplicitParam(name="not_cash",value = "未提现",dataType = "int",paramType = "query",required=false),
            @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false)
    })
    public Result requireItems(Long id,Date startTime,Date stopTime,
                               Integer total_cash,Integer already_cash,Integer wart_cash,Integer not_cash,Integer index,Integer itemNum) throws ParseException {

        QueryWrapper<MerchantEntity> mweqw = new QueryWrapper<>();

        if(id != null)
            mweqw.eq("id",id);
        if(startTime != null && stopTime != null){
            mweqw.between("create_date",startTime,stopTime);
        }else if(startTime != null){
            mweqw.gt("create_date",startTime);//大于
        }else if(stopTime != null){

            mweqw.lt("create_date",stopTime);
        }
        if(total_cash != null)
            mweqw.gt("total_cash",total_cash);
        if(already_cash != null)
            mweqw.gt("already_cash",already_cash);
        if(wart_cash != null)
            mweqw.gt("wart_cash",wart_cash);
        if(not_cash !=  null)
            mweqw.gt("not_cash",not_cash);


        PagePlus<MerchantEntity> map = new PagePlus<MerchantEntity>(index,itemNum);
        map.setCurrent(index);
        map.setSize(itemNum);
        IPage<MerchantEntity> merchantEntityIPage = merchantDao.selectPage(map, mweqw);

        //汇总
        if(merchantEntityIPage == null)
            return null;

        //更新附加内容
        List<MerchantEntity> records = merchantEntityIPage.getRecords();
        Double total = 0d;
        Double  already = 0d;
        Double not = 0d;

        for(int i=0;i<records.size();i++){
            MerchantEntity item = records.get(i);
            total += item.getTotalCash();
            already += item.getAlreadyCash();
            not += item.getNotCash();
        }
        //System.out.println("总页数"+map.getTotal());
        map.setTotal_cash(total);
        map.setAlready_cash(already);
        map.setNot_cash(not);

        return new Result().ok(merchantEntityIPage);
        //return new Result().ok(merchantWithdrawEntityIPage.getRecords());
    }

    @GetMapping("auditMerchantStatus")
    @ApiOperation("商户审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query", required = true, dataType="long")
    })
    public Result<Integer> auditMerchantStatus(@ApiIgnore @RequestParam Long id){
        return new Result<Integer>().ok(merchantService.AuditMerchantStatus(id));
    }
    @GetMapping("selectMerchantStatus")
    @ApiOperation("查询商户开闭店状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "商户id", paramType = "query", required = true, dataType = "long")
    })
    public Result<Integer> selectMerchantStatus(@ApiIgnore @RequestParam Long martId){
        MerchantEntity merchantEntity = merchantService.selectById(martId);
        if (merchantEntity!=null){
            return new Result<Integer>().ok(merchantEntity.getStatus());
        }else {
        return new Result<Integer>().error("此商户不存在");
        }
    }
    @GetMapping("selectByUserlongitudeandlatitude")
    @ApiOperation("通过用户经纬度查询可外卖商家")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "longitude", value = "顾客的经度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "latitude", value = "顾客的纬度", paramType = "query",required=true, dataType="String")
    })
    public Result<PageData<MerchantDTO>> selectByUserlongitudeandlatitude(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantDTO> page = merchantService.selectByUserlongitudeandlatitude(params);
        return new Result<PageData<MerchantDTO>>().ok(page);
    }

    @GetMapping("getOutside")
    @ApiOperation("开启外卖功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deliveryArea", value = "配送范围(单位米)", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "distribution", value = "配送方式 0 ----商家配送 1 ---由平台配送", paramType = "query",required=true, dataType="int"),
            @ApiImplicitParam(name = "martId", value = "商户id", paramType = "query", required = true, dataType = "long")
    })
    public Result getOutside(@RequestParam String deliveryArea ,@RequestParam int distribution,@RequestParam long martId){

        return new Result().ok(merchantService.getOutside(deliveryArea,distribution,martId));
    }

    @GetMapping("updateRecommend")
    @ApiOperation("开启外卖功能")
    public Result updateRecommend(@RequestParam Long id ,@RequestParam Integer recommend){
        return new Result().ok(merchantService.updateRecommend(id, recommend));
    }

    @CrossOrigin
    @GetMapping("delPC")
    @ApiOperation("删除PC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result delPC(@RequestParam Long id){
        merchantService.deleteById(id);
        return new Result();
    }

}

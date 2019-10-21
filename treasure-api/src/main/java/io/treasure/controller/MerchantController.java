package io.treasure.controller;


import io.swagger.annotations.*;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantUserDTO;
import io.treasure.enm.Audit;
import io.treasure.enm.Common;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantUserEntity;
import io.treasure.service.CategoryService;
import io.treasure.service.MerchantService;
import io.treasure.service.MerchantUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


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
    @CrossOrigin
    @Login
    @GetMapping("page")
    @ApiOperation("列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name ="merchantId", value = "id", paramType = "query", dataType="String"),
            @ApiImplicitParam(name ="name", value = "店铺名称", paramType = "query", dataType="String"),
    })
    public Result<PageData<MerchantDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Common.STATUS_DELETE.getStatus()+"");
        PageData<MerchantDTO> page = merchantService.page(params);
        return new Result<PageData<MerchantDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
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
        MerchantEntity flag = merchantService.getByNameAndCards(dto.getName(),dto.getCards());
        if(null!=flag){
            return new Result().error("该商户您已经注册过了！");
        }

        dto.setStatus(Common.STATUS_ON.getStatus());
        dto.setCreateDate(new Date());
        dto.setAuditstatus(Audit.STATUS_NO.getStatus());
        merchantService.save(dto);
        //修改创建者的商户信息
        MerchantUserDTO user=merchantUserService.get(dto.getCreator());
        //根据商户名称、身份证号查询商户信息
        MerchantEntity  entity= merchantService.getByNameAndCards(dto.getName(),dto.getCards());
        String merchantId=user.getMerchantid();
        if(StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)){
            user.setMerchantid(merchantId+","+entity.getId());
        }else{
            user.setMerchantid(String.valueOf(entity.getId()));
        }
        merchantUserService.update(user);
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
            entity.setBusinesslicense(cards);
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
    @CrossOrigin
    @Login
    @PutMapping("setUpShop")
    @ApiOperation("营业中")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result setUpShop(@RequestParam Long id){
        merchantService.closeShop(id,Common.STATUS_ON.getStatus());
        return new Result();
    }

}
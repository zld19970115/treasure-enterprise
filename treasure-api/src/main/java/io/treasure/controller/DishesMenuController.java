package io.treasure.controller;

import cn.hutool.core.util.PinyinUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dao.GoodDao;
import io.treasure.dto.MerchantQueryDto;
import io.treasure.service.DishesMenuService;
import io.treasure.vo.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/dishes_menu")
@Api(tags="菜单与查询")
public class DishesMenuController {

    @Autowired
    private DishesMenuService dishesMenuService;

    @Autowired(required = false)
    private GoodDao goodDao;

    //@CrossOrigin
    //@Login
    @GetMapping("mlist")
    @ApiOperation("菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码，从1开始", paramType = "query",defaultValue = "1",required = false, dataType="Integer") ,
            @ApiImplicitParam(name = "num", value = "每页显示记录数", paramType = "query",defaultValue = "10",required = false, dataType="Integer") ,
            @ApiImplicitParam(name="inList",value="list-列表;空-search",paramType = "query",defaultValue = "list",required = false,dataType = "String"),
            @ApiImplicitParam(name="startLetter",value="关键字",paramType = "query",dataType = "String")
    })
    public Result dishesMenu(@ApiIgnore @RequestParam Map<String, Object> params){
        String startLetter = params.get("startLetter")+"";
        if(startLetter == null){
            startLetter = "'1'";
        }else{
            startLetter = "'"+startLetter+"'";
        }
        String inList = params.get("inList")+"";
        int page = Integer.parseInt(params.get("page")+"");
        int num = Integer.parseInt(params.get("num")+"");
        if(page >0)
            page --;
        if(!inList.equals("list")){
           inList = null;
        }
        List<SimpleDishesVo> list = dishesMenuService.getList(startLetter, page, num, inList);
        if(inList != null){
            return getOrderGroup(list);
        }
        return new Result().ok(list);
    }


    @GetMapping("mcount")
    @ApiOperation("菜单数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name="startLetter",value="关键字",paramType = "query",dataType = "String")
    })
    public Result dishesMenuCount(@ApiIgnore @RequestParam Map<String, Object> params){
        String startLetter = params.get("startLetter")+"";
        if(startLetter == null){
            startLetter = "'1'";
        }else{
            startLetter = "'"+startLetter+"'";
        }


        Integer integer = goodDao.selectDishesMenuCount(startLetter).size();

        return new Result().ok(integer);
    }


    public Result getOrderGroup(List<SimpleDishesVo> gList){

        List<GoodsGroup> res = new ArrayList<>();
        String tmpName = null;
        for(int i=97;i<123;i++){
            for(int j=0;(j<gList.size()+2);j++){
                if(gList.size()>0){
                    String gname = gList.size()>0?gList.get(0).getName():null;
                    int nameValue = gname.equals(null)?200:PinyinUtil.getFirstLetter(gname.charAt(0));
                    GoodsGroup goodsGroup = null;
                    if(i>=nameValue){
                        if(res.size()==(i-96)){
                            //当前对象已经存在
                            goodsGroup = initGoodsGroup(res.get(i - 97), (char) i + "", gList.get(0));
                            res.set((i-97),goodsGroup);
                        }else{
                            //当前对象不存在
                            goodsGroup = initGoodsGroup(null, (char) i + "", gList.get(0));
                            res.add(goodsGroup);
                        }
                        //System.out.println("当前值："+((char)i)+gList.get(0));
                        if(gList.size()>0)
                            gList.remove(0);

                    }
                }

                tmpName = (char)i+"";
            }

            if(res.size() < (i-96)){
                GoodsGroup goodsGroup1 = initNullGoodsGroup((char) i + "");
                res.add(goodsGroup1);
            }
        }
        List<GoodsGroup> result = new ArrayList<>();
        List<String> indexs = new ArrayList<>();
        for(int i = 0;i<res.size();i++){
            if(res.get(i).getList().size()>0){
                indexs.add(res.get(i).getName());
                result.add(res.get(i));
            }
        }
        DishesOrderComboVo  dishesOrderComboVo = new DishesOrderComboVo();
        dishesOrderComboVo.setResult(result);
        dishesOrderComboVo.setIndexs(indexs);

        return new Result().ok(dishesOrderComboVo);
    }

    public GoodsGroup initNullGoodsGroup(String name){

        GoodsGroup  gg = new GoodsGroup();
        gg.setName(name.toUpperCase());
        gg.setList(new ArrayList<SimpleDishesVo>());
        return gg;
    }

    public GoodsGroup initGoodsGroup(GoodsGroup gg,String name,SimpleDishesVo vo){
        if(gg == null)
            gg = new GoodsGroup();
        gg.setName(name.toUpperCase());

        List<SimpleDishesVo> list = gg.getList();
        if(list == null)
            list = new ArrayList<SimpleDishesVo>();
        list.add(vo);

        gg.setList(list);
        return gg;
    }

    @PostMapping("get_mchlist")
    @ApiOperation("点菜选饭店列表")
    public Result getMchList(@RequestBody MerchantQueryDto merchantQueryDto){
        List<String> dl = new ArrayList<>();
        String[] dishes = merchantQueryDto.getDishes();
        System.out.println("获得参数"+merchantQueryDto.toString());
        for(int i=0;i<dishes.length;i++){
            dl.add(dishes[i]);
        }

        merchantQueryDto.setRecordsCounter(dl.size());
        String s = toComboString(dl);
        merchantQueryDto.setNames(s);
        int page = merchantQueryDto.getPage();
        if(page>0){
            page --;
        }
        merchantQueryDto.setPage(page);
        System.out.println("请求内容如下："+merchantQueryDto.toString());

        Integer totalRecords = goodDao.selectMchCountViaWholePrice(merchantQueryDto);
        List<SpecifyMerchantVo> specifyMerchantVos = goodDao.selectMchViaWholePrice(merchantQueryDto);

        QueryMerchantVo queryMerchantVo = new QueryMerchantVo();
        queryMerchantVo.setSpecifyMerchantVos(specifyMerchantVos);
        queryMerchantVo.setTotalRecords(totalRecords);

        return new Result().ok(queryMerchantVo);
    }

    public String toComboString(List<String> list){
        StringBuilder sb = new StringBuilder();

        for(int i=0;i<list.size();i++){
            sb.append("'"+list.get(i)+"',");
        }

        String res = sb.toString();
        return res.substring(0,res.length()-1);
    }
    public String toListStyleString(String str){
        StringBuilder sb = new StringBuilder();
        if(str.contains(",")){
            String[] split = str.split(",");
            for(int i=0;i<split.length;i++){
                sb.append("'"+split[i]+"',");
            }
        }else{
            sb.append("'"+str+"',");
        }
        String res = sb.toString();
        return res.substring(0,res.length()-1);
    }

}

package io.treasure.controller;

import cn.hutool.core.util.PinyinUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dao.GoodDao;
import io.treasure.dto.MerchantQueryDto;
import io.treasure.jra.impl.DishesMenuJRA;
import io.treasure.service.DishesMenuService;
import io.treasure.utils.MyPingyInUtil;
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

    @Autowired
    private DishesMenuJRA dishesMenuJRA;

    @GetMapping("mlist_redis")
    @ApiOperation("菜单列表nosql")
    public Result dishesMenuRedis(){
        List<GoodsGroup> allList = dishesMenuJRA.getAllList();

        return new Result().ok(allList);
    }
    /*
    @GetMapping("mlist_redis_add")
    @ApiOperation("菜单列表nosql")
    @ApiImplicitParam(name="dname",value="菜名",paramType = "query",dataType = "String")
    public Result addDishesMenu(@ApiIgnore @RequestParam String dname){
        dishesMenuJRA.add(dname);
        return new Result().ok("成功");
    }

    @GetMapping("mlist_redis_update")
    @ApiOperation("菜单列表nosql")
    @ApiImplicitParams({
            @ApiImplicitParam(name="dname",value="菜名",paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="nname",value="菜名",paramType = "query",dataType = "String")
    })

    public Result updateDishesMenu(@ApiIgnore @RequestParam String dname,String nname){
        dishesMenuJRA.update(dname,nname);
        return new Result().ok("成功");
    }
    */

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

    public List<Integer> updateMap(List<SimpleDishesVo> gList,int step){
        int differentCounter = 0;         //不同计数器
        int diffFlag = 0;                //不同值出现的位置
        boolean isSame = true;           //与前一次检测的值相同
        boolean isContain = false;       //是否包含内容
        int lastIndex = 0;               //上一次检测的位置
        int checkIndex = 0;              //检测位置
        boolean scanDetail = false;      //详细扫描
        String sLetter = "a";//97a-->122z
        String currentChar = null;
        Map<String,String> map = new HashMap<>();

        for(int i=0;i<gList.size();i++){
            if((gList.size())>=checkIndex+step){
                if(isSame){
                    if(differentCounter <= 0){
                        checkIndex= checkIndex+step;
                    }else{
                        differentCounter--;
                    }
                }else{

                    if(checkIndex%step==0 && diffFlag != checkIndex ){
                        diffFlag = checkIndex;
                        differentCounter = step-1;
                        if(checkIndex>step){
                            checkIndex=checkIndex+1-step;
                        }
                    }
                    else
                    {
                        //更新不同数组索引值
                        if(isContain){
                            map.put(currentChar,lastIndex+""+checkIndex);
                        }
                        checkIndex++;
                        differentCounter--;
                    }
                }
            }else{
                //索引保护
                if(checkIndex < (gList.size()-1)){

                    //更新不同数组索引值


                    checkIndex++;
                }else{
                    break;
                }
            }
            System.out.println("check_pos:"+checkIndex);
            char c = gList.get(checkIndex).getName().charAt(0);
            String letter = MyPingyInUtil.toPyString(c+"",true);


        }

        /*
            select group_concat(name)  from ct_good
            where status != 1
         */

        return null;
    }

    public Result getOrderGroup(List<SimpleDishesVo> gList){

        List<GoodsGroup> res = new ArrayList<>();
        String tmpName = null;
        GoodsGroup numbicGoodGroup = null;

        updateMap(gList,20);
        //检查数字分组
        /*
        for(int n=0;n<gList.size();n++){
            String gname = gList.size()>0?gList.get(0).getName():null;
            int nameValue = gname.equals(null)?200:PinyinUtil.getFirstLetter(gname.charAt(0));

            if(nameValue <= 59){
                if(numbicGoodGroup == null){
                    numbicGoodGroup = new GoodsGroup();
                    numbicGoodGroup.setName("#");
                    List<SimpleDishesVo> list0 = new ArrayList<SimpleDishesVo>();
                    numbicGoodGroup.setList(list0);
                }
                List<SimpleDishesVo> list1 = numbicGoodGroup.getList();
                list1.add(gList.get(n));
                numbicGoodGroup.setList(list1);
                gList.remove(n);
            }else{
                break;
            }
        }
        */
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

        //添加数字分组
        //if(numbicGoodGroup != null){
         //   res.add(numbicGoodGroup);
        //}
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

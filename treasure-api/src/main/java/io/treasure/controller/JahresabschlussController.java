package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.utils.Result;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.service.JahresabschlussService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Jahresabschluss")
@Api(tags="商户端财务报表")
public class JahresabschlussController {
    @Autowired
    private io.treasure.service.JahresabschlussService JahresabschlussService;

    @Login
    @GetMapping("getJahresabschluss")
    @ApiOperation("获取财务报表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType ="query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "startTime1", value = "开始日期", paramType = "query", required = false, dataType="String"),
            @ApiImplicitParam(name = "endTime1", value = "截止日期", paramType = "query", required = false, dataType="String")
    })
    public Result getJahresabschluss(@ApiIgnore @RequestParam Map<String, Object> params) {
        String startTime1 = (String) params.get("startTime1");
        String endTime1 = (String) params.get("endTime1");
        List<GoodCategoryEntity> goodCategoryEntities = JahresabschlussService.selectCategory(params);
        for (GoodCategoryEntity goodCategoryEntity : goodCategoryEntities) {
            List<GoodDTO> goodDTOS = JahresabschlussService.selectByCategoeyid(goodCategoryEntity.getId());
            for (GoodDTO goodDTO : goodDTOS) {
                List<SlaveOrderDTO> slaveOrderDTOS = JahresabschlussService.selectBYgoodID(goodDTO.getId(),startTime1,endTime1);










            }




        }














//
//
//        BigDecimal Umsatz = new BigDecimal("0");
//        Map map = new HashMap();
//        for (GoodDTO goodCategoryEntity : goodCategoryEntities) {
//            Long goodCategoryId = goodCategoryEntity.getGoodCategoryId();
//            if (goodCategoryEntity.getGoodCategoryId()){
//
//            }
//            List<SlaveOrderDTO> slaveOrderDTOS = JahresabschlussService.selectBYgoodID(goodCategoryEntity.getId(),startTime1,endTime1);
//            BigDecimal a = new BigDecimal("0");
//            for (SlaveOrderDTO slaveOrderDTO : slaveOrderDTOS) {
//                BigDecimal quantity = slaveOrderDTO.getQuantity();
//                a = a.add(quantity);
//            }
//            Umsatz = Umsatz.add(a);
//            map.put("Umsatz",Umsatz);
//
//        }



     return new Result().ok(goodCategoryEntities);



    }

}

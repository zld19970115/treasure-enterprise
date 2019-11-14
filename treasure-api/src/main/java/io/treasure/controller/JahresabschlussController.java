package io.treasure.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.utils.Result;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantOrderDTO;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.GoodCategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import java.math.BigDecimal;
import java.util.ArrayList;
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
        List<MerchantOrderDTO> merchantOrderDTOS = JahresabschlussService.selectBymerchantId(params);
        List<MerchantWithdrawDTO> merchantWithdrawDTO = JahresabschlussService.selectBymerchantId2(params);

        List list = new ArrayList();
        for (GoodCategoryEntity goodCategoryEntity : goodCategoryEntities) {
            List<GoodDTO> goodDTOS = JahresabschlussService.selectByCategoeyid(goodCategoryEntity.getId());
            List a = new ArrayList();
            BigDecimal AllPayMoney = new BigDecimal("0");
            BigDecimal Alquantity = new BigDecimal("0");
            BigDecimal liyun = new BigDecimal("0.15");
            for (GoodDTO goodDTO : goodDTOS) {
                List<SlaveOrderDTO> slaveOrderDTOS = JahresabschlussService.selectBYgoodID(goodDTO.getId(),startTime1,endTime1);
                for (SlaveOrderDTO slaveOrderDTO : slaveOrderDTOS) {
                    BigDecimal payMoney = slaveOrderDTO.getPayMoney();
                    AllPayMoney=AllPayMoney.add(payMoney);
                    BigDecimal quantity = slaveOrderDTO.getQuantity();
                    Alquantity=Alquantity.add(quantity);
                }
            }
            BigDecimal multiply = AllPayMoney.multiply(liyun);
            BigDecimal subtract = AllPayMoney.subtract(multiply);
            a.add(goodCategoryEntity.getName());//类别名称
            a.add(Alquantity);//销量
            a.add(AllPayMoney);//交易金额
            a.add(subtract);//可提现金额
            a.add(multiply);//平台服务费
            list.add(a);
        }
        list.add(merchantOrderDTOS);
        list.add(merchantWithdrawDTO);
        return new Result().ok(list);
 }

}

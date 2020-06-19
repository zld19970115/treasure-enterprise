package io.treasure.controller;

import com.alipay.api.AlipayApiException;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.exception.RenException;


import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;

import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.entity.MerchantEntity;
import io.treasure.service.ApiMerchantWithdrawService;
import io.treasure.service.MerchantService;
import io.treasure.utils.SendSMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 提现表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-29
 */
@RestController
@RequestMapping("merchant/merchantwithdraw")
@Api(tags="提现表")
public class ApiMerchantWithdrawController {
    @Autowired
    private ApiMerchantWithdrawService merchantWithdrawService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private SMSConfig smsConfig;


    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantWithdrawDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.page(params);
        List<MerchantWithdrawDTO> merchantWithdrawDTOList = page.getList();
        int size = merchantWithdrawDTOList.size();
        for (int n = 0; n < size; n++) {
            MerchantWithdrawDTO merchantWithdrawDTO=merchantWithdrawDTOList.get(n);
            Long id=merchantWithdrawDTO.getMerchantId();
            MerchantDTO merchantDTO=merchantService.get(id);
            merchantWithdrawDTO.setMerchantInfo(merchantDTO);
        }
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<MerchantWithdrawDTO> get(@PathVariable("id") Long id){
        MerchantWithdrawDTO data = merchantWithdrawService.get(id);

        return new Result<MerchantWithdrawDTO>().ok(data);
    }

    @PutMapping("audit")
    @ApiOperation("审核")
    public Result audit(@RequestBody MerchantWithdrawDTO dto, HttpServletRequest request) throws AlipayApiException {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        Result result=merchantWithdrawService.audit(dto,request);
        LinkedHashMap<String, String> map=new LinkedHashMap<String,String>();
        MerchantEntity merchantDTO =  merchantService.selectById(dto.getMerchantId());
        map.put("code",merchantDTO.getName());
        map.put("money",dto.getMoney().toString());
        if(dto.getVerifyState()==2){
            map.put("state","已经通过");
            map.put("content","请及时查看");
        }else if(dto.getVerifyState()==3){
            map.put("state","没有通过");
            map.put("content",dto.getVerifyReason());
        }else {
            throw new RenException("审核异常！");
        }
//短信服务
//        AbstractSmsService service = SmsFactory.build();
//        if(service == null){
//            throw new RenException(ErrorCode.SMS_CONFIG);
//        }
//
////发送短信
//        String tel=merchantDTO.getMobile();
//        if(tel.length()!=11){
//            throw new RenException("手机号不正确！");
//        }
//        service.sendSms(tel,map,"聚宝科技","SMS_190792014");
        SendSMSUtil.sendmerchantWithdraw(merchantDTO.getMobile(),map, smsConfig);
        return result;
    }

    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody MerchantWithdrawDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        merchantWithdrawService.save(dto);

        return new Result();
    }
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody MerchantWithdrawDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        merchantWithdrawService.update(dto);

        return new Result();
    }
        @DeleteMapping
    @ApiOperation("删除")


    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        merchantWithdrawService.delete(ids);

        return new Result();
    }
//            @GetMapping("export")
//        @ApiOperation("导出")
//
//        public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
//            List<MerchantWithdrawDTO> list = merchantWithdrawService.list(params);
//
//            ExcelUtils.exportExcelToTarget(response, null, list, MerchantWithdrawExcel.class);
//        }

}

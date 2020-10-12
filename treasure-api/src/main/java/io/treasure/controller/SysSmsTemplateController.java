package io.treasure.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.dto.SysSmsTemplateDTO;
import io.treasure.service.SysSmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/smsTemplate")
@Api(tags="短息模板")
public class SysSmsTemplateController {

    @Autowired
    private SysSmsTemplateService service;

    @Autowired
    private SMSConfig smsConfig;

    @Login
    @PostMapping("/addSmsTem")
    @ApiOperation("新增模板")
    public Result addSmsTem(@RequestBody SysSmsTemplateDTO obj) {
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou", smsConfig.getAccessKeyId(), smsConfig.getAccessSecret()
        );
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("AddSmsTemplate");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("TemplateType", obj.getType()+"");
        request.putQueryParameter("TemplateName", obj.getName());
        request.putQueryParameter("TemplateContent", obj.getContent());
        request.putQueryParameter("Remark", obj.getRemark());
        try {
            CommonResponse response = client.getCommonResponse(request);
            JSONObject json = JSON.parseObject(response.getData());
            if(!json.getString("Code").equals("OK")) {
                return new Result().error();
            }
            obj.setCode(json.getString("TemplateCode"));
            obj.setTime(new Date());
            obj.setState(0);
            service.save(obj);
        } catch (ServerException e) {
            e.printStackTrace();
            return new Result().error();
        } catch (ClientException e) {
            e.printStackTrace();
            return new Result().error();
        }
        return new Result().ok("ok");
    }

    @Login
    @GetMapping("/delSmsTem")
    @ApiOperation("删除模板")
    public Result delSmsTem(String code,Long id) {
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou", smsConfig.getAccessKeyId(), smsConfig.getAccessSecret()
        );
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("DeleteSmsTemplate");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("TemplateCode", code);
        try {
            CommonResponse response = client.getCommonResponse(request);
            JSONObject json = JSON.parseObject(response.getData());
            if(!json.getString("Code").equals("OK")) {
                return new Result().error();
            }
            service.deleteById(id);
        } catch (ServerException e) {
            e.printStackTrace();
            return new Result().error();
        } catch (ClientException e) {
            e.printStackTrace();
            return new Result().error();
        }
        return new Result().ok("ok");
    }

    @Login
    @GetMapping("/querySmsTemplate")
    @ApiOperation("查询模板")
    public Result querySmsTemplate() {
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou", smsConfig.getAccessKeyId(), smsConfig.getAccessSecret()
        );
        IAcsClient client = new DefaultAcsClient(profile);
        Map<String, Object> map = new HashMap<>();
        map.put("limit",1000);
        map.put("page",1);
        List<SysSmsTemplateDTO> list = service.pageList(map).getList();
        for(SysSmsTemplateDTO info : list) {
            if (info.getState() != 1) {
                CommonRequest request = new CommonRequest();
                request.setSysMethod(MethodType.POST);
                request.setSysDomain("dysmsapi.aliyuncs.com");
                request.setSysVersion("2017-05-25");
                request.setSysAction("QuerySmsTemplate");
                request.putQueryParameter("RegionId", "cn-hangzhou");
                request.putQueryParameter("TemplateCode", info.getCode());
                try {
                    CommonResponse response = client.getCommonResponse(request);
                    JSONObject json = JSON.parseObject(response.getData());
                    if (json.getString("Code").equals("OK")) {
                        info.setState(json.getInteger("TemplateStatus"));
                        info.setReason(json.getString("Reason"));
                        service.update(info);
                    }
                } catch (ServerException e) {
                    e.printStackTrace();
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Result().ok("ok");
    }

    @Login
    @PostMapping("/updateSmsTemplate")
    @ApiOperation("修改模板")
    public Result updateSmsTemplate(@RequestBody SysSmsTemplateDTO dto) {
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou", smsConfig.getAccessKeyId(), smsConfig.getAccessSecret()
        );
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("ModifySmsTemplate");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("TemplateType", dto.getType()+"");
        request.putQueryParameter("TemplateName", dto.getName()+"");
        request.putQueryParameter("TemplateCode", dto.getCode());
        request.putQueryParameter("TemplateContent", dto.getContent());
        request.putQueryParameter("Remark", dto.getRemark());
        try {
            CommonResponse response = client.getCommonResponse(request);
            JSONObject json = JSON.parseObject(response.getData());
            if(json.getString("Code").equals("OK")) {
                service.update(dto);
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return new Result().ok("ok");
    }

    @Login
    @GetMapping("/selectById")
    @ApiOperation("查询")
    public Result querySmsTemplate(Long id) {
        return new Result().ok(service.selectById(id));
    }

    @Login
    @GetMapping("page")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int")
    })
    public Result<PageData<SysSmsTemplateDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<SysSmsTemplateDTO>>().ok(service.pageList(params));
    }

}

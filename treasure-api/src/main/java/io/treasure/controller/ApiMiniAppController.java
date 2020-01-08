package io.treasure.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaUserServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.config.WxMaConfig;
import lombok.Data;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
@RestController
@RequestMapping("/api/miniApp")
@Api(tags="小程序")
public class ApiMiniAppController {

    @Autowired
    protected  WxMaConfig wxMaConfig;

    /**
     * @param code    支付金额
     * @return
     */
//    @Login
    @GetMapping("/login")
    @ApiOperation(value="小程序登录凭证校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name="code",value="登录时获取的 code",required=true,paramType="query"),
    })
    public Result login(String code) {
        WxMaService wxMaService=new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaConfig);
        WxMaJscode2SessionResult result= null;
        try {
            result = wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return  new Result().error(e.getMessage());
        }
        return  new Result().ok(result);
    }

    @GetMapping("/merchant/login")
    @ApiOperation(value="小程序商户登录凭证校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name="code",value="登录时获取的 code",required=true,paramType="query"),
    })
    public Result merchantLogin(String code) {
        WxMaService wxMaService=new WxMaServiceImpl();
        wxMaConfig.setAppid("wx37dffd395a91d089");
        wxMaConfig.setSecret("0b8e57ffef72294720a43fda4dd1afde");
        wxMaService.setWxMaConfig(wxMaConfig);
        WxMaJscode2SessionResult result= null;
        try {
            result = wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return  new Result().error(e.getMessage());
        }
        return   new Result().ok(result);
    }
}

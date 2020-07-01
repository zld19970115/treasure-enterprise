package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dao.DevciceInfoDao;
import io.treasure.enm.ESharingInitiator;
import io.treasure.entity.DeviceInfoEntity;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingInitiatorEntity;
import io.treasure.vo.ProposeSharingActivityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/MobileService")
@Api(tags="移动设备信息")
public class MobileTokenController {

    @Autowired(required = false)
    DevciceInfoDao devciceInfoDao;

    @GetMapping("deviceInfo")
    @ApiOperation("设备TOKEN")
    public Result startRelay(String deviceToken,String clientId){

        if(deviceToken.equals(null) || clientId.equals(null))
            return new Result().error("deviceToken or clientId is null !!");

            System.out.println("deviceToken:"+deviceToken);
            System.out.println("clientId:"+clientId);

        DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
        deviceInfoEntity.setClientId(clientId);
        deviceInfoEntity.setDeviceToken(deviceToken);

        devciceInfoDao.insert(deviceInfoEntity);

        return new Result().ok("recived msg success!!");
    }
}

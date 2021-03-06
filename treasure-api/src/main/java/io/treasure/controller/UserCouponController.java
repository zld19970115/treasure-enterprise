package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.utils.Result;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.dto.UserCouponDTO;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.entity.UserCouponEntity;
import io.treasure.service.impl.UserCouponServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户优惠卷表
 * 2019.8.28
 */
@RestController
@RequestMapping("/UserCoupon")
@Api(tags="用户优惠卷表")
public class UserCouponController {
    @Autowired
    UserCouponServiceImpl userCouponService;
    @Login
    @GetMapping("/selectMartCoupon")
    @ApiOperation("查询商家可领取优惠卷")
    public  Result selectMartCoupon(@RequestParam(value = "userId") Long userId,@RequestParam(value = "martId")long martId){
        List<MerchantCouponEntity> AllMerchantCouponEntities = userCouponService.selectBymartId(martId);
        if (userId==null){
            List<MerchantCouponEntity> merchantCouponEntities = userCouponService.selectMartCoupon(userId, martId);
            //   List merchantCouponEntitiesList = new ArrayList();
            //   merchantCouponEntitiesList.add(merchantCouponEntities);

            //  List AllMerchantCouponEntitiesList = new ArrayList();
            //    AllMerchantCouponEntitiesList.add(AllMerchantCouponEntities);

            AllMerchantCouponEntities.removeAll(merchantCouponEntities);

        }


          return new Result().ok(AllMerchantCouponEntities);
    }
    @Login
    @PostMapping("/addCoupon")
    @ApiOperation("用户领取商家优惠卷")
    public  Result addCoupon(@RequestBody UserCouponDTO dto){
        if(dto.getUserId()==0){
            //领取付此类型优惠卷
            return new Result().error("请先登录");
        }
         //查询用户是否领取过本优惠劵
        UserCouponEntity userCouponEntity = userCouponService.selectByCouponId(dto.getCouponId(),dto.getUserId());
        if (userCouponEntity==null){
          //没领取付此类型优惠卷
            UserCouponEntity userCouponEntity1 = new UserCouponEntity();
         userCouponEntity1.setCouponId(dto.getCouponId());
         userCouponEntity1.setUserId(dto.getUserId());
         userCouponEntity1.setCreator(dto.getCreator());
         userCouponEntity1.setUpdater(dto.getUpdater());
         userCouponEntity1.setUpdateDate(dto.getUpdateDate());
         userCouponEntity1.setCreateDate(new Date());
         userCouponEntity1.setMartId(dto.getMartId());
         userCouponEntity1.setStatus(1);
         userCouponService.insert(userCouponEntity1);
          return  new Result();
      }else {
          //领取付此类型优惠卷
          return new Result().error("此优惠劵已经领取过，不能重复领取");
      }


    }

    @Login
    @GetMapping("/selectCoupon")
    @ApiOperation("查询用户可使用的优惠表")
    public Result selectCoupon(@RequestParam(value = "userId") Long userId,@RequestParam(value = "martId")long martId,@RequestParam(value = "money")double money){
       List<MerchantCouponDTO>  list = userCouponService.selectByUserId(userId, martId, money);

        return  new  Result().ok(list);

    }
    @Login
    @GetMapping("/selectGift")
    @ApiOperation("查询用户赠送金")
    public Result selectGift(@RequestParam Long userId){
        BigDecimal gift = userCouponService.selectGift(userId);
        return  new  Result().ok(gift);
    }
    @Login
    @GetMapping("/myCoupon")
    @ApiOperation("查询我的优惠卷")

    public Result myCoupon(@RequestParam(value = "userId") Long userId){
        List<MerchantCouponDTO> list = userCouponService.selectMyCouponByUserId(userId);

        return  new  Result().ok(list);

    }
}

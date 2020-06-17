package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.utils.Result;
import io.treasure.dao.MasterOrderSimpleDao;
import io.treasure.entity.MasterOrderSimpleEntity;
import io.treasure.service.MasterOrderSimpleService;
import io.treasure.vo.OrderPagePlus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterOrderSimpleServiceImpl implements MasterOrderSimpleService {

    @Autowired(required = false)
    private MasterOrderSimpleDao masterOrderSimpleDao;

    /*
    statusType:
        1==>待接单(status= 4)
        2==>进行中(status = 2 or status = 7)
        3==>退单(status = 6 and )
        4==>退菜(status = 6 and )
        其它==>除进行中外的全部分类

     */

    @Override
    public Result getOrderList(Long merchantId,Long merchantIdPosition,Integer statusType,Integer checkStatus,Integer index,Integer pageNumber){

        IPage<MasterOrderSimpleEntity> pages = new OrderPagePlus<MasterOrderSimpleEntity>(index,pageNumber);
        pages.setCurrent(index);
        pages.setSize(pageNumber);

        QueryWrapper<MasterOrderSimpleEntity> mseQueryWrapper = new QueryWrapper<>();

        mseQueryWrapper.eq("merchant_id",merchantId);
        if(statusType == null)
            statusType = 0;
        switch(statusType){
            case 1:
                mseQueryWrapper.eq("status",4);
                mseQueryWrapper.orderByAsc("update_date");
                break;
            case 2:
                mseQueryWrapper.in("status",2,7);
                mseQueryWrapper.orderByAsc("update_date");
                break;
            case 3:
                mseQueryWrapper.eq("status",4);
                mseQueryWrapper.orderByAsc("update_date");
                break;
            case 4:
                mseQueryWrapper.eq("status",4);
                mseQueryWrapper.orderByAsc("update_date");
                break;
            default:
                mseQueryWrapper.notIn("status",1,2,5,7,9,11);
                mseQueryWrapper.orderByAsc("update_date","response_status");
                break;
        }


        mseQueryWrapper.eq("check_status",0);//未结帐


        IPage<MasterOrderSimpleEntity> masterOrderSimpleEntityIPage = masterOrderSimpleDao.selectPage(pages,mseQueryWrapper);

        Result result = new Result();
        result.setCode(200);
        result.setData(masterOrderSimpleEntityIPage);

        return result;
    }

}

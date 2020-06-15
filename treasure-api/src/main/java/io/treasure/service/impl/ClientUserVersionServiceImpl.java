package io.treasure.service.impl;

import io.treasure.dao.ClientUserVersionDao;
import io.treasure.entity.ClientUserVersionEntity;
import io.treasure.service.ClientUserVersionService;
import io.treasure.utils.TmpVersionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientUserVersionServiceImpl implements ClientUserVersionService {

    @Autowired(required = false)
    private ClientUserVersionDao clientUserVersionDao;


    @Override
    public boolean checkNormalOperation(String mobile, int processType){
        //准备更新内容
        ClientUserVersionEntity entity = new ClientUserVersionEntity();
        entity.setClientMobile(mobile);
        entity.setProcessType(processType);
        long nowTime = System.currentTimeMillis();
        entity.setUpdatePmt(nowTime);

        //检查当前是否有此用户的记录
        ClientUserVersionEntity clientUserVersionEntity = clientUserVersionDao.selectById(mobile);
        if(clientUserVersionEntity == null){
            clientUserVersionDao.insert(entity);
            System.out.println("s-version(0):"+mobile);
            return true;
        }

        if(clientUserVersionEntity.getProcessType() == processType){

            long lastTime = clientUserVersionEntity.getUpdatePmt();//上次处理时间
            if((nowTime-lastTime)< TmpVersionUtil.getAllowIntervalTime()){
                System.out.println("s-version(1): 操作过频"+((nowTime-lastTime)));
                return false;
            }
        }
        clientUserVersionDao.updateById(entity);
        System.out.println("s-version(2):update");
        return true;
    }
}

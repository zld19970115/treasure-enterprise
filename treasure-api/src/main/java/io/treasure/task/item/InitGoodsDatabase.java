package io.treasure.task.item;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.GoodDao;
import io.treasure.entity.GoodEntity;
import io.treasure.jra.impl.DishesMenuJRA;
import io.treasure.task.TaskCommon;
import io.treasure.utils.MyPingyInUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商品pinyin标志列
 */
@Component
public class InitGoodsDatabase extends TaskCommon {

    @Autowired(required = false)
    private GoodDao goodDao;

    @Autowired
    private DishesMenuJRA dishesMenuJRA;

    public void updateRedis(){
        lockedProcessLock();
        updateTaskCounter();  //更新执行程序计数器
        QueryWrapper<GoodEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",1);
        List<GoodEntity> goodEntities = goodDao.selectList(queryWrapper);

        for(int i=0;i<goodEntities.size();i++){
            GoodEntity item = goodEntities.get(i);
            String name = item.getName();
            System.out.println("当前正在处理：第"+i+"条记录!");
            if(name != null){
                dishesMenuJRA.add(name);
            }
        }
        System.out.println("处理完毕!!!");
        freeProcessLock();
    }
    public void initGoodsPy(){
        lockedProcessLock();
        updateTaskCounter();  //更新执行程序计数器

        List<GoodEntity> goodEntities = goodDao.selectList(null);
        for(int i=0;i<goodEntities.size();i++){
            GoodEntity item = goodEntities.get(i);
            String name = item.getName();
            System.out.println("当前正在处理：第"+i+"条记录!");
            if(name != null){
                if(item.getFullPyName() == null || item.getFullPyName() ==""){
                    String s = MyPingyInUtil.toPyString(name,false);
                    if(s.length()>30){
                        s.substring(0,30);
                    }
                    item.setFullPyName(s);
                }

                //if(item.getSimplePyName()== null || item.getSimplePyName()=="")
                    item.setSimplePyName(MyPingyInUtil.toCharPyString(name));
                goodDao.updateById(item);
            }
        }
        System.out.println("处理完毕!!!");
        freeProcessLock();
    }

}

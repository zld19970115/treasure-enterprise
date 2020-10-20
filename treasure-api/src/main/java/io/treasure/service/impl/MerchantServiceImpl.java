package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.CategoryDao;
import io.treasure.dao.MerchantDao;
import io.treasure.dao.MerchantUserRoleDao;
import io.treasure.dto.CategoryDTO;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MakeListDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.CategoryEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantUserEntity;
import io.treasure.entity.MerchantUserRoleEntity;
import io.treasure.jra.impl.UserSearchJRA;
import io.treasure.service.MerchantRoomParamsSetService;
import io.treasure.service.MerchantRoomService;
import io.treasure.service.MerchantService;
import io.treasure.service.MerchantUserService;
import io.treasure.vo.AutoAceptVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@Service
public class MerchantServiceImpl extends CrudServiceImpl<MerchantDao, MerchantEntity, MerchantDTO> implements MerchantService {
    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;
    @Autowired
    private UserSearchJRA userSearchJRA;
    @Autowired
    private MerchantRoomService merchantRoomService;

    @Autowired(required = false)
    private MerchantDao merchantDao;

    @Autowired(required = false)
    private CategoryDao categoryDao;

    @Autowired(required = false)
    private MerchantUserService merchantUserService;

    @Autowired(required = false)
    private MerchantUserRoleDao merchantUserRoleDao;

    /**
     * 删除
     * @param id
     */
    @Override
    public void remove(long id,int status) {
        baseDao.updateStatusById(id,status);
    }

    /**
     * 根据名称和身份证号查询
     * @param name
     * @param cards
     * @return
     */
    @Override
    public MerchantEntity getByNameAndCards(String name, String cards) {
        return baseDao.getByNameAndCards(name,cards);
    }

    /**
     * 商户名称查询
     * @param name
     * @param status
     * @return
     */
    @Override
    public MerchantEntity getByName(String name, int status) {
        return baseDao.getByName(name,status);
    }

    @Override
    public String selectOfficialMobile() {
        return baseDao.selectOfficialMobile();
    }

    /**
     * 闭店
     * @param id
     * @param status
     */
    @Override
    public void closeShop(long id, int status) {
        baseDao.updateStatusById(id,status);
    }

    @Override
    public List<MerchantDTO> selectByMartId(Map<String, Object> params) {
        return baseDao.selectByMartId(params);
    }


    @Override
    public PageData<MerchantDTO> selectByUserlongitudeandlatitude(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, Constant.CREATE_DATE, false);
        //查询
        List<MerchantDTO> list = baseDao.selectByUserlongitudeandlatitude(params);
        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public Result getOutside(String deliveryArea, int distribution, long martId) {
        MerchantEntity merchantEntity = baseDao.selectById(martId);
        if (merchantEntity==null){
            return new Result().error("请稍后再试");
        }
        if (deliveryArea!=null){
            merchantEntity.setDeliveryArea(deliveryArea);
        }
        merchantEntity.setDistribution(distribution);
        baseDao.updateById(merchantEntity);
        return new Result().ok("成功");
    }


    @Override
    public PageData<MerchantDTO> queryRoundPage(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = baseDao.selectPage(
                getPage(params, null, false),
                getQueryWrapper(params))
                ;
        //查询
        List<MerchantDTO> list = baseDao.getMerchantList(params);
        for (MerchantDTO s:list) {
            int availableRoomsDesk = merchantRoomService.selectCountDesk(s.getId());
            int availableRooms = merchantRoomService.selectCountRoom(s.getId());
            s.setRoomNum(availableRooms);
            s.setDesk(availableRoomsDesk);
        }

        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public PageData<MerchantDTO> getMerchantByCategoryId(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, Constant.CREATE_DATE, false);
        //查询
        List<MerchantDTO> list = baseDao.getMerchantByCategoryId(params);
        for (MerchantDTO s:list) {
            int availableRoomsDesk = merchantRoomService.selectCountDesk(s.getId());
            int availableRooms = merchantRoomService.selectCountRoom(s.getId());
            s.setRoomNum(availableRooms);
            s.setDesk(availableRoomsDesk);
        }

        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public PageData<MerchantDTO> getMerchantByparty(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, Constant.CREATE_DATE, false);
        //查询
        List<MerchantDTO> list = baseDao.getMerchantByparty(params);
        for (MerchantDTO s:list) {
            int availableRoomsDesk = merchantRoomService.selectCountDesk(s.getId());
            int availableRooms = merchantRoomService.selectCountRoom(s.getId());
            s.setRoomNum(availableRooms);
            s.setDesk(availableRoomsDesk);
        }

        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public PageData<MerchantDTO> getMerchantByspecial(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, Constant.CREATE_DATE, false);
        //查询
        List<MerchantDTO> list = baseDao.getMerchantByspecial(params);
        for (MerchantDTO s:list) {
            int availableRoomsDesk = merchantRoomService.selectCountDesk(s.getId());
            int availableRooms = merchantRoomService.selectCountRoom(s.getId());
            s.setRoomNum(availableRooms);
            s.setDesk(availableRoomsDesk);
        }

        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public PageData<MerchantDTO> getLikeMerchant(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, Constant.CREATE_DATE, false);
        //查询
        List<MerchantDTO> list = baseDao.getLikeMerchant(params);
        for (MerchantDTO s:list) {
            int availableRoomsDesk = merchantRoomService.selectCountDesk(s.getId());
            int availableRooms = merchantRoomService.selectCountRoom(s.getId());
            s.setRoomNum(availableRooms);
            s.setDesk(availableRoomsDesk);
        }
        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public List<MerchantDTO> getListByOn() {
        return baseDao.getListByOn();
    }

    @Override
    public PageData<MerchantDTO> merchantSortingPage(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, (String) params.get("ORDER_FIELD"), false);
        //查询
        List<MerchantDTO> list = baseDao.merchantSortingPage(params);
        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public MerchantEntity getMerchantById(Long id) {
        return baseDao.getMerchantById(id);
    }

    @Override
    public PageData<MerchantDTO> martLike(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, (String) params.get("ORDER_FIELD"), false);
        //查询
        List<MerchantDTO> list = baseDao.martLike(params);
        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public Integer AuditMerchantStatus(Long id) {
        baseDao.updateAuditById(id,2);
        MerchantUserEntity merchantUser = merchantUserService.selectByMerchantId(id);
        if(merchantUser != null) {
            MerchantUserRoleEntity e = new MerchantUserRoleEntity();
            e.setUserId(merchantUser.getId());
            e.setRoleId(1278886982954143745L);
            merchantUserRoleDao.insert(e);
        }
        return 1;
    }

    @Override
    public List<MakeListDTO> selectName(Map<String, Object> params) {
        return baseDao.selectName(params);
    }

    @Override
    public Integer updateRecommend(Long id, Integer recommend) {
        return baseDao.updateRecommend(id, recommend);
    }

    @Override
    public Integer updateCommissionType(Long id, Integer type) {
        return baseDao.updateCommissionType(id, type);
    }

    @Override
    public Integer updateParty(Long id, Integer recommend) {
        return baseDao.updateParty(id, recommend);
    }

    @Override
    public Integer updateSpecial(Long id, Integer recommend) {
        return baseDao.updateSpecial(id, recommend);
    }

    @Override
    public PageData<MerchantDTO> search(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, (String) params.get("ORDER_FIELD"), false);
        //查询
        List<MerchantDTO> list = baseDao.selectbYGoods(params);
//        List<CategoryDTO>  categoryDTOS = baseDao.selectcategoryDTOS((String) params.get("value"));
//        for (CategoryDTO categoryDTO : categoryDTOS) {
//            params.put("acategoryId",categoryDTO.getId());
//            List<MerchantDTO> list1 = baseDao.selectBygreay(params);
//            list.removeAll(list1);
//            list.addAll(list1);
//        }

        //迟（临时修改）：查询商家菜品
        for (MerchantDTO merchantDTO : list) {
            List<GoodDTO> goodDTOS = baseDao.selectByMidAndValueCopy(merchantDTO.getId(), (String) params.get("value"));
            //更新搜索菜品
            if(goodDTOS.size()>0){
                merchantDTO.setGoodDTO(goodDTOS.get(0));
                goodDTOS.remove(0);
            }
            if(goodDTOS.size()>0)
                merchantDTO.setGoodDTOs(goodDTOS);
        }
        /*
        for (MerchantDTO merchantDTO : list) {
            List<GoodDTO> goodDTOS = baseDao.selectByMidAndValue(merchantDTO.getId(),(String) params.get("value"));
            for (GoodDTO goodDTO : goodDTOS) {
                merchantDTO.setGoodDTO(goodDTO);
                goodDTOS.remove(goodDTO);
                break;
            }
            if (goodDTOS.size()>0){
                List<GoodDTO> goodDTOS1 = baseDao.selectByMidAndSales(merchantDTO.getId());
                goodDTOS.removeAll(goodDTOS1);
                goodDTOS.addAll(goodDTOS1);
            }
            merchantDTO.setGoodDTOs(goodDTOS);
        }*/



        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public PageData<MerchantDTO> searchMart(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, (String) params.get("ORDER_FIELD"), false);
        //查询
        List<MerchantDTO> list = baseDao.searchMart(params);
        List<CategoryDTO>  categoryDTOS = baseDao.selectcategoryDTOS((String) params.get("value"));
        for (CategoryDTO categoryDTO : categoryDTOS) {
            params.put("acategoryId",categoryDTO.getId());
            List<MerchantDTO> list1 = baseDao.selectBygreay(params);
            list.removeAll(list1);
            list.addAll(list1);
        }
        for (MerchantDTO s:list) {
            int availableRoomsDesk = merchantRoomService.selectCountDesk(s.getId());
            int availableRooms = merchantRoomService.selectCountRoom(s.getId());
            s.setRoomNum(availableRooms);
            s.setDesk(availableRoomsDesk);
        }
        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public PageData<MerchantDTO> queryPage(Map<String, Object> params){
//        IPage<MerchantEntity> page = baseDao.selectPage(
//                getPage(params, null, false),
//                selectWrapper(params)
//        );
        //分页
        IPage<MerchantEntity> page = getPage(params, Constant.CREATE_DATE, false);
        //查询
        List<MerchantDTO> list = baseDao.getMerchantList(params);
        for (MerchantDTO s:list) {
            int availableRoomsDesk = merchantRoomService.selectCountDesk(s.getId());
            int availableRooms = merchantRoomService.selectCountRoom(s.getId());
            s.setRoomNum(availableRooms);
            s.setDesk(availableRoomsDesk);
        }
        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public void updateWX(String martId) {
        baseDao.updateWX(martId);
    }

    @Override
    public PageData<MerchantDTO> queryALLMerchantBydistance(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, (String) params.get("orderField"), true);
        //查询
        List<MerchantDTO> list = baseDao.getMerchantList(params);
        for (MerchantDTO s:list) {
            int availableRoomsDesk = merchantRoomService.selectCountDesk(s.getId());
            int availableRooms = merchantRoomService.selectCountRoom(s.getId());
            s.setRoomNum(availableRooms);
            s.setDesk(availableRoomsDesk);
        }
        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    /**
     * 查询条件
     * @param params
     * @return
     */
    public QueryWrapper<MerchantEntity> selectWrapper(Map<String, Object> params){
        //是否推荐
        String recommend= (String) params.get("recommend");
        QueryWrapper<MerchantEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(recommend), "recommend", recommend);
        return wrapper;
    }

    /**
     * 查询条件
     * @param params
     * @return
     */
    public QueryWrapper<MerchantEntity>
    getQueryWrapper(Map<String, Object> params){
        //店铺名称
        String name= (String) params.get("name");
        //手机号码
        String mobile=(String)params.get("mobile");
        QueryWrapper<MerchantEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);
        wrapper.like(StringUtils.isNotBlank(mobile),"mobile",mobile);
        wrapper.eq("status",1);
        String shortField = (String)params.get("orderByField");
        if(shortField == "score" || "score".equals(shortField)){
            wrapper.orderByDesc(shortField,"monthy_sales");
       }
      else if(shortField == "monthy_sales"|| "monthy_sales".equals(shortField)){
            wrapper.orderByDesc(shortField,"score");
         }
        return wrapper;
    }

    /**
     * 查询条件
     * @param params
     * @return
     */
    @Override
    public QueryWrapper<MerchantEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status= (String) params.get("status");
        //店铺名称
        String name= (String) params.get("name");
        //手机号码
        String mobile=(String)params.get("mobile");
        //店铺
        String merchantId=(String)params.get("merchantId");
        //类型
        String categoryId =(String) params.get("categoryId");
        QueryWrapper<MerchantEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(mobile),"mobile",mobile);
        wrapper.like(StringUtils.isNotBlank(categoryId),"categoryId",categoryId);
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.like(StringUtils.isNotBlank(name), "name", name);
        wrapper.ne(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(merchantId), "id", merchantId);
        return wrapper;
    }



    public int attachCategoryByName(Long merchantId,String categoryName){
        QueryWrapper<CategoryEntity> queryWrapperCategory = new QueryWrapper<>();
        queryWrapperCategory.eq("name",categoryName);
        List<CategoryEntity> categoryEntities = categoryDao.selectList(queryWrapperCategory);
        CategoryEntity ceItem = null;
        if(categoryEntities.size()>0){
            ceItem = categoryEntities.get(0);
        }
        Long ceId = null;
        if(ceItem == null)
            return -1;

        ceId = ceItem.getId();
        MerchantEntity merchantEntity = merchantDao.selectById(merchantId);
        if(merchantEntity == null)
            return -2;

        String categoryid = merchantEntity.getCategoryid();

        if(!categoryid.contains(ceId+"")){
            merchantEntity.setCategoryid(categoryid+","+ceId);
            merchantDao.updateById(merchantEntity);//附加分类
            return 1;//附加
        }
        return 0;//不需要附加
    }

    public int attachCategoryByNamePlus(String merchantName,String categoryName){
        QueryWrapper<CategoryEntity> queryWrapperCategory = new QueryWrapper<>();
        queryWrapperCategory.eq("name",categoryName);
        List<CategoryEntity> categoryEntities = categoryDao.selectList(queryWrapperCategory);
        CategoryEntity ceItem = null;
        if(categoryEntities.size()>0){
            ceItem = categoryEntities.get(0);
        }
        Long ceId = null;
        if(ceItem == null)
            return -1;

        ceId = ceItem.getId();
        QueryWrapper<MerchantEntity> qw = new QueryWrapper<>();
        qw.eq("name",merchantName);
        List<MerchantEntity> merchantEntitys = merchantDao.selectList(qw);
        MerchantEntity merchantEntity = null;
        if(merchantEntitys.size()>0)
            merchantEntity = merchantEntitys.get(0);

        if(merchantEntity == null)
            return -2;

        String categoryid = merchantEntity.getCategoryid();

        if(!categoryid.contains(ceId+"")){
            merchantEntity.setCategoryid(categoryid+","+ceId);
            merchantDao.updateById(merchantEntity);//附加分类
            return 1;//附加
        }
        return 0;//不需要附加
    }


    public void updateAutoAceptStatus(AutoAceptVo vo){
        List<Long> ids = vo.getIds();
        Integer status = vo.getStatus();
        if(status != 2){
            status = 1;
        }

        merchantDao.updateAutoAceptOrderStatus(ids,status);


    }

    @Override
    public int updateCommission(Long mid, BigDecimal proportion) {
        return merchantDao.updateCommission(mid, proportion);
    }
}

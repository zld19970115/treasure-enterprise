package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dao.NewsDao;
import io.treasure.dto.NewsDto;
import io.treasure.entity.NewsEntity;
import io.treasure.entity.RecordNewsEntity;
import io.treasure.service.NewsService;
import io.treasure.service.RecordNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/news")
@Api(tags="公告消息")
public class NewsController {

    @Autowired
    private NewsService newsService;
    @Autowired
    private RecordNewsService recordNewsService;
    @Autowired(required = false)
    private NewsDao newsDao;


    @Login
    @GetMapping("news_page")
    @ApiOperation("分页查询新闻消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "startDate", value = "开始time", paramType = "query",dataType="date") ,
            @ApiImplicitParam(name = "endDate", value = "结束time", paramType = "query",dataType="date"),
            @ApiImplicitParam(name = "id", value = "用户/商家 id", paramType = "query",dataType="Long"),
            @ApiImplicitParam(name = "type", value = "类型", paramType = "query",dataType="int")
    })
    public Result newsList(@ApiIgnore @RequestParam int page,int limit,Date startDate,Date endDate,Long id,Integer type) {

        QueryWrapper<NewsEntity> saqw = new QueryWrapper<>();

        if(startDate != null && endDate != null)
            saqw.between("create_date",startDate,endDate);
        saqw.orderByDesc("create_date");
        saqw.eq("status",1);
        if(type == 1) {
            saqw.in("type", 1,3);
        } else if(type == 2) {
            saqw.in("type", 2,3);
        }

        //奖励数量设置
        Page<NewsEntity> record = new Page<NewsEntity>(page,limit);
        IPage<NewsEntity> records = newsDao.selectPage(record, saqw);
        List<NewsEntity> records1 = records.getRecords();

        if(records == null){
            return new Result().error("nothing");}else {

            List<RecordNewsEntity> recordNewsEntities = recordNewsService.selectByUid(id, type);
            for (NewsEntity newsEntity : records1) {
                for (RecordNewsEntity recordNewsEntity : recordNewsEntities) {
                    if (newsEntity.getId()==recordNewsEntity.getNId() || newsEntity.getId().equals(recordNewsEntity.getNId()) ){
                        newsEntity.setSee(1);
                    }

                }
            }

        }

        return new Result().ok(records);
    }


    @Login
    @GetMapping("page")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "startDate", value = "开始time", paramType = "query",dataType="String") ,
            @ApiImplicitParam(name = "endDate", value = "结束time", paramType = "query",dataType="String")
    })
    public Result<PageData<NewsDto>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<NewsDto>>().ok(newsService.page(params));
    }

    @Login
    @GetMapping("agreePage")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int")
    })
    public Result<PageData<NewsDto>> agreePage(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<NewsDto>>().ok(newsService.agreePage(params));
    }

    @Login
    @CrossOrigin
    @GetMapping("newsById")
    @ApiOperation("根据id查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query",dataType="String") ,
    })
    public Result<NewsDto> bannerById(@ApiIgnore @RequestParam Long id) {
        return new Result<NewsDto>().ok(newsService.get(id));
    }

    @Login
    @GetMapping("del")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query",dataType="String") ,
    })
    public Result<Boolean> del(@ApiIgnore @RequestParam Long id) {
        return new Result<Boolean>().ok(newsService.deleteBatchIds(Arrays.asList(new Long[]{id})));
    }

    @Login
    @PostMapping("update")
    @ApiOperation("更新")
    public Result<String> update(@RequestBody NewsDto dto) {
        newsService.update(dto);
        return new Result<String>().ok("ok");
    }

    @Login
    @PostMapping("insert")
    @ApiOperation("新增")
    public Result<String> insert(@RequestBody NewsEntity dto) {
        newsService.insert(dto);
        return new Result<String>().ok("ok");
    }

    @Login
    @GetMapping("privacyAgrre")
    @ApiOperation("隐私协议")
    public Result<NewsDto> privacyAgrre() {
        return newsService.privacyAgrre();
    }

    @Login
    @GetMapping("userAgrre")
    @ApiOperation("用户协议")
    public Result<NewsDto> userAgrre() {
        return newsService.userAgrre();
    }

    @Login
    @GetMapping("insertNews")
    @ApiOperation("用户/商户 查看新闻")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户/商户 id", paramType = "query",dataType="Long") ,
            @ApiImplicitParam(name = "nId", value = "新闻id", paramType = "query",dataType="Long"),
            @ApiImplicitParam(name = "type", value = "类型", paramType = "query",dataType="int")
    })
    public Result insertNews(@ApiIgnore @RequestParam Long id , Long nId , int type ) {
        RecordNewsEntity recordNewsEntity1 = recordNewsService.selectByUandNid(id, nId, type);
        if (recordNewsEntity1!=null){
            return new Result().ok("已查看");
        }

        RecordNewsEntity recordNewsEntity = new RecordNewsEntity();
        recordNewsEntity.setNId(nId);
        recordNewsEntity.setUId(id);
        recordNewsEntity.setStatus(type);
        recordNewsService.insert(recordNewsEntity);
         return new Result().ok("已查看");
    }
    @GetMapping("countNews")
    @ApiOperation("用户查询未读消息数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户/商户 id", paramType = "query",dataType="Long") ,
            @ApiImplicitParam(name = "type", value = "类型", paramType = "query",dataType="int")
    })
    public Result countNews(@ApiIgnore @RequestParam Long id , int type ) {
        List<NewsEntity> newsEntities = newsService.selectByOn(type);
        int a = 0;
        for (NewsEntity newsEntity : newsEntities) {
            RecordNewsEntity recordNewsEntity = recordNewsService.selectByUandNid(id, newsEntity.getId(), type);
            if (recordNewsEntity!=null){
                a++;
            }
        }
        return new Result().ok(newsEntities.size()-a);
    }
}

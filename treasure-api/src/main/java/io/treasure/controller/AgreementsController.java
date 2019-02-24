package io.treasure.controller;


import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.AgreementsDTO;
import io.treasure.service.AgreementsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 协议管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@RestController
@RequestMapping("/agreements")
@Api(tags="协议管理")
public class AgreementsController {
    @Autowired
    private AgreementsService agreementsService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<AgreementsDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<AgreementsDTO> page = agreementsService.page(params);

        return new Result<PageData<AgreementsDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<AgreementsDTO> get(@PathVariable("id") Long id){
        AgreementsDTO data = agreementsService.get(id);

        return new Result<AgreementsDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody AgreementsDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        agreementsService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody AgreementsDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        agreementsService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        agreementsService.delete(ids);

        return new Result();
    }
}
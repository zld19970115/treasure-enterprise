package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.BannerDto;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MyPrinterDto;
import io.treasure.entity.BannerEntity;
import io.treasure.service.BannerService;
import io.treasure.service.MerchantService;
import io.treasure.service.PrinterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/printer")
@Api(tags = "打印机设置")
public class PrinterController {

    @Autowired
    private PrinterService service;

    @GetMapping("myPrinter")
    @ApiOperation("商户打印机")
    public Result myPrinter(@RequestParam Long mid) {
        return service.myPrinter(mid);
    }

    @PostMapping("savePrinter")
    @ApiOperation("保存商户打印机")
    public Result savePrinter(@RequestBody MyPrinterDto dto) {
        return service.savePrinter(dto);
    }

}

package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.MyPrinterDto;
import io.treasure.dto.PrinterDto;
import io.treasure.entity.PrinterEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface PrinterService extends CrudService<PrinterEntity, PrinterDto> {

    Result myPrinter(@RequestParam Long mid);

    Result savePrinter(MyPrinterDto dto);

}

package io.treasure.dto;

import io.treasure.dao.TakeoutOrdersDao;
import io.treasure.dao.TakeoutOrdersDetailDao;
import lombok.*;

import java.util.List;


@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlaceTOrderDTO {

    private TakeoutOrdersDao takeoutOrdersDao;

    private TakeoutOrdersDetailDTO takeoutOrdersDetailDTO;

    private List<TakeoutOrdersDetailDao> takeoutOrdersDetailDaos;
}

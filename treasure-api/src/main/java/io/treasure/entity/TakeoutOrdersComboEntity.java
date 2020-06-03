package io.treasure.entity;


import lombok.*;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TakeoutOrdersComboEntity {


    private TakeoutOrdersEntity takeoutOrdersEntity;
    private TakeoutOrdersExtendsEntity takeoutOrdersExtendsEntity;


}

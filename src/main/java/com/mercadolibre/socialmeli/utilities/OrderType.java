package com.mercadolibre.socialmeli.utilities;
import lombok.Getter;

@Getter
public enum OrderType {
    ORDER_DATE_ASC("date_asc"),
    ORDER_DATE_DESC("date_desc");

    private final String value;

    OrderType(String value){
        this.value=value;
    }
}

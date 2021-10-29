package com.pratice.juicestock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JuiceType {

    APPLE("Apple"),
    CRANBERRY("Cranberry"),
    ORANGE("Orange"),
    GRAPEFRUIT("Graprefruit"),
    ACAI_BERRY("Acai Berry"),
    LIMONADE("Limonade");

    private final String description;

}

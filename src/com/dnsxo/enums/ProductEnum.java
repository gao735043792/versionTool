package com.dnsxo.enums;

/**
 * @author Mr.peak
 * @description 产品类型
 * @date 2020-05-10 00:20:00
 */
public enum ProductEnum {

    BOS(0, "金蝶云苍穹平台"),
    BIZ(1, "金蝶云苍穹业务"),
    INDUSTRY(2, "行业产品");


    private int code;
    private String name;

    ProductEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ProductEnum getEnumByName(Object name) {
        if (name == null) {
            return null;
        }
        for (ProductEnum enums : ProductEnum.values()) {
            if (name.equals(enums.getName())) {
                return enums;
            }
        }
        return null;
    }
}

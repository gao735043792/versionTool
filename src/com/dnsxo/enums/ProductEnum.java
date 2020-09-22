package com.dnsxo.enums;

/**
 * @description 产品类型
 * @author Mr.peak
 * @date 2020-05-10 00:20:00
 */
public enum ProductEnum {

    BIZ(1,"标准产品（业务）","biz"),

    CR(2,"行业产品（建筑与房地产）","cr");

    private int code;
    private String name;
    private String typeCode;
    ProductEnum(int code,String name,String typeCode) {
        this.code = code;
        this.name = name;
        this.typeCode = typeCode;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getTypeCode() {
        return typeCode;
    }
}

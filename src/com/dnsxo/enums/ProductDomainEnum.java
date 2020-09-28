package com.dnsxo.enums;

/**
 * @author Mr.peak
 * @description 产品所属领域
 * @date 2020-05-10 00:20:00
 */
public enum ProductDomainEnum {

    //标准产品
    PMGT(100, "项目云", "pmgt", true),
    EPM(101, "企业绩效云", "epm", true),
    //行业产品
    EC(1001, "建筑项目云", "ec", false),
    ASC(1005, "我家云-ASC云", "asc", false),
    PSC(1010, "我家云-PSC云", "psc", false);


    private int code;
    private String name;
    private String cloudCode;
    private boolean isStd;

    ProductDomainEnum(int code, String name, String cloudCode, boolean isStd) {
        this.code = code;
        this.name = name;
        this.cloudCode = cloudCode;
        this.isStd = isStd;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCloudCode() {
        return cloudCode;
    }

    public boolean isStd() {
        return isStd;
    }

    public static ProductDomainEnum getEnumByName(Object name) {
        if (name == null) {
            return null;
        }
        for (ProductDomainEnum enums : ProductDomainEnum.values()) {
            if (name.equals(enums.getName())) {
                return enums;
            }
        }
        return null;
    }
}

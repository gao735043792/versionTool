package com.dnsxo.enums;

/**
 * @author Mr.peak
 * @description 产品所属领域
 * @date 2020-05-10 00:20:00
 */
public enum ProductDomainEnum {

    //标准产品
    PMGT(100, "项目云", "pmgt", "constellation_pmgt","项目云",true),
    EPM(101, "企业绩效云", "epm", "constellation_epbc","预算&报表",true),
    //行业产品
    EC(1001, "建筑项目云", "ec", "comsic_cr","建筑项目云",false),
    ASC(1005, "资产服务云", "asc", "comsic_asc","资产服务云",false),
    PSC(1010, "物业服务云", "psc", "comsic_psc","物业服务云",false);


    private int code;
    private String name;
    private String cloudCode;
    private String domainCode;
    private String domainName;
    private boolean isStd;

    ProductDomainEnum(int code, String name, String cloudCode, String domainCode,String domainName,boolean isStd) {
        this.code = code;
        this.name = name;
        this.cloudCode = cloudCode;
        this.domainCode = domainCode;
        this.domainName = domainName;
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

    public String getDomainCode() {
        return domainCode;
    }

    public String getDomainName() {
        return domainName;
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

package org.jumutang.giftpay.entity;

/**
 * Created by RuanYJ on 2017/7/5.
 */
public class OilBalanceModel {
    private String id;
    private String openId;
    private String status;
    private String createTime;
    private String updateTime;
    private String oilBalance;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOilBalance() {
        return oilBalance;
    }

    public void setOilBalance(String oilBalance) {
        this.oilBalance = oilBalance;
    }
}

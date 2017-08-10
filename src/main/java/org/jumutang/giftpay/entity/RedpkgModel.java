package org.jumutang.giftpay.entity;

/**
 * Created by RuanYJ on 2017/7/19.
 */
public class RedpkgModel {
    private String id;
    private String openId;
    private String zshOpenId;
    private String redpkgId;
    private String redpkgValue;
    private String redpkgDesc;
    private String updateTime;
    private String createTime;
    private String redpkgStatus;

    public String getRedpkgStatus() {
        return redpkgStatus;
    }

    public void setRedpkgStatus(String redpkgStatus) {
        this.redpkgStatus = redpkgStatus;
    }

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

    public String getZshOpenId() {
        return zshOpenId;
    }

    public void setZshOpenId(String zshOpenId) {
        this.zshOpenId = zshOpenId;
    }

    public String getRedpkgId() {
        return redpkgId;
    }

    public void setRedpkgId(String redpkgId) {
        this.redpkgId = redpkgId;
    }

    public String getRedpkgValue() {
        return redpkgValue;
    }

    public void setRedpkgValue(String redpkgValue) {
        this.redpkgValue = redpkgValue;
    }

    public String getRedpkgDesc() {
        return redpkgDesc;
    }

    public void setRedpkgDesc(String redpkgDesc) {
        this.redpkgDesc = redpkgDesc;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

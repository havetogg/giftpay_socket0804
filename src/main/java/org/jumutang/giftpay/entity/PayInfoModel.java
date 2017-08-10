package org.jumutang.giftpay.entity;

import java.io.Serializable;

/**
 * 支付信息
 * 
 * @author 鲁雨
 * @since 20170117
 * @version v1.0
 * 
 *          copyright Luyu(18994139782@163.com)
 *
 */
public class PayInfoModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * 支付id
	 */
	private String dealId;

	/*
	 * 订单信息
	 */
	private String orderNo;

	/*
	 * 账号id
	 */
	private String accountId;

	/*
	 * 商户信息
	 */
	private String businessInfo;

	/*
	 * 支付信息
	 */
	private String dealInfo;

	/*
	 * 支付金额
	 */
	private Double dealMoney;

	/*
	 * 真实付款金额
	 */
	private Double dealRealMoney;

	/*
	 * 支付时间
	 */
	private String dealTime;

	/*
	 * 支付类型
	 */
	private Short dealType;

	/*
	 * 支付状态
	 */
	private Short dealState;

	/*
	 * 支付备注
	 */
	private String dealRemark;

	private String phone;

	private String ofOrder;

	public String getOfOrder() {
		return ofOrder;
	}

	public void setOfOrder(String ofOrder) {
		this.ofOrder = ofOrder;
	}

	/*
         * 微信支付订单号
         */
	private String wxOrder;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWxOrder() {
		return wxOrder;
	}

	public void setWxOrder(String wxOrder) {
		this.wxOrder = wxOrder;
	}

	public Double getDealRealMoney() {
		return dealRealMoney;
	}

	public void setDealRealMoney(Double dealRealMoney) {
		this.dealRealMoney = dealRealMoney;
	}

	public String getDealId() {
		return dealId;
	}

	public void setDealId(String dealId) {
		this.dealId = dealId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getBusinessInfo() {
		return businessInfo;
	}

	public void setBusinessInfo(String businessInfo) {
		this.businessInfo = businessInfo;
	}

	public String getDealInfo() {
		return dealInfo;
	}

	public void setDealInfo(String dealInfo) {
		this.dealInfo = dealInfo;
	}

	public Double getDealMoney() {
		return dealMoney;
	}

	public void setDealMoney(Double dealMoney) {
		this.dealMoney = dealMoney;
	}

	public String getDealTime() {
		return dealTime;
	}

	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}

	public Short getDealType() {
		return dealType;
	}

	public void setDealType(Short dealType) {
		this.dealType = dealType;
	}

	public Short getDealState() {
		return dealState;
	}

	public void setDealState(Short dealState) {
		this.dealState = dealState;
	}

	public String getDealRemark() {
		return dealRemark;
	}

	public void setDealRemark(String dealRemark) {
		this.dealRemark = dealRemark;
	}

	@Override
	public String toString() {
		return "PayInfoModel [dealId=" + dealId + ", orderNo=" + orderNo + ", accountId=" + accountId
				+ ", businessInfo=" + businessInfo + ", dealInfo=" + dealInfo + ", dealMoney=" + dealMoney
				+ ", dealRealMoney=" + dealRealMoney + ", dealTime=" + dealTime + ", dealType=" + dealType
				+ ", dealState=" + dealState + ", dealRemark=" + dealRemark + ", wxOrder=" + wxOrder + "]";
	}

}

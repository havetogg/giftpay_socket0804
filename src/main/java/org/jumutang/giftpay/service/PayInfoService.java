package org.jumutang.giftpay.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jumutang.giftpay.dao.PayInfoDaoI;
import org.jumutang.giftpay.entity.PayInfoModel;
import org.jumutang.giftpay.tools.DateFormatUtil;
import org.jumutang.giftpay.tools.UUIDUtil;
import org.jumutang.giftpay.tools.UniqueX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service("/payInfoServiceI")
public class PayInfoService{
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PayInfoDaoI payInfoDaoI;

	/**
	 * 查询支付信息
	 * @param infoModel
	 * @return
	 */
	public List<PayInfoModel> queryPayInfos(PayInfoModel infoModel) {
		
		return payInfoDaoI.queryPayInfos(infoModel);
	}


	/**
	 * 更新支付信息
	 * @param payInfoModel
	 * @return
	 */
	public int updatePayInfo(PayInfoModel payInfoModel) {
	
		return payInfoDaoI.updatePayInfo(payInfoModel);
	}

	/**
	 * 生成支付信息
	 * @param payInfoModel
	 * @return
	 */
	public int insertPayInfo(PayInfoModel payInfoModel) {
		payInfoModel.setDealId(UUIDUtil.getUUID());
		if(payInfoModel.getOrderNo()==null){
			payInfoModel.setOrderNo(UniqueX.randomUnique());
		}
		if(payInfoModel.getDealState()==null){
			payInfoModel.setDealState(new Short("1"));
		}
		if(payInfoModel.getDealTime()==null){
			payInfoModel.setDealTime(DateFormatUtil.formateString());
		}
		
		return payInfoDaoI.insertPayInfo(payInfoModel);
	}

}

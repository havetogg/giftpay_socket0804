package org.jumutang.giftpay.dao;


import org.jumutang.giftpay.entity.PayInfoModel;

import java.util.ArrayList;
import java.util.List;

public interface PayInfoDaoI {

	/**
	 * 查询支付信息
	 * @param infoModel
	 * @return
	 */
	public List<PayInfoModel> queryPayInfos(PayInfoModel infoModel);

	/**
	 * 更新支付信息
	 * @param payInfoModel
	 * @return
	 */
	public int updatePayInfo(PayInfoModel payInfoModel);
	
	/**
	 * 生成支付信息
	 * @param payInfoModel
	 * @return
	 */
	public int insertPayInfo(PayInfoModel payInfoModel);

	public int queryPayInfoSum(PayInfoModel payInfoModel);


}

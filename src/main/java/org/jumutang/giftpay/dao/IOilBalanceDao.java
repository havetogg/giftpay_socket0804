package org.jumutang.giftpay.dao;

import org.jumutang.giftpay.entity.OilBalanceModel;

import java.util.List;

/**
 * Created by RuanYJ on 2017/7/5.
 */
public interface IOilBalanceDao {
    List<OilBalanceModel> queryOilBalanceList(OilBalanceModel oilBalanceModel);
    int addOilBalanceModel(OilBalanceModel oilBalanceModel);
    int updateOilBalanceModel(OilBalanceModel oilBalanceModel);
}

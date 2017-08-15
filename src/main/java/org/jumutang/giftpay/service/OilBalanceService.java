package org.jumutang.giftpay.service;

import org.jumutang.giftpay.dao.IOilBalanceDao;
import org.jumutang.giftpay.entity.OilBalanceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by RuanYJ on 2017/7/5.
 */
@Service
public class OilBalanceService {

    @Autowired
    private IOilBalanceDao oilBalanceDao;

    public List<OilBalanceModel> queryOilBalanceList(OilBalanceModel oilBalanceModel){
        return this.oilBalanceDao.queryOilBalanceList(oilBalanceModel);
    }
    public int addOilBalanceModel(OilBalanceModel oilBalanceModel){
        return this.oilBalanceDao.addOilBalanceModel(oilBalanceModel);
    }
    public int updateOilBalanceModel(OilBalanceModel oilBalanceModel){
        return this.oilBalanceDao.updateOilBalanceModel(oilBalanceModel);
    }

    public int queryOilBalanceSum(OilBalanceModel oilBalanceModel){
        return this.oilBalanceDao.queryOilBalanceSum(oilBalanceModel);
    }

}

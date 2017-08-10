package org.jumutang.giftpay.service;

import org.jumutang.giftpay.dao.IOilRecordModelDao;
import org.jumutang.giftpay.entity.OilRecordModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by RuanYJ on 2017/6/29.
 */
@Service
public class OilRecordModelService {

    @Autowired
    private IOilRecordModelDao oilRecordModelDao;

    public List<OilRecordModel> queryOilRecordList(OilRecordModel oilRecordModel){
        return this.oilRecordModelDao.queryOilRecordList(oilRecordModel);
    }
    public List<OilRecordModel> queryOilRecordRankList(OilRecordModel oilRecordModel){
        return this.oilRecordModelDao.queryOilRecordRankList(oilRecordModel);
    }
    public int addOilRecordModel(OilRecordModel oilRecordModel){
        return this.oilRecordModelDao.addOilRecordModel(oilRecordModel);
    }
    public int updateRecordModel(OilRecordModel oilRecordModel){
        return this.oilRecordModelDao.updateRecordModel(oilRecordModel);
    }

}

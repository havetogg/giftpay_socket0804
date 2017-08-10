package org.jumutang.giftpay.dao;

import org.jumutang.giftpay.entity.OilRecordModel;

import java.util.List;

/**
 * Created by RuanYJ on 2017/6/29.
 */
public interface IOilRecordModelDao {
    List<OilRecordModel> queryOilRecordList(OilRecordModel oilRecordModel);
    List<OilRecordModel> queryOilRecordRankList(OilRecordModel oilRecordModel);
    int addOilRecordModel(OilRecordModel oilRecordModel);
    int updateRecordModel(OilRecordModel oilRecordModel);

}

package org.jumutang.giftpay.dao;

import org.jumutang.giftpay.entity.UserRateModel;

import java.util.List;

/**
 * Created by RuanYJ on 2017/7/17.
 */
public interface IUserRateDao {
    List<UserRateModel> queryUserRateList(UserRateModel userRateModel);
    String queryUserAllRate(UserRateModel userRateModel);
    int addUserRateModel(UserRateModel userRateModel);
}

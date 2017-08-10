package org.jumutang.giftpay.service;

import org.jumutang.giftpay.dao.IUserRateDao;
import org.jumutang.giftpay.entity.UserRateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by RuanYJ on 2017/6/29.
 */
@Service
public class UserRateService {

    @Autowired
    private IUserRateDao userRateDao;

    public List<UserRateModel> queryUserRateList(UserRateModel userRateModel){
        return this.userRateDao.queryUserRateList(userRateModel);
    }
    public String queryUserAllRate(UserRateModel userRateModel){
        return this.userRateDao.queryUserAllRate(userRateModel);
    }
   public int addUserRateModel(UserRateModel userRateModel){
       return this.userRateDao.addUserRateModel(userRateModel);
   }
}

package org.jumutang.giftpay.service;

import org.jumutang.giftpay.dao.IUserModelDao;
import org.jumutang.giftpay.entity.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by RuanYJ on 2017/6/29.
 */
@Service
public class UserModelService {

    @Autowired
    private IUserModelDao userModelDao;

    public List<UserModel> queryUserModelList(UserModel userModel){
        return this.userModelDao.queryUserModelList(userModel);
    }
    public int addUserModel(UserModel userModel){
        return this.userModelDao.addUserModel(userModel);
    }
    public int updateUserPhone(UserModel userModel){
        return this.userModelDao.updateUserPhone(userModel);
    }
    public int updateUserInitStatus(UserModel userModel){
        return this.userModelDao.updateUserInitStatus(userModel);
    }
    public int updateUserID(UserModel userModel){
        return this.userModelDao.updateUserID(userModel);
    }
}

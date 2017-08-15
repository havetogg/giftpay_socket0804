package org.jumutang.giftpay.dao;

import org.jumutang.giftpay.entity.UserModel;

import java.util.List;

/**
 * Created by RuanYJ on 2017/6/29.
 */
public interface IUserModelDao {
    List<UserModel> queryUserModelList(UserModel userModel);
    int queryUserCount(UserModel userModel);
    int addUserModel(UserModel userModel);
    int updateUserPhone(UserModel userModel);
    int updateUserInitStatus(UserModel userModel);
    int updateUserID(UserModel userModel);
}

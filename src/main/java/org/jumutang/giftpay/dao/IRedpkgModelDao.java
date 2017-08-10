package org.jumutang.giftpay.dao;

import org.jumutang.giftpay.entity.RedpkgModel;

import java.util.List;

/**
 * Created by RuanYJ on 2017/7/19.
 */
public interface IRedpkgModelDao {
    List<RedpkgModel> queryRedpkgList(RedpkgModel redpkgModel);
    int addRedpkgRecord(RedpkgModel redpkgModel);
    int updateRedpkgStatus(RedpkgModel redpkgModel);
}

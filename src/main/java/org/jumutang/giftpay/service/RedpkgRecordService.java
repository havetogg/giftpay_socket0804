package org.jumutang.giftpay.service;

import org.jumutang.giftpay.dao.IRedpkgModelDao;
import org.jumutang.giftpay.entity.RedpkgModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by RuanYJ on 2017/7/19.
 */
@Service
public class RedpkgRecordService {
    @Autowired
    private IRedpkgModelDao redpkgModelDao;


    public List<RedpkgModel> queryRedpkgList(RedpkgModel redpkgModel){
        return this.redpkgModelDao.queryRedpkgList(redpkgModel);
    }
    public int addRedpkgRecord(RedpkgModel redpkgModel){
        return this.redpkgModelDao.addRedpkgRecord(redpkgModel);
    }
    public int updateRedpkgStatus(RedpkgModel redpkgModel){
        return this.redpkgModelDao.updateRedpkgStatus(redpkgModel);
    }


}

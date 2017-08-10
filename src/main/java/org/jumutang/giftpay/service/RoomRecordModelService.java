package org.jumutang.giftpay.service;

import org.jumutang.giftpay.dao.IOilRecordModelDao;
import org.jumutang.giftpay.dao.IRoomRecordDao;
import org.jumutang.giftpay.entity.OilRecordModel;
import org.jumutang.giftpay.entity.RoomRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by RuanYJ on 2017/6/29.
 */
@Service
public class RoomRecordModelService {

    @Autowired
    private IRoomRecordDao roomRecordDao;

   public List<RoomRecord> queryRoomRecordList(RoomRecord roomRecord){
       return  this.roomRecordDao.queryRoomRecordList(roomRecord);
   }
    public int addRoomRecord(RoomRecord roomRecord){
        return this.roomRecordDao.addRoomRecord(roomRecord);
    }

    public int updateRoomRecord(RoomRecord roomRecord){
        return this.roomRecordDao.updateRoomRecord(roomRecord);
    }

}

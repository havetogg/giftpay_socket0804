package org.jumutang.giftpay.dao;

import org.jumutang.giftpay.entity.RoomRecord;

import java.util.List;

/**
 * Created by RuanYJ on 2017/6/29.
 */
public interface IRoomRecordDao {
    List<RoomRecord> queryRoomRecordList(RoomRecord roomRecord);
    int addRoomRecord(RoomRecord roomRecord);
    int updateRoomRecord(RoomRecord roomRecord);
}

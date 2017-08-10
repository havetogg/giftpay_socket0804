package org.jumutang.giftpay.task;

import org.jumutang.giftpay.tools.UniqueX;
import org.jumutang.giftpay.websocket.WebSocketTwo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 返款通知 [一句话功能简述]
 * <p>
 * [功能详细描述]
 * <p>
 * 
 * @author YeFei
 * @version 1.0, 2015年12月4日
 * @see
 * @since gframe-v100
 */
@Component
public class RefundNoticeTask {

	private static final Logger _LOGGER = LoggerFactory.getLogger(RefundNoticeTask.class);

	/**
	 * 每月20号 返款通知 cron表达式：* * * * * *（共6位，使用空格隔开，具体如下） cron表达式：*(秒0-59)
	 * *(分钟0-59) *(小时0-23) *(日期1-31) *(月份1-12或是JAN-DEC) *(星期1-7或是SUN-SAT)
	 * 0 0 0 20 * *
	 */
	@Scheduled(cron = "0 0 0 20 * *")
	public void messageNotice() throws Exception{
		//_LOGGER.info("每月20号 返款扫描!");
	}

	@Scheduled(cron = "0/1 * * * * ?")
	public void oilMessageNotice() throws Exception{
		//WebSocketTwo.sendAllMsg();
		//_LOGGER.info("油礼付定时广播!");
	}

	public static void main(String[] args) {
	}
}
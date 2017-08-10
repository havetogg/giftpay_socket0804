package org.jumutang.giftpay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jumutang.giftpay.common.redis.RedisCacheUtil;
import org.jumutang.giftpay.entity.*;
import org.jumutang.giftpay.service.OilBalanceService;
import org.jumutang.giftpay.service.PayInfoService;
import org.jumutang.giftpay.service.RoomRecordModelService;
import org.jumutang.giftpay.service.UserModelService;
import org.jumutang.giftpay.tools.DateFormatUtil;
import org.jumutang.giftpay.tools.MD5Util;
import org.jumutang.giftpay.tools.UUIDUtil;
import org.jumutang.giftpay.tools.UniqueX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by RuanYJ on 2017/7/10.
 */
@Controller
public class PayOilController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OilBalanceService oilBalanceService;
    @Autowired
    private PayInfoService payInfoService;
    @Autowired
    private UserModelService userModelService;
    @Autowired
    private RoomRecordModelService roomRecordModelService;

    @Autowired
    private RedisCacheUtil<CodeMess> redisCacheUtil;

    @Value("#{propertyFactoryBean['DOMAINURL']}")
    private String doMainUrl;
    /**
     * 执行当前业务方法(加入微信签名认证)
     */
    @RequestMapping(value = "/preOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String preOrder(HttpServletRequest request, HttpServletResponse response, ModelMap model,
                           HttpSession session) throws Exception {
        CodeMess codeMess;
        String obj =session.getAttribute(OilContant.SESSION_USERMODEL).toString();
        UserModel userModel=JSONObject.parseObject(obj,UserModel.class);
        if (userModel == null) {
            codeMess = new CodeMess("30000", "重新登录");
            return JSON.toJSONString(codeMess);
        }
        String openId = userModel.getOpenId();
        String money = request.getParameter("dealMoney");
        String isTiming=request.getParameter("isTiming");
        String timingTime=request.getParameter("timingTime");
        if (StringUtils.isEmpty(openId)) {
            codeMess = new CodeMess("30000", "重新登录");
            return JSON.toJSONString(codeMess);
        }
        String dateTime = (String) session.getAttribute("BalanceTime");
        if (StringUtils.isEmpty(dateTime) || dateTime == null) {
            session.setAttribute("BalanceTime", DateFormatUtil.formateString());
        } else {
            String now = DateFormatUtil.formateString();
            if (DateFormatUtil.compare_date(dateTime, now)) {
                //超过2分钟
                session.setAttribute("BalanceTime", now);
            } else {
                codeMess = new CodeMess("20000", "订单提交过快，5秒后再试");
                return JSON.toJSONString(codeMess);
            }
        }

        PayInfoModel payInfoModel = new PayInfoModel();
        payInfoModel.setAccountId(openId);
        payInfoModel.setOrderNo(UniqueX.randomUnique());
        payInfoModel.setBusinessInfo("油礼付油滴充值");
        payInfoModel.setDealId(UUIDUtil.getUUID());
        payInfoModel.setDealInfo("油礼付油滴充值(一码付)");
        payInfoModel.setDealState(new Short("2"));
        payInfoModel.setDealType(new Short("2"));
        payInfoModel.setDealTime(DateFormatUtil.formateString());
        payInfoModel.setDealMoney(Double.parseDouble(money));
        payInfoModel.setDealRealMoney(Double.parseDouble(money));
        if(StringUtils.isEmpty(timingTime)){
            timingTime=DateFormatUtil.formateString();
        }
        payInfoModel.setDealRemark(request.getParameter("shareContent")+";;"+isTiming+";;"+timingTime);
        int result = payInfoService.insertPayInfo(payInfoModel);
        if (result == 0) {
            codeMess = new CodeMess("20000", "业务失败");
            return JSON.toJSONString(codeMess);
        }
        String redirectUrl = this.getBaseUrl(request, response).replace(":80", "") +
                "receivePreOrder.htm";//回调地址
        String timestamp = DateFormatUtil.formateString();
        DecimalFormat df = new DecimalFormat("######0");
        String md5 = MD5Util.MD5Encode("cmbcpay" + openId + String.valueOf(df.format(payInfoModel.getDealRealMoney() * 100) + timestamp)).toUpperCase();
        JSONObject object = new JSONObject();
        object.put("openId", openId);
        object.put("money", Double.parseDouble(money) * 100);
        object.put("goodsName", "油礼付油滴充值");
        object.put("redirectUrl", redirectUrl);
        object.put("orderNo", payInfoModel.getOrderNo());
        object.put("remark", "");
        object.put("fromName", "giftpay");
        object.put("payType", "");
        object.put("timestamp", timestamp);
        object.put("md5", md5);
        String backUrl = doMainUrl+"/giftpay_socket/shareInfo.html?orderId=" + payInfoModel.getOrderNo();
        object.put("backUrl", backUrl);
        codeMess = new CodeMess("10000", object.toString());
        return JSON.toJSONString(codeMess);
    }

    /**
     * 支付回调方法
     */
    @RequestMapping(value = "/receivePreOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String receivePreOrder(HttpServletRequest request, HttpServletResponse response, ModelMap model, HttpSession session)
            throws Exception {
        logger.info("=================进入支付回调方法");
        String orderNo = request.getParameter("orderNo");
        String bizOrderNumber = request.getParameter("bizOrderNumber");
        String srcAmt = request.getParameter("srcAmt");
        String openId = request.getParameter("openId");
        logger.error("订单参数：" + orderNo + ",民生参数：" + bizOrderNumber);
        PayInfoModel payInfoModel = new PayInfoModel();
        payInfoModel.setOrderNo(orderNo);
        List<PayInfoModel> list = this.payInfoService.queryPayInfos(payInfoModel);
        if (list.size() != 0) {
            payInfoModel = list.get(0);
            if (2 == payInfoModel.getDealState()) {
                payInfoModel.setDealState(new Short("1"));
                payInfoModel.setWxOrder(bizOrderNumber);//订单ID
                int result1 = payInfoService.updatePayInfo(payInfoModel);
                RoomRecord roomRecord = new RoomRecord();
                roomRecord.setOpenId(payInfoModel.getAccountId());
                roomRecord.setId(orderNo);
                UserModel userModel = new UserModel();
                userModel.setOpenId(payInfoModel.getAccountId());
                List<UserModel> userModels = this.userModelService.queryUserModelList(userModel);
                userModel = userModels.get(0);
                roomRecord.setHeadImg(userModel.getHeadImg());
                roomRecord.setNickName(userModel.getNickName());
                DecimalFormat df = new DecimalFormat("######0");
                roomRecord.setOilNum(String.valueOf(df.format(Double.parseDouble(srcAmt)*100)));
                String[] params=payInfoModel.getDealRemark().split(";;");
                roomRecord.setShareContent(params[0]);
                roomRecord.setIsTiming(params[1]);
                if(!StringUtils.isEmpty(params[2])){
                    roomRecord.setTimingTime(params[2]);
                }else{
                    roomRecord.setTimingTime(DateFormatUtil.formateString());
                }
                redisCacheUtil.incrRedisCacheMap("room:oilNum:"+orderNo,Integer.valueOf(roomRecord.getOilNum()));
                this.roomRecordModelService.addRoomRecord(roomRecord);
            }
        }
        return null;
    }

    /**
     * 执行当前业务方法(自定义支付)
     */
    @RequestMapping(value = "/freeOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String freeOrder(HttpServletRequest request, HttpServletResponse response, ModelMap model,
                            HttpSession session) {
        String oilNum = request.getParameter("oilNum");
        String shareContent=request.getParameter("shareContent");
        String isTiming=request.getParameter("isTiming");
        String timingTime=request.getParameter("timingTime");
        CodeMess codeMess;

        UserModel userModel=JSONObject.parseObject(session.getAttribute(OilContant.SESSION_USERMODEL).toString(), UserModel.class);
        if (userModel == null) {
            codeMess = new CodeMess("30000", "重新登录");
            return JSON.toJSONString(codeMess);
        }
        String openId = userModel.getOpenId();
        OilBalanceModel balanceModel=new OilBalanceModel();
        balanceModel.setOpenId(openId);
        List<OilBalanceModel> balanceModels=this.oilBalanceService.queryOilBalanceList(balanceModel);
        if(balanceModels.size()==0){
            codeMess = new CodeMess("30000", "获取用户信息失败");
            return JSON.toJSONString(codeMess);
        }
        if(Integer.valueOf(balanceModels.get(0).getOilBalance())<(Integer.valueOf(oilNum))){
            codeMess = new CodeMess("30000", "油滴数余额不足");
            return JSON.toJSONString(codeMess);
        }
        balanceModel=balanceModels.get(0);
        balanceModel.setOilBalance(String.valueOf(Integer.valueOf(balanceModel.getOilBalance())-Integer.valueOf(oilNum)));
        int res=this.oilBalanceService.updateOilBalanceModel(balanceModel);
        if(res==0){
            codeMess = new CodeMess("30000", "分享失败");
            return JSON.toJSONString(codeMess);
        }
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setOpenId(openId);
        roomRecord.setId(UUIDUtil.getUUID());
        roomRecord.setHeadImg(userModel.getHeadImg());
        roomRecord.setNickName(userModel.getNickName());
        roomRecord.setOilNum(oilNum);
        roomRecord.setShareContent(shareContent);
        roomRecord.setIsTiming(isTiming);
        if(!StringUtils.isEmpty(timingTime)){
            roomRecord.setTimingTime(timingTime);
        }else{
            roomRecord.setTimingTime(DateFormatUtil.formateString());
        }
        redisCacheUtil.incrRedisCacheMap("room:oilNum:"+roomRecord.getId(),Integer.valueOf(roomRecord.getOilNum()));
        this.roomRecordModelService.addRoomRecord(roomRecord);
        codeMess = new CodeMess("10000", roomRecord.getId());
        return JSONObject.toJSONString(codeMess);
    }



    protected String getBaseUrl(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path
                + "/";
        return basePath;
    }

    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("######0");
    }

}

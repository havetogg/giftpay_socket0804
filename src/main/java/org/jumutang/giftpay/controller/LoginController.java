package org.jumutang.giftpay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.ThreadLocalCache;
import io.goeasy.GoEasy;
import org.jumutang.giftpay.common.redis.RedisCacheUtil;
import org.jumutang.giftpay.common.util.SensitiveWordFilter;
import org.jumutang.giftpay.entity.*;
import org.jumutang.giftpay.service.*;
import org.jumutang.giftpay.tools.EmojiFilter;
import org.jumutang.giftpay.tools.MD5Util;
import org.jumutang.giftpay.tools.WXShareUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by RuanYJ on 2017/6/29.
 */
@Controller
public class LoginController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("#{propertyFactoryBean['SCOPEBASE']}")
    private String SCOPEBASE;
    @Value("#{propertyFactoryBean['SCOPEUSERINFO']}")
    private String SCOPEUSERINFO;
    @Value("#{propertyFactoryBean['OTPKEK']}")
    private static String OTPKEK;
    @Value("#{propertyFactoryBean['app_id']}")
    protected String appId;
    @Value("#{propertyFactoryBean['app_secret']}")
    protected String appSecret;
    @Value("#{propertyFactoryBean['getUserInfoUrl']}")
    private String getUserInfoUrl;
    @Value("#{propertyFactoryBean['DOMAINURL']}")
    private String doMainUrl;

    @Autowired
    private RedisCacheUtil<Object> redisCacheUtil;
    @Autowired
    private OilRecordModelService oilRecordModelService;
    @Autowired
    private UserModelService userModelService;
    @Autowired
    private RoomRecordModelService roomRecordModelService;
    @Autowired
    private ContentModelService contentModelService;
    @Autowired
    private OilBalanceService oilBalanceService;
    @Autowired
    private UserRateService userRateService;

    @RequestMapping("/loginBase")
    public void loginBase(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String roomRecordId = req.getParameter(OilContant.SESSION_ROOMRECORDID);
        String entryUrl = URLEncoder.encode(doMainUrl + "/queryUserInfo.htm?roomRecordId=" + roomRecordId);
        String targetUrl = getUserInfoUrl + "?entryUrl=" + entryUrl + "&scope=" + SCOPEUSERINFO;
        logger.info(targetUrl);
        resp.sendRedirect(resp.encodeRedirectURL(targetUrl));
    }

    @RequestMapping("/queryUserInfo")
    public void queryUserInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        logger.info("获取用户信息");
        String openId = request.getParameter("openid");
        String roomRecordId = request.getParameter(OilContant.SESSION_ROOMRECORDID);
        String headImgUrl = request.getParameter(OilContant.SESSION_HEADIMG);
        logger.info("头像地址:" + headImgUrl + ",用户OPNEID:" + openId + "房间号:" + roomRecordId);
        String nickName = filterNickName(request);
        logger.info("用户昵称:" + nickName);
        String targetUrl = "";
        UserModel userModel = isExistUserInfo(openId, headImgUrl, nickName);//判断用户是否存在  余额表是否存在
        if (openIdISNull(openId)) {
            String entryUrl = URLEncoder.encode(doMainUrl + "/queryUserInfo.htm?roomRecordId=" + roomRecordId);
            logger.info("静默授权未获取到openId，跳转到手动授权");
            targetUrl = getUserInfoUrl + "?entryUrl=" + entryUrl + "&scope=" + SCOPEUSERINFO;
        } else {
            UserRateModel rateModel = new UserRateModel();
            rateModel.setOpenId(userModel.getOpenId());
            List<UserRateModel> rates = this.userRateService.queryUserRateList(rateModel);
            String rate = "";
            if (rates.size() == 0) {
                //不存在概率记录
                rate = "20";
                rateModel.setRate(rate);
                rateModel.setRuleDesc("有礼付授权用户");
                rateModel.setStatus("0");
                this.userRateService.addUserRateModel(rateModel);
            } else {
                rate = this.userRateService.queryUserAllRate(rateModel);
            }
            userModel.setRate(rate);
            session.setAttribute(OilContant.SESSION_USERMODEL, JSON.toJSONString(userModel));
            session.setAttribute(OilContant.SESSION_ROOMRECORDID, roomRecordId);
            RoomRecord roomRecord = new RoomRecord();
            roomRecord.setId(roomRecordId);
            List<RoomRecord> roomRecordList = this.roomRecordModelService.queryRoomRecordList(roomRecord);//获取房间号ID
            if (roomRecordList.size() == 0) {
                logger.info("未查询到分享的房间");

            } else if (roomRecordList.get(0).getStatus().equals(OilContant.ONE)) {
                roomRecord = roomRecordList.get(0);
                session.setAttribute("roomData", JSON.toJSONString(roomRecord));
                logger.info("游戏已结束");
                targetUrl = "catchOilSuccess.html?openId=" + openId + "&img=" + headImgUrl + "&name=" + URLEncoder.encode(URLEncoder.encode(nickName, "utf-8")
                        , "utf-8") + "&roomOpenId=" + roomRecord.getOpenId() + "&roomHead=" + roomRecord.getHeadImg() + "&roomName=" + URLEncoder.encode(URLEncoder.encode(roomRecord.getNickName(), "utf-8")) + "&roomRecordId=" + roomRecordId + "&isInit=" + userModel.getIsInit();
            } else {
                roomRecord = roomRecordList.get(0);//获取到分享人的头像 id等信息 包括房间号
                session.setAttribute("roomData", JSON.toJSONString(roomRecord));
                String userOilNum = redisCacheUtil.getCacheObject("room:" + roomRecordId + ":" + openId);
                if (userOilNum == null) {
                    redisCacheUtil.incrRedisCacheMap("room:" + roomRecordId + ":" + openId, 0);
                    logger.info("不存在该房间号的用户");
                    OilRecordModel oilRecordModel = new OilRecordModel();
                    oilRecordModel.setRoomId(roomRecordId);
                    oilRecordModel.setOpenId(openId);
                    oilRecordModel.setNickName(nickName);
                    oilRecordModel.setHeadImg(headImgUrl);
                    oilRecordModel.setRoomId(roomRecord.getId());
                    oilRecordModel.setOilNum("");
                    oilRecordModel.setOpenId(openId);
                    oilRecordModel.setRate(rate);
                    this.oilRecordModelService.addOilRecordModel(oilRecordModel);
                }
              /*  OilRecordModel oilRecordModel=new OilRecordModel();
                oilRecordModel.setRoomId(roomRecordId);
                oilRecordModel.setOpenId(openId);
                List<OilRecordModel> list=this.oilRecordModelService.queryOilRecordList(oilRecordModel);
                if(list.size()==0){
                    logger.info("不存在该房间号的用户");
                    oilRecordModel.setNickName(nickName);
                    oilRecordModel.setHeadImg(headImgUrl);
                    oilRecordModel.setRoomId(roomRecord.getId());
                    oilRecordModel.setOilNum("");
                    oilRecordModel.setOpenId(openId);
                    oilRecordModel.setRate(rate);
                    this.oilRecordModelService.addOilRecordModel(oilRecordModel);
                }*/
                redisCacheUtil.setCacheObject("room:user:" + userModel.getOpenId(), JSON.toJSONString(userModel));
                redisCacheUtil.setCacheObject("room:roomRecord:" + roomRecord.getId(), JSON.toJSONString(roomRecord));
                targetUrl = "catchOil.html";
            }
        }
        response.sendRedirect(response.encodeRedirectURL(targetUrl));
    }

    @RequestMapping("/getSocketNowTime")
    @ResponseBody
    public String getSocketNowTime(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Date date = new Date();
        String str = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        CodeMess codeMess = new CodeMess("1", str);
        return JSONObject.toJSONString(str);
    }

    @RequestMapping("/queryContetList")
    @ResponseBody
    public String queryContetList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        JSONObject object = new JSONObject();
        String roomRecordId = req.getParameter(OilContant.SESSION_ROOMRECORDID);
        ContentModel contentModel = new ContentModel();
        contentModel.setRoomId(roomRecordId);
        List<ContentModel> list = this.contentModelService.queryContentList(contentModel);
        if (list.size() == 0) {
            object.put(OilContant.SUCCESS, false);
        } else {
            object.put(OilContant.SUCCESS, true);
            object.put("data", JSONArray.toJSONString(list));
        }
        return object.toJSONString();
    }

    @RequestMapping("/isExistUserLogin")
    @ResponseBody
    public String isExistUserLogin(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        String openId = req.getParameter(OilContant.SESSION_OPENID);
        CodeMess codeMess = null;
        String obj = session.getAttribute(OilContant.SESSION_USERMODEL).toString();
        UserModel userModel = JSONObject.parseObject(obj, UserModel.class);
        logger.error("(isExistUserLogin)查询用户是否登录:" + userModel);
        if (userModel == null) {
            logger.error("未登录");
            codeMess = new CodeMess(OilContant.ONE, "未登录");
        } else if (!userModel.getOpenId().equals(openId)) {
            codeMess = new CodeMess(OilContant.ONE, "未登录");
        } else {
            codeMess = new CodeMess(OilContant.ZERO, "");
        }
        return JSONObject.toJSONString(codeMess);
    }

    @RequestMapping("/addContentModel")
    @ResponseBody
    public String addContentModel(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String roomRecordId = req.getParameter(OilContant.SESSION_ROOMRECORDID);
        String content = req.getParameter("content");
        content = URLDecoder.decode(URLDecoder.decode(content, "utf-8"), "utf-8");
        String obj = session.getAttribute(OilContant.SESSION_USERMODEL).toString();
        UserModel userModel = JSONObject.parseObject(obj, UserModel.class);

        SensitiveWordFilter filter = SensitiveWordFilter.getInstance();
        content = filter.replaceSensitiveWord(content, SensitiveWordFilter.maxMatchType, "*");

        ContentModel contentModel = new ContentModel();
        contentModel.setRoomId(roomRecordId);
        contentModel.setHeadImg(userModel.getHeadImg());
        contentModel.setOpenId(userModel.getOpenId());
        contentModel.setNickName(userModel.getNickName());
        contentModel.setContent(URLEncoder.encode(URLEncoder.encode(content, "utf-8"), "utf-8"));
        int result = this.contentModelService.addContentModel(contentModel);
        JSONObject object = new JSONObject();
        object.put("result", result);
        object.put("data", JSONObject.toJSONString(contentModel));
        return object.toJSONString();
    }

    @RequestMapping("/queryRoomData")
    @ResponseBody
    public String queryRoomData(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        JSONObject object = new JSONObject();
        String roomRecordId = session.getAttribute(OilContant.SESSION_ROOMRECORDID).toString();
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setId(roomRecordId);
        List<RoomRecord> roomRecordList = this.roomRecordModelService.queryRoomRecordList(roomRecord);//获取房间号ID
        if (roomRecordList.size() == 0) {
            logger.info("未查询到分享的房间");
            object.put("success", false);
            return object.toJSONString();
        }
        object.put("roomData", JSONObject.toJSONString(roomRecordList.get(0)));
        return object.toJSONString();
    }

    @RequestMapping("/initAllData")
    @ResponseBody
    public String initAllData(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        String room = session.getAttribute(OilContant.SESSION_ROOMRECORDID).toString();
        logger.error("房间ID:" + room);
        String userModel = (String) session.getAttribute(OilContant.SESSION_USERMODEL);
        logger.error("用户信息:" + JSONObject.toJSONString(userModel));
        String roomRecord = (String) session.getAttribute("roomData");
        JSONObject returnObj = new JSONObject();
        returnObj.put("userData", userModel);
        returnObj.put("roomData", roomRecord);
        String returnStr = JSON.toJSONString(returnObj);
        return returnStr;
    }


    @RequestMapping("/quertOilRecordListRank")
    @ResponseBody
    public String quertOilRecordListRank(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        JSONObject object = new JSONObject();
        String roomRecordId = session.getAttribute(OilContant.SESSION_ROOMRECORDID).toString();
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setId(roomRecordId);
        List<RoomRecord> roomRecordList = this.roomRecordModelService.queryRoomRecordList(roomRecord);//获取房间号ID
        if (roomRecordList.size() == 0) {
            logger.info("未查询到分享的房间");
        }
        roomRecord = roomRecordList.get(0);//获取到分享人的头像 id等信息 包括房间号
        OilRecordModel oilRecordModel = new OilRecordModel();
        oilRecordModel.setRoomId(roomRecord.getId());
        List<OilRecordModel> list = this.oilRecordModelService.queryOilRecordRankList(oilRecordModel);
        if (list.size() == 0) {
            object.put("success", false);
            return object.toJSONString();
        }
        object.put("data", JSONArray.toJSONString(list));
        object.put("roomData", JSONObject.toJSONString(roomRecord));
        return object.toJSONString();
    }

    @RequestMapping("/quertOilRecordList")
    @ResponseBody
    public String quertOilRecordList(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        JSONObject object = new JSONObject();
        object.put(OilContant.SUCCESS, true);
        String openId = req.getParameter(OilContant.SESSION_OPENID);
        String roomRecordId = session.getAttribute(OilContant.SESSION_ROOMRECORDID).toString();
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setId(roomRecordId);
        List<RoomRecord> roomRecordList = this.roomRecordModelService.queryRoomRecordList(roomRecord);//获取房间号ID
        if (roomRecordList.size() == 0) {
            logger.info("未查询到分享的房间");
        }
        roomRecord = roomRecordList.get(0);//获取到分享人的头像 id等信息 包括房间号
        OilRecordModel oilRecordModel = new OilRecordModel();
        oilRecordModel.setRoomId(roomRecord.getId());
        List<OilRecordModel> list = this.oilRecordModelService.queryOilRecordList(oilRecordModel);
        if (list.size() == 0) {
            object.put(OilContant.SUCCESS, false);
            return object.toJSONString();
        }
        List<OilRecordModel> oilList = new ArrayList<OilRecordModel>();
        for (OilRecordModel oil : list) {
            if (oil.getOpenId().equals(openId)) {
                continue;
            } else {
                oilList.add(oil);
            }
        }
        if (oilList.size() == 0) {
            object.put(OilContant.SUCCESS, false);
            return object.toJSONString();
        }
        object.put("data", JSONArray.toJSONString(oilList));
        object.put("roomData", JSONObject.toJSONString(roomRecord));
        return object.toJSONString();
    }


    protected boolean openIdISNull(String openId) {
        if (StringUtils.isEmpty(openId) || openId.equals("") || openId == null || openId.equals("null")) {
            return true;
        }
        return false;
    }

    protected String getBaseUrl(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path
                + "/";
        return basePath;
    }

    protected String filterNickName(HttpServletRequest request) throws UnsupportedEncodingException {
        String nickName = request.getParameter("nickname");
//        nickName = new String(nickName.getBytes("ISO-8859-1"), "UTF-8");
        nickName = URLEncoder.encode(nickName, "utf-8");
        return nickName;
    }

    @RequestMapping("/updateRoomRecordStatus")
    @ResponseBody
    public String updateRoomRecordStatus(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String roomRecordId = req.getParameter(OilContant.SESSION_ROOMRECORDID);
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setId(roomRecordId);
        List<RoomRecord> roomRecords = this.roomRecordModelService.queryRoomRecordList(roomRecord);
        CodeMess codeMess = null;
        if (roomRecords.size() == 0) {
            codeMess = new CodeMess(OilContant.ONE, "房间状态错误");
        } else if (roomRecords.get(0).getStatus().equals("1")) {
            codeMess = new CodeMess(OilContant.ONE, "油滴已抢完");
        } else {
            roomRecord.setStatus(OilContant.ONE);
            this.roomRecordModelService.updateRoomRecord(roomRecord);
            codeMess = new CodeMess(OilContant.ZERO, "更新成功");
        }
        return JSONObject.toJSONString(codeMess);
    }

    @RequestMapping("/queryUserOilBalance")
    @ResponseBody
    public String queryUserOilBalance(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        CodeMess codeMess = null;
        UserModel userModel = JSONObject.parseObject(session.getAttribute(OilContant.SESSION_USERMODEL).toString(), UserModel.class);
        logger.error("(queryUserOilBalance)查询用户是否登录:" + userModel);
        if (userModel == null) {
            codeMess = new CodeMess("1", "未登录");
            return JSONObject.toJSONString(codeMess);
        } else {
            session.setAttribute(OilContant.SESSION_USERMODEL, JSONObject.toJSONString(userModel));
        }
        OilBalanceModel oilBalanceModel = new OilBalanceModel();
        oilBalanceModel.setOpenId(userModel.getOpenId());
        List<OilBalanceModel> oilBalanceModels = this.oilBalanceService.queryOilBalanceList(oilBalanceModel);
        if (oilBalanceModels.size() == 0) {
            codeMess = new CodeMess(OilContant.ONE, "获取用户余额失败，请重新登录");
            return JSONObject.toJSONString(codeMess);
        } else {
            codeMess = new CodeMess(OilContant.ZERO, JSONObject.toJSONString(oilBalanceModels.get(0)));
        }
        return JSONObject.toJSONString(codeMess);
    }

    @RequestMapping("/updateOilRecordStatus")
    @ResponseBody
    public String updateOilRecordStatus(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        synchronized (LoginController.class) {
//        String params=req.getParameter("params");
            String roomRecordId = session.getAttribute(OilContant.SESSION_ROOMRECORDID).toString();
//        String roomRecordId=req.getParameter("roomRecordId");
            CodeMess codeMess = null;
            RoomRecord roomRecord = new RoomRecord();
            roomRecord.setId(roomRecordId);
            List<RoomRecord> roomRecords = this.roomRecordModelService.queryRoomRecordList(roomRecord);
            if (roomRecords.get(0).getStatus().equals(OilContant.ONE)) {
                codeMess = new CodeMess(OilContant.ONE, "房间数据错误");
                return JSONObject.toJSONString(codeMess);
            }
            roomRecord = roomRecords.get(0);
            OilRecordModel oilRecordModel = new OilRecordModel();
            oilRecordModel.setRoomId(roomRecordId);
            List<OilRecordModel> recordModels = this.oilRecordModelService.queryOilRecordList(oilRecordModel);
            int sumOil = 0;
            OilBalanceModel balanceModel = null;

            int numA = 0;//余数
            int aveNum = 0;//平均数
            boolean isJian = true;
            List<OilRecordModel> tempBigNumOilRecord = new ArrayList<OilRecordModel>();


            int redOilNum = 0;//redis存储总油量
            for (OilRecordModel oil : recordModels) {
                String oilNum = redisCacheUtil.getCacheObject("room:" + roomRecordId + ":" + oil.getOpenId());
                oil.setOilNum(oilNum);
                int res = this.oilRecordModelService.updateRecordModel(oil);
                logger.error("更新加油结果:" + res);
                redOilNum = Integer.valueOf(oilNum) + redOilNum;
            }
            logger.error("房间号为:" + roomRecord.getId() + "的油量为:" + redOilNum);
            int roomAllOilNum = Integer.valueOf(roomRecord.getOilNum());//房间总油量
            int countOilNum = 0;//有没有多余油量
            if (redOilNum > roomAllOilNum) {
                logger.error("油量大于用户点击总油量");
                countOilNum = redOilNum - roomAllOilNum;//
                logger.error("多余："+countOilNum+"滴");
                numA = countOilNum % roomRecords.size();//获取余数
                logger.error("余数为："+numA);
                aveNum = (countOilNum - numA) / roomRecords.size();//获取平均数
                logger.error("平均数为:"+aveNum);

                List<OilRecordModel> topOilRecord = this.oilRecordModelService.queryOilRecordRankList(oilRecordModel);
                for (OilRecordModel re : topOilRecord) {
                    if (Integer.valueOf(re.getOilNum()) > aveNum) {
                        tempBigNumOilRecord.add(re);//保存到临时记录表
                    }
                }
                numA = countOilNum % tempBigNumOilRecord.size();//获取余数
                logger.error("2余数为："+numA);
                aveNum = (countOilNum - numA) / tempBigNumOilRecord.size();//获取平均数
                logger.error("2平均数为:"+aveNum);

                logger.error("2大于平均数的集合:"+JSONArray.toJSONString(tempBigNumOilRecord));

                for (OilRecordModel re : tempBigNumOilRecord) {
                    re.setOilNum(String.valueOf(Integer.valueOf(re.getOilNum()) - aveNum));
                    if (Integer.valueOf(re.getOilNum()) > numA&&isJian) {
                        isJian=false;
                        re.setOilNum(String.valueOf(Integer.valueOf(re.getOilNum()) - numA));
                    }
                    this.oilRecordModelService.updateRecordModel(re);
                    redisCacheUtil.setCacheObject("room:" + roomRecordId + ":" + re.getOpenId(), re.getOilNum());
                    logger.error("处理后的数据为:"+JSONObject.toJSONString(re));
                }
                //  OilRecordModel oo = topOilRecord.get(0);
                //获取第一名油量
                //String oilNum = redisCacheUtil.getCacheObject("room:" + roomRecordId + ":" + oo.getOpenId());
                //oilNum = String.valueOf(Integer.valueOf(oilNum) - countOilNum);
                //redisCacheUtil.setCacheObject("room:" + roomRecordId + ":" + oo.getOpenId(), oilNum);
            }
            for (OilRecordModel oil : recordModels) {
//                String oilNum = redisCacheUtil.getCacheObject("room:" + roomRecordId + ":" + oil.getOpenId());
//                sumOil = Integer.valueOf(oilNum) + sumOil;
//                logger.error("房间号:" + roomRecordId + ":" + oil.getOpenId() + "油量为:" + oilNum);
//                oil.setOilNum(oilNum);
//                this.oilRecordModelService.updateRecordModel(oil);
                balanceModel = new OilBalanceModel();
                balanceModel.setOpenId(oil.getOpenId());
                List<OilBalanceModel> oilBalanceModels = this.oilBalanceService.queryOilBalanceList(balanceModel);
                balanceModel = oilBalanceModels.get(0);
                balanceModel.setOilBalance(String.valueOf(Integer.valueOf(balanceModel.getOilBalance()) + Integer.valueOf(oil.getOilNum())));
                this.oilBalanceService.updateOilBalanceModel(balanceModel);
            }
            roomRecord.setStatus("1");
            this.roomRecordModelService.updateRoomRecord(roomRecord);
            logger.error("房间号" + roomRecordId + "的总油数---->" + sumOil);
            codeMess = new CodeMess(OilContant.ZERO, OilContant.ZERO);
            return JSONObject.toJSONString(codeMess);
        }
    }

    @RequestMapping("/loginIndexBase")
    public void loginIndex(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String local_target = doMainUrl + "/indexUserInfo.htm";
        long timestamp = System.currentTimeMillis();
        String md5 = MD5Util.MD5Encode(appId + appSecret + timestamp);
        String gate_domain = "http://www.linkgift.cn/gift_gate/gateGetUserInfo.action?appId=%s&timestamp=%s&signature=%s";
        String gateUrl = String.format(gate_domain, appId, timestamp, md5);

        String target = gateUrl + "&redirect_client_url=" + URLEncoder.encode(local_target);
        final String reVisitUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri=" + URLEncoder.encode(target) + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        resp.sendRedirect(reVisitUrl);
    }

    @RequestMapping("/indexUserInfo")
    public ModelAndView indexUserInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        logger.info("跳转首页获取用户信息方法");
        ModelAndView modelAndView = new ModelAndView("");
        String openId = request.getParameter("openid");
        logger.info("用户OPNEID:" + openId);
        String targetUrl = "";
        if (openIdISNull(openId)) {
            String entryUrl = URLEncoder.encode(doMainUrl + "/indexUserInfo.htm");
            logger.info("跳转首页获取用户信息静默授权未获取到openId，跳转到手动授权");
            targetUrl = getUserInfoUrl + "?entryUrl=" + entryUrl + "&scope=" + SCOPEUSERINFO;
        } else {
            //获取openid  判断用户是否在用户表
            String headImgUrl = request.getParameter(OilContant.SESSION_HEADIMG);
            logger.info("头像--------------地址" + headImgUrl);
            String nickName = filterNickName(request);
            UserModel userModel = isExistUserInfo(openId, headImgUrl, nickName);
            logger.info("==========" + JSONObject.toJSONString(userModel));

            //session.setAttribute(OilContant.SESSION_USERMODEL,userModel);
            session.setAttribute(OilContant.SESSION_USERMODEL, JSON.toJSONString(userModel));

            redisCacheUtil.setCacheObject("room:user:" + userModel.getOpenId(), JSON.toJSONString(userModel));
            //判断用户拥有余额表
            modelAndView.setViewName("index.jsp");
            modelAndView.addObject(OilContant.SESSION_OILBALANCE, userModel.getOilBalance());
            modelAndView.addObject(OilContant.SESSION_OPENID, userModel.getOpenId());
        }
        return modelAndView;

    }

    @RequestMapping("/quertAllOilRecordList")
    @ResponseBody
    public String quertAllOilRecordList(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        String obj = session.getAttribute(OilContant.SESSION_USERMODEL).toString();
        UserModel userModel = JSONObject.parseObject(obj, UserModel.class);
        if (userModel == null) {
            logger.info("请求超时");
        } else {
            session.setAttribute(OilContant.SESSION_USERMODEL, obj);
        }
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setOpenId(userModel.getOpenId());
        List<RoomRecord> list = this.roomRecordModelService.queryRoomRecordList(roomRecord);
        return JSONArray.toJSONString(list);
    }

    @RequestMapping("/getRoomInfo")
    @ResponseBody
    public String getRoomInfo(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        CodeMess codeMess = null;
        String id = req.getParameter("id");
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setId(id);
        List<RoomRecord> list = this.roomRecordModelService.queryRoomRecordList(roomRecord);
        if (list.size() == 0) {
            codeMess = new CodeMess(OilContant.ONE, "订单异常");
        } else {
            roomRecord = list.get(0);
            codeMess = new CodeMess(OilContant.ZERO, JSONObject.toJSONString(roomRecord));
        }
        return JSONObject.toJSONString(codeMess);
    }

    @RequestMapping("/shareFriend")
    @ResponseBody
    public String shareFriend(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws Exception {
        logger.error("------------------------------进入分享方法-----[当前分享url:" + request.getParameter("url") + "]----------------------------------------------------------------------");
        String config = null;
        WXShareUtil shareUtil = WXShareUtil.getInstance(this.appId, this.appSecret, "prod");
        String url = request.getParameter("url");
        try {
            config = shareUtil.genJSSDKConf(url);
            return config;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    @RequestMapping("/updateInitStatus")
    @ResponseBody
    public String updateInitStatus(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {
        String obj = session.getAttribute(OilContant.SESSION_USERMODEL).toString();
        UserModel userModel = JSONObject.parseObject(obj, UserModel.class);
        if (userModel == null) {
            logger.info("请求超时");
        } else {
            session.setAttribute(OilContant.SESSION_USERMODEL, obj);
        }
        userModel.setIsInit(OilContant.ONE);
        int res = this.userModelService.updateUserInitStatus(userModel);
        logger.error("修改用户不显示引导动画:" + JSONObject.toJSONString(userModel) + ",结果:" + res);
        return String.valueOf(res);
    }

    protected UserModel isExistUserInfo(String openId, String headImgUrl, String nickName) {
        UserModel userModel = new UserModel();
        userModel.setOpenId(openId);
        List<UserModel> userModels = this.userModelService.queryUserModelList(userModel);
        if (userModels.size() == 0) {
            //不存在用户表
            userModel.setHeadImg(headImgUrl);
            userModel.setNickName(nickName);
            userModel.setPhone("");
            this.userModelService.addUserModel(userModel);
        } else {
            userModel = userModels.get(0);
        }
        OilBalanceModel oilBalance = new OilBalanceModel();
        oilBalance.setOpenId(openId);
        List<OilBalanceModel> oilBalanceModels = this.oilBalanceService.queryOilBalanceList(oilBalance);
        if (oilBalanceModels.size() == 0) {
            //用户油滴余额信息不存在
            oilBalance.setOilBalance(OilContant.ZERO);
            this.oilBalanceService.addOilBalanceModel(oilBalance);
            userModel.setOilBalance(OilContant.ZERO);
        } else {
            userModel.setOilBalance(oilBalanceModels.get(0).getOilBalance());
        }
        return userModel;
    }

    public String goEasyOTP(String secretKey) {
        try {
            String otp = "000" + System.currentTimeMillis();
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            byte[] otpBytes = otp.getBytes();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedOTP = cipher.doFinal(otpBytes);
            otp = new BASE64Encoder().encode(encryptedOTP);
            return otp;
        } catch (Exception e) {
            logger.error("获取OTP错误:" + e.toString());
            return "";
        }
    }

    public static void main(String[] args) {
        int aa = 307;
        System.out.println(aa / 10);
        System.out.println(aa % 10);
        //


    }

}

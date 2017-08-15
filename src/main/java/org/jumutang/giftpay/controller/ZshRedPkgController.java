package org.jumutang.giftpay.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jumutang.giftpay.common.redis.RedisCacheUtil;
import org.jumutang.giftpay.entity.*;
import org.jumutang.giftpay.service.*;
import org.jumutang.giftpay.tools.DateFormatUtil;
import org.jumutang.giftpay.tools.HttpUtil;
import org.jumutang.giftpay.tools.MobileMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by RuanYJ on 2017/7/13.
 */
@Controller
public class ZshRedPkgController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String ZSHOPENIDUrl = "http://sgs-wx.jspec.cn/api/getOpenid?sgsAppID=ipay&noticeUrl=REDURL";
    @Value("#{propertyFactoryBean['ADDGIFTPAYUSER']}")
    private String ADDGIFTPAYUSER ;
    @Value("#{propertyFactoryBean['QUERYGIFTPAYUSER']}")
    private String QUERYGIFTPAYUSER ;
    @Value("#{propertyFactoryBean['ADDREDPKG']}")
    private String ADDREDPKG;
    @Value("#{propertyFactoryBean['UPDATEREDPKG']}")
    private String UPDATEREDPKG;
    @Value("#{propertyFactoryBean['DOMAINURL']}")
    private String doMainUrl;

    @Autowired
    private RedisCacheUtil<CodeMess> redisCacheUtil;

    @Autowired
    private UserModelService userModelService;
    @Autowired
    private RedpkgRecordService redpkgRecordService;
    @Autowired
    private OilBalanceService oilBalanceService;

    @RequestMapping("/isExistUserPhone")
    @ResponseBody
    public String isExistUserPhone(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        CodeMess codeMess = null;
        String obj = session.getAttribute(OilContant.SESSION_USERMODEL).toString();
        UserModel model=JSONObject.parseObject(obj,UserModel.class);
        if (model == null) {
            codeMess = new CodeMess("1", "链接超时");
            return JSONObject.toJSONString(codeMess);
        } else {
            session.setAttribute(OilContant.SESSION_USERMODEL, obj);
        }
        if (model.getPhone().equals("")) {
            //判断用户手机号是否填写
            codeMess = new CodeMess("2", "手机号未填写");
            return JSONObject.toJSONString(codeMess);
        } else {
            codeMess = new CodeMess("0", "已填写手机号");
            return JSONObject.toJSONString(codeMess);
        }
    }

    @RequestMapping("/updateUserPhone")
    @ResponseBody
    public String updateUserPhone(HttpServletRequest request, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        String mobile = request.getParameter("mobile");
        String valNum = request.getParameter("valNum");//用户输入的验证码
        String redPkgMsgNum = session.getAttribute(mobile).toString();//获取到的验证码
        CodeMess codeMess = null;
        String obj = session.getAttribute(OilContant.SESSION_USERMODEL).toString();
        UserModel model=JSONObject.parseObject(obj,UserModel.class);
        if (model == null) {
            codeMess = new CodeMess("20000", "链接超时");
            return JSONObject.toJSONString(codeMess);
        } else {
            session.setAttribute(OilContant.SESSION_USERMODEL, obj);
        }
        if (!valNum.equals(redPkgMsgNum)) {
            codeMess = new CodeMess("20000", "验证码错误！");
            return JSONObject.toJSONString(codeMess);
        } else {
            codeMess = new CodeMess("10000", "验证通过");
            model.setPhone(mobile);
            this.userModelService.updateUserPhone(model);
            session.setAttribute(OilContant.SESSION_USERMODEL, obj);
            return JSONObject.toJSONString(codeMess);
        }
    }

    /**
     * 获取验证码
     */
    @RequestMapping(value = "/sendMsg", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void sendMsg(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws UnsupportedEncodingException {
        String phone = request.getParameter("phone");
        Random random = new Random();
        int valNum = random.nextInt(899999);
        valNum = valNum + 100000;//随机六位数
        String msgContent = "【有礼付】您的验证码是" + valNum + "，如非本人操作请忽略该短信。";
        logger.info("发送短信验证码为：" + valNum);
        net.sf.json.JSONObject jsonObject = MobileMessageUtil.sendMessage(phone, msgContent);
        logger.info("发送短信结果返回：" + jsonObject);
        session.setAttribute(phone, String.valueOf(valNum));
    }

    @Transactional
    @RequestMapping(value = "/getZshOpenId")
    @ResponseBody
    public void getZshOpenId(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String oilCardNum = request.getParameter("oilCardNum");
        String redUrl = URLEncoder.encode(doMainUrl+ "/addUserId.htm?oilCardNum=" + oilCardNum);
        response.sendRedirect(response.encodeRedirectURL(ZSHOPENIDUrl.replace("REDURL", redUrl)));
    }

    @Transactional
    @RequestMapping(value = "/addUserId")
    @ResponseBody
    public void addThirdUserInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        CodeMess codeMess = null;
        String oilCardNum = request.getParameter("oilCardNum");
        String userOpenid = request.getParameter("userOpenid");           //中石化openId
        String obj = session.getAttribute(OilContant.SESSION_USERMODEL).toString();
        UserModel model=JSONObject.parseObject(obj,UserModel.class);
        int cardNum = Integer.valueOf(oilCardNum);
        if (model == null) {
            logger.error("链接超时,重新登录");
        } else {
            logger.error("添加有礼付用户:" + JSONObject.toJSONString(model) + ",中石化openId：" + userOpenid);
            session.setAttribute(OilContant.SESSION_USERMODEL, obj);
        }
        String openId = model.getOpenId();
        String phone = model.getPhone();
        Map<String, String> params = new HashMap<String, String>();
        params.put("openId", openId);
        params.put("phone", phone);
        params.put("type", "10");
        HttpUtil.sendPost(ADDGIFTPAYUSER, HttpUtil.UTF8, params);
        params.put("openId", userOpenid);
        HttpUtil.sendPost(ADDGIFTPAYUSER, HttpUtil.UTF8, params);
        String result = HttpUtil.sendPost(QUERYGIFTPAYUSER, HttpUtil.UTF8, params);
        if (StringUtils.isEmpty(result)) {
            logger.error("查询用户数据出错");
        }
        JSONObject object = JSONObject.parseObject(result);
        JSONObject userInfo = object.getJSONObject("mainUser");
        String userId = userInfo.getString("id");
        params = new HashMap<String, String>();
        params.put("openId", userOpenid);
        params.put("channelId", "0001");
        params.put("userId", userId);
        params.put("phone", model.getPhone());

        Map<String, String> updateParams = new HashMap<String, String>();
        updateParams.put("redpkgId", "80");
        updateParams.put("openId", userOpenid);
        updateParams.put("phone", model.getPhone());
        updateParams.put("state", "1");
        updateParams.put("reState", "4");
        logger.error("添加红包参数:" + JSONObject.toJSONString(params));
//        for(int i=0;i<cardNum;i++){
        result = HttpUtil.sendPost(ADDREDPKG, HttpUtil.UTF8, params);
        logger.error("查询添加红包返回信息:" + result);
//        }
//        for(int i=0;i<cardNum;i++){
        result = HttpUtil.sendPost(UPDATEREDPKG, HttpUtil.UTF8, updateParams);
        logger.error("查询更新红包返回信息:" + result);
//        }

        RedpkgModel redpkgModel = new RedpkgModel();
        redpkgModel.setOpenId(openId);
        redpkgModel.setZshOpenId(userOpenid);
        redpkgModel.setRedpkgDesc("油礼付50元红包");
        redpkgModel.setRedpkgValue("50");
        redpkgModel.setRedpkgStatus("0");
        redpkgModel.setRedpkgId("80");
        this.redpkgRecordService.addRedpkgRecord(redpkgModel);
        OilBalanceModel oilBalanceModel = new OilBalanceModel();
        oilBalanceModel.setOpenId(openId);
        List<OilBalanceModel> list = this.oilBalanceService.queryOilBalanceList(oilBalanceModel);
        oilBalanceModel = list.get(0);
        String payOilNum = String.valueOf(Integer.valueOf(oilBalanceModel.getOilBalance()) - cardNum * 5000);
        oilBalanceModel.setOilBalance(payOilNum);
        this.oilBalanceService.updateOilBalanceModel(oilBalanceModel);
        String targetUrl = "exchange.html?goodsNum=" + cardNum +
                "&goodsName=" + URLEncoder.encode("中石化50元无门槛加油红包", "utf-8") + "&goodsPrice=" + (cardNum * 5000) + "&goodsTime=" + DateFormatUtil.formateString();
        response.sendRedirect(response.encodeRedirectURL(targetUrl));
    }


    @Transactional
    @RequestMapping(value = "/queryRedpkgList")
    @ResponseBody
    public String queryRedpkgList(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        CodeMess codeMess = null;
        String obj = session.getAttribute(OilContant.SESSION_USERMODEL).toString();
        UserModel model=JSONObject.parseObject(obj,UserModel.class);
        if (model == null) {
            logger.error("链接超时,重新登录");
        } else {
            session.setAttribute(OilContant.SESSION_USERMODEL, obj);
        }
        RedpkgModel redpkgModel = new RedpkgModel();
        redpkgModel.setOpenId(model.getOpenId());
        List<RedpkgModel> list = this.redpkgRecordService.queryRedpkgList(redpkgModel);
        if (list.size() == 0) {
            codeMess = new CodeMess("1", "无红包数据");
        } else {
            codeMess = new CodeMess("0", JSONArray.toJSONString(list));
        }
        return JSONObject.toJSONString(codeMess);
    }


    protected String getBaseUrl(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path
                + "/";
        return basePath;
    }


}

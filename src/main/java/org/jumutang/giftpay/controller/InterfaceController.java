package org.jumutang.giftpay.controller;

import com.alibaba.fastjson.JSONObject;
import org.jumutang.giftpay.common.constant.NumConstant;
import org.jumutang.giftpay.entity.CodeMess;
import org.jumutang.giftpay.entity.OilBalanceModel;
import org.jumutang.giftpay.entity.PayInfoModel;
import org.jumutang.giftpay.entity.UserModel;
import org.jumutang.giftpay.service.OilBalanceService;
import org.jumutang.giftpay.service.PayInfoService;
import org.jumutang.giftpay.service.UserModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by RuanYJ on 2017/8/3.
 */
@Controller
@RequestMapping(value = "/interface", method = {RequestMethod.POST, RequestMethod.GET})
public class InterfaceController {
    private  static Logger logger= LoggerFactory.getLogger(InterfaceController.class);
    @Autowired
    private OilBalanceService oilBalanceService;
    @Autowired
    private UserModelService userModelService;
    @Autowired
    private PayInfoService payInfoService;

    private static final String REQUEST_GET = "GET";
    private static final String REQUEST_POST = "POST";
    private static final String REQUEST_METHOD = "methodName";
    private static final String REQUEST_URLADDR = "remoteAddr";
    private static final String REQUEST_HOST = "remoteHost";
    private static final String REQUEST_PARAM_OPENID = "openId";
    private static final String REQUEST_PARAM_OILNUM = "oilNum";
    private static final String REQUEST_RIGHTURL = "121.40.127.141";

    /**
     * 执行当前业务方法(自定义支付)
     */
    @RequestMapping(value = "/exchangeOil", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String exchangeOil(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        CodeMess codeMess = null;
        Pattern pattern = Pattern.compile("[0-9]*");
        Map<String, String> map = getUrlRequestParameter(request);
        synchronized (InterfaceController.class) {
            if (map.get(REQUEST_METHOD).toString().equals(REQUEST_GET)) {
                codeMess = new CodeMess(String.valueOf(NumConstant.TWO), "请求方式不正确!");
                return JSONObject.toJSONString(codeMess);
            }
          /*  if (!map.get(REQUEST_HOST).toString().equals(REQUEST_RIGHTURL)) {
                codeMess = new CodeMess(String.valueOf(NumConstant.TWO), "服务器地址不正确调用!");
                return JSONObject.toJSONString(codeMess);
            }*/
            if (map.get(REQUEST_PARAM_OPENID) == null || map.get(REQUEST_PARAM_OPENID).toString().equals("")) {
                codeMess = new CodeMess(String.valueOf(NumConstant.TWO), "缺少参数!");
                return JSONObject.toJSONString(codeMess);
            }
            if (map.get(REQUEST_PARAM_OILNUM) == null || map.get(REQUEST_PARAM_OILNUM).toString().equals("")) {
                codeMess = new CodeMess(String.valueOf(NumConstant.TWO), "缺少参数!");
                return JSONObject.toJSONString(codeMess);
            }
            Matcher isNum = pattern.matcher(map.get(REQUEST_PARAM_OILNUM).toString());
            if (!isNum.matches()) {
                codeMess = new CodeMess(String.valueOf(NumConstant.TWO), "格式非法!");
                return JSONObject.toJSONString(codeMess);
            }
            if (map.get(REQUEST_PARAM_OILNUM).toString().equals("0")) {
                codeMess = new CodeMess(String.valueOf(NumConstant.TWO), "格式非法!");
                return JSONObject.toJSONString(codeMess);
            }
            String openId = map.get(REQUEST_PARAM_OPENID).toString();
            OilBalanceModel userBalance = new OilBalanceModel();
            userBalance.setOpenId(openId);
            List<OilBalanceModel> balanceList = this.oilBalanceService.queryOilBalanceList(userBalance);
            if (balanceList.size() == 0) {
                //不存在余额表
                userBalance.setOilBalance(map.get(REQUEST_PARAM_OILNUM).toString());
                this.oilBalanceService.addOilBalanceModel(userBalance);
                codeMess = new CodeMess(String.valueOf(NumConstant.ONE), "添加用户油滴余额信息成功!");
                return JSONObject.toJSONString(codeMess);
            } else {
                userBalance = balanceList.get(0);
                userBalance.setOilBalance(String.valueOf(Integer.valueOf(userBalance.getOilBalance()) + Integer.valueOf(map.get(REQUEST_PARAM_OILNUM))));
                this.oilBalanceService.updateOilBalanceModel(userBalance);
                codeMess = new CodeMess(String.valueOf(NumConstant.ONE), "更新用户油滴余额信息成功!");
                return JSONObject.toJSONString(codeMess);
            }
        }
    }


    /**
     * 油礼付用户油滴账户统计
     */
    @RequestMapping(value = "/oilUserStatistics", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String oilUserStatistics(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Integer oilSum=this.oilBalanceService.queryOilBalanceSum(new OilBalanceModel());
        Integer userSum=this.userModelService.queryUserCount(new UserModel());
        Integer paySum=this.payInfoService.queryPayInfoSum(new PayInfoModel());
        JSONObject object=new JSONObject();
        //总油滴 总人数 总充值
        object.put("oilSum",oilSum);
        object.put("userSum",userSum);
        object.put("paySum",paySum);
        return object.toJSONString();
    }


    private static Map<String, String> getUrlRequestParameter(HttpServletRequest request) {
        String methodName = request.getMethod();
        String remoteAddr = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();
        String openId = request.getParameter(REQUEST_PARAM_OPENID);
        String oilNum = request.getParameter(REQUEST_PARAM_OILNUM);
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        map.put(REQUEST_METHOD, methodName);
        map.put(REQUEST_URLADDR, remoteAddr);
        map.put(REQUEST_HOST, remoteHost);
        map.put(REQUEST_PARAM_OPENID, openId);
        map.put(REQUEST_PARAM_OILNUM, oilNum);
        logger.error("参数数据:"+JSONObject.toJSONString(map));
        return map;
    }
}

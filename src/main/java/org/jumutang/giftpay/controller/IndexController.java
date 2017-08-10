package org.jumutang.giftpay.controller;

import com.alibaba.fastjson.JSON;
import org.jumutang.giftpay.common.redis.RedisCacheUtil;
import org.jumutang.giftpay.common.util.SensitiveWordFilter;
import org.jumutang.giftpay.entity.CodeMess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/***
 * 测试类
 * @author chencq
 * @date 2017/8/1
 **/
@Controller
@RequestMapping(value = "index")
public class IndexController {

/*
    @Autowired
    private RedisCacheUtil<Object> redisCacheUtil;

    @RequestMapping(value = "login")
    public String login(HttpServletRequest request, String username) {
        String success = JSON.toJSONString(new CodeMess("0000", "success"));
        request.getSession().setAttribute("user", success);
        request.getSession().setAttribute("user2", new CodeMess("0000", "success"));
        return "login";
    }

    @RequestMapping(value = "index")
    public String index(HttpServletRequest request, Model model) {
        Object user1 = request
                .getSession().getAttribute("user");
        System.out.println(user1);

        Object user2 = request
                .getSession().getAttribute("user2");
        System.out.println("user2:" + JSON.toJSONString(user2));


        CodeMess user = JSON.parseObject(request.getSession().getAttribute("user").toString(), CodeMess.class);
        System.out.println("====>>>>>>" + request.getSession().getAttribute("user").toString());
        model.addAttribute("user", user);

        return "index";
    }

    @RequestMapping("/test")
    public void test() {
        ValueOperations<String, String> success = redisCacheUtil.setCacheObject("room:1001:user", JSON.toJSONString(new CodeMess("0000", "success")));
        System.out.println("success:" + success);
        Object cacheObject = redisCacheUtil.getCacheObject("room:1001:user");
        System.out.println("codeMess:" + JSON.toJSONString(cacheObject));


        List<CodeMess> arrayList = new ArrayList<CodeMess>();
        arrayList.add(new CodeMess("1000", "success"));
        arrayList.add(new CodeMess("2000", "success"));
        redisCacheUtil.setCacheList("room:1002:user",arrayList);

        List<Object> cacheList = redisCacheUtil.getCacheList("room:1002:user");
        System.out.println("list:" + JSON.toJSONString(cacheList));

        Integer key1111 = redisCacheUtil.getCacheObject("key1111");
        System.out.println(key1111 == null);
        //System.out.println("--->>>>>" + key1111 == null);

        System.out.println("--->>>" + redisCacheUtil.getCacheObject("room:f277e10d941d4a88ae65767f45d4d385:o4FD4v6oEoNMA9Fbggg4mS-HKg5U"));
    }

    *//***
     * 敏感字过滤
     * @param request
     * @param response
     * @throws IOException
     *//*
    @RequestMapping("/word")
    public void testWord(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String word = request.getParameter("word");

        String sourceWord = ""
               // + "太多的伤感情怀也许只局限于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
               // + "然后法轮功 我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
               // + "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三级片 深人静的晚上，关上电话静静的发呆着。"
                + "你妈个逼干你王八蛋狗日子小鬼子傻逼"
               // + "操你大爷的干你狗日的呆逼傻逼傻X傻帽傻B我日日你妈个逼逼养的比样的假钞办证你妹的共X党阿凡提机菊花洞"
                + "";
        //1.
        SensitiveWordFilter filter =  SensitiveWordFilter.getInstance();

        if(StringUtils.isEmpty(word)) {
            //默认测试
            word = sourceWord;
        }
        //2.
        String x = filter.replaceSensitiveWord(word, SensitiveWordFilter.maxMatchType, "*");
        System.out.println(x);
        CodeMess codeMess = new CodeMess("1001", x);
        response.getOutputStream().println(JSON.toJSONString(codeMess));
    }*/
}


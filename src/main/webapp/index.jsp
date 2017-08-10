<%@ page language="java" import="java.util.*" isELIgnored="false" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="initial-scale=0.5,maximum-scale=0.5,minimum-scale=0.5, width=640, target-densitydpi=device-dpi">
    <meta http-eqiv="X-UA-Compatible" content="IE=Edge,chrome=1">
    <meta content="yes" name="apple-mobile-web-app-capable">
    <link rel="shortcut icon" href="oil.ico">
    <meta content="black" name="apple-mobile-web-app-status-bar-style">
    <meta content="telephone=no" name="format-detection">
    <title>油礼付</title>
    <link type="text/css" href="css/common/common.css" rel="stylesheet">
    <link rel="stylesheet" href="css/animate.min.css">
    <link type="text/css" href="css/app.css" rel="stylesheet">
    <script type="text/javascript" src="js/common/jQuery-1.11.3.js"></script>
    <script type="text/javascript" src="js/common/common.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
    <script type="text/javascript" src="js/common/jWeChat-Adaptive.js"></script>
    <script type="text/javascript" src="js/common/m.tool.juxinbox.com.js"></script>
</head>
<script>
    $(function(){
        $(".number").text('${oilBalance}');
    })
</script>
<body style="background-color: #ff7e37 !important;">
<div class="zoomer" style="background-color: #ff7e37 !important;">
    <div class="content index_bg">
        <div class="" onclick="toPayMentInfo()">
            <img class="index_oilBtn" src="img/oil.png" alt="">
            <div class="index_oilBtn_text">我也要发油</div>
        </div>
        <div class="index_oils">
            <label>我的油库:</label>
            <label class="number"></label>
            <label>滴</label>
        </div>
        <div class="index_btns">
            <label onclick="exchangeOilRed('${oilBalance}')">兑换加油红包</label>
            <label onclick="haveSend()">我发出的</label>
        </div>
        <div class="index_logo">
            <img src="img/logo.png" alt="">
        </div>
    </div>
    <input id="oilBalance" type="hidden" value="${oilBalance}" />
    <input id="openId" type="hidden" value="${openId}" />
</div>
</body>
</html>
/**
 * backdrop UI component
 */
var paytype = '';
var TipShow = function (msg, duration) {
    var timeoutId = -1;
    var $backdropObj = $(".loading-backdrop");
    if (!$backdropObj[0]) {
        htmlStr = "<div class='loading-backdrop'>" +
            "<div class='loading-wrapper'>" +
            "<div class='loading-content'>" +
            msg +
            "</div></div></div>";
        $("body").append(htmlStr);
        $(".loading-backdrop").addClass('visible');
        if (typeof duration == "number" && duration > 0) {
            if (timeoutId > 0) {
                clearTimeout(timeoutId);
                delete timeoutId;
            }
            timeoutId = setTimeout(function () {
                TipHide()
            }, duration);
        }
    } else {
        $(".loading-content")[0].innerText = msg;
        $(".loading-backdrop").addClass('visible');
        if (typeof duration == "number" && duration > 0) {
            if (timeoutId > 0) {
                clearTimeout(timeoutId);
                delete timeoutId;
            }

            timeoutId = setTimeout(function () {
                TipHide()
            }, duration);
        }
    }
};
var TipHide = function () {
    $(".loading-backdrop").removeClass('visible');
};
var payOilNumber = 0;
//首页
function exchangeOilRed() {
    self.location = './exchangeOilRed.html';
}
function haveSend() {
    window.location.href = './redTicket.html';
}
//支付 页面
var selectItem = function (self) {
    paytype = "wxpay";
    $('.oilSelect').find('li').attr('class', 'flex-1');
    $(self).attr('class', 'flex-1 selectedItem');
    payOilNumber = $(self).find('a').html();
    $(".moneyNumber").html(payOilNumber * 1 / 100 + "元");
    if (payOilNumber >= 5000) {
        $('.catchTime').show();
    }
    else {
        $('.catchTime').hide();
        $("#date").val("");
    }
}
function oilBossPay() {
    var date = $("#date").val();
    var isTiming = '';
    var timingTime = '';
    if (date == '') {
        isTiming = '0';
    } else {
        isTiming = '1';
        timingTime = date;
    }
    var shareContent = $("#shareContent").val();
    if (shareContent == '') {
        shareContent = '天天来抢油,油价再涨也不怕！';
    }
    localStorage.setItem("shareContent", shareContent);
    shareContent = encodeURI(encodeURI(shareContent));
    if (paytype == 'wxpay') {
        wxPayOil(shareContent, isTiming, timingTime);//微信支付
    } else if (paytype == 'freepay') {
        freePayOil(shareContent, isTiming, timingTime);//自定义支付
    } else {
        TipShow("请选择支付方式", 1000);
        return;
    }
}
function freePayOil(shareContent, isTiming, timingTime) {
    var payNumbers = $("#payNumbers").val();
    if (isNaN(payNumbers * 1) || payNumbers == '') {
        TipShow("输入格式不正确!", 1000);
        return;
    } else if (payNumbers < 100) {
        TipShow("自定义油滴数不得低于一百", 1200);
        return;
    }
    $.ajax({
        type: "post",
        url: getRootPath() + "/freeOrder.htm",
        data: {"oilNum": payNumbers, "shareContent": shareContent, "isTiming": isTiming, "timingTime": timingTime},
        dataType: "json",
        success: function (data) {
            if (data.code == '10000') {
                location.href = getRootPath() + "/shareInfo.html?orderId=" + data.mess;
            } else {
                TipShow(data.mess, 1000);
            }
        }
    })
}
function wxPayOil(shareContent, isTiming, timingTime) {
    if (payOilNumber == 0) {
        TipShow("请选择油滴数量", 1000);
        return;
    }
    var json = {};
    json.dealMoney = payOilNumber / 100;
    json.dealRealMoney = payOilNumber / 100;
    json.shareContent = shareContent;
    json.isTiming = isTiming;
    json.timingTime = timingTime;
    $.ajax({
        type: "post",
        url: getRootPath() + "/preOrder.htm",
        data: json,
        dataType: "json",
        success: function (data) {
            loading("stop");
            if (data.code == "10000") {
                var res = data.mess;
                res = JSON.parse(res);
                localStorage.setItem("payStatus", "1");
                location.href = "http://www.linkgift.cn/wxpay_gate/information.jsp?backUrl=" + res.backUrl +
                    '&fromName=' + res.fromName +
                    '&goodsName=' + encodeURI(encodeURI(res.goodsName)) +
                    '&md5=' + res.md5 +
                    '&money=' + res.money +
                    '&openId=' + res.openId +
                    '&orderNo=' + res.orderNo +
                    '&payType=' + res.payType +
                    '&redirectUrl=' + res.redirectUrl +
                    '&remark=' + res.remark +
                    '&timestamp=' + res.timestamp;
            } else {
                alert(data.mess);
            }
        }, error: function (res) {
            console.log(res);
            loading("stop");
        }
    });
}
//确认兑换关闭按钮事件
function close_ExchangeTip() {
    $('#tip_').attr('class', 'exchange_nav');
}
function myRedList() {
    self.location = './exchangeRecord.html';
}
function toPayMentInfo() {
    window.location.href = './payMentInfo.html';
}
function returnBack() {
    window.location.href = getRootPath() + '/loginIndexBase.htm';
}
// $(function() {
//     FastClick.attach(document.body);
// });
var audio_count = 0;
function catchOil() {
    //播放音乐
    if (audio_count < 20) {
        audio_count++;
    }
    else {
        audio_count = 1;
    }
    var media = $('#coin_audio').find('audio')[audio_count - 1];
    media.play();

    //播放动画
    var html = "<img id=\"coin\" src=\"img/coin.png\" class=\"coin_animation\" alt=\"\">";
    var coins = $('#coin_animation').find('img').length;
    if (coins < 20) {
        $('#coin_animation').append(html);
    }
    else {
        $('#coin_animation').append("");
        $('#coin_animation').append(html);
    }
}

//播放音乐
var big_audio_count = 0;
function coin_music() {
    if (big_audio_count < 5) {
        big_audio_count++;
    }
    else {
        big_audio_count = 1;
    }
    var media = $('#coin_audio').find('audio')[big_audio_count - 1];
    media.play();
}
//暴击
function Crit() {
    console.log('crit');
    var coins_big = $('#coin_big_animation').find('img').length;
    var html_big = "<img id=\"coin_big\" src=\"img/coin_big.png\" class=\"coin_big_animation\" alt=\"\">";
    if (coins_big < 20) {
        $('#coin_big_animation').append(html_big);
    }
    else {
        $('#coin_big_animation').html('');
        $('#coin_big_animation').append(html_big);
    }
    //暴击音效
    coin_music();
}
function freeOil() {
    paytype = "freepay";
    $('.oilSelect').find('li').attr('class', 'flex-1');
    $(".moneyNumber").html("0元");
}

//填写手机号  验证码
function close_ExchangeTip2() {
    $('#bg_nav').attr('class', 'bg_nav1');
}
var countdown = 60;
function sendCode(self) {
    console.log("发送验证码操作。。。。");
    var phone = $("#tel").val();
    if (!phone) {
        TipShow("请输入手机号码号码", 1000);
        return;
    }
    else if (!jxTool.isMobile(phone)) {
        TipShow('请输入正确的手机号', 1000);
        return;
    }
    if (countdown == 60) {
        $.ajax({
            url: getRootPath() + "/sendMsg.htm",
            type: "post",
            data: {"phone": phone},
            dataType: "json",
            success: function (data) {
                console.log("发送验证码返回信息:" + data)
            }
        });
    }
    setValMsgTime(self);
}
function setValMsgTime(obj) {
    if (countdown == 0) {
        $(obj).show();
        $('#timer').hide();
        $(obj).html("获取验证码");
        countdown = 60;
        return;
    } else {
        obj.value = "重新发送(" + countdown + ")";
        $(obj).hide();
        $('#timer').show();
        $('#timer').html("重新发送(" + countdown + ")");
        countdown--;
    }
    setTimeout(function () {
            setValMsgTime(obj)
        }
        , 1000)
}

//引导  点击抢油
function block_catch() {
    if (audio_count < 20) {
        audio_count++;
    }
    else {
        audio_count = 1;
    }
    var media = $('#coin_audio').find('audio')[audio_count - 1];
    media.play();
    var html = "<img id=\"coin\" src=\"img/coin.png\" class=\"block_coin_animation\" alt=\"\">";
    var coins = $('#block_coin').find('img').length;
    if (coins < 20) {
        $('#block_coin').append(html);
    }
    else {
        $('#block_coin').html(html);
    }
}
function close_block() {
    if ($("#check").is(':checked')) {
        console.log('选中');
        $.ajax({
            url: getRootPath() + "/updateInitStatus.htm",
            type: "post",
            dataType: "json",
            success: function (data) {
            }
        });
    }
    else {
        console.log('未选中');
    }
    $('.catch_block').hide();
}
function fiexedBody() {
    document.getElementsByTagName("body")[0].setAttribute("style", "position:fixed")
}
function freeBody() {
    document.getElementsByTagName("body")[0].setAttribute("style", "position:inherit")
}

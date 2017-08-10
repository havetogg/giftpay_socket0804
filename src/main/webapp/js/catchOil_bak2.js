var roomRecordId = '';//房间ID
var ws = null;//websocket
var selfOpenId = '';
var allnum = 0;
var isContent = true;
var rate = '';
var oilObj = null;
var allUserData=null;

$(function () {
    initAllData();//初始化所有数据 房间数据用户数据
    initWsConf();//初始化WS
    initOilRecordList();//获取已存入数据库用户数据
    initAllContentList();//获取已存入数据库用户留言
    userIsExist();//判断用户是否存在
    $("#catch").on("click", function () {
        var ran = (Math.random() * 100).toFixed(0);
        var oilNum = $(".oilNum").html();
        if (ran < (rate * 1) && (oilNum * 1) > ($(".catch_oil").length * 2)) {
            Crit();
            allnum += 2;
            ran = 2;
        } else {
            allnum += 1;
            ran = 1;
        }
        var val = localStorage.getItem(selfOpenId + "_" + roomRecordId);
        if (val == null || val == '') {
            localStorage.setItem(selfOpenId + "_" + roomRecordId, '1')
        } else {
            localStorage.setItem(selfOpenId + "_" + roomRecordId, allnum)
        }
        oilObj = new Object();
        oilObj.name = "catch";
        oilObj.key = roomRecordId;
        oilObj.value = selfOpenId;
        oilObj.desc = ran;
        websocket.send(JSON.stringify(oilObj));
    })
    $(".saying").on("click", function () {
        if (isContent) {
            isContent = false;
            var content = $(".contentInput").val();
            if (content == null || $.trim(content) == '') {
                TipShow("请输入内容!", 1000);
                return;
            }
            setTimeout(function () {
                isContent = true;
            }, 1000)
            initAddContent(content);
        } else {
            TipShow("留言太快，休息一下!", 1000);
        }
    })
})
function initAllData() {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/initAllData.htm",
        dataType: "json",
        success: function (data) {
            console.log(data);
            var roomData = JSON.parse(data.roomData);
            var userData = JSON.parse(data.userData);
            initUserInfo(roomData, userData);
            filterTiming(roomData);
            initWsConf();
        }
    });
}
function initUserInfo(roomData, userData, allnum) {
    //初始化用户 房间数据
    var isInit = userData.isInit;
    if (isInit == '0') {
        $('.catch_block').show();
        fiexedBody();
    } else {
        $('.catch_block').hide();
        freeBody();
    }
    var baseOpenId = userData.openId;
    var nickName = userData.nickName;
    nickName = decodeURI(decodeURI(nickName));
    var headImg = userData.headImg;
    var roomHead = roomData.headImg;
    var roomName = roomData.nickName;
    roomName = decodeURI(decodeURI(roomName));
    var oilNum = roomData.oilNum;
    $(".oilNum").html(oilNum);
    if ($(".oilNum").html().length > 4) {
        $(".oilNum").css('font-size', '50px');
    }
    else {
        $(".oilNum").css('font-size', '60px');
    }
    $(".roomImg").attr("src", roomHead);
    $(".roomName").html(roomName + "正在送油");
    $(".catch_name_self").html(nickName);
    $(".catch_icon").attr("src", headImg);
    $(".contentInput").attr("placeholder", "你有什么想对" + roomName + "说的吗");
    $(".demoOil").removeAttr("class").attr("class", 'catch_' + baseOpenId + "");
    $("#selfUserOpenId").val(baseOpenId);
    rate = userData.rate;
    $("#selfUserRate").val(rate);
    $("#selfUserRate").addClass("userRate");
    $(".nowOilNum").html(allnum);
    $('.catch_' + baseOpenId + "").html(allnum);
    var alloilNum = $(".oilNum").html();
    alloilNum = alloilNum * 1;
    $(".oilNum").html(alloilNum - ($('.catch_' + baseOpenId + "").html()) * 1);
}
//倒计时校验
function filterTiming(roomData) {
    var isTim = roomData.isTiming;
    var timingTime = roomData.timingTime;
    if (isTim != '1') {
        $("#catch_btn").hide();
        $("#catch").show();
        $('#catch_out').hide();
        $(".catch_out_time").hide();
        $(".two2").hide();
    } else {
        timingTimeString(timingTime);
    }
}



function initWsConf() {
    if ('WebSocket' in window) {
        websocket = new WebSocket("wss://" + getWSRootPath() + "/websocketTwo/onOpen");
    }
    else {
        TipShow('当前浏览器版本不支持该活动', 1000);
    }
    //连接发生错误的回调方法
    websocket.onerror = function () {
        TipShow("房间通信连接发生错误", 1000);
    };

    //连接成功建立的回调方法
    websocket.onopen = function (event) {
        if (event.isTrusted) {
            console.log("初次建立ws链接返回数据错误!")
        }
    }
    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        console.log("ws返回信息为:" + event.data);
        var result = JSON.parse(event.data);
        var type = result.name;
        if (type == 'baseInfo') {
            receiveInitUserInfo(result);
        } else if (type == 'catch') {
          //  receiveWSInfo(result);
        }
    }
    //连接关闭的回调方法
    websocket.onclose = function () {
        console.log("一名用户退出------------------------------------------------");
    }
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket();
    }
}
//关闭WebSocket连接
function closeWebSocket() {
    websocket.close();
}
//抢油信息返回
function receiveWSInfo(result) {
    var oilNum = $("#allCountOilNum").val();
    if (oilNum.length > 4) {
        $(".oilNum").css('font-size', '50px');
    }
    else {
        $(".oilNum").css('font-size', '60px');
    }
    oilNum = oilNum * 1;
    if (oilNum != 0) {
        var returnData=result.jsonArray;
        $.each(returnData,function(index,item){
            $(".catch_" + item.openId).html(item.oilNum);
        })
        var num = 0;
        $.each($(".catch_oil label"), function (index, item) {
            num += $(item).html() * 1;
        })
        var currentOilNum = $(".oilNum").html();
        $(".oilNum").html(currentOilNum * 1 - (result.desc) * 1);
        if ((oilNum * 1 - num) <= 0) {
            TipShow("已抢完!", 1000);
            $(".catch_text").show();
            $(".catch_btn").attr("onclick", "backHome()");
            $("#catch").attr("src", "img/send.png");
            var arr = new Array();
            $(".catch_oil label").each(function (index, item) {
                var obj = new Object();
                var clazz = $(item).attr("class");
                clazz = clazz.substring(clazz.indexOf("catch_") + 6, clazz.length);
                obj.openId = clazz;
                var num = $(item).html();
                obj.num = num;
                var rate = $("rate_" + clazz).html();
                obj.rate = rate;
                arr.push(obj)
            });
            $.ajax({
                type: "GET",
                url: getRootPath() + "/updateOilRecordStatus.htm",
                data: {"params": JSON.stringify(arr), "roomRecordId": roomRecordId},
                dataType: "json",
                success: function (data) {
                    $.ajax({
                        type: "GET",
                        url: getRootPath() + "/updateRoomRecordStatus.htm",
                        data: {"roomRecordId": roomRecordId},
                        dataType: "json",
                        success: function (data) {
                            // if(data.code=='1'){
                            TipShow("已抢完", 1000);
                            // }else{
                            // }
                        }
                    });
                    localStorage.clear();
                    location.href = getRootPath() + "/loginBase.htm?roomRecordId=" + roomRecordId;
                }
            });

        }
    }
}
//收到留言返回
function receiveWSContent() {
    var res = JSON.parse(message.content);
    var content = decodeURI(res.content);
    var headImg = res.headImg;
    var name = decodeURI(res.nickName);
    var str = "";
    str += '<li class="flex"><div>';
    str += '<img class="catch_icon" src="' + headImg + '" alt="">';
    str += '</div><div class="flex-1 message_content">';
    str += '<div class="message_userName">' + name + '</div><div>';
    str += content;
    str += '</div></div>';
    str += '</li>';
    $(".messageList_ul").prepend(str);
}
//收到初始化信息返回
function receiveInitUserInfo(result) {
    $(".topOilNum_"+selfOpenId).html(result.oilNum);
    var userData = JSON.parse(result.userData);
    var openId = userData.openId;
    selfOpenId = openId;
    var head = userData.headImg;
    var name = userData.nickName;
    name = decodeURI(decodeURI(name));
    var rate = userData.rate;
    var oil = result.oilNum;
    allnum = oil;
    //openid  头像 姓名 概率 次数
    var isExist = true;

    var roomData = JSON.parse(result.roomData);
    $("#allCountOilNum").val(roomData.oilNum);
    roomRecordId = roomData.id;
    $.each($(".userOpenId"), function (index, item) {
        if (item.value == openId) {
            isExist = false;
            return;
        }
    })
    if (isExist) {
        $(".roomPeopleNum").html($(".roomPeopleNum").html() * 1 + 1);
        var str = '<li class="catch_box">';
        str += '<img class="catch_icon" src="' + head + '" alt="">';
        str += '<a class="rank"></a>';
        str += '<a class="catch_name">' + name + '</a>';
        str += '<a class="catch_oil"><label class="catch_' + openId + '">' + oil + '</label>滴</a><input class="userOpenId" type="hidden" value="' + openId + '">';
        str += '<span class="rate_' + openId + '"  style="display: none">' + rate + '</span></li>';
        $(".catchList_ul").append(str);
    }
    allUserData=result.jsonArray;
    $(".oilNum").html(result.totalOilNum);
}

function userIsExist() {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/isExistUserLogin.htm",
        data: {"openId": selfOpenId},
        dataType: "json",
        success: function (data) {
            /* if (data.code == '1') {
             alert("用户异常!");
             location.href = getRootPath() + '/loginIndexBase.htm';
             }*/
        }
    });
}
function initAllContentList() {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/queryContetList.htm",
        data: {"roomRecordId": roomRecordId},
        dataType: "json",
        success: function (data) {
            console.log("查询留言:" + data);
            if (data.success) {
                //有留言
                var res = JSON.parse(data.data);
                var str = "";
                $.each(res, function (index, item) {
                    str += '<li class="flex"><div>';
                    str += '<img class="catch_icon" src="' + item.headImg + '" alt="">';
                    str += '</div><div class="flex-1 message_content">';
                    str += '<div class="message_userName">' + decodeURI(decodeURI(item.nickName)) + '</div><div>';
                    str += decodeURI(item.content);
                    str += '</div></div>';
                    str += '</li>';
                })
                $(".messageList_ul").html(str);
            } else {
                //无留言
                console.log("无留言");
            }
        }
    });
}
function initAddContent(content) {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/addContentModel.htm",
        data: {"roomRecordId": roomRecordId, "content": encodeURI(encodeURI(content))},
        dataType: "json",
        success: function (data) {
            console.log("添加留言:" + data);
            if (data.result != 0) {
                //添加成功
                $(".contentInput").val('');
                oilObj.name = "content";
                oilObj.key = roomRecordId;
                oilObj.value = selfOpenId;
                oilObj.desc = data.data;
                websocket.send(JSON.stringify(oilObj));
            } else {
                TipShow("留言失败", 1000);
            }
        }
    });
}
function initOilRecordList() {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/quertOilRecordList.htm",
        dataType: "json",
        success: function (data) {
            console.log(data);
            if (data.success) {
                var record = JSON.parse(data.data);
                $(".roomPeopleNum").html(record.length + 1);
                var str = '';
                for (var re in record) {
                    var isExist = true;
                    var oil = record[re];
                    $.each($(".userOpenId"), function (index, item) {
                        if (item.value == oil.openId) {
                            isExist = false;
                            return;
                        }
                    })
                    if (isExist) {
                        str += '<li class="catch_box">';
                        str += '<img class="catch_icon" src="' + oil.headImg + '" alt="">';
                        str += '<a class="rank"></a>';
                        str += '<a class="catch_name">' + decodeURI(decodeURI(oil.nickName)) + '</a>';
                        str += '<a class="catch_oil"><label class="catch_' + oil.openId + '">0</label>滴</a><input class="userOpenId" type="hidden" value="' + oil.openId + '">';
                        str += '<span class="rate_' + oil.openId + '"  style="display: none">' + oil.rate + '</span></li>';

                    }
                }
                $(".catchList_ul").html(str);
                $.each(allUserData,function(index,item){
                    $(".catch_"+item.openId).html(item.oilNum);
                })
            }
        }
    });
}

function backHome() {
    location.href = getRootPath() + "/loginIndexBase.htm";
}


//时间计算
var time;
var h = 0;
var m = 0;
var s = 0;
function timingTimeString(tim) {
    var date1 = getDate(getUrlParam("now"));  //开始时间
    var date2 = getDate(tim);    //结束时间
    var date3 = date2.getTime() - date1.getTime()  //时间差的毫秒数
    if (date3 <= 0) {
        $("#catch_btn").hide();
        $("#catch").show();
        $('#catch_out').hide();
        $(".catch_out_time").hide();
        $(".two2").hide();
        return;
    } else {
        $("#catch_btn").show();
        $("#catch").hide();
        $('#catch_out').show();
        $(".catch_out_time").show();
        $(".two2").hide();
        time = setInterval("run()", 1000);
    }
    var leave1 = date3 % (24 * 3600 * 1000)
    //计算天数后剩余的毫秒数
    h = Math.floor(leave1 / (3600 * 1000))
    //计算相差分钟数
    var leave2 = leave1 % (3600 * 1000)
    //计算小时数后剩余的毫秒数
    m = Math.floor(leave2 / (60 * 1000))


    //计算相差秒数
    var leave3 = leave2 % (60 * 1000)
    //计算分钟数后剩余的毫秒数
    s = Math.round(leave3 / 1000);
}
function run() {
    if (h == 0 && m == 0 && s == 0) {
        $('#a1').html('0');
        $('#a2').html('0');
        $('#a3').html('0');
        $('#a4').html('0');
        $('#a5').html('0');
        $('#a6').html('0');
        clearInterval(time);
        $('#catch').show();
        $('#catch_out').hide();
        $(".catch_out_time").hide();
    }
    else {
        --s;
        if (s < 0) {
            --m;
            s = 59;
        }
        if (m < 0) {
            --h;
            m = 59
        }
        if (h < 0) {
            s = 0;
            m = 0;
        }
        if (h < 10) {
            $('#a1').html('0');
            $('#a2').html(h);
        }
        else {
            $('#a1').html(h.toString().substr(0, 1));
            $('#a2').html(h.toString().substr(1, 1));
        }
        if (m < 10) {
            $('#a3').html('0');
            $('#a4').html(m);
        }
        else {
            $('#a3').html(m.toString().substr(0, 1));
            $('#a4').html(m.toString().substr(1, 1));
        }
        if (s < 10) {
            $('#a5').html('0');
            $('#a6').html(s);
        }
        else {
            $('#a5').html(s.toString().substr(0, 1));
            $('#a6').html(s.toString().substr(1, 1));
        }
    }
}
function getDate(strDate) {
    var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,
            function (a) {
                return parseInt(a, 10) - 1;
            }).match(/\d+/g) + ')');
    return date;
}
function getWSRootPath() {
    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPaht = curWwwPath.substring(curWwwPath.indexOf("//"), pos);
    var projectName = pathName
        .substring(0, pathName.substr(1).indexOf('/') + 1);
    return (localhostPaht + projectName);
}
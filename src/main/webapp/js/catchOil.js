var websocket = null;
var myOpenId = "";
var totalOilNum = "";
var wsMesg = null;
var myRate = "";
var myClickNum = 0;
var roomId = 0;
var goEasy = null;
var isContent = true;
var isEnd = true;

//rate_(OPNEID) 所有用户暴击率
//catch_(OPNEID) 所有页面用户油滴数
$(function () {
    /**初始化页面部分显示*/
    initPageInfo();
    /**初始化自己相关信息**/
    initMyDataInfo();
    /**初始化房间内信息**/
    initOilRecordList();
    /**初始化各个按钮点击事件**/
    initPageButtonClick();
    /**初始化页面数据*/
    initWSSource();
})
function initWSSource() {
    if ('WebSocket' in window) {
        // websocket = new WebSocket("wss://" + getWSRootPath() + "/websocketTwo/onOpen");
        websocket = new WebSocket("wss://" + getWSRootPath() + "/websocketTwo/onOpen");
    } else {
        TipShow('当前浏览器版本不支持该活动', 5000);
    }
    //连接发生错误的回调方法
    websocket.onerror = function () {
        TipShow("房间通信连接发生错误", 5000);
        initWSSource(); //再次调用方法
    };
    websocket.onopen = function (event) {
        if (event.isTrusted) {
            console.log("初次建立ws链接返回数据错误!")
        }
    }
    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        var result = JSON.parse(event.data);
        var type = result.name;
        console.log("ws返回信息为:" + event.data + ",接受数据处理类型为:" + type);
        if (type == 'baseInfo') {
            initWSBaseInfo(result);
        } else if (type == 'catch') {
            initWSCatchInfo(result);
        } else if (type == 'content') {
            initWSAddContent(result);
        }
    }
    //连接关闭的回调方法
    websocket.onclose = function () {
        console.log("一名用户退出当前房间");
        TipShow("已与房间断开链接，请关闭重新进入", 5000);
    }
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket();
    }
}
function initWSAddContent(result) {
    result = JSON.parse(result.content);
    var content = decodeURI(decodeURI(result.content));
    var headImg = result.headImg;
    var name = decodeURI(decodeURI(result.nickName));
    var str = "";
    str += '<li class="flex"><div>';
    str += '<img class="catch_icon" src="' + headImg + '" alt="">';
    str += '</div><div class="flex-1 message_content">';
    str += '<div class="message_userName">' + name + '</div><div>';
    str += content;
    str += '</div></div>';
    str += '</li>';
    $(".messageList_ul").prepend(str);
    $(".contents").html(content);
    $(".person_title").attr("src", headImg);
    $("#tipMessage").show();
    setTimeout('leaveOn()', 1);
    setTimeout('leaveOff()', 2000);
}
//抢油。。。
function initWSCatchInfo(result) {
    $(".catch_" + result.openId).html(result.oilNum);
    var num=result.sumOilNum;
    if((num*1)<=0){
        $(".oilNum").html('0');
    }else{
        $(".oilNum").html(num);
    }
    // $(".oilNum").html(result.sumOilNum);
    if ($(".oilNum").html() <= 0) {
        $('.end_' + result.openId).click();
    }
}

function toEnd() {
    TipShow("已抢完!", 1000);
    $(".catch_text").show();
    $(".catch_btn").attr("onclick", "backHome()");
    $("#catch").attr("src", "img/send.png");
    $.ajax({
        type: "GET",
        url: getRootPath() + "/updateOilRecordStatus.htm",
        dataType: "json",
        success: function (data) {
            localStorage.clear();
            location.href = getRootPath() + "/loginBase.htm?roomRecordId=" + roomId;
            closeWebSocket();
        }
    });
}
function initWSBaseInfo(result) {
    var oilNum = result.oilNum;
    var openId = result.openId;
    var rate = result.rate;
    var isExist = true;
    $.each($(".userOpenId"), function (index, item2) {
        if (item2.value == openId) {
            isExist = false;
            return;
        }
    });
    if (isExist) {
        $(".roomPeopleNum").html($(".roomPeopleNum").html() * 1 + 1);
        var str = '<li class="catch_box">';
        str += '<img class="catch_icon" src="' + result.headImg + '" alt="">';
        str += '<a class="rank"></a>';
        str += '<a class="catch_name">' + decodeURI(decodeURI(result.nickName)) + '</a>';
        str += '<a class="catch_oil"><label class="catch_' + openId + '">' + oilNum + '</label>滴</a><input class="userOpenId" type="hidden" value="' + openId + '">';
        str += '<span class="rate_' + openId + '"  style="display: none">' + rate + '</span></li>';
        $(".catchList_ul").append(str);
    }
    $(".catch_" + openId).html(oilNum);
    var totalNum = result.totalOilNum;
    $(".oilNum").html(totalNum);//当前房间总油滴数
}
function initPageButtonClick() {
    $(".two2").on("click", function () {
        var ran = getRandRate();
        myClickNum = myClickNum + ran;
        wsMesg = new Object();
        wsMesg.name = "catch";
        wsMesg.desc = ran;
        wsMesg.roomId=roomId;
        wsMesg.allOilNum=totalOilNum;
        wsMesg.openId=myOpenId;
        websocket.send(JSON.stringify(wsMesg));
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
            TipShow("留言太快，休息一下!", 3000);
            setTimeout(function () {
                isContent = true;
            }, 3000)
        }
    })
}
//关闭WebSocket连接
function closeWebSocket() {
    websocket.close();
}
function initOilRecordList() {
    $.ajax({
        type: "POST",
        url: getRootPath() + "/quertOilRecordList.htm",
        dataType: "json",
        success: function (data) {
            if (data.success) {
                var record = JSON.parse(data.data);
                $(".roomPeopleNum").html(record.length);
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
            }
        }
    });
}
function initAllContentList() {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/queryContetList.htm",
        data: {"roomRecordId": roomId},
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
                    str += decodeURI(decodeURI(item.content));
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
function initMyDataInfo() {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/initAllData.htm",
        dataType: "json",
        success: function (data) {
            var roomData = JSON.parse(data.roomData);
            var userData = JSON.parse(data.userData);
            setAllOilNum(roomData.oilNum);
            setMyHeadImg(userData.headImg);
            setMyName(decodeURI(decodeURI(userData.nickName)));
            setMyParams(userData.openId);
            setMyRateNum(userData.rate);
            setRoomUserHeadImg(roomData.headImg);
            setRoomUserName(decodeURI(decodeURI(roomData.nickName)));
            filterTiming(roomData);
            $(".nowOilNum").addClass("catch_" + userData.openId);
            roomId = roomData.id;
            initAllContentList();
        }
    });
}
//倒计时校验
function filterTiming(roomData) {
    var isTim = roomData.isTiming;
    var timingTime = roomData.timingTime;
    if (isTim != '1') {
        $("#catch_btn").hide();
        $("#catch").hide();
        $('#catch_out').hide();
        $(".catch_out_time").hide();
        $(".two2").show();
    } else {
        timingTimeString(timingTime);
    }
}
function initPageInfo() {
    //提示是否需要引导
    $('.catch_block').hide();
    freeBody();

    //点击按钮不可选
    $("#catch_btn").hide();
    $("#catch").hide();
    $('#catch_out').hide();
    $(".catch_out_time").hide();
    $(".two2").hide();
}
function initAddContent(content) {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/addContentModel.htm",
        data: {"roomRecordId": roomId, "content": encodeURI(encodeURI(content))},
        dataType: "json",
        success: function (data) {
            console.log("添加留言:" + data);
            if (data.result != 0) {
                //添加成功
                $(".contentInput").val('');
                wsMesg = new Object();
                wsMesg.name = "content";
                wsMesg.roomId=roomId;
                wsMesg.content = data.data;
                websocket.send(JSON.stringify(wsMesg));
            } else {
                TipShow("留言失败", 3000);
            }
        }
    });
}
//时间计算
var time;
var h = 0;
var m = 0;
var s = 0;
function timingTimeString(tim) {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/getSocketNowTime.htm",
        dataType: "json",
        success: function (data) {
            var date1 = getDate(data);  //开始时间
            var date2 = getDate(tim);    //结束时间
            var date3 = date2.getTime() - date1.getTime()  //时间差的毫秒数
            if (date3 <= 0) {
                $("#catch_btn").hide();
                $("#catch").hide();
                $('#catch_out').hide();
                $(".catch_out_time").hide();
                $(".two2").show();
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
    })
}
function getDate(strDate) {
    var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,
            function (a) {
                return parseInt(a, 10) - 1;
            }).match(/\d+/g) + ')');
    return date;
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
        $('#catch').hide();
        $('#catch_out').hide();
        $(".catch_out_time").hide();
        $(".two2").show();
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
function setMyName(name) {
    //房主名称
    $(".catch_name_self").html(name);
}
function setAllOilNum(oilnum) {
    //房间总油滴数
    $(".allCountOilNum").val(oilnum);
    totalOilNum = oilnum;
}
function setMyHeadImg(imgsrc) {
    //房主头像
    $(".catch_icon").attr("src", imgsrc);
}
function setMyOilNum(oilnum) {
    //设置我的油滴数
    $(".demoOil").html(oilnum);
}
function setMyRateNum(rate) {
    //设置我的暴击率
    $("#selfUserRate").val(rate);
    myRate = rate;
}
function setMyParams(id) {
    //设置我的参数
    $(".demoOil").addClass("catch_" + id);
    $("#selfUserRate").addClass("rate_" + id);
    $("#demoEnd").addClass("end_" + id);
    $("#selfUserOpenId").val(id);
    myOpenId = id;
}
function setRoomUserHeadImg(imgsrc) {
    $(".roomImg").attr("src", imgsrc);
}
function setRoomUserName(rname) {
    var newName = '';
    newName = rname.length > 5 ? rname.substring(0, 5) + ".." : rname;
    $(".contentInput").attr("placeholder", "你有什么想对" + newName + "说的吗");
    rname = rname + "正在送油";
    $(".roomName").html(rname);
}
function getRandRate() {
    var ran = (Math.random() * 100).toFixed(0);
    var oilNum = $(".oilNum").html();
    if (ran < (myRate * 1) && (oilNum * 1) > ($(".catch_oil").length * 2)) {
        Crit();
        ran = 2;
    } else if ((oilNum * 1) == 0) {
        ran = 0;
    } else {
        ran = 1;
    }
    return ran;
}
function leaveOn() {
    $('#tipMessage_div').attr('class', 'Tip_Messages_active');
}
function leaveOff() {
    $('#tipMessage_div').attr('class', 'Tip_Messages_active_leave');
}
var roomRecordId = '';
var goEasy;
var wsMesg=null;

var isContent=true;
$(function () {
    var selfOpenId = getUrlParam("openId");
    roomRecordId = getUrlParam("roomRecordId");
    $("#catchBtn").on("click", function () {
        location.href = getRootPath() + "/loginIndexBase.htm";
    })
    initUserInfo();
    initAllContentList();
    initRecordRank();
    initWSSource();
    $(".saying").on("click",function(){
        if(isContent){
            isContent=false;
            var content=$(".contentInput").val();
            if(content==null||$.trim(content)==''){
                TipShow("请输入内容!",1000);
                return ;
            }
            setTimeout(function(){
                isContent=true;
            },1000)
            initAddContent(content);
        }else{
            TipShow("留言太快，休息一下!",1000);
            TipShow("留言太快，休息一下!", 3000);
            setTimeout(function () {
                isContent = true;
            }, 3000)
        }
    })
})
function initAllContentList() {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/queryContetList.htm",
        data: {"roomRecordId": roomRecordId},
        dataType: "json",
        success: function (data) {
            console.log("查询流言:" + data);
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

function initRecordRank() {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/quertOilRecordListRank.htm",
        data: {"roomRecordId": roomRecordId},
        dataType: "json",
        success: function (data) {
            console.log(data);
            var res = JSON.parse(data.data);
            var roomData=JSON.parse(data.roomData);
            $(".catchOil_ad").html(decodeURI(decodeURI(roomData.shareContent)));
            var str = '';
            var oil1='';
            var oil2='';
            var oil3='';
            $.each(res,function(index,oil){
                if(oil.id=='1') {
                    oil1 += '<li class="catch_box">';
                    oil1 += '<img class="catch_icon" src="' + oil.headImg + '" alt="">';
                    oil1 += '<a class="rank rank_' + oil.openId + '"><img src="img/rank1.png" /></a>';
                    oil1 += '<a class="catch_name">' + decodeURI(oil.nickName) + '</a>';
                    oil1 += '<a class="catch_oil"><label class="catch_' + oil.openId + '">' + (oil.oilNum == '' ? '0' : oil.oilNum) + '</label>滴</a><input class="userOpenId" type="hidden" value="' + oil.openId + '">';
                    oil1 += '</li>';
                }else if(oil.id=='2'){
                    oil2 += '<li class="catch_box">';
                    oil2 += '<img class="catch_icon" src="' + oil.headImg + '" alt="">';
                    oil2 += '<a class="rank rank_' + oil.openId + '"><img src="img/rank2.png" /></a>';
                    oil2 += '<a class="catch_name">' + decodeURI(decodeURI(oil.nickName)) + '</a>';
                    oil2 += '<a class="catch_oil"><label class="catch_' + oil.openId + '">' + (oil.oilNum == '' ? '0' : oil.oilNum) + '</label>滴</a><input class="userOpenId" type="hidden" value="' + oil.openId + '">';
                    oil2 += '</li>';
                }
                else if(oil.id=='3'){
                    oil3 += '<li class="catch_box">';
                    oil3 += '<img class="catch_icon" src="' + oil.headImg + '" alt="">';
                    oil3 += '<a class="rank rank_' + oil.openId + '"><img src="img/rank3.png" /></a>';
                    oil3 += '<a class="catch_name">' + decodeURI(decodeURI(oil.nickName)) + '</a>';
                    oil3 += '<a class="catch_oil"><label class="catch_' + oil.openId + '">' + (oil.oilNum == '' ? '0' : oil.oilNum) + '</label>滴</a><input class="userOpenId" type="hidden" value="' + oil.openId + '">';
                    oil3 += '</li>';
                }else{
                    str += '<li class="catch_box">';
                    str += '<img class="catch_icon" src="' + oil.headImg + '" alt="">';
                    str += '<a class="rank rank_' + oil.openId + '"></a>';
                    str += '<a class="catch_name">' + decodeURI(decodeURI(oil.nickName)) + '</a>';
                    str += '<a class="catch_oil"><label class="catch_' + oil.openId + '">' + (oil.oilNum == '' ? '0' : oil.oilNum) + '</label>滴</a><input class="userOpenId" type="hidden" value="' + oil.openId + '">';
                    str += '</li>';
                }
            })
            $(".catchList_ul").html(oil1+oil2+oil3+str);
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
            console.log("添加留言:"+data);
            if(data.result!=0){
                //添加成功
                $(".contentInput").val('');
                wsMesg = new Object();
                wsMesg.name = "content";
                wsMesg.roomId=roomRecordId;
                wsMesg.content=decodeURI(decodeURI(data.data));
                websocket.send(JSON.stringify(wsMesg));
            }else{
                TipShow("留言失败",1000);
            }
        }
    });
}

function initOilRecordList() {
    $.ajax({
        type: "GET",
        url: getRootPath() + "/quertOilRecordList.htm",
        data: {"roomRecordId": roomRecordId, "openId": getUrlParam("openId")},
        dataType: "json",
        success: function (data) {
            console.log(data);
            if (data.success) {
                var openId = getUrlParam("openId");
                var record = JSON.parse(data.data);
                $(".roomPeopleNum").html(record.length + 1);
                var str = '';
                for (var re in record) {
                    var oil = record[re];
                    $.each($(".userOpenId"), function (index, item) {
                        if (item.value == openId) {
                            isExist = false;
                            return;
                        }
                    })
                    localStorage.setItem(oil.openId, oil.openId);
                    str += '<li class="catch_box">';
                    str += '<img class="catch_icon" src="' + oil.headImg + '" alt="">';
                    str += '<a class="rank rank_' + oil.openId + '"></a>';
                    str += '<a class="catch_name">' + decodeURI(decodeURI(oil.nickName)) + '</a>';
                    str += '<a class="catch_oil"><label class="catch_' + oil.openId + '">' + (oil.oilNum == '' ? '0' : oil.oilNum) + '</label>滴</a><input class="userOpenId" type="hidden" value="' + oil.openId + '">';
                    str += '</li>';
                }
                $(".catchList_ul").html(str);
            }
        }
    });
}

function initUserInfo() {
    var nickName = getUrlParam("name");
    nickName = decodeURI(decodeURI(nickName));
    var headImg = getUrlParam("img");
    var openId = getUrlParam("openId");
    var roomOpenId = getUrlParam("roomOpenId");
    var roomHead = getUrlParam("roomHead");
    var roomName = getUrlParam("roomName");
    roomName = decodeURI(decodeURI(roomName))
    // var oilNum = getUrlParam("oilNum");
    $(".selfRank").addClass("rank_" + openId);
    $(".oilNum").html("0");
    $(".roomImg").attr("src", roomHead);
    $(".roomName").html(roomName + "老板正在送油");
    $(".catch_icon").attr("src", headImg);
    $(".contentInput").attr("placeholder", "你有什么想对" + roomName + "说的吗");
    // $(".demoOil").html((oilNum == '' ? '0' : oilNum));
    $(".demoOil").removeAttr("class").attr("class", 'catch_' + openId + "");
}

function initWSSource() {
    if ('WebSocket' in window) {
        // websocket = new WebSocket("wss://" + getWSRootPath() + "/websocketTwo/onOpen");
        websocket = new WebSocket("wss://" + getWSRootPath() + "/websocketTwo/onOpen");
    } else {
        TipShow('当前浏览器版本不支持该活动', 1000);
    }
    //连接发生错误的回调方法
    websocket.onerror = function () {
        TipShow("房间通信连接发生错误", 1000);
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
         if(type=='content'){
            initWSAddContent(result);
        }
    }
    //连接关闭的回调方法
    websocket.onclose = function () {
        console.log("一名用户退出当前房间");
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
function initWSAddContent(result){
    result=JSON.parse(result.content);
    var content=decodeURI(decodeURI(result.content));
    var headImg=result.headImg;
    var name=decodeURI(decodeURI(result.nickName));
    var str="";
    str+='<li class="flex"><div>';
    str+='<img class="catch_icon" src="'+headImg+'" alt="">';
    str+='</div><div class="flex-1 message_content">';
    str+='<div class="message_userName">'+name+'</div><div>';
    str+=content;
    str+='</div></div>';
    str+='</li>';
    $(".messageList_ul").prepend(str);
    $(".contents").html(content);
}

/**
 *
 *  build by rwson @ 2015-01-10
 *
 *  完成微信端的一些自适应屏幕功能(css3中的缩放特性)
 *
 */





//js获取项目根路径，如： http://localhost:8083/uimcardprj
function getRootPath() {
    // 获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
    var curWwwPath = window.document.location.href;
    // 获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    // 获取主机地址，如： http://localhost:8083
    var localhostPaht = curWwwPath.substring(0, pos);
    // 获取带"/"的项目名，如：/uimcardprj
    var projectName = pathName
        .substring(0, pathName.substr(1).indexOf('/') + 1);
    return (localhostPaht + projectName);
}
//获取网址里的name参数
function getUrlParam(name) {
    //构造一个含有目标参数的正则表达式对象
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    //匹配目标参数
    var r = window.location.search.substr(1).match(reg);
    //alert(unescape(r[2]))
    //返回参数值.
    if (r != null) return unescape(r[2]);
    return null;
}
//var totalMoneys = getUrlParam("name");   页面获取参数


$(function(){
    wxShare();
})

function wxShare(){
    var roomRecordId=getUrlParam("orderId");
    try {
        roomRecordId = roomRecordId.substring(0, roomRecordId.indexOf("?"));
    } catch (e) {
        roomRecordId=roomRecordId;
    }
    if(roomRecordId==''){
        roomRecordId=getUrlParam("orderId");
        if(roomRecordId==''){
            TipShow('订单异常',1000);
        }
    }
    var shareContent=localStorage.getItem("shareContent");
    $.ajax({
        url: getRootPath() + "/shareFriend.htm",
        data: {'url': location.href},
        dataType: "json",
        success: function (config) {
            wx.config(config);
            shareData = {
                title:shareContent,
                desc: shareContent,
                link:getRootPath()+'/loginBase.htm?roomRecordId='+roomRecordId,
                imgUrl:getRootPath()+'/img/oil.png',
                // linkpath + '/share.jsp='+wxId+'&id='+currentId;
                trigger: function (res) {
                    //alert('用户点击发送给朋友');
                },
                success: function (res) {
                    //alert('已分享');
                    location.href=getRootPath()+'/loginBase.htm?roomRecordId='+roomRecordId;
                },
                cancel: function (res) {
                    //alert('已取消');
                },
                fail: function (res) {
                    //alert("this is "+JSON.stringify(res));
                }
            };
            //发送给朋友圈
            shareFriends = {
                title:shareContent,
                link:getRootPath()+'/loginBase.htm?roomRecordId='+roomRecordId,
                imgUrl:getRootPath()+'/img/oil.png',
                // linkpath + '/share.jsp='+wxId+'&id='+currentId;
                trigger: function (res) {
                    //alert('用户点击发送给朋友');
                },
                success: function (res) {
                    //alert('已分享');
                    location.href=getRootPath()+'/loginBase.htm?roomRecordId='+roomRecordId;
                },
                cancel: function (res) {
                    //alert('已取消');
                },
                fail: function (res) {
                    //alert("this is "+JSON.stringify(res));
                }
            };
            wx.ready(function () {
                wx.onMenuShareAppMessage(shareData);
                wx.onMenuShareTimeline(shareFriends);
            });
            wx.error(function (res) {
                //alert(JSON.stringify(res));
            });
        },
        error: function (json) {
            //alert(JSON.stingify(json));
        }
    });
}
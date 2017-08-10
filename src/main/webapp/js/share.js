
$(function(){
    var shareData = {};
    var shareFriends={};
    $.ajax({
        url: getRootPath()+"/weChatJSConfigC/getWeCharJSConfigM.htm",
        data: {currUrl: location.href},
        dataType: "json",
        success: function (config) {
            wx.config(config.resultObject);
            shareData = {
                title: '中石化游戏中心正式上线，登录送好礼！',
                desc: "更有福利惊喜等你发现哦~",
                link: getRootPath()+"/weixinMng/ManageC/userIn.htm",
                imgUrl: getRootPath()+'/jsp/weixinMng/mallMng/img/common/share.png',
                // linkpath + '/share.jsp='+wxId+'&id='+currentId;
                trigger: function (res) {
                    //alert('用户点击发送给朋友');
                },
                success: function (res) {
                	
                },
                cancel: function (res) {
//                    alert('已取消');
                },
                fail: function (res) {
//                    alert("this is "+JSON.stringify(res));
                }
            };
            shareFriends = {
                title: '中石化游戏中心正式上线，登录送好礼！',
                link: getRootPath()+"/weixinMng/ManageC/userIn.htm",
                imgUrl: getRootPath()+'/jsp/weixinMng/mallMng/img/common/share.png',
                
                // linkpath + '/share.jsp='+wxId+'&id='+currentId;
                trigger: function (res) {
                    //alert('用户点击发送给朋友');
                },
                success: function (res) {
                    //alert('已分享');
                    closeShareTip();
                },
                cancel: function (res) {
                    //alert('已取消');
                },
                fail: function (res) {
                    //alert("this is "+JSON.stringify(res));
                }
            };
            wx.ready(function(){
                wx.onMenuShareAppMessage(shareData);
                wx.onMenuShareTimeline(shareFriends);
            });
            wx.error(function(res){
                alert(JSON.stringify(res));
            });
        },
        error: function (json) {
            alert(JSON.stingify(json));
        }
    });
});



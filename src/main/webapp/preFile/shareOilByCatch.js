function getWSRootPath() {
    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPaht = curWwwPath.substring(curWwwPath.indexOf("//"), pos);
    var projectName = pathName
        .substring(0, pathName.substr(1).indexOf('/') + 1);
    return (localhostPaht + projectName);
}
function getDate(strDate) {
    var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,
            function (a) {
                return parseInt(a, 10) - 1;
            }).match(/\d+/g) + ')');
    return date;
}
function getRootPath() {
    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPaht = curWwwPath.substring(0, pos);
    var projectName = pathName
        .substring(0, pathName.substr(1).indexOf('/') + 1);
    return (localhostPaht + projectName);
}
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

$(function () {
    $.ajax({
        url: getRootPath() + "/queryRoomData.htm",
        dataType: "json",
        success: function (data) {
            var roomData = JSON.parse(data.roomData);
            var shareContent=decodeURI(decodeURI(roomData.shareContent));
            $(".catchOil_ad").html(decodeURI(decodeURI(shareContent)));
           wxShare(roomData.id,shareContent);
        }
    })
})

function wxShare(roomRecordId,shareContent) {
    $.ajax({
        url: getRootPath() + "/shareFriend.htm",
        data: {'url': location.href},
        dataType: "json",
        success: function (config) {
            wx.config(config);
            shareData = {
                title: shareContent,
                desc: shareContent,
                link: getRootPath() + '/loginBase.htm?roomRecordId=' + roomRecordId,
                imgUrl: getRootPath() + '/img/oil.png',
                trigger: function (res) {
                },
                success: function (res) {
                    location.href = getRootPath() + '/loginBase.htm?roomRecordId=' + roomRecordId;
                },
                cancel: function (res) {
                },
                fail: function (res) {
                }
            };
            //发送给朋友圈
            shareFriends = {
                title: shareContent,
                link: getRootPath() + '/loginBase.htm?roomRecordId=' + roomRecordId,
                imgUrl: getRootPath() + '/img/oil.png',
                trigger: function (res) {
                },
                success: function (res) {
                    location.href = getRootPath() + '/loginBase.htm?roomRecordId=' + roomRecordId;
                },
                cancel: function (res) {
                },
                fail: function (res) {
                }
            };
            wx.ready(function () {
                wx.onMenuShareAppMessage(shareData);
                wx.onMenuShareTimeline(shareFriends);
            });
            wx.error(function (res) {
            });
        },
        error: function (json) {
        }
    });
}
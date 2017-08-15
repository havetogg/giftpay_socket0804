/**
 *
 *  build by rwson @ 2015-01-10
 *
 *  完成微信端的一些自适应屏幕功能(css3中的缩放特性)
 *
 */
var _LoadingHtml="<div id=\"_loading_\" class=\"_loading_\"></div>";
document.write(_LoadingHtml);
$(document).ready(function(){
    $("._loading_").animate({opacity:0});
    $("._loading_").animate({height:0});
});
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

$(function(){

    $(".perCenter").click(function(){
        location.href=getRootPath()+"/personCenter.jsp";
    })

});




$(function () {

    var w = $(window).width();
    var h = $(window).height();

    var scale = w / 640;

    var tw = Math.ceil(w / scale);
    var th = Math.ceil(h / scale);

    $(".zoomer").css({
        "-webkit-transform-origin": "0 0",
        "-moz-transform-origin": "0 0",
        "-ms-transform-origin": "0 0",
        "-o-transform-origin": "0 0",
        "transform-origin": "0 0"
    });
    $(".zoomer").css({
        "-webkit-transform": "scale(" + scale + "," + scale + ")",
        "-moz-transform": "scale(" + scale + "," + scale + ")",
        "-ms-transform": "scale(" + scale + "," + scale + ")",
        "-o-transform": "scale(" + scale + "," + scale + ")",
        "transform": "scale(" + scale + "," + scale + ")"
    });

    $("html").css({"width": w});
    // $("html").css({"height": '100%'});
    $("body").css({"width": w});
    // $("body").css({"height": '100%'});
    $(".zoomer").css({"width": tw});
    // $(".zoomer").css({"height": '100%'});
    //
    $(".content").css({"width": tw});
    // $(".content").css({"height": th});


});

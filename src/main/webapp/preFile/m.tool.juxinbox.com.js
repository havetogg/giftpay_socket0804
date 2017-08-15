/**
 *
 *  build by bsawang @ 2013-11-20
 *
 * history
 *  rwson  @ 2015-01-10 二次封装，修复bug
 *  rwson  @ 2015-01-17 增加animationEnd/transitionEnd事件的兼容处理
 *  rwson  @ 2015-02-04 修复bug，增加百度touch.js插件，单体模式创建jxTool对象，并提供属性方法
 *  rwson  @ 2015-02-06 修复bug，增加各类型浏览器判断、音乐播放器，并提供属性方法，简化调用
 *
 *  一些工具方法
 *
 */

if (!window.jxTool) {
    jxTool = {};
    window.jxTool = top.jxTool = jxTool;
}

$.extend({
	  getUrlVars: function(params){
	    var vars = [], hash;
	    var hashes = params != undefined ? params.split('&') : window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
	    for(var i = 0; i < hashes.length; i++)
	    {
	      hash = hashes[i].split('=');
	      vars.push(hash[0]);
	      var value = "";
	      for(var j = 1;j < hash.length;j++)
	      {
	    	  value += hash[j];
	    	  if(j < hash.length - 1)
	    		  value += "=";
	      }
	      vars[hash[0]] = value;
	    }
	    return vars;
	  },
	  getUrlVar: function(name){
	    return $.getUrlVars()[name];
	  },
	  
	  getUrlVar2: function(name){
		 var pageParam = $.getUrlVar('pageParam');
		 if(pageParam != undefined)
	     {
			 return $.getUrlVars(pageParam)[name];
	     }
		 else
		 {
			 return $.getUrlVars()[name];
		 }
		    
	  }
	});

/**
 * String方法扩展
 */
String.prototype.endWith=function(s){
	  if(s==null||s==""||this.length==0||s.length>this.length)
	     return false;
	  if(this.substring(this.length-s.length)==s)
	     return true;
	  else
	     return false;
	  return true;
	 };

	 String.prototype.startWith=function(s){
	  if(s==null||s==""||this.length==0||s.length>this.length)
	   return false;
	  if(this.substr(0,s.length)==s)
	     return true;
	  else
	     return false;
	  return true;
	 };
	 String.prototype.trim = function()
	 {
		 return this.replace(/(^\s*)|(\s*$)/g, "");
	 };
	 String.prototype.lTrim = function()
	 {
		 return this.replace(/(^\s*)/g, "");
	 };
	 String.prototype.rtrim = function()
	 {
		 return this.replace(/(\s*$)/g, "");
	 };
	 
	 
	 String.prototype.format = function(args) { 
	 if (arguments.length>0) { 
	 var result = this; 
	 if (arguments.length == 1 && typeof (args) == "object") { 
	 for (var key in args) { 
	 var reg=new RegExp ("({"+key+"})","g"); 
	 result = result.replace(reg, args[key]); 
	 } 
	 } 
	 else { 
	 for (var i = 0; i < arguments.length; i++) { 
	 if(arguments[i]==undefined) 
	 { 
	 return ""; 
	 } 
	 else 
	 { 
	 var reg=new RegExp ("({["+i+"]})","g"); 
	 result = result.replace(reg, arguments[i]); 
	 } 
	 } 
	 } 
	 return result; 
	 } 
	 else { 
	 return this; 
	 } 
	 };
	 
	 String.prototype.capitalize = function() {
		 return this.substring(0, 1).toUpperCase() + this.substring(1, this.length);
	 };

var browser = {
        versions: function () {
            var u = navigator.userAgent, app = navigator.appVersion;
            return {//移动终端浏览器版本信息
                "trident": u.indexOf("Trident") > -1, //IE内核
                "presto": u.indexOf("Presto") > -1, //opera内核
                "webKit": u.indexOf("AppleWebKit") > -1, //苹果、谷歌内核
                "gecko": u.indexOf("Gecko") > -1 && u.indexOf("KHTML") == -1, //火狐内核
                "mobile": !!u.match(/AppleWebKit.*Mobile.*/) || !!u.match(/AppleWebKit/), //是否为移动终端
                "ios": !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
                "android": u.indexOf("Android") > -1 || u.indexOf("Linux") > -1, //android终端或者uc浏览器
                "iPhone": u.indexOf("iPhone") > -1 || u.indexOf("Mac") > -1, //是否为iPhone或者QQHD浏览器
                "iPad": u.indexOf("iPad") > -1, //是否iPad
                "webApp": u.indexOf("Safari") == -1 //是否web应该程序，没有头部与底部
            };
        }(),
        language: (navigator.browserLanguage || navigator.language).toLowerCase()
    },

    jxTool = {
        /**
         *
         * @param key required
         * @param val required
         * @param time optional
         * 设置cookie，并可以自定义保存时间
         *
         */
        "setCookie": function (key, val, time) {
            var days = time || 30,
                date = new Date();
            date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
            document.cookie = key + "=" + escape(val) + ";expires=" + data.toGMTString();
        },

        /**
         *
         * @param key
         * @returns {*}
         * 根据当前传入的值获取相应的cookie
         *
         */
        "getCookie": function (key) {
            var arr, reg = new RegExp("(^| )" + key + "=([^;]*)(;|$)");
            if (arr = document.cookie.match(reg))
                return unescape(arr[2]);
            else
                return null;
        },

        /**
         *
         * @param key
         * 根据当前传入的key值删除相应的
         *
         */
        "deleteCookie": function (key) {
            var date = new Date();
            date.setTime(date.getTime() - 1);
            var cval = this.getCookie(key);
            if (cval != null) {
                document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
            }
        },
        
        /**
         * 相等验证
         * @param val 验证元素值
         * @param expandData 扩展验证数据(包括需要验证所有元素, 当前元素, 验证元素validate属性, 验证参数)
         */
        "equal" : function(val, expandData){
			var errorMsg = "";
			var equalElement = $(expandData["params"][0]);
			
			if($(equalElement).val() != val)
			{
				var title1 = $(expandData["element"]).attr("title");
				var title2 = $(equalElement).attr("title");
				errorMsg = title1 + "必须和" + title2 + "相同!";
			}
			return errorMsg;
        },
        
        /**
         * 选填验证
         * @param val 验证元素值
         * @param expandData 扩展验证数据(包括需要验证所有元素, 当前元素, 验证元素validate属性, 验证参数)
         */
        "choice" : function(val, expandData){
        	var errorMsg = "";
        	var allElements = expandData["allElements"];
        	$(allElements).each(function(){
        		if($(this).attr("choice") != undefined && $(this).val().trim() == "")
        		{
        			$(allElements).each(function(index){
        				if($(this).attr("choice") != undefined)
        				{
	        				errorMsg += $(this).attr("title");
	        				if(index < allElements.length - 1){errorMsg += "和";}
        				}
        			});
        			return false;
        		}
        	});

        	if(errorMsg != ""){errorMsg += "至少填写一个!";}
        	return errorMsg;
        },
        

        /**
         *
         * @param string required
         * @returns {boolean}
         * 判断是否为空
         *
         */
        "isNull": function (obj) {
            return (typeof obj == "undefined") || (obj == null) || (obj.length == 0);
        },

        /**
         *
         * @param obj required
         * @returns {boolean}
         * 根据传入参数的构造器判断是否为一个日期对象
         *
         */
        "isDate": function (obj) {
            return (obj.constructor == Date);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 根据传入参数验证是否为数字,最少一位
         *
         */
        "isNumber": function (string) {
            return (/[\d]{1,}/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 根据传入参数验证是否为浮点数
         *
         */
        "isFloat": function (string) {
            return (/^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 根据传入参数验证是否为整数,最少一位
         *
         */
        "isInt": function (string) {
        	var r = /^\+?[1-9][0-9]*$/;  //正整数 
            return r.test(string);
           // return (/^-?\d+$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 根据传入参数验证是否为小写字母,最少一位
         *
         */
        "isLowerCase": function (string) {
            return (/^[a-z]+$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 根据传入参数验证是否为大写字母,最少一位
         *
         */
        "isUpperCase": function (string) {
            return (/^[A-Z]+$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 根据传入参数验证是否为字母,最少一位
         *
         */
        "isLetter": function (string) {
            return (/^[a-zA-Z]+$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 根据传入参数验证是否为中文,最少一位
         *
         */
        "isChinese": function (string) {
            return (/^[\u4e00-\u9fa5]+$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 根据传入参数验证是否为ip地址
         *
         */
        "isIp": function (string) {
            if ((/^([0-9]{1,3}\.){3}[0-9]{1,3}$/).test(string)) {
                var stringArr = string.split("."),
                    res = true;
                $.each(stringArr, function (i, j) {
                    if (jxTool.isNumber(j) && parseInt(j) > 0 && j < 255) {
                    } else {
                        res = false;
                    }
                });
                return res;
            } else {
                return false;
            }
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 判断是否为字符串
         *
         */
        "isWord": function (string) {
            return /^[a-zA-Z0-9_]+$/.test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 判断是否为邮箱
         *
         */
        "isEmail": function (string) {
            return (/^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/i).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 判断是否为手机号
         *
         */
        "isMobile": function (string) {
            return (/^1\d{10}$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 判断是否为网址
         *
         */
        "isUrl": function (string) {
            return (/^[A-Za-z]+:\/\/[A-Za-z0-9-_]+\\.[A-Za-z0-9-_%&\?\/.=]+$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 判断是否为身份证号
         *
         */
        "isIdNumber": function (string) {
            return (/(\d{18}|\d{17}\w{1})/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 判断是否为QQ号
         *
         */
        "isQQ": function (string) {
            return (/^[1-9]{1}[\d]{4,11}$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 判断是否为电话号
         *
         */
        "isTelephone": function (string) {
            return (/^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {boolean}
         * 判断是否为邮编
         *
         */
        "isPostalCode": function (string) {
            return (/^[0-9]{6}$/).test(string);
        },

        /**
         *
         * @param string required
         * @returns {number}
         * 返回字符串长度，中文算2个字符
         *
         */
        "stringLength": function (string) {
            var length = 0;
            for (var i = 0; i < string.length; i++) {
                if (string.charCodeAt(i) > 255) {
                    length += 2;
                } else {
                    length++;
                }
            }
            return length;
        },

        /**
         *
         * @param min required
         * @param max required
         * @returns {number}
         * 根据传入的最值区间返回区间内任意值
         *
         */
        "getRangeRandom": function (min, max) {
            return (Math.floor(Math.random() * (max - min)) + min);
        },

        /**
         *
         * @param opt required
         * 创建音乐播放器
         *
         */
        "musicPlayer": function (opt) {
            var defaults = {
                    "music": "music/small-apple.mp3",
                    "autoPlay": true,
                    "controller": true
                },
                opts = $.extend({}, defaults, opt || {}),
                music = $("<audio id='mp3Player' style='display:none;width:0;height:0;opacity:0;'> + <source src='" + opts["music"] + "' type='audio/mpeg'>"),
                controller = $("<div class='mp3controller wait absolute'></div>");
            $("div.zoomer").append(music);
            if (opts["autoPlay"]) {
                $(music).ready(function () {
                    document.getElementById("mp3Player").play();
                });
            }
            if (opts["controller"]) {
                $("div.zoomer").append(controller);
                $("div.mp3controller").click(function () {
                    if (document.getElementById("mp3Player").paused) {
                        $(this).removeClass("pause");
                        document.getElementById("mp3Player").play();
                    } else {
                        $(this).addClass("pause");
                        document.getElementById("mp3Player").pause();
                    }
                });
            }
        }
    };

validateFailed = {
        "isNull": "{0}不能为空!",						

        "isDate": "日期格式错误!",

        "isNumber": "{0}必须是数字!",

        "isFloat": "{0}必须是小数!",

        "isInt": "{0}必须是整数!",
        
        "isLowerCase": "{0}必须是小写字母!",

        "isUpperCase": "{0}必须是大写字母!",

        "isLetter": "{0}必须是字母!",

        "isChinese": "{0}必须是中文!",

        "isIp": "ip格式错误!",

        "isWord": "{0}必须是字符串!",

        "isEmail": "邮箱格式错误!",

        "isMobile": "手机号必须是11位有效数字!",

        "isUrl": "url格式错误!",

        "isIdNumber": "身份证号格式错误!",
        
        "isQQ": "qq号格式错误!",

        "isTelephone": "电话号码格式错误!",

        "isPostalCode": "邮编格式错误!"
    };

//异步菊花
function loading(state, callback) {
    if (state == "start") {
        $("body").prepend('<div id="loading"><div id="loading_bg"></div><div id="loading_div"><div id="loading_pic"></div></div></div>');
    } else if (state == "stop") {
        $("#loading").remove();
    }

}

//jquery ajax 异步调用
function gA(url, type, data, useLoading, callbackFunction) {
        $.ajax({
        type: type,
        dataType: "json",
        url: url,
        data: data,
        beforeSend: function () {
            if (useLoading) {
                loading("start");
            }
        },
        complete: function (XMLHttpRequest) {
            if (useLoading) {
                loading("stop");
            }
        },
        success: function (json) {
            if (json.code == 1) {
                callbackFunction(json);
            } else {
                alerw(json.mess);
            }
        },
        error: function (XMLHttpRequest, textStatus, thrownError) {
            alerw("访问出错，请刷新或重新登录");
        }
    });
}

function gAcode(url, type, data, useLoading, callbackFunction) {
    $.ajax({
        type: type,
        dataType: "json",
        url: url,
        data: data,
        beforeSend: function () {
            if (useLoading) {
                loading("start");
            }
        },
        complete: function (XMLHttpRequest) {
            if (useLoading) {
                loading("stop");
            }
        },
        success: function (json) {
            //if (json.code == code) {
            callbackFunction(json);
            //} else {
            //alerw(json.mess);
            //}
        },
        error: function (XMLHttpRequest, textStatus, thrownError) {
            alerw("访问出错，请刷新或重新登录");
        }
    });
}

//自定义警告框
function alerw(titletext, btext, okFunction) {

    with (window.top.document) {

        if ($("#alerw")[0]) {
            $("#alerw").remove();
        }

//        if (content == null || String(content) == "") {
//            content = "发生错误，请重试";
//        }
        if (titletext == null || String(titletext) == "") {
            titletext = "注意";
        }
        if (btext == null || String(btext) == "") {
            btext = "确定";
        }

        $("body").append("<div id='alerw'></div>");
        $("#alerw").hide();
        $("#alerw").append("<div id='alerw_bg'></div>");
        $("#alerw").append("<div id='alerw_div'></div>");
        $("#alerw_div").append("<div class='title'>" + titletext + "</div>");
//        $("#alerw_div").append("<div class='content'>" + content + "</div>");
        $("#alerw_div").append("<a class='ab ab_one ab_ok' href='javascript:;'>" + btext + "</a>");

        $("#alerw").animate({opacity: 'show'}, 100, "", function () {
            $("#alerw").focus();
        });

        $("#alerw_div .wx_close").click(function () {
            $("#alerw").animate({opacity: 'hide'}, 100);
        });

        $("#alerw_div .ab_ok").click(function () {
            $("#alerw").animate({ opacity: 'hide'}, 100, "", function () {
                if (okFunction) {
                    okFunction();
                }
            });
        });

        $("#alerw").bind("keypress", function (e) {
            var ev = document.all ? window.event : e;
            if (ev.keyCode == 13) {
                $("#alerw_div .ab_ok").addClass("active");
                $("#alerw_div .ab_ok").click();
            }
        });

    }

}

//自定义确认框
function confirw(content, titletext, btext, bctext, okFunction, cancelFunction) {

    with (window.top.document) {

        if ($("#alerw")[0]) {
            $("#alerw").remove();
        }
        if (content == null || String(content) == "") {
            content = "发生错误，请重试";
        }
        if (titletext == null || String(titletext) == "") {
            titletext = "请确认";
        }
        if (btext == null || String(btext) == "") {
            btext = "确定";
        }
        if (bctext == null || String(bctext) == "") {
            bctext = "取消";
        }

        $("body").append("<div id='alerw'></div>");
        $("#alerw").hide();
        $("#alerw").append("<div id='alerw_bg'></div>");
        $("#alerw").append("<div id='alerw_div'></div>");
        $("#alerw_div").append("<div class='title'>" + titletext + "</div>");
        $("#alerw_div").append("<div class='content'>" + content + "</div>");

        $("#alerw_div").append("<a class='ab ab_two ab_c' href='javascript:;'>" + bctext + "</a>");
        $("#alerw_div").append("<a class='ab ab_two ab_ok' href='javascript:;'>" + btext + "</a>");

        $("#alerw").animate({opacity: 'show'}, 100, "", function () {
            $("#alerw").focus();
        });

        $("#alerw_div .ab_ok").click(function () {
            $("#alerw").animate({ opacity: 'hide'}, 100, "", function () {
                if (okFunction) {
                    okFunction();
                }
            });
        });

        $("#alerw_div .ab_c").click(function () {
            $("#alerw").animate({opacity: 'hide'}, 100, "", function () {
                if (cancelFunction) {
                    cancelFunction();
                }
            });
        });

        $("#alerw").bind("keypress", function (e) {
            var ev = document.all ? window.event : e;
            if (ev.keyCode == 13) {
                $("#alerw_div .ab_ok").addClass("active");
                $("#alerw_div .ab_ok").click();
            } else if (ev.keyCode == 27) {
                $("#alerw_div .ab_c").addClass("active");
                $("#alerw_div .ab_c").click();
            }
        })

    }

}

//检测网址合法
function checkurl(url) {

    if (url != null && url != "") {
        url = url.toLocaleLowerCase();
        var strRegex = "(https?|ftp|mms):\/\/([_\-]?[A-z0-9]+\.)*[A-z0-9]+\-?[A-z0-9]+\.[A-z]{2,}(\/.*)*\/?";
        var re = new RegExp(strRegex);
        if (!re.test(url)) {
            alerw("网址格式不合法");
            return false;
        } else {
            return true;
        }
    } else {
        return true;
    }
}

//检测邮箱合法性
function checkemail(email) {

    var strRegex = "^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$";
    var re = new RegExp(strRegex);
    if (!re.test(email)) {
        alerw("请输入正确的邮箱格式");
        return false;
    } else {
        return true;
    }
}

//缩减字符串长度
function sss(str, sl) {

    var str1 = $('<div>' + str + '</div>').text();
    return str1.length > sl ? str1.substr(0, sl) + ".." : str;

}

//保留两位小数
function ff(money) {
    return parseFloat(Math.floor(money * 10000) / 10000).toFixed(2);
}

//时间字符串转换为时间对象
function ttime(str) {
    var s = str.split(" ");
    var s1 = s[0].split("-");
    var s2 = s[1].split(":");
    return new Date(s1[0], s1[1] - 1, s1[2], s2[0], s2[1], s2[2]);
}

function ttime2(mms) {
    return new Date(mms);
}

function ftime(time) {
    return time.format("yyyy-MM-dd hh:mm:ss");
}

//时间输出格式化
Date.prototype.format = function (format) {

    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(), //day
        "h+": this.getHours(), //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    }

    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }

    return format;
};

/**
 *
 * @param show
 * @param text
 *
 * 给层做一个高度的自适应
 */

function screenalert(show, text) {
    if (show) {
        var alertdom = $('<div class="screenalert">' +
            '<div class="mask">' +
            '<div class="info">' +
            text +
            '</div></div></div>');

        if ($("body .screenalert")[0]) {
            $("div .screenalert").replaceWith(alertdom);
        } else {
            $("body").append(alertdom);
        }

    } else {
        $("div.screenalert").remove();
    }

    $("#loading").css({
        "height": $(document).height() > $("body").height() ? $(document).height() : $("body").height()
    });
}

/**
 *
 * @param imgurls   [String/Array]
 * @param callback  [Function]
 *
 * 用于预加载页面静态图片，在图片未加载完全时，显示一个遮罩，优化用户体验
 */

function preloadImage(imgurls, callback) {
    if (imgurls.length > 0) {
        loading("start");
        screenalert(true, "正在加载图片，请稍候 ( 0 / " + imgurls.length + " )");
        var loadedimgs = [];
        $.each(imgurls, function (i, imgurl) {
            var img = new Image();
            img.src = imgurl;
            img.onload = function () {
                loadedimgs.push(imgurl);
                $(".info").text("正在加载图片，请稍候 （ " + loadedimgs.length + " / " + imgurls.length + " ）");
                if (loadedimgs.length == imgurls.length) {
                    $(".info").text("加载完成");
                    loading("stop");
                    screenalert(false);
                    callback && $.isFunction(callback) && callback();
                }
            };
        })
    } else {
        callback && $.isFunction(callback) && callback();
    }
}

/**
 *
 * @param callback Function Required
 *
 * CSS3 中的animationEnd事件兼容处理
 */

$.fn.animateEnd = function (callback) {
    var names = {
        "Moz": "animationend", "webkit": "webkitAnimationEnd", "ms": "MSAnimationEnd", "O": "oAnimationEnd"
    };
    for (var i in names) {
        if (names[i]) {
            this.on(names[i], function () {
                callback && $.isFunction(callback) && callback();
            });
        }
    }
};


/**
 *
 * @param callback Function Required
 *
 * CSS3 中的transitionEnd事件兼容处理
 */
$.fn.transitionEnd = function (callback) {
    var names = {
        "Moz": "transitionend", "webkit": "webkitTransitionEnd", "ms": "MSTransitionEnd", "O": "oTransitionEnd"
    };
    for (var i in names) {
        if (names[i]) {
            this.on(names[i], function () {
                callback && $.isFunction(callback) && callback();
            });
        }
    }
};

/**
 *
 * 阻止页面滚动
 */
function preventScroll() {
    $(document).bind("touchmove", function (ev) {
        var oEv = ev || event;
        if (oEv.preventDefault) {
            oEv.preventDefault();
        } else {
            return false;
        }
    });
}

/**
 * 获得关联字段
 */
function getCascade(getElement)
{
	var cascade = "";
	var elements = $(getElement).findFirstAttr("name");
	$(elements).each(function(){
		var field = $(this).attr("name");
		
		if(field != undefined && field != "")
		{
			if($(this).find("[name]").length > 0 || /^link-.+$/.test(field))
			{
				if(/^link-.+$/.test(field))
				{
					field = field.split("-")[1];
				}
				
				cascade += field + ",";
			}
		}
	});
	if(cascade != ""){cascade = cascade.substring(0, cascade.length - 1);}
	return cascade;
};

/**
 * 获得查询字段
 * @returns {String}
 */
function getQueryField(entityName, getElement)
{
	var queryField = "";
	var elements = $(getElement).findFirstAttr("name");
	$(elements).each(function(){
		var field = $(this).attr("name");
		
		if(field != undefined && field != "")
		{
			var parent = $(this);
			var elements2 = $(parent).findFirstAttr("name");
			
			if(elements2.length > 0 || /^link-.+$/.test(field))
			{
				if(/^link-.+$/.test(field))
				{
					var linkname = field.split("-")[1];
					parent = $("body").find("[name=" + linkname + "]");
					elements2 = $(parent).findFirstAttr("name");
				}
				
				$(elements2).each(function(){
					var field2 = $(this).attr("name");
					queryField += parent.attr("name") + "." + field2 + ",";
				});
			}
			else
			{
				queryField += entityName + "." + field + ",";
			}
		}
	});
	if(queryField != ""){queryField = queryField.substring(0, queryField.length - 1);}
	return queryField;
};

var tipdom;
/*
分享页面
 */
    function showShareTip(type) {

    var pictype = type || "share";

    tipdom = $('<div id="jxtip">' +
    '<img src="http://www.juxinbox.com/img/jxshare/jx' + pictype + '.png" />' +
    '<a class="jxtiplogo"></a>' +
    '</div>');

    tipdom.css({
        "width": "100%",
        "height": "100%",
        "backgroundColor": "rgba(102,102,102,0.98)",
        "position": "fixed",
        "left": "0",
        "top": "0",
        "zIndex": "99999"
    });

    $("img", tipdom).css({
        "width": "100%",
        "position": "absolute"
    });

    $(".jxtiplogo", tipdom).css({
        "display": "block",
        "width": "188px",
        "height": "20px",
        "backgroundImage": "url('http://www.juxinbox.com/img/jxshare/jxlogo.png')",
        "backgroundSize": "contain",
        "position": "absolute",
        "marginLeft": "-94px",
        "left": "50%",
        "bottom": "40px"
    });

    tipdom.click(function () {
        $(this).remove();
    });

    $("body").append(tipdom);
}
    
    function showShareTip2(type) {

        var pictype = type || "share";

        tipdom = $('<div id="jxtip" style="background: url(img/share1.png)">' +
        //'<img src="img/' + pictype + '.png" />' +
        '<a class="jxtiplogo"></a>' +
        '</div>');

        tipdom.css({
            "width": "100%",
            "height": "100%",
            "background-size":"cover",
            "backgroundColor": "rgba(227,83,75,1)",
            "position": "fixed",
            "left": "0",
            "top": "0",
            "zIndex": "99999"
        });

        //$("img", tipdom).css({
        //    "width": 640/scale,
        //    "height":970/scale-100,
        //    "position": "absolute",
        //    "bottom":0
        //});

        $(".jxtiplogo", tipdom).css({
            "display": "block",
            "width": "188px",
            "height": "20px",
            "backgroundImage": "url('http://www.juxinbox.com/img/jxshare/jxlogo.png')",
            "backgroundSize": "contain",
            "position": "absolute",
            "marginLeft": "-94px",
            "left": "50%",
            "bottom": "40px"
        });

        tipdom.bind("touchend",function () {
            $(this).remove();
        });

        $("body").append(tipdom);
    }

//按钮扩展
$.fn.extend({
    /**
     *
     * @param obj  属性对象可以自己添加
     * 默认的为下面正常按钮定义
     */
        addButton: function(obj){
            var attr={
                "id":1,
                "word":"立即提交",
                "width":560,
                "height":80,
                "fontSize":"30px",
                "color":"#ffffff",
                "borderRadius":"12px",
                "textAlign":"center",
                "background":"#ea3947",
                "margin":"10px "+"auto",
                "display":"block",
                "lineHeight":"56px",
                "border":"none"
            };
            var objs = $.extend({},attr,obj||{});
        $(this).append('<button id=btn-'+objs.id+'>'+objs.word+'</button>');
            $('#btn-'+objs.id).css({
                "width":objs.width,
                "height":objs.height,
                "font-size":objs.fontSize,
                "-webkit-border-radius":objs.borderRadius,
                "border-radius":objs.borderRadius,
                "color":objs.color,
                "text-center":objs.textAlign,
                "background":objs.background,
                "margin": objs.margin,
                "display":objs.display,
                "line-height":objs.lineHeight,
                "border":objs.border
            });
        }
});
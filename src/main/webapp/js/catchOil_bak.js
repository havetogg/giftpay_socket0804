var roomRecordId = '';
var goEasy;
var allnum=0;
var isContent=true;
var rate='';

var time;
var h=0;
var m=0;
var s=0;
$(function() {
	var selfOpenId = getUrlParam("openId");
	roomRecordId = getUrlParam("roomRecordId");
	console.log(localStorage.getItem("aaaa"))
	goEasy = new GoEasy({
		appkey: 'PC-28366e012ef24185ad8ea95a4c370c73',
		otp:getUrlParam("temp"),
		onConnected: function() {
		},
		onDisconnected: function() {
		},
		onConnectFailed: function(error) {
		}
	});

	goEasy.subscribe({
		channel: roomRecordId+"catch",
		onMessage: function(message) {
			var res=(message.content).split(",");
			var oilNum=$(".oilNum").html();
			if($(".oilNum").html().length>4){
				$(".oilNum").css('font-size','50px');
			}
			else{
				$(".oilNum").css('font-size','60px');
			}
			oilNum=oilNum*1;
			var allOilNum=getUrlParam("oilNum");
			if(oilNum!=0){
				$(".catch_"+res[0]).html(res[1]);
				var num=0;
				$.each($(".catch_oil label"),function(index,item){
					num+=$(item).html()*1;
				})
				$(".oilNum").html(allOilNum*1-num);
				$(".nowOilNum").html(allnum);
				if((allOilNum*1-num)<=0){
					TipShow("已抢完!",1000);
					$(".catch_text").show();
					$(".catch_btn").attr("onclick","backHome()");
					$("#catch").attr("src","img/send.png");
					var arr=new Array();
					$(".catch_oil label").each(function(index,item){
						var obj=new Object();
						var clazz=$(item).attr("class");
						clazz=clazz.substring(clazz.indexOf("catch_")+6,clazz.length);
						obj.openId=clazz;
						var num=$(item).html();
						obj.num=num;
						var rate=$("rate_"+clazz).html();
						obj.rate=rate;
						arr.push(obj)
					});
					$.ajax({
						type: "GET",
						url: getRootPath() + "/updateOilRecordStatus.htm",
						data: {"params": JSON.stringify(arr),"roomRecordId": roomRecordId},
						dataType: "json",
						success: function (data) {
							$.ajax({
								type: "GET",
								url: getRootPath() + "/updateRoomRecordStatus.htm",
								data: {"roomRecordId": roomRecordId},
								dataType: "json",
								success: function (data) {
									// if(data.code=='1'){
										TipShow("已抢完",1000);
									// }else{
									// }
								}
							});
							localStorage.clear();
							location.href=getRootPath()+"/loginBase.htm?roomRecordId="+roomRecordId;
						}
					});

				}
			}
		}
	});

	goEasy.subscribe({
		channel: roomRecordId+"content",
		onMessage: function(message) {
			var res=JSON.parse(message.content);
			var content=decodeURI(res.content);
			var headImg=res.headImg;
			var name=decodeURI(res.nickName);
			var str="";
			str+='<li class="flex"><div>';
			str+='<img class="catch_icon" src="'+headImg+'" alt="">';
			str+='</div><div class="flex-1 message_content">';
			str+='<div class="message_userName">'+name+'</div><div>';
			str+=content;
			str+='</div></div>';
			str+='</li>';
			$(".messageList_ul").prepend(str);
		}
	});

	initUserInfo();
	initOilRecordList();
	initAllContentList();
	userIsExist();
	$("#catch").on("click",function(){http://tdev.juxinbox.com/giftpay_socket/loginIndexBase.htm
		var ran=(Math.random()*100).toFixed(0);
		var oilNum=$(".oilNum").html();
		var rannum=0;
		if(ran<(rate*1)&&(oilNum*1)>($(".catch_oil").length*2)){
			Crit();
			rannum=2;
			allnum+=2;
		}else{
			rannum=1;
			allnum+=1;
		}
		var val=localStorage.getItem(selfOpenId+"_"+roomRecordId);
		if(val==null||val==''){
			localStorage.setItem(selfOpenId+"_"+roomRecordId,'1')
		}else{
			localStorage.setItem(selfOpenId+"_"+roomRecordId,allnum)
		}
		goEasy.publish({
			channel: roomRecordId+"catch",
			message: selfOpenId+","+allnum+""
		});
	})
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
		}
	})
	var isTim=getUrlParam("isTim");
	var timingTime=getUrlParam("timingTime");
	timingTime=decodeURI(decodeURI(timingTime));
	if(isTim !='1'){
		$("#catch_btn").hide();
		$("#catch").show();
		$('#catch_out').hide();
		$(".catch_out_time").hide();
	}else{
		timingTimeString(timingTime);
	}
})
function run(){
	if(h==0&&m==0&&s==0){
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
	else{
		--s;
		if(s<0){
			--m;
			s=59;
		}
		if(m<0){
			--h;
			m=59
		}
		if(h<0){
			s=0;
			m=0;
		}
		if(h<10){
			$('#a1').html('0');
			$('#a2').html(h);
		}
		else{
			$('#a1').html(h.toString().substr(0,1));
			$('#a2').html(h.toString().substr(1,1));
		}
		if(m<10){
			$('#a3').html('0');
			$('#a4').html(m);
		}
		else{
			$('#a3').html(m.toString().substr(0,1));
			$('#a4').html(m.toString().substr(1,1));
		}
		if(s<10){
			$('#a5').html('0');
			$('#a6').html(s);
		}
		else{
			$('#a5').html(s.toString().substr(0,1));
			$('#a6').html(s.toString().substr(1,1));
		}
	}
}

function userIsExist(){
	$.ajax({
		type: "GET",
		url: getRootPath() + "/isExistUserLogin.htm",
		data:{"openId":getUrlParam("openId")},
		dataType: "json",
		success: function (data) {
			if(data.code=='1'){
				alert("用户异常!");
				location.href=getRootPath()+'/loginIndexBase.htm';
			}
		}
	});
}
function initAllContentList(){
	$.ajax({
		type: "GET",
		url: getRootPath() + "/queryContetList.htm",
		data: {"roomRecordId": roomRecordId},
		dataType: "json",
		success: function (data) {
			console.log("查询留言:"+data);
			if(data.success){
				//有留言
				var res=JSON.parse(data.data);
				var str="";
				$.each(res,function(index,item){
					str+='<li class="flex"><div>';
					str+='<img class="catch_icon" src="'+item.headImg+'" alt="">';
					str+='</div><div class="flex-1 message_content">';
					str+='<div class="message_userName">'+decodeURI(decodeURI(item.nickName))+'</div><div>';
					str+=decodeURI(item.content);
					str+='</div></div>';
					str+='</li>';
				})
				$(".messageList_ul").html(str);
			}else{
				//无留言
				console.log("无留言");
			}
		}
	});
}
function initAddContent(content){
	$.ajax({
		type: "GET",
		url: getRootPath() + "/addContentModel.htm",
		data: {"roomRecordId": roomRecordId, "content": encodeURI(encodeURI(content))},
		dataType: "json",
		success: function (data) {
			console.log("添加留言:"+data);
			if(data.result!=0){
				//添加成功
				// alert("留言成功");
                $(".contentInput").val('');
				goEasy.publish({
					channel: roomRecordId+"content",
					message: data.data
				});
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
		data: { "roomRecordId": roomRecordId,"openId":getUrlParam("openId")},
		dataType: "json",
		success: function(data) {
			console.log(data);
			if(data.success){
				var openId = getUrlParam("openId");
				var record = JSON.parse(data.data);
				$(".roomPeopleNum").html(record.length + 1);
				var str = '';
				for(var re in record) {
					var oil = record[re];
					$.each($(".userOpenId"), function(index, item) {
						if(item.value == openId) {
							isExist = false;
							return;
						}
					})
					localStorage.setItem(oil.openId, oil.openId);
					str += '<li class="catch_box">';
					str += '<img class="catch_icon" src="' + oil.headImg + '" alt="">';
					str += '<a class="rank"></a>';
					str += '<a class="catch_name">' + decodeURI(decodeURI(oil.nickName)) + '</a>';
					str += '<a class="catch_oil"><label class="catch_'+oil.openId+'">0</label>滴</a><input class="userOpenId" type="hidden" value="' + oil.openId + '">';
					str += '<span class="rate_'+oil.openId +'"  style="display: none">' + oil.rate  + '</span></li>';
				}
				$(".catchList_ul").html(str);
			}
		}
	});
}

function backHome(){
	location.href=getRootPath()+"/loginIndexBase.htm";
}
function initUserInfo() {
	var isInit=getUrlParam("isInit");
	if(isInit=='0'){
		$('.catch_block').show();
		fiexedBody();
	}else{
		$('.catch_block').hide();
		freeBody();
	}
	var nickName = getUrlParam("name");
	nickName = decodeURI(decodeURI(nickName));
	var headImg = getUrlParam("img");
	var openId = getUrlParam("openId");
	var roomOpenId = getUrlParam("roomOpenId");
	var roomHead = getUrlParam("roomHead");
	var roomName = getUrlParam("roomName");
	roomName=decodeURI(decodeURI(roomName));
	var oilNum=getUrlParam("oilNum");
	$(".oilNum").html(oilNum);
	if($(".oilNum").html().length>4){
		$(".oilNum").css('font-size','50px');
	}
	else{
		$(".oilNum").css('font-size','60px');
	}
	$(".roomImg").attr("src", roomHead);
	$(".roomName").html(roomName + "老板正在送油");
	$(".catch_name_self").html(nickName);
	$(".catch_icon").attr("src", headImg);
	$(".contentInput").attr("placeholder","你有什么想对"+roomName+"说的吗");
	$(".demoOil").removeAttr("class").attr("class",'catch_'+openId+"");
	$("#selfUserOpenId").val(openId);
	rate=getUrlParam("rate")
	$("#selfUserRate").val(rate);
	$("#selfUserRate").addClass("userRate");
	var val=localStorage.getItem(openId+"_"+roomRecordId);
	if(val==null||val==''){
		val='0';
	}else{
		allnum=val*1;
		$(".nowOilNum").html(allnum);
	}
	$('.catch_'+openId+"").html(val);
	var alloilNum=$(".oilNum").html();
	alloilNum=alloilNum*1;
	$(".oilNum").html(alloilNum-($('.catch_'+openId+"").html())*1);
	goEasy.publish({
		channel: roomRecordId,
		message: openId+","+headImg+","+getUrlParam("name")+","+getUrlParam("rate")
	});
	goEasy.subscribe({
		channel: roomRecordId,
		onMessage: function(message) {
			var res=(message.content).split(",");
			var isExist = true;
			$.each($(".userOpenId"), function(index, item) {
				if(item.value == res[0]) {
					isExist = false;
					return;
				}
			})
			if(isExist) {
				$(".roomPeopleNum").html($(".roomPeopleNum").html() * 1 + 1);
				var str = '<li class="catch_box">';
				str += '<img class="catch_icon" src="' + res[1] + '" alt="">';
				str += '<a class="rank"></a>';
				str += '<a class="catch_name">' +decodeURI(decodeURI(res[2]))  + '</a>';
				str += '<a class="catch_oil"><label class="catch_'+res[0]+'">0</label>滴</a><input class="userOpenId" type="hidden" value="' + res[0] + '">';
				str += '<span class="rate_'+res[0]+'"  style="display: none">' + res[3] + '</span></li>';
				$(".catchList_ul").append(str);
			}
		}
	});
}
function timingTimeString(tim){
	var date1=getDate(getUrlParam("now"));  //开始时间
	var date2=getDate(tim);    //结束时间
	var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
	if(date3<=0){
		$("#catch_btn").hide();
		$("#catch").show();
		$('#catch_out').hide();
		$(".catch_out_time").hide();
		return ;
	}else{
		$("#catch_btn").show();
		$("#catch").hide();
		time=setInterval("run()",1000);
	}
	var leave1=date3%(24*3600*1000)
	//计算天数后剩余的毫秒数
	h=Math.floor(leave1/(3600*1000))
	//计算相差分钟数
	var leave2=leave1%(3600*1000)
	//计算小时数后剩余的毫秒数
	m=Math.floor(leave2/(60*1000))


	//计算相差秒数
	var leave3=leave2%(60*1000)
	//计算分钟数后剩余的毫秒数
	s=Math.round(leave3/1000);
}
function getDate(strDate){
	var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,
			function (a) { return parseInt(a, 10) - 1; }).match(/\d+/g) + ')');
	return date;
}

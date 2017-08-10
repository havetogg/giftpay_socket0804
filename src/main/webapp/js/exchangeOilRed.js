var isBindPhone=false;//是否绑定手机号
var oilCardNum=0;   //兑换默认个数
var errMsg='';
var maxOilCardNum=0;


$(function(){
    initUserOilNum();
    isExistUserPhone();
    close_ExchangeTip2();
});



function initUserOilNum(){
    $.ajax({
        type: "post",
        url: getRootPath() + "/queryUserOilBalance.htm",
        dataType: "json",
        success: function (data) {
            if(data.code=='0'){
                var oilBalance=(JSON.parse(data.mess)).oilBalance;
                $(".exchange_smallTile").html("油库："+oilBalance+"滴");
                oilBalance=oilBalance*1;
                maxOilCardNum=oilBalance/5000;
                maxOilCardNum=Math.floor(maxOilCardNum);
                console.log("----------------------------"+maxOilCardNum)
            }else{
                TipShow(data.mess,1000);
            }
        }
    })
}
function toPay() {
    if(maxOilCardNum==0){
        TipShow("未满足兑换条件",1000);
        return;
    }
    if(oilCardNum==0){
        TipShow("请选择兑换数量",1000);
        return;
    }
    if(isBindPhone){
        $('#tip_').attr('class','exchange_nav1');// 显示兑换是否兑换的兑换框
    }else{
        $('#bg_nav').attr('class','bg_nav2');
    }
}
function sure() {
    var phone = $("#tel").val();
    var code = $("#code").val();
    if(!phone){
        TipShow("请输入手机号码号码",1000);
        return ;
    }
    else if(!jxTool.isMobile(phone)){
        TipShow('请输入正确的手机号',1000);
        return ;
    }
    else if(!code){
        TipShow('请输入验证码',1000);
        return ;
    }
    else{
        TipShow('成功',1000);
        var json = {};
        json.mobile = phone;
        json.valNum=code;
        $.ajax({
            url:getRootPath()+"/updateUserPhone.htm",
            type:"post",
            data:json,
            dataType:"json",
            success:function(data){
                if(data.code =="10000" ){
                    TipShow("绑定成功",1000);
                    isBindPhone=true;
                    $('#bg_nav').attr('class','bg_nav1');
                }else{
                    TipShow(data.mess,1000);
                }
            }
        });
    }
}
function exchangeSure() {
    $('.exchange_nav').hide();
    window.location.href=getRootPath()+"/getZshOpenId.htm?oilCardNum="+oilCardNum;
}

function isExistUserPhone(){
    $.ajax({
        type: "post",
        url: getRootPath() + "/isExistUserPhone.htm",
        dataType: "json",
        success: function (data) {
            console.log("用户是否填写手机号:"+data);
            if(data.code=='0'){
                isBindPhone=true;
            }else{
                isBindPhone=false;
                errMsg=data.mess;
            }
        }
    })
}
function addRedpkgNum(){
    // if(oilCardNum<maxOilCardNum){
    if(oilCardNum<1){
        oilCardNum=oilCardNum+1;
    }
    $(".exchange_number_number").html("x"+oilCardNum+"张");
}
function removeRedpkgNum(){
    if(oilCardNum>0){
        oilCardNum=oilCardNum-1;
    }
    $(".exchange_number_number").html("x"+oilCardNum+"张");
}

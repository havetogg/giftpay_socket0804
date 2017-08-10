<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>index Page</title>
</head>
<script>
    //js获取项目根路径，如： http://localhost:8083/uimcardprj
    function getRootPath() {
        // 获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
        var curWwwPath = window.document.location.href;
        // 获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        // 获取主机地址，如： http://localhost:8083
        var localhostPaht = curWwwPath.substring(curWwwPath.indexOf("//"), pos);
        // 获取带"/"的项目名，如：/uimcardprj
        var projectName = pathName
                .substring(0, pathName.substr(1).indexOf('/') + 1);
        return (localhostPaht + projectName);
    }
</script>
<body>
    Welcome<p id="totalOilNum"></p><br/>
    <button onclick="send()">抢</button>
    <hr/>
    <button onclick="closeWebSocket()">关闭WebSocket连接</button>
    <hr/>
    <div id="message"></div>
    <table id="tb" class="altrowstable">
		<th align="center"  colspan="9">实时信息监控</th>
	</table>
</body>

<script type="text/javascript">
    var websocket = null;
    var storage = window.localStorage;
    var openId = "";
    if(storage.getItem("openId")==null){
        openId = randomString(10);
        storage.setItem("openId",openId);
    }else{
        openId = storage.getItem("openId");
    }
    var room = 1;
    //判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://"+getRootPath()+"/websocketTwo/room="+room+"&openId="+openId);
    }
    else {
        alert('当前浏览器 Not support websocket')
    }

    //连接发生错误的回调方法
    websocket.onerror = function () {
        console.log("WebSocket连接发生错误");
    };

    //连接成功建立的回调方法
    websocket.onopen = function (event) {
        console.log("WebSocket连接成功");
    }

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        console.log(event.data);
        setMessageInnerHTML(event.data);
    }

    //连接关闭的回调方法
    websocket.onclose = function () {
        console.log("WebSocket连接关闭");
    }

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket();
    }

    var openIds = [];
    //将消息显示在网页上
    function setMessageInnerHTML(data) {
        var jsonDate = JSON.parse(data);
        document.getElementById("totalOilNum").innerHTML=jsonDate.totalOilNum;
        if(jsonDate.type==0){
            var jsonArray = jsonDate.jsonArray;
            for(var i=0;i<jsonArray.length;i++){
                var thisOpenId = jsonArray[i].openId;
                if(openIds.indexOf(thisOpenId)<0){
                    openIds.push(thisOpenId);
                    var row;
                    var table=document.getElementById("tb");
                    row=table.insertRow(1);
                    //设置文字
                    var row1 = row.insertCell(0);
                    var element1 = document.createElement('p');
                    if(thisOpenId == openId){
                        element1.style = "color:red;";
                    }
                    element1.appendChild(document.createTextNode(thisOpenId))
                    row1.appendChild(element1);
                    //设置数字
                    var row2 = row.insertCell(1);
                    var element2 = document.createElement('p');
                    element2.id=thisOpenId;
                    if(thisOpenId == openId){
                        element2.style = "color:blue;";
                    }
                    element2.appendChild(document.createTextNode(jsonArray[i].oilNum));
                    row2.appendChild(element2);
                }
            }
            for(var i=0;i<openIds.length;i++){
                var del = true;
                for(var j=0;j<jsonArray.length;j++){
                    if(openIds[i] == jsonArray[j].openId){
                        del =false;
                    }
                }
                if(del){
                    var index = openIds.indexOf(openIds[i]);
                    var table=document.getElementById("tb");
                    table.deleteRow(openIds.length-index);
                    openIds.remove(openIds[i]);
                }
            }
        }else if(jsonDate.type==1){
                document.getElementById(jsonDate.openId).innerHTML=jsonDate.oilNum;
        }
    }

    //关闭WebSocket连接
    function closeWebSocket() {
        websocket.close();
    }

    //发送消息
    function send() {
        websocket.send(name);
    }

    function randomString(len) {
        len = len || 32;
        var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';    /****默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/
        var maxPos = $chars.length;
        var pwd = '';
        for (i = 0; i < len; i++) {
            pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
        }
        return pwd;
    }

    Array.prototype.remove = function(val) {
        var index = this.indexOf(val);
        if (index > -1) {
            this.splice(index, 1);
        }
    };

    function setOilNum(totalOilNum) {
        websocket = new WebSocket("ws://"+getRootPath()+"/websocketTwo/room="+room+"&openId="+openId+"&totalOilNum="+totalOilNum);
    }
</script>
</html>
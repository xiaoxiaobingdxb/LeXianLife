<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: dengxiaobing
  Date: 2017/9/20
  Time: 下午3:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=1.0, user-scalable=0">

    <title>地址管理</title>

    <link rel="icon" href="../../../img/lexian.jpg" type="image/x-icon"/>
    <script type="text/javascript" src="../../../js/jquery-3.2.1.js"></script>
    <script type="text/javascript" src="../../../js/bootstrap.js"></script>
    <script type="text/javascript" src="../../../js/foreground/main.js"></script>
    <script type="text/javascript" src="../../../js/foreground/user.js"></script>
    <link rel="stylesheet" href="../../../style/foreground/user.css">
    <link rel="stylesheet" href="../../../style/bootstrap.css">
    <link rel="stylesheet" href="../../../style/foreground/main.css">
    <script type="text/javascript" src="../../../js/foreground/ProJson.js"></script>


    <link href="../../../AmazeUI-2.4.2/assets/css/admin.css" rel="stylesheet" type="text/css">
    <link href="../../../AmazeUI-2.4.2/assets/css/amazeui.css" rel="stylesheet" type="text/css">
    <link href="../../../style/foreground/personal.css" rel="stylesheet" type="text/css">
    <link href="../../../style/foreground/addstyle.css" rel="stylesheet" type="text/css">
    <script src="../../../AmazeUI-2.4.2/assets/js/jquery.min.js" type="text/javascript"></script>
    <script src="../../../AmazeUI-2.4.2/assets/js/amazeui.js"></script>

</head>

<body>
<c:if test="${sessionScope.result!=null}">
    <%="<script>alert('"+session.getAttribute("result").toString()+"')</script>"%>
    <%session.removeAttribute("result");%>
</c:if>
<c:if test="${sessionScope.addressRsult!=null}">
    <%="<script>alert('"+session.getAttribute("addressRsult").toString()+"')</script>"%>
    <%session.removeAttribute("addressRsult");%>
</c:if>
<header>
    <div class="topBox">
        <div class="pull-right">
            <ul class="topList">
                <li>
                    <c:if test="${sessionScope.customer==null}">
                        <a href="/page/foreground/user/Login.jsp">登录</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="/page/foreground/user/Register.jsp">注册</a>
                    </c:if>
                    <c:if test="${sessionScope.customer!=null}">
                        欢迎,<a href="/page/foreground/user/UserCenter.jsp">${sessionScope.customer.username}</a>
                    </c:if>
                </li>
                <li><a href="/viewOrder.do">我的订单</a></li>
                <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">我的乐鲜
                    <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="/viewOrder.do">待处理订单</a></li>
                        <%--<li><a href="#">我的消息</a></li>--%>
                        <li><a href="/page/foreground/user/Collection.jsp">我的关注</a></li>
                    </ul>
                </li>
                <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">客户服务
                    <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="/page/foreground/HelpCenter.jsp">帮助中心</a></li>
                        <%--<li><a href="#">售后服务</a></li>--%>
                        <%--<li><a href="#">在线客服</a></li>--%>
                        <li><a href="/page/foreground/PhoneHelp.jsp">电话客服</a></li>
                        <%--<li><a href="#">客服邮箱</a></li>--%>
                    </ul>
                </li>
                <li style="width:50px;"></li>
            </ul>
        </div>
    </div>
</header>
<nav>
    <div class="navbar">
        <div class="container">
            <div class="row">
                <div class="col-md-4">
                    <a class="logoBox" href="/main.do">
                        <img src="../../../img/lexian.jpg" class="img-circle" style="width: 150px;height: 150px;">
                        <img src="../../../img/lexiantxt.png" class="img-rounded" style="width: 200px;"/>
                    </a>
                </div>
                <div class="col-md-5">
                    <div class="searchBox">
                        <form action="/findGoods.do" method="post">
                            <div class="form-group">
                                <div class="row">
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="name">
                                    </div>
                                    <div class="col-md-3">
                                        <button type="submit" class="searchBtn">搜索</button>
                                    </div>
                                </div>
                            </div>
                        </form>
                        <ul class="list-inline">
                            <%-- <li><a href="/page/foreground/product/viewproduct.html">茶油</a></li>
                             <li>|</li>
                             <li><a href="#">洗面奶</a></li>
                             <li>|</li>
                             <li><a href="#">米</a></li>
                             <li>|</li>
                             <li><a href="#">枣类</a></li>
                             <li>|</li>
                             <li><a href="#">桂圆</a></li>
                             <li>|</li>
                             <li><a href="#">纯牛奶</a></li>--%>
                            <c:forEach items="${sessionScope.catalogs}" begin="0" end="4" var="catalog">
                                <li><a href="/viewCategory.do?category_id=${catalog.categories[0].category_id}&pageIndex=1">${catalog.categories[0].name}</a></li>
                                <li>|</li>
                            </c:forEach>
                            <li><a href="/viewCategory.do?category_id=${catalogs[5].categories[0].category_id}&pageIndex=1">${sessionScope.catalogs[5].categories[0].name}</a> </li>
                        </ul>
                    </div>
                </div>
                <div class="col-md-2">
                    <a href="/viewCart.do">
                        <div class="cartBox">
                            <img src="../../../img/cart.png" style="width: 32px;height: 32px;">&nbsp;&nbsp;<span>我的购物车</span>
                        </div>
                    </a>
                </div>
            </div>
        </div>
    </div>
</nav>
<article>
    <div class="container">
        <div class="center">
            <div class="col-main">
                <div class="main-wrap">

                    <div class="user-address">
                        <!--标题-->
                        <div class="am-cf am-padding">
                            <div class="am-fl am-cf"><strong class="am-text-danger am-text-lg">地址管理</strong> / <small>Address&nbsp;list</small></div>
                        </div>
                        <hr/>
                        <ul class="am-avg-sm-1 am-avg-md-3 am-thumbnails">
                            <c:forEach var="recAddr" items="${sessionScope.recAddrs}">
                              <li class="user-addresslist" id="${recAddr.recaddrId}">
                                <%--<span class="new-option-r"><i class="am-icon-check-circle"></i>设为默认</span>--%>
                                <p class="new-tit new-p-re">
                                    <span class="new-txt">${recAddr.recName}</span>
                                    <span class="new-txt-rd2">${recAddr.phone}</span>
                                </p>
                                <div class="new-mu_l2a new-p-re">
                                    <p class="new-mu_l2cw">
                                        <span class="title">地址：</span>
                                        <span class="province">${recAddr.province}</span>
                                        <span class="city">${recAddr.city}</span>
                                        <span class="dist">${recAddr.district}</span>
                                        <span class="street">${recAddr.addr}</span></p>
                                </div>
                                <div class="new-addr-btn">
                                    <a href="/toEditAddr.do?id=${recAddr.recaddrId}"><i class="am-icon-edit"></i>编辑</a>
                                    <span class="new-addr-bar">|</span>
                                    <a href="javascript:void(0);" onclick="delClick('${recAddr.recaddrId}');"><i class="am-icon-trash"></i>删除</a>
                                </div>
                            </li>
                            </c:forEach>
                        </ul>
                        <div class="clear"></div>
                        <a class="new-abtn-type" data-am-modal="{target: '#doc-modal-1', closeViaDimmer: 0}">添加新地址</a>
                        <!--例子-->
                        <div class="am-modal am-modal-no-btn" id="doc-modal-1">

                            <div class="add-dress">

                                <!--标题 -->
                                <div class="am-cf am-padding">
                                    <div class="am-fl am-cf"><strong class="am-text-danger am-text-lg">新增地址</strong> / <small>Add&nbsp;address</small></div>
                                </div>
                                <hr/>

                                <div class="am-u-md-12 am-u-lg-8" style="margin-top: 20px;">
                                    <form class="am-form am-form-horizontal" action="/addAddress.do" method="post">
                                        <div class="am-form-group">
                                            <label for="user-name" class="am-form-label">收货人</label>
                                            <div class="am-form-content">
                                                <input type="text" id="user-name" name="recName" placeholder="收货人">
                                            </div>
                                        </div>
                                        <div class="am-form-group">
                                            <label for="user-phone" class="am-form-label">手机号码</label>
                                            <div class="am-form-content">
                                                <input id="user-phone" name="phone" placeholder="手机号必填" type="text">
                                            </div>
                                        </div>
                                        <div class="am-form-group">
                                            <label for="postcode" class="am-form-label">邮编</label>
                                            <div class="am-form-content">
                                                <input id="postcode" name="postcode" placeholder="邮政编码" type="text">
                                            </div>
                                        </div>
                                        <div class="am-form-group">
                                            <label class="am-form-label">所在地</label>
                                            <div class="am-form-content address">
                                                <select  name="province" id="cmbProvince">
                                                </select>
                                                <select  name="city" id="cmbCity">
                                                </select>
                                                <select name="district" id="cmbArea">
                                                </select>
                                            </div>
                                        </div>

                                        <script type="text/javascript">
                                            addressInit('cmbProvince', 'cmbCity', 'cmbArea');
                                        </script>

                                        <div class="am-form-group">
                                            <label for="user-intro" class="am-form-label">详细地址</label>
                                            <div class="am-form-content">
                                                <textarea class="" rows="3" id="user-intro" name="addr" placeholder="输入详细地址"></textarea>
                                                <small>100字以内写出你的详细地址...</small>
                                            </div>
                                        </div>

                                        <div class="am-form-group">
                                            <div class="am-u-sm-9 am-u-sm-push-3">
                                                <input type="submit" class="am-btn am-btn-success" value="保存"/>
                                                <input type="reset" class="am-close am-btn am-btn-danger" data-am-modal-close value="取消"/>
                                            </div>
                                        </div>
                                    </form>
                                </div>

                            </div>

                        </div>

                    </div>

                    <script type="text/javascript">
                        /*$.getScript('http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js',function(){
                            alert(remote_ip_info.country);//国家
                            alert(remote_ip_info.province);//省份
                            alert(remote_ip_info.city);//城市
                            alert(remote_ip_info.district)
                        });*/
                        $(document).ready(function() {
                            $(".new-option-r").click(function() {
                                $(this).parent('.user-addresslist').addClass("defaultAddr").siblings().removeClass("defaultAddr");
                            });

                            var $ww = $(window).width();
                            if($ww>640) {
                                $("#doc-modal-1").removeClass("am-modal am-modal-no-btn")
                            }

                        })
                    </script>

                    <div class="clear"></div>

                </div>
            </div>
            <aside class="menu">
                <ul>
                    <li class="person">
                        <div class="h4">个人中心</div>
                    </li>
                    <li class="person">
                        <div class="h5">个人资料</div>
                        <ul>
                            <li> <a href="/page/foreground/user/UserInfo.jsp">个人信息</a></li>
                            <li> <a href="/page/foreground/user/Safety.jsp">安全设置</a></li>
                            <li class="active"> <a href="/manageAddress.do">收货地址</a></li>
                        </ul>
                    </li>
                    <li class="person">
                        <div class="h5">我的交易</div>
                        <ul>
                            <li><a href="/viewOrder.do">订单管理</a></li>
                            <%--<li> <a href="bill.html">账单明细</a></li>--%>
                        </ul>
                    </li>

                    <li class="person">
                        <div class="h5">我的小窝</div>
                        <ul>
                            <li> <a href="/page/foreground/user/Collection.jsp">关注</a></li>
                            <li> <a href="/page/foreground/user/Foot.jsp">足迹</a></li>
                            <%--<li> <a href="comment.html">评价</a></li>--%>
                            <%--<li> <a href="news.html">消息</a></li>--%>
                        </ul>
                    </li>

                </ul>

            </aside>
        </div>
    </div>
</article>
<footer>
    <div class="container">
        <div class="footer-content">
            <a href="#">关于我们</a>  |  <a href="#">网站声明</a>
            <p>版权所有 © 2008-2017 中南大学软件学院&nbsp;&nbsp;&nbsp;&nbsp;Benson科技工作室</p>
            <p>京ICP备15003716号-3  |  京ICP证150437号</p>
        </div>
    </div>
</footer>
</body>
<script>
    function delClick(recaddrId) {
        var url = "/delRecAddr.do";
        var data = {id:recaddrId}
        $.getJSON(url,data,function (json) {
            if(json.result=="true"){//成功
                $("#"+recaddrId).remove()
            }else {
                alert(json.msg)
            }
        })
    }
</script>
</html>

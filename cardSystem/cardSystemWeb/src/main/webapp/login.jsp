<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>登陆</title>
    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
</head>
<body>

<center>
    <div class="error">
        ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message }</div>
    <form name='f' id='f'
          action='<%=request.getContextPath()%>/j_spring_security_check'
          method='POST'>
        <table style="width: 50%">
            <tr>
                <td style="text-align: right; width: 35%">邮箱 :</td>
                <td style="text-align: left"><input type='text'
                                                    name='j_username' value='346012526@qq.com'></td>
            </tr>
            <tr>
                <td style="text-align: right">密码 :</td>
                <td style="text-align: left"><input type='password'
                                                    name='j_password' value="654321"/></td>
            </tr>
            <tr>
                <td style="text-align: right"><label for="j_captcha"> 验证码: </label></td>
                <td>
                    <input type='text' name='j_captcha' class="required"
                           size='5' style="text-align: left" />
                    <img id="imageF" src="captcha.jsp" />
                    <a href="#" id="flashImage">看不清楚换一张</a>

                </td>
            </tr>
            <tr>
                <td align="right"><input id="_spring_security_remember_me"
                                         name="_spring_security_remember_me" type="checkbox" value="true" />

                </td>
                <td><label for="_spring_security_remember_me">Remember Me?</label>
                </td>
            </tr>
            <tr>
                <td align="right">&nbsp;</td>
                <td style="text-align: left"><input
                        type="submit" name="submit" value="登录" />&nbsp;<a href="register.jsp">注册</a>&nbsp;<a href="prepareResetPwd.jsp">忘记密码</a> </td>
            </tr>
        </table>
    </form>
</center>
<script type="text/javascript">

    document.f.j_username.focus();
    if ('${message}' == 1) {
        alert("用户名或密码错误");
    }

    $(document).ready(function(){
        $("#flashImage").click(function(){

            $('#imageF').hide().attr('src','captcha.jsp'+ '?'+ Math.floor(Math.random() * 100)).fadeIn();
        });
    });


</script>


</body>
</html>  
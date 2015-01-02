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
    <div class="error"></div>
    <form name='f' id='f'
          action='rs/user/add'
          method='POST'>
        <table style="width: 50%">
            <tr>
                <td style="text-align: right; width: 35%">用户名称 :</td>
                <td style="text-align: left"><input id="username" type='text'
                                                    name='username' value='xiaozhujun'></td>
            </tr>
            <tr>
                <td style="text-align: right">邮箱 :</td>
                <td style="text-align: left"><input id="email" type='text'
                                                    name='email' value="346012526@qq.com"/></td>
            </tr>
            <tr>
                <td style="text-align: right">密码 :</td>
                <td style="text-align: left"><input id="password" type='password'
                                                    name='password' value="123456"/></td>
            </tr>
            <tr>
                <td colspan="2" style="text-align: center"><input
                        type="submit" name="submit" value="注册" /></td>
            </tr>
        </table>
    </form>
</center>
<script type="text/javascript">
    $(document).ready(function(){
       $("#username").focus();
    });
</script>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>修改密码</title>
    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="js/getParam.js"></script>
</head>
<body>

<center>
    <div class="error"></div>
    <form id='resetPwdForm'
          action='rs/user/retrievePwd'
          method='POST'>
        <table style="width: 50%">
            <tr>
                <td style="text-align: right">新密码 :</td>
                <td style="text-align: left"><input id="password" type='password'
                                                    name='password'/></td>
            </tr>
            <tr>
                <td style="text-align: right">再次输入 :</td>
                <td style="text-align: left"><input id="passwordAgain" type='password'
                                                    name='passwordAgain'/></td>
            </tr>
            <tr>
                <td colspan="2" style="text-align: center">
                    <input type="hidden" id="email" name="email"/>
                    <input type="hidden" id="retrievePwdCode" name="retrievePwdCode"/>
                    <input id="submitBtn"
                        type="submit" name="submit" value="提交" /></td>
            </tr>
        </table>
    </form>
</center>
<script type="text/javascript">
    $(document).ready(function(){
        $("#email").val($.getUrlVar("email"));
        $("#retrievePwdCode").val($.getUrlVar("retrievePwdCode"));
        $("#password").focus();
        $("#submitBtn").click(function(){
            var password=$("#password").val();
            var passwordAgain=$("#passwordAgain").val();
            if(password==undefined||password==""){
                alert("密码不能为空！");
                $("#password").focus();
            }else if(passwordAgain==undefined||passwordAgain==""){
                alert("密码不能为空！");
                $("#passwordAgain").focus();
            }else if(password!=passwordAgain){
                alert("新密码与确认密码不一致！请再次输入！");
                $("#password").val("");
                $("#passwordAgain").val("");
                $("#password").focus();
            }else{
                $("#resetPwdForm").submit();
            }
            return false;
        });
    });
</script>
</body>
</html>
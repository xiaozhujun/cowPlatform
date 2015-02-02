<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>登陆</title>
    <script src="js/jquery-1.7.2.min.js" type="text/javascript"></script>
    <script src="js/jquery.json-2.4.min.js" type="text/javascript"></script>
</head>
<body>

<center>
    <div class="error"></div>
    <form name='f' id='f'
          action='rs/user/add'
          method='POST'>
        <table style="width: 50%">
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
                <td style="text-align: right">同意使用协议 :</td>
                <td style="text-align: left"><input id="isAgree" type='checkbox'
                                                    name='isAgree' value="true" checked/></td>
            </tr>
            <tr>
                <td colspan="2" style="text-align: center"><input
                        type="button" id="submitBtn" name="submit" value="注册" /></td>
            </tr>
        </table>
    </form>
</center>
<script type="text/javascript">
    $(function(){
        $("#submitBtn").click(function(){
            var data={};
            data.email=$("#email").val();
            data.password=$("#password").val();
            data.isAgree=true;
            var jsonString= $.toJSON(data);
            $.post('rs/user/add',{"jsonString":jsonString},registerCallback,"json");

        });
       $("#username").focus();
    });

    function registerCallback(data){
        if(data.code==200){
            $.ligerDialog.success("修改成功") ;
        }
        else{
            $.ligerDialog.error(data.message);
        }
    }
</script>
</body>
</html>
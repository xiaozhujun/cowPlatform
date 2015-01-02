(function($){
    $.URL = {
        "common":{
            "rootPath":"http://www.cseicms.com/"
        },
        "power":{
            "add":"rs/power/add",
            "update":"rs/power/update",
            "delete":"rs/power/delete",
            "list":"rs/power/list"
        },
        "user":{
            "add":"rs/user/add",
            "update":"rs/user/update",
            "delete":"rs/user/delete",
            "list":"rs/user/list",
            "getId":"rs/user/getIdByName" ,
            "currentUserId": "rs/user/currentUserId",
            "currentUserInfo":"rs/user/currentUser"
        },
        "authority":{
            "add":"rs/authority/add",
            "update":"rs/authority/update",
            "delete":"rs/authority/delete",
            "list":"rs/authority/list"
        },
        "userAuthority":{
            "add":"rs/userAuthority/add"
        },
        "classmate":{
            "add":"rs/classmate/add",
            "update":"rs/classmate/update",
            "delete":"rs/classmate/delete",
            "list":"rs/classmate/list",
            "findByCondition":"rs/classmate/findByCondition"
        }
    }
})(jQuery);
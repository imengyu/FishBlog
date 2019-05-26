appendLoaderJS("/assets/libs/compress/base64.min.js");

if(typeof authSetInfoLoadFinishCallback != 'undefined'){
    authSetInfoLoadFinishCallback(function(authedUser){
        if(authedUser){
            fastUserCenterLoad();
            fastUserCenterData(authedUser);
        }
    });
}

function fastUserCenterLoad() {
    $('#current_user_center_dropdown').append($('<div class="dropdown-menu shadow-dirty dropdown-menu-right " id="current_user_center_message_box">\
        <div class="message-list" id="current_user_message_list"></div>\
    </div>'));
    $('#current_user_center_dropdown').click(function(){
        if($(this).hasClass('show')){
            $(this).removeClass('show')
            $('#current_user_center_message_box').fadeOut();
        }else{
            $(this).addClass('show')
            $('#current_user_center_message_box').fadeIn();
        }
    })
}
function fastUserCenterData(authedUser) {
    if(authedUser.messageCount) {
        var url = address_blog_api + "user/" + authedUser.id + '/messages/notread?maxCount=10';
            $.ajax({
                url: url,
                success: function (response) {
                    if (response.success) {
                       var list = response.data;
                       for(var k in list){
                           var item = list[k];
                            $('#current_user_message_list').append(
                                $('<div class="message-item"><a href="/user/#message-' + item.id + '">' + 
                                item.title + '</a><br/><span>' + base64.decode(item.content) + '</span></div>')
                            );   
                       }
                    }
                }, error: function (xhr, err) {$('#current_user_message_list').append($('<div class="text-seceondary text-center mt2 mb-2">加载错误</div>'));   }
            });
    }else{
        $('#current_user_message_list').append($('<div class="text-seceondary text-center mt2 mb-2">当前没有新消息</div>'));   
    }
}
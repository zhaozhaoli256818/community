/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = $(e).attr("data-id");
    $("#comment-body-"+id).append("<div class='col-lg-12 col-md-12 col-sm-12 col-sm-12 collapse sub-comments'\n" +
        "                             id='comment-"+id+"'></div>")
    if ($("#comment-" + id).hasClass("in")) {
        //折叠二级评论
        $("#comment-" + id).removeClass("in");
        $(e).removeClass("active");
        $(".btn-comment2").remove()
        $("#comment-"+id).remove()
    } else {
        $.ajax({
            type: "GET",
            url: "/comment/"+id,
            contentType: "application/json",
            dataType: "json",
            success: function (msg) {

                $.each(msg,function (i,comment) {
                    var $comment = $(
                        "                            <div class='col-lg-12 col-md-12 col-sm-12 col-sm-12 media'>\n" +
                        "                                <div class='media-left'>\n" +
                        "                                    <span href=#>\n" +
                        "                                        <img class='media-object img-rounded' src="+comment.user.avatarUrl+" alt=...>\n" +
                        "                                    </span>\n" +
                        "                                </div>\n" +
                        "                                <div class='media-body'>\n" +
                        "                                    <span>\n" +
                        "                                        <h5 class='media-heading'>"+comment.user.name+"</h5>\n" +
                        "                                    </span>\n" +
                        "                                    <div>"+comment.content+"</div>\n" +
                        "                                    <div class='menu'>\n" +
                        "                                        <span class='pull-right'\n" +
                        "                                            >"+formatDate(new Date(comment.gmtCreate))+"</span>\n" +
                        "                                    </div>\n" +
                        "                                </div>\n" +
                        "                                <hr class='col-lg-12 col-md-12 col-sm-12 col-sm-12'>\n" +
                        "                            </div>");
                    $("#comment-"+id).append($comment);

                });
                $("#comment-"+id).append($(" <div class='col-lg-12 col-md-12 col-sm-12 col-sm-12 btn-comment2'>\n" +
                    "                                <input type='hidden' id='comment-id' value='"+id+"'>\n" +
                    "                                <input type='text' id='comment2-content' class='form-control' placeholder='评论一下......'>\n" +
                    "                                <button type='button' class='btn btn-success pull-right' onclick='post2Comment()'>评论\n" +
                    "                                </button>\n" +
                    "                            </div>"))
            }
        });
        //打开二级评论
        $("#comment-" + id).addClass("in");
        $(e).addClass("active");
    }
}

function formatDate(now) {
    var year=now.getFullYear();
    var month=now.getMonth()+1;
    var date=now.getDate();
    return year+"-"+month+"-"+date;
}
function comment(targetId,content,type) {
    if (!content) {
        alert("评论不能为空~~~")
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (msg) {
            if (msg.code == 200) {
                $(".comment-section").hide();
                window.location.reload();
            } else {
                if (msg.code == 2003) {
                    var isAccepted = confirm(msg.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=04756ad84718e094174b&redirect_uri=http://localhost:8080/callback&scope=user&state=1")
                        window.localStorage.setItem("closeable", "true");
                    }
                } else {
                    alert(msg.message)
                }

            }
        }
    });
}
/**
 *发送评论内容
 */
function post() {
    var id = $("#question_id").val();
    var content = $("#comment-content").val();
    comment(id,content,1)
}

/**
 * 发送二级评论
 */
function post2Comment() {
    var content = $("#comment2-content").val();
    var id = $("#comment-id").val();
    comment(id,content,2)
}
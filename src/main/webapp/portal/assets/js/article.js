var host = "http://localhost:8080/"
window.onload=function(){
	var thisUrl = window.location.href;
	var start = thisUrl.lastIndexOf("=");
	if(start>0){
		var blogId = thisUrl.substr(start+1);
		//判断是否是数字
		if(!isNaN(blogId)){
			$.ajax({
				type:"post",
				dataType:"json",
				url:host+"user/blog/article.do",
				data:{"blogId":blogId},
				success:function(result){
					  if(result.status !=0){
					 	alert("参数不合法");
					 	return ;
					 }
					 //解析参数
					 var blogVo = result.data.blogVo;
					 var blogVoList = result.data.blogVoList;
					 //正文部分初始化
					 initArticle(blogVo);


					 	 //计算阅读时间
					 // (jQuery);
			         $('article').readingTime({
                    	wordCountTarget: '.word-count',
                    	readingTimeTarget: '.reading-time',
						wordCountTarget: '.word-count',
						wordsPerMinute: 400,
						round: true,
						lang: 'ch',
                		});
					// var defaults = {
					//       readingTimeTarget: '.eta',
					//       wordCountTarget: null,
					//       wordsPerMinute: 270,
					//       round: false,
					//       lang: 'en',
					//	     lessThanAMinuteString: '',
					//	     prependTimeString: '',
					//	     prependWordString: '',
					//       remotePath: null,
					//       remoteTarget: null
					//      }

					 initGuessYouLike(blogVoList);


				}
			})
			
		}else {
			alert("参数无效");
			return;
		}
		
	}else{
		alert("无效参数");
		return ;
	}

	$(window).scroll(function () {
		if($(window).scrollTop()>=$(".sidebar").height()){
			//固定猜你喜欢
			 $(".d_postlist").addClass("fixed");
			 $(".sidebar").css("marginTop",-$(".d_postlist").height());
		}else{
			$(".d_postlist").removeClass('fixed');
			$(".sidebar").css("marginTop",0);
		}

	})
}




function initArticle(blogVo){
	var content = blogVo.content;
	var author = blogVo.author;
	var categoryId = blogVo.categoryId;
	var categoryName = blogVo.categoryName;
	var createTimeStr = blogVo.createTimeStr;
	var commentCount = blogVo.commentCount;
	var shareCount = blogVo.shareCount;
	var viewCount = blogVo.viewCount;
	var title = blogVo.title;
	//标签处理
	var tags = blogVo.tags;

	var headerArticleInfo = $("#herder_article_info");
	var articleContentContainer = $("#div_article_container");
	// var heraderChild="<h1 class=article-title><a href=3798.html>"+title+"</a></h1><div id='article-header' class=meta><span id=mute-category class=muted><i class='fa fa-list-alt'></i><a href='"+categoryId+"'>&nbsp;"+categoryName+"</a></span><span class=muted><i class='fa fa-user'></i>&nbsp;"+author+"</span><time class=muted><i class='fa fa-clock-o'></i>&nbsp;"+createTimeStr+"</time><span class=muted style='display:none;'><i class='fa fa-eye'></i>&nbsp;"+viewCount+"</span><span class=muted><i class='fa fa-comments-o'></i><a href='3798.html#comments'>&nbsp;"+commentCount+"&nbsp;评论</a></span></div>";
	var heraderChild="<h1 class=article-title><a href=3798.html>"+title+"</a></h1><div id='article-header' class=meta><span id=mute-category class=muted><i class='fa fa-list-alt'></i><a href='"+categoryId+"'>&nbsp;"+categoryName+"</a></span><span class=muted><i class='fa fa-user'></i>&nbsp;"+author+"</span><time class=muted><i class='fa fa-clock-o'></i>&nbsp;"+createTimeStr+"</time><span class=muted style='display:none;'><i class='fa fa-eye'></i>&nbsp;"+viewCount+"</span><span class=muted><i class='fa fa-comments-o'></i><a href='3798.html#comments'>&nbsp;"+commentCount+"&nbsp;评论</a></span></div>";

	headerArticleInfo.append(heraderChild);

	articleContentContainer.append("<div class='article-content'>"+content+"</div>");
		// articleContentContainer.append("<article><div class='eta'></div><div class='article-content'>"+content+"</div></article>");


}
function initGuessYouLike(blogVoList){
	var title ;
	var blogId;
	var imgUrl;
	var createTimeStr;
	var commentCount;
	var ulGuessLike =$("#ul_guess_like");
	$.each(blogVoList,function(index,value){
		title = value.title;
		imgUrl = value.imgHost+value.imgUri;
		commentCount = value.commentCount;
		createTimeStr=value.createTimeStr;
		var liChild = "<li><a href='"+blogId+"' title='"+title+"'><span class=thumbnail><img src='"+imgUrl+"' alt='"+title+"'></span><span class=text>"+title+"</span><span class=muted>"+createTimeStr+"</span><span class=muted style='float: right;'>"+commentCount+"评论</span></a>"

		ulGuessLike.append(liChild);
	})

}

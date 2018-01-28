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
				url:host+"/user/blog/article.do",
				data:{"blogId":blogId},
				success:function(result){
					  if(result.status !=0){
					 	alert("参数不合法");
					 	return ;
					 }
					 //解析参数
					 var blogVo = result.data.blogVo;
					 var blogVoList = result.data.blogVoList;
					 var tagVoList = result.data.tagVoList;
					 var categoryVoList = result.data.categoryList;
					 //正文部分初始化
					 initArticle(blogVo);
					 //todo 点赞功能缓存优化 点赞id填充
					 var span_like_count = $("#span_like_count");
					 span_like_count.attr("data-id");

					 initialCategory(categoryVoList);
					 initialTagList(tagVoList);
			 	  	 //启动标签特效
	 	  	 		var tc = tagcloud(); 
					 	 //计算阅读时间
					 // (jQuery);
			         $('article').readingTime({
                    	wordCountTarget: '.word-count',
                    	readingTimeTarget: '.reading-time',
						wordCountTarget: '.word-count',
						wordsPerMinute: 238,
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

	var headerArticleInfo = $("#herder_article_info");
	headerArticleInfo.empty();
	var articleContentContainer = $("#div_article_container");
	articleContentContainer.empty();
	var div_tag_bog = $("#div_tag_bog");


	var content = blogVo.content;
	var author = blogVo.author;
	var categoryId = blogVo.categoryId;
	var categoryName = blogVo.categoryName;
	var createTimeStr = blogVo.createTimeStr;
	var commentCount = blogVo.commentCount;
	var shareCount = blogVo.shareCount;
	var viewCount = blogVo.viewCount;
	var likeCount = blogVo.likeCount;
	var title = blogVo.title;
	//标签处理
	var tagList=blogVo.tagsList;
	$.each(tagList,function(index,value){
			if(index==0){
					div_tag_bog.append("<i class='fa fa-tags'></i><a href='category.html?tagId="+value.tagId+"' rel='tag'>"+value.tagName+"</a>");
			}else{
				div_tag_bog.append("<a href='category.html?tagId="+value.tagId+"' rel='tag'>这是标签</a>");
			}
	});

	//处理标题
	changeThePageTitle(title);
	//处理菜单选项
	changeMenuItem(categoryId);

	// var heraderChild="<h1 class=article-title><a href=3798.html>"+title+"</a></h1><div id='article-header' class=meta><span id=mute-category class=muted><i class='fa fa-list-alt'></i><a href='"+categoryId+"'>&nbsp;"+categoryName+"</a></span><span class=muted><i class='fa fa-user'></i>&nbsp;"+author+"</span><time class=muted><i class='fa fa-clock-o'></i>&nbsp;"+createTimeStr+"</time><span class=muted style='display:none;'><i class='fa fa-eye'></i>&nbsp;"+viewCount+"</span><span class=muted><i class='fa fa-comments-o'></i><a href='3798.html#comments'>&nbsp;"+commentCount+"&nbsp;评论</a></span></div>";
	var heraderChild="<h1 class=article-title>"+title+"</h1><div id='article-header' class=meta><span id=mute-category class=muted><i class='fa fa-list-alt'></i><a href='category.html?categoryId="+categoryId+"'>&nbsp;"+categoryName+"</a></span><span class=muted><i class='fa fa-user'></i>&nbsp;"+author+"</span><time class=muted><i class='fa fa-clock-o'></i>&nbsp;"+createTimeStr+"</time><span class=muted style='display:none;'><i class='fa fa-eye'></i>&nbsp;"+viewCount+"</span><span class=muted><i class='fa fa-eye'></i>&nbsp;"+viewCount+"&nbsp;</a></span></div>";

	headerArticleInfo.append(heraderChild);

	articleContentContainer.append("<div class='article-content'>"+content+"</div>");
		// articleContentContainer.append("<article><div class='eta'></div><div class='article-content'>"+content+"</div></article>");

	//喜欢次数
	var likeCountContainer = $("#span_like_count");
	likeCountContainer.empty();
	likeCountContainer.append(likeCount);

	//分享次数
	var shareCountContainer = $("#span_share_count");
	shareCountContainer.empty();
	shareCountContainer.attr("title","累计分享"+shareCount+"次");
	
	shareCountContainer.append(shareCount);


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
		blogId=value.blogId;
		var liChild = "<li><a href='article.html?blogId="+blogId+"' title='"+title+"'><span class=thumbnail><img src='"+imgUrl+"' alt='"+title+"'></span><span class=text>"+title+"</span><span class=muted>"+createTimeStr+"</span><span class=muted style='float: right;'>"+commentCount+"评论</span></a>"

		ulGuessLike.append(liChild);
	})

}

 	function changeThePageTitle(name){
 		var title = $("#title_page_name");
 		title.empty();
 		title.append(name);
 	}

	//标签列表模块
 	function initialTagList(tagList){
	 	var tagscloud = $("#tagscloud");
	 	$.each(tagList,function(index,value){
	 		var tagId = value.tagId;
	 		var tagName = value.tagName;
	 		var tagCount = value.tagCount;
	 		tagscloud.append("<a target='_blank' href='category.html?tagId="+tagId+"' class='tagc"+getRandom(1,6)+"'>"+tagName+'('+tagCount+')'+"</a>");
 		})

 	}
 	

	//获取随机数
	function getRandom(min,max){
		//Math.random()*(上限-下限+1)+下限  
	    return parseInt(Math.random() * (max - min + 1) + min); 
	}



 	function initialCategory(categoryList){
 		var d_category = $("#div_category");
 		d_category.empty();

 		$.each(categoryList,function(index,value){
 			d_category.append("<a class=cate"+getRandom(1,6)+" title='"+value.categoryName+"' href='category.html?categoryId="+value.categoryId+"'>"+value.categoryName+"("+value.blogCount+")</a>");
 		})
 	}

 	function lastAndNext(BlogVoList){

 	}

 	function addLike(){
 		var span_like_count = $("#span_like_count");
 		var count = span_like_count.text();
 		var blogId = span_like_count.attr("data-id");
 		var status = span_like_count.attr("data-status");
 		//todo 如果是没点赞就加1 
 		if(status==0){
 			$("#span_like_icon").removeClass("fa-heart-o").addClass("fa-heart");
 			span_like_count.attr("data-status",1);
 			span_like_count.text(++count);
 			//todo  数据库
 		}else{
			$("#span_like_icon").removeClass("fa-heart").addClass("fa-heart-o");
 			span_like_count.attr("data-status",0);
 			span_like_count.text(--count);
 		}
 	}

 		function changeMenuItem(key){
		if(key<=5){
			$("#menu-item-"+key).addClass("current-menu-item current_page_item");
		}else{
			$("#menu-item-1").addClass("current-menu-item current_page_item");
 		}
	}
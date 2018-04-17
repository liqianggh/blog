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
					 //猜你喜欢
					 var blogVoList = result.data.blogVoList;
					 var tagVoList = result.data.tagVoList;
					 var categoryVoList = result.data.categoryList;
					 //上下篇
					 var lastBlog = result.data.lastBlog;
					 var nextBlog = result.data.nextBlog; 
					 //正文部分初始化
					 initArticle(blogVo);
					 //todo 点赞功能缓存优化 点赞id填充
					 initialCategory(categoryVoList,0);
					 initialTagClouds(tagVoList,0);
 					 initGuessYouLike(blogVoList);
 					 initialLastAndNext(lastBlog,nextBlog);
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
}

function initArticle(blogVo){

	var headerArticleInfo = $("#herder_article_info");
	headerArticleInfo.empty();
	var articleContentContainer = $("#div_article_container");
	articleContentContainer.empty();
	var div_tag_bog = $("#div_tag_bog");
	div_tag_bog.empty();

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
	var blogId = blogVo.blogId;
	//标签处理
	var tagList=blogVo.tagsList;
	$.each(tagList,function(index,value){
			if(index==0){
			    div_tag_bog.append("<i class='fa fa-tags'></i><a href='category.html?tagId="+value.tagId+"' rel='tag'>"+value.tagName+"</a>");
			}else{
				div_tag_bog.append("<a href='category.html?tagId="+value.tagId+"' rel='tag'>"+value.tagName+"</a>");
			}
	});
	//处理标题
	changeThePageTitle(title);
	//处理菜单选项
	changeMenuItem(categoryId);
	var heraderChild="<h1 class=article-title>"+title+"</h1><div id='article-header' class=meta><span id=mute-category class=muted><i class='fa fa-list-alt'></i><a href='category.html?categoryId="+categoryId+"'>&nbsp;"+categoryName+"</a></span><span class=muted><i class='fa fa-user'></i>&nbsp;"+author+"</span><time class=muted><i class='fa fa-clock-o'></i>&nbsp;"+createTimeStr+"</time><span class=muted ><i class='fa fa-eye'></i>&nbsp;"+viewCount+"&nbsp;浏览</span></div>";

	headerArticleInfo.append(heraderChild);

	articleContentContainer.append("<div class='article-content'>"+content+"</div>");
		// articleContentContainer.append("<article><div class='eta'></div><div class='article-content'>"+content+"</div></article>");
	//喜欢次数
	var span_like = $("#span_like");
	span_like.empty().append("<a target='_self' href='javascript:addLike("+blogId+")'   class='action'><i id='fa_"+blogId+"' status='0' class='fa fa-heart-o'></i><span id='like_count_"+blogId+"' class='count'>"+likeCount+"人喜欢</span>");

	//分享次数
	var shareCountContainer = $("#span_share_count");
	shareCountContainer.empty();
	shareCountContainer.attr("title","累计分享"+shareCount+"次");
	
	shareCountContainer.append(shareCount);

	//浏览次数
	$.ajax({
			type:"post",
			dataType:"json",
			url:host+"/user/blog/add_view.do",
			data:{"blogId":blogId}
		   })
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
		var liChild = "<li><a href='javascript:initNewArticle("+blogId+")' title='"+title+"'><span class=thumbnail><img src='"+imgUrl+"' alt='"+title+"'></span><span class=text>"+title+"</span><span class=muted>"+createTimeStr+"</span><span class=muted style='float: right;'>"+commentCount+"&nbsp;评论</span></a>"
		ulGuessLike.append(liChild);
	})

}
 	function changeThePageTitle(name){
 		var title = $("#title_page_name");
 		title.empty();
 		title.append(name);
 	}
 
 	function initialLastAndNext(lastBlog,nextBlog){
 		var last_and_next=$("#last_and_next");
 		last_and_next.empty();
 		if(lastBlog!=null){
 			last_and_next.append("<div><strong>上一篇</strong>：<a href='javascript:initNewArticle("+lastBlog.blogId+")'>"+lastBlog.title+"</a></div>");
 		}
 		if(nextBlog!=null){
 			last_and_next.append("<div><strong>下一篇</strong>：<a href='javascript:initNewArticle("+nextBlog.blogId+")'>"+nextBlog.title+"</a></div>");
 		}
 	}


 
	function initNewArticle(blogId){
		$.ajax({
			type:"post",
			dataType:"json",
			url:host+"/user/blog/loadAt_by_id.do",
			data:{"blogId":blogId},
			success:function(result){
				if(result.status==0){
					var lastBlog = result.data.lastBlog;
					var nextBlog = result.data.nextBlog;
					var blogVo = result.data.blogVo;

					if(blogVo!=null){
						initArticle(blogVo);
					}
					initialLastAndNext(lastBlog,nextBlog);
					goTop();

				}else{
					alert("未知错误!");
				}
				
			}

		})
	}

	function goTop(){
		$('html').animate( { scrollTop: '0px' }, 600 ); 
	}

 
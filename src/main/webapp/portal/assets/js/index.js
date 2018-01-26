 window.onload=function(){

	//首页数据初始化
	//数据冗余 todo
	$.ajax({
		type:"post",
		dataType:"json",
		url:"http://localhost:8080/user/blog/load_index.do",
		success:function(result){
		    // 推荐博客
			var recBlogs = result.data.recommendBlog;
			//热门博客
			var hotBlogs = result.data.hotBlogs;
			//标签云数据
			var tagList = result.data.tagVoList;
			//猜你喜欢 todo
			//博客
			var blogPInfo = result.data.blogPageInfo;
			var blogList = blogPInfo.list;

		 	//图片窗口部分
		 	var ul_ws_images= $("#ul_ws_images");
	 		var div_ws_bullets=$("#div_ws_bullets");
		 	$.each(hotBlogs ,function(index,value){
		 		var title = value.title;
			    var blogId = value.blogId;
			    var summary = value.summary;
			    var imgUrl  = value.imgHost+value.imgUri;
			    //拼接元素
			    /*<li><img src="image/images/eee.jpg" alt="麻将少年" title="麻将少年" id="wows1_0"/></li>*/
			    ul_ws_images.append("<li><a  href='article.html?blogId="+blogId+"'><img src='"+imgUrl+"'  alt='"+title+"' title='"+title+"'  id='wows1_"+index+"' /></a></li>");
// $("#wows1_"+index).attr("src",imgUrl);
// $("#wows1_"+index).attr("title",title);
// $("#div_wow_js").append(" <script type='text/javascript' src='assets/engine/wowslider.js'></script><script type='text/javascript' src='assets/engine/script.js'></script>");

				/*<a href="#" title="麻将少年"><span><img src="image/tooltips/eee.jpg" alt="麻将少年"/>1</span></a>*/
				div_ws_bullets.append("<a href='article.html?blogId="+blogId+"' title='"+title+"'><span></span></a>")
		 	})

		 	jQuery("#wowslider-container1").wowSlider({effect:"bubbles,parallax,seven,blur,domino,blast,blinds,basic,flip,page,stack,stack_vertical",prev:"",next:"",duration:20*100,delay:20*100,width:640,height:360,autoPlay:true,autoPlayVideo:false,playPause:true,stopOnHover:false,loop:false,bullets:1,caption:true,captionEffect:"parallax",controls:true,controlsThumb:false,responsive:1,fullScreen:false,gestures:2,onBeforeStep:0,images:0});



		 	//推荐博客模块
		 	var  ul_blog_recommend=$("#ul_blog_recommend");
		 	$.each(recBlogs ,function(index,value){
		 		var title = value.title;
		 		var viewCount = value.viewCount;
		 		var blogId = value.blogId;

     	         var childEle = "<li><p><span class='muted'><a data-action='ding' data-id='1052' class='action'><i class='fa fa-eye'></i><span class='count'>"+viewCount+"</span> 浏览</a></span></p><span class='label label-"+(index+1)+"'>1</span> <a href='article.html?blogid="+blogId+"' title='"+title+"'>"+title+"</a></li>";

     	         ul_blog_recommend.append(childEle);
		 	})

		 	//博客列表模块
		 	var blogContainer = $("#div_article_container");
		 	$.each(blogList,function(index,value){
		 		var title = value.title;
		 		var summary = value.summary;
		 		var blogId = value.blogId;
		 		var viewCount = value.viewCount;
		 		var likeCount = value.likeCount;
		 		var commentCount = value.commentCount;
		 		var categoryId = value.categoryId;
		 		var shareCount = value.shareCount;
		 		var createTimeStr = value.createTimeStr;
		 		var categoryName = value.categoryName;
		 		var imgUrl=value.imgHost+value.imgUri;
		 		var blogChild = "<article class='excerpt'><header><a class='label label-important' href='category.html?categoryId="+categoryId+"'>"+categoryName+"<i class='label-arrow'></i></a> <h2><a  href='article.html?blogId="+blogId+"' title='"+title+"'>"+title+" </a></h2></header><div class='focus'> <a target='blank' href='#'><img class='thumb' src='"+imgUrl+"' alt='"+title+"' /></a>	</div> 	<span class='note'>"+summary+"</span>	<p class='auth-span'> <span class='muted'><i class='fa fa-clock-o'></i> "+createTimeStr+"</span> <span class='muted'><i class='fa fa-eye'></i> "+viewCount+"℃</span> <span class='muted'><i class='fa fa-comments-o'></i> <a target='_blank' href='3849.html#comments'>"+commentCount+"评论</a></span><span class='muted'> <a href='javascript:;' data-action='ding' data-id='3849' id='Addlike' class='action'><i class='fa fa-heart-o'></i><span class='count'>"+likeCount+"</span>喜欢</a></span></p> </article>";
		 		blogContainer.append(blogChild);
		 	})

		 	//标签列表模块
		 	var tagscloud = $("#tagscloud");
		 	$.each(tagList,function(index,value){
		 		var tagId = value.tagId;
		 		var tagName = value.tagName;
		 		var tagCount = value.tagCount;
		 		tagscloud.append("<a href='category.html?tagId="+tagId+"' class='tagc"+getRandom(1,5)+"'>"+tagName+'('+tagCount+')'+"</a>");
		 		
		 	})

		 	//猜你喜欢模块
		 	var guessYouLike = $("#ul_guess_you");
		 	$.each(hotBlogs,function(index,value){
	 			var title = value.title;
		 		var commentCount = value.commentCount;
		 		var blogId = value.blogId;
		 		var createTimeStr = value.createTimeStr;
		 		var imgUrl=value.imgHost+value.imgUri;
		 		var childEle =  "<li><a href='article.html?blogId="+blogId+"' title='"+title+"'><span class='thumbnail'><img src='"+imgUrl+"' alt='"+title+"' /></span><span class='text'>"+title+"</span><span class='muted'>"+createTimeStr+"</span><span class='muted' style='float: right;'>"+commentCount+"评论</span></a></li>";
		 		guessYouLike.append(childEle);
		 	})
			//启动标签特效
	 	  	 var tc = tagcloud(); 
		}
	});

 }
	//获取随机数
	function getRandom(min,max){
		//Math.random()*(上限-下限+1)+下限  
	    return parseInt(Math.random() * (max - min + 1) + min); 
	}


 

 window.onload=function(){
	//首页数据初始化

	//数据冗余 todo
	$.ajax({
		type:"post",
		dataType:"json",
		url:host+"/user/blog/load_index.do",
		success:function(result){
		    // 推荐博客
			var recBlogs = result.data.recommendBlog;
			//热门博客
			var hotBlogs = result.data.hotBlogs;
			//标签云数据
			var tagList = result.data.tagVoList;
			//猜你喜欢 todo
			//博客
			var pageInfo = result.data.blogPageInfo;
			var blogList = pageInfo.list;

			var categoryVoList = result.data.categoryVoList;

			//分页部分
			var pageNum =pageInfo.pageNum;
			var pages=pageInfo.pages;
			var firstPage=pageInfo.firstPage;
			var isFirstPage=pageInfo.isFirstPage;
			var lastPage = pageInfo.lastPage;
			var isLastPage=pageInfo.isLastPage;
			var nextPage=pageInfo.nextPage;
			var hasNextPage=pageInfo.hasNextPage;
			var prePage = pageInfo.prePage;
			var hasPreviousPage = pageInfo.hasPreviousPage;
		pagination(pageNum,pages,firstPage,isFirstPage,lastPage
		,isLastPage,nextPage,hasNextPage,prePage,hasPreviousPage);


		 	//图片窗口部分
		 	var ul_ws_images= $("#ul_ws_images");
	 		var div_ws_bullets=$("#div_ws_bullets");
		 	$.each(hotBlogs ,function(index,value){
		 		var title = value.title;
			    var blogId = value.blogId;
			    var summary = value.summary;
			    var imgUrl  = value.imgHost+value.imgUri;
			    //拼接元素
			    /*<li><img class='lazyload' src="image/images/eee.jpg" alt="麻将少年" title="麻将少年" id="wows1_0"/></li>*/
			    ul_ws_images.append("<li><a target='_self'  href='article.html?blogId="+blogId+"'><img class='lazyload' src='"+imgUrl+"'  alt='"+title+"' title='"+title+"'  id='wows1_"+index+"' /></a></li>");

				/*<a target='_self' href="#" title="麻将少年"><span><img class='lazyload' src="image/tooltips/eee.jpg" alt="麻将少年"/>1</span></a>*/
				div_ws_bullets.append("<a target='_self' href='article.html?blogId="+blogId+"' title='"+title+"'><span></span></a>");

		 	})

		 	jQuery("#wowslider-container1").wowSlider({effect:"bubbles,parallax,seven,blur,domino,blast,blinds,basic,flip,page,stack,stack_vertical",prev:"",next:"",duration:20*100,delay:20*100,width:640,height:360,autoPlay:true,autoPlayVideo:false,playPause:true,stopOnHover:false,loop:false,bullets:1,caption:true,captionEffect:"parallax",controls:true,controlsThumb:false,responsive:1,fullScreen:false,gestures:2,onBeforeStep:0,images:0});

		 	//推荐博客模块
		 	var  ul_blog_recommend=$("#ul_blog_recommend");
		 	$.each(recBlogs ,function(index,value){
		 		var title = value.title;
		 		var viewCount = value.viewCount;
		 		var blogId = value.blogId;

     	         var childEle = "<li><p><span class='muted'><a target='_self' data-action='ding' data-id='1052' class='action'><i class='fa fa-eye'></i>&nbsp;<span class='count'>"+viewCount+"</span>&nbsp;浏览</a></span></p><span class='label label-"+(index+1)+"'>1</span> <a target='_self' href='article.html?blogid="+blogId+"' title='"+title+"'>"+title+"</a></li>";

     	         ul_blog_recommend.append(childEle);
		 	})



 			//博客列表
		 	initBlogList(blogList);
		 	//分类列表
		 	initialCategory(categoryVoList,0);
		 	//标签列表
		 	initialTagClouds(tagList,0);

		 	//猜你喜欢模块
		 	var guessYouLike = $("#ul_guess_you");
		 	$.each(hotBlogs,function(index,value){
	 			var title = value.title;value
		 		var commentCount = value.commentCount;
		 		var blogId = value.blogId;
		 		var createTimeStr = value.createTimeStr;
		 		var imgUrl=value.imgHost+value.imgUri;
		 		// var childEle =  "<li><a target='_self' href='article.html?blogId="+blogId+"' title='"+title+"'><span class='thumbnail'><img class='lazyload' src='"+imgUrl+"' alt='"+title+"' /></span><span class='text'>"+title+"</span><span class='muted'>"+createTimeStr+"</span><span class='muted' style='float: right;'><a href='http://www.mycookies.cn/portal/article.html?blogId="+blogId+"'><span id = 'url::http://www.mycookies.cn/portal/article.html?blogId="+blogId+"' class = 'cy_cmt_count' ></span><script id='cy_cmt_num' src='https://changyan.sohu.com/upload/plugins/plugins.list.count.js?clientId=cytqlFQwr'></script> 评论</a></span></li>";
			    var childEle =  "<li><a target='_self' href='article.html?blogId="+blogId+"' title='"+title+"'><span class='thumbnail'><img class='lazyload' src='"+imgUrl+"' alt='"+title+"' /></span><span class='text'>"+title+"</span><span class=muted>"+createTimeStr+"</span><span class=muted style='float: right;'><span id = 'url::http://www.mycookies.cn/article.html?blogId="+blogId+"' class = 'cy_cmt_count' ></span><script id='cy_cmt_num' src='https://changyan.sohu.com/upload/plugins/plugins.list.count.js?clientId=cytqlFQwr'></script>&nbsp;评论</span></a>"

		 		guessYouLike.append(childEle);
		 	})



		}
	});

 }


 
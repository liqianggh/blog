
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
			    /*<li><img src="image/images/eee.jpg" alt="麻将少年" title="麻将少年" id="wows1_0"/></li>*/
			    ul_ws_images.append("<li><a target='_blank'  href='article.html?blogId="+blogId+"'><img src='"+imgUrl+"'  alt='"+title+"' title='"+title+"'  id='wows1_"+index+"' /></a></li>");

				/*<a target='_blank' href="#" title="麻将少年"><span><img src="image/tooltips/eee.jpg" alt="麻将少年"/>1</span></a>*/
				div_ws_bullets.append("<a target='_blank' href='article.html?blogId="+blogId+"' title='"+title+"'><span></span></a>")

		 	})

		 	jQuery("#wowslider-container1").wowSlider({effect:"bubbles,parallax,seven,blur,domino,blast,blinds,basic,flip,page,stack,stack_vertical",prev:"",next:"",duration:20*100,delay:20*100,width:640,height:360,autoPlay:true,autoPlayVideo:false,playPause:true,stopOnHover:false,loop:false,bullets:1,caption:true,captionEffect:"parallax",controls:true,controlsThumb:false,responsive:1,fullScreen:false,gestures:2,onBeforeStep:0,images:0});

		 	//下拉时固定指定窗口
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

		 	//推荐博客模块
		 	var  ul_blog_recommend=$("#ul_blog_recommend");
		 	$.each(recBlogs ,function(index,value){
		 		var title = value.title;
		 		var viewCount = value.viewCount;
		 		var blogId = value.blogId;

     	         var childEle = "<li><p><span class='muted'><a target='_blank' data-action='ding' data-id='1052' class='action'><i class='fa fa-eye'></i><span class='count'>"+viewCount+"</span> 浏览</a></span></p><span class='label label-"+(index+1)+"'>1</span> <a target='_blank' href='article.html?blogid="+blogId+"' title='"+title+"'>"+title+"</a></li>";

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
		 		var blogChild = "<article class='excerpt'><header><a target='_blank' class='label label-important' href='category.html?categoryId="+categoryId+"'>"+categoryName+"<i class='label-arrow'></i></a> <h2><a target='_blank'  href='article.html?blogId="+blogId+"' title='"+title+"'>"+title+" </a></h2></header><div class='focus'> <a target='_blank' target='blank' href='article.html?blogId="+blogId+"'><img class='thumb' src='"+imgUrl+"' alt='"+title+"' /></a>	</div> 	<span class='note'>"+summary+"</span>	<p class='auth-span'> <span class='muted'><i class='fa fa-clock-o'></i> "+createTimeStr+"</span> <span class='muted'><i class='fa fa-eye'></i> "+viewCount+"℃</span> <span class='muted'><i class='fa fa-comments-o'></i><span id = 'url::http://www.mycookies.cn/portal/article.do?blogId="+blogId+"' class = 'cy_cmt_count' ></span>评论</span><span class='muted'> <a target='_blank' href='javascript:;' data-action='ding' data-id='3849' id='Addlike' class='action'><i class='fa fa-heart-o'></i><span class='count'>"+likeCount+"</span>喜欢</a></span></p> </article>";
		 		blogContainer.append(blogChild);
		 	})

		 	//分类列表
		 	initialCategory(categoryVoList);



		 	//标签列表模块
		 	var tagscloud = $("#tagscloud");
		 	$.each(tagList,function(index,value){
		 		var tagId = value.tagId;
		 		var tagName = value.tagName;
		 		var tagCount = value.tagCount;
		 		tagscloud.append("<a target='_blank' href='category.html?tagId="+tagId+"' class='tagc"+getRandom(1,6)+"'>"+tagName+'('+tagCount+')'+"</a>");
		 		
		 	})

		 	//猜你喜欢模块
		 	var guessYouLike = $("#ul_guess_you");
		 	$.each(hotBlogs,function(index,value){
	 			var title = value.title;
		 		var commentCount = value.commentCount;
		 		var blogId = value.blogId;
		 		var createTimeStr = value.createTimeStr;
		 		var imgUrl=value.imgHost+value.imgUri;
		 		var childEle =  "<li><a target='_blank' href='article.html?blogId="+blogId+"' title='"+title+"'><span class='thumbnail'><img src='"+imgUrl+"' alt='"+title+"' /></span><span class='text'>"+title+"</span><span class='muted'>"+createTimeStr+"</span><span class='muted' style='float: right;'><span id = 'http://www.mycookies.cn/portal/article.html?blogId="+blogId+" class = 'cy_cmt_count'></span>评论</span></a></li>";
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

	/*pages总页数，firstPage,pageNum,lastPage
	firstPage 第一页，isFirstPage 是否是第一页
	lastPage 最后一页 ，isLastPage
	nextPage 下一页 	hasNextPage是否有下一页,
	prePage 上一页  hasPreviousPage 是否有上一页，
	pageNum 当前页码数，
	 start
	*/
	function pagination(pageNum,pages,firstPage,isFirstPage,lastPage
		,isLastPage,nextPage,hasNextPage,prePage,hasPreviousPage){
		var ul_pagination = $("#ul_pagination");
		// alert(ul_pagination.text());
		ul_pagination.empty();
		if(pages>0){
			//首页
			if(isFirstPage!=true){
			ul_pagination.append("<li><a target='_blank' href='javascript:goToPage(1)'>首页</a></li>");
			}
			//上一页
			if(hasPreviousPage==true){
				ul_pagination.append("<li class='prev-page'><a target='_blank' href='javascript:goToPage("+(pageNum-1)+")'>上一页</a></li>");
			}
			//数字部分
			//总页数小于7
			if(pages<7){
				for(var i=firstPage;i<=lastPage;i++){
					//当前页高亮显示
					if(i==pageNum){
						ul_pagination.append("<li class='active'><span>"+i+"</span></li>");
					}else{
						ul_pagination.append("<li><a target='_blank' href='javascript:goToPage("+i+")'>"+i+"</a></li>")
					}
				}
			//总页数大于7 但是是前三页
			}else if(pageNum<=3){
				for(var i=firstPage;i<=7;i++){
					//当前页高亮显示
					if(i==pageNum){
						ul_pagination.append("<li class='active'><span>"+i+"</span></li>");
					}else{
						ul_pagination.append("<li><a target='_blank' href='javascript:goToPage("+i+")'>"+i+"</a></li>")
					}
				}
			//总页数大于7 但是是最后三页
			}else if((pages-pageNum)<=3){
					for(var i = pages-6;i<=pages;i++){
					//当前页高亮显示
					if(i==pageNum){
						ul_pagination.append("<li class='active'><span>"+i+"</span></li>");
					}else{
						ul_pagination.append("<li><a target='_blank' href='javascript:goToPage("+i+")'>"+i+"</a></li>")
					}
				}
			//总页数大于7 又刚好能将本页显示在和中间
			}else{
				for(var i = pageNum-3;i<=pageNum+3;i++){
					//当前页高亮显示
					if(i==pageNum){
						ul_pagination.append("<li class='active'><span>"+i+"</span></li>");
					}else{
						ul_pagination.append("<li><a target='_blank' href='javascript:goToPage("+i+")'>"+i+"</a></li>")
					}
				}
			}

			//拼接下一页
			if(hasNextPage==true){
				ul_pagination.append("<li class='next-page'><a target='_blank' href='javascript:goToPage("+(pageNum+1)+")'>下一页</a></li>")
			} 
			//拼接尾页
			if(isLastPage!=true){
				ul_pagination.append("<li class='last-page'><a target='_blank' href='javascript:goToPage("+pages+")'>尾页</a></li>")
			}
			//总页数
			ul_pagination.append("<li class='last-page'><a target='_blank' href='#'>共"+pages+"页</a></li>")

		}
	}

	function goToPage(pageNum){
		if(pageNum<=0){
			return;
		}else{
			 $.ajax({
			 	type:"post",
			 	dataType:"json",
			 	url:host+"/user/blog/next_page.do",
			 	data:{"pageNum":pageNum},
			 	success:function(data){
		 			var blogList = data.data.list;
		 			var pageInfo = data.data;
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



 		 		 	var blogContainer = $("#div_article_container");
 		 		 	blogContainer.empty();
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
				 		var blogChild = "<article class='excerpt'><header><a target='_blank' class='label label-important' href='category.html?categoryId="+categoryId+"'>"+categoryName+"<i class='label-arrow'></i></a> <h2><a target='_blank'  href='article.html?blogId="+blogId+"' title='"+title+"'>"+title+" </a></h2></header><div class='focus'> <a target='_blank' target='blank' href='#'><img class='thumb' src='"+imgUrl+"' alt='"+title+"' /></a>	</div> 	<span class='note'>"+summary+"</span>	<p class='auth-span'> <span class='muted'><i class='fa fa-clock-o'></i> "+createTimeStr+"</span> <span class='muted'><i class='fa fa-eye'></i> "+viewCount+"℃</span> <span class='muted'><i class='fa fa-comments-o'></i> <a target='_blank' target='_blank' href='3849.html#comments'><span id = 'http://www.mycookies.cn/portal/article.html?blogId="+blogId+" class = 'cy_cmt_count'></span>评论</a></span><span class='muted'> <a target='_blank' href='javascript:;' data-action='ding' data-id='3849' id='Addlike' class='action'><i class='fa fa-heart-o'></i><span class='count'>"+likeCount+"</span>喜欢</a></span></p> </article>";
				 		blogContainer.append(blogChild);
				 	})
			 	}
			 })


		}
	}	

 	function initialCategory(categoryList){
 		var d_category = $("#div_category");
 		d_category.empty();

 		$.each(categoryList,function(index,value){
 			d_category.append("<a class=cate"+getRandom(1,6)+" title='"+value.categoryName+"' href='category.html?categoryId="+value.categoryId+"'>"+value.categoryName+"("+value.blogCount+")</a>");
 		})
 	}
 	
 	function search(){
 		var value = $("#input_search").val();
 		alert(value);
 	}

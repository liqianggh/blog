// var host = "http://localhost:8080/"

var host = "http://www.mycookies.cn/"

window.onload=function(){
	var thisUrl = window.location.href;
	var start = thisUrl.lastIndexOf("?");
	if(start>0){
		var keyValue = thisUrl.substr(start+1);
		//截取keyvalue
		var start2 = keyValue.lastIndexOf("=");
		if(start2>0){
			 var arr =  keyValue.split("=");
			 var key = arr[0];
			 var value = arr[1];
			 if(key=="tagId"){
			 		$.ajax({
						type:"post",
						dataType:"json",
						url:host+"user/blog/category_tag_init.do",
						data:{"tagId":value},
						success:function(data){
							var isCategory = data.data.isCategory;
							var name= data.data.name;
							var id = data.data.id;
							var pageInfo = data.data.blogPageInfo;
							var newBlogs = data.data.recommendBlog;
							var blogList = pageInfo.list;
							var tagList = data.data.tagVoList;
							var categoryVoList = data.data.categoryVoList;
							initialHeader(isCategory,name,id);
							initialBlogList(blogList);
							initialNewBlogs(newBlogs);
							initialTagClouds(tagList);
							initialCategory(categoryVoList);
					    }
					})

			 }else if(key=="categoryId"){
			 		changeMenuItem(value);
			 		$.ajax({
						type:"post",
						dataType:"json",
						url:host+"user/blog/category_tag_init.do",
						data:{"categoryId":value},
						success:function(data){
							var isCategory = data.data.isCategory;
							var name= data.data.name;
							var id = data.data.id;
							var pageInfo = data.data.blogPageInfo;
							var blogList = pageInfo.list;
							var newBlogs = data.data.recommendBlog;
							var tagList = data.data.tagVoList;
							var categoryVoList = data.data.categoryVoList;
							initialCategory(categoryVoList);

							initialHeader(isCategory,name,id);
							initialBlogList(blogList);
							initialNewBlogs(newBlogs);
							initialTagClouds(tagList);
						}
					})
		    }
		}else{
			return;
		}
	}else{
		return;
	}
}
//初始化标签、分类名（导航）
function initialHeader(isCategory,name,id){
	var header_category_tag = $("#header_category_tag");
	header_category_tag.empty();
	if(isCategory==0){
	var childEle = "<h1><i class='fa fa-folder-open'></i>&nbsp;&nbsp;标签："+name+"</h1>"
		header_category_tag.append(childEle);
	}else if(isCategory==1){
		var childEle =    "<h1><i class='fa fa-folder-open'></i>&nbsp;&nbsp;分类："+name+"</h1>"
		header_category_tag.append(childEle);
	}else{
		return;
	}
}

function initialBlogList(blogList){
	//博客列表模块
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
 		var blogChild = "<article class='excerpt'><header><a class='label label-important' href=javascript:findByCategory("+categoryId+",'"+categoryName+"')  >"+categoryName+"<i class='label-arrow'></i></a> <h2><a  href='article.html?blogId="+blogId+"' title='"+title+"'>"+title+" </a></h2></header><div class='focus'> <a target='blank' href='#'><img class='thumb' src='"+imgUrl+"' alt='"+title+"' /></a>	</div> 	<span class='note'>"+summary+"</span>	<p class='auth-span'> <span class='muted'><i class='fa fa-clock-o'></i> "+createTimeStr+"</span> <span class='muted'><i class='fa fa-eye'></i> "+viewCount+"℃</span> <span class='muted'><i class='fa fa-comments-o'></i>   <span id = 'http://www.mycookies.cn/portal/article.do?blogId="+blogId+"' class = 'cy_cmt_count' ></span>评论</span><span class='muted'> <a href='javascript:;' data-action='ding' data-id='3849' id='Addlike' class='action'><i class='fa fa-heart-o'></i><span class='count'>"+likeCount+"</span>喜欢</a></span></p> </article>";
 		blogContainer.append(blogChild);
 	})

}

function initialNewBlogs(newBlogs){

 	//最新发布
 	var newBlogsContainer = $("#ul_new_blogs");
 	newBlogsContainer.empty();
 	$.each(newBlogs,function(index,value){
			var title = value.title;
 		var commentCount = value.commentCount;
 		var blogId = value.blogId;
 		var createTimeStr = value.createTimeStr;
 		var imgUrl=value.imgHost+value.imgUri;
 		var childEle =  "<li><a href='article.html?blogId="+blogId+"' title='"+title+"'><span class='thumbnail'><img src='"+imgUrl+"' alt='"+title+"' /></span><span class='text'>"+title+"</span><span class='muted'>"+createTimeStr+"</span><span class='muted' style='float: right;'>"+commentCount+"评论</span></a></li>";
 		newBlogsContainer.append(childEle);
 	})
}

function initialTagClouds(tagList){
 	//标签列表模块
 	var tagscloud = $("#tagscloud");

 	tagscloud.empty();
 	$.each(tagList,function(index,value){
 		var tagId = value.tagId;
 		var tagName = value.tagName;
 		var tagCount = value.tagCount;
 		tagscloud.append("<a href=javascript:findByTag("+tagId+",'"+tagName+"') class='tagc"+getRandom(1,5)+"'>"+tagName+'('+tagCount+')'+"</a>");
 		
 	})
 	//启动标签特效
	 	  	 var tc = tagcloud(); 
}
//获取随机数
function getRandom(min,max){
	//Math.random()*(上限-下限+1)+下限  
    return parseInt(Math.random() * (max - min + 1) + min); 
}

function findByTag(tagId,tagName){
	initialHeader(0,tagName,tagId);
	$.ajax({
		type:"post",
		dataType:"json",
		url:host+"user/blog/list_by_tag.do",
		data:{"tagId":tagId},
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

			initialBlogList(blogList);

		}
	})

}

function findByCategory(categoryId,categoryName){
	changeMenuItem(categoryId);
	initialHeader(1,categoryName,categoryId);
	$.ajax({
		type:"post",
		dataType:"json",
		url:host+"user/blog/list_by_category.do",
		data:{"categoryId":categoryId},
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
			initialBlogList(blogList);
			 
		}
	})
}
	//生成分页部分
	function pagination(pageNum,pages,firstPage,isFirstPage,lastPage
		,isLastPage,nextPage,hasNextPage,prePage,hasPreviousPage){
		var ul_pagination = $("#ul_pagination");
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
//跳转到指定页面
	function goToPage(pageNum){
		if(pageNum<=0){
			return;
		}else{
			 $.ajax({
			 	type:"post",
			 	dataType:"json",
			 	url:url+"/user/blog/next_page.do",
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
				 		var blogChild = "<article class='excerpt'><header><a target='_blank' class='label label-important' href='category.html?categoryId="+categoryId+"'>"+categoryName+"<i class='label-arrow'></i></a> <h2><a target='_blank'  href='article.html?blogId="+blogId+"' title='"+title+"'>"+title+" </a></h2></header><div class='focus'> <a target='_blank' target='blank' href='#'><img class='thumb' src='"+imgUrl+"' alt='"+title+"' /></a>	</div> 	<span class='note'>"+summary+"</span>	<p class='auth-span'> <span class='muted'><i class='fa fa-clock-o'></i> "+createTimeStr+"</span> <span class='muted'><i class='fa fa-eye'></i> "+viewCount+"℃</span> <span class='muted'><i class='fa fa-comments-o'></i> <a target='_blank' target='_blank' href='3849.html#comments'>"+commentCount+"评论</a></span><span class='muted'> <a target='_blank' href='javascript:;' data-action='ding' data-id='3849' id='Addlike' class='action'><i class='fa fa-heart-o'></i><span class='count'>"+likeCount+"</span>喜欢</a></span></p> </article>";
				 		blogContainer.append(blogChild);
				 	})
			 	}
			 })


		}
	}	

	function changeMenuItem(key){
		if(key<=5){
			$("#menu-item-"+key).addClass("current-menu-item current_page_item");
		}else{
			$("#menu-item-1").addClass("current-menu-item current_page_item");
 		}
	}
	//分类初始化
 	function initialCategory(categoryList){
 		var d_category = $("#div_category");
 		d_category.empty();

 		$.each(categoryList,function(index,value){
 			d_category.append("<a class=cate"+getRandom(1,6)+" title='"+value.categoryName+"'   href=javascript:findByCategory("+value.categoryId+",'"+value.categoryName+"')  >"+value.categoryName+"("+value.blogCount+")</a>");
 		})
 	}
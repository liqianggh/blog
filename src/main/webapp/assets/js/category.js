
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
						url:host+"/user/blog/category_tag_init.do",
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
							initBlogList(blogList);
							initialNewBlogs(newBlogs);
							initialTagClouds(tagList,1);
							initialCategory(categoryVoList,1);

							// add_script_cy();

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

 					    }
					})

			 }else if(key=="categoryId"){
			 		changeMenuItem(value);
			 		$.ajax({
						type:"post",
						dataType:"json",
						url:host+"/user/blog/category_tag_init.do",
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
							initialCategory(categoryVoList,1);

							initialHeader(isCategory,name,id);
							initBlogList(blogList);
							initialNewBlogs(newBlogs);
							initialTagClouds(tagList,1);

							// add_script_cy();
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
 		var childEle =  "<li><a href='article.html?blogId="+blogId+"' title='"+title+"'><span class='thumbnail'><img src='"+imgUrl+"' alt='"+title+"' /></span><span class='text'>"+title+"</span><span class='muted'>"+createTimeStr+"</span><span class='muted' style='float: right;'><span id = 'url::http://www.mycookies.cn/article.html?blogId="+blogId+"' class = 'cy_cmt_count' ></span><script id='cy_cmt_num' src='https://changyan.sohu.com/upload/plugins/plugins.list.count.js?clientId=cytqlFQwr'></script> 评论</span></a></li>";
 		newBlogsContainer.append(childEle);
 	})
}



function findByCategory(categoryId,categoryName){
	changeThePageTitle(categoryName);
	changeMenuItem(categoryId);
	initialHeader(1,categoryName,categoryId);
	$.ajax({
		type:"post",
		dataType:"json",
		url:host+"/user/blog/list_by_category.do",
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
			initBlogList(blogList);
			 
		}
	})
}
 
 	function changeThePageTitle(name){
 		var title = $("#title_page_name");
 		title.empty();
 		title.append(name);
 	}


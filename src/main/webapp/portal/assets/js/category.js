var host = "http://localhost:8080/"
 

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
							

					    }
					})

			 }else if(key=="categoryId"){
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
function initialHeader(isCategory,name,id){
	var header_category_tag = $("#header_category_tag");
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

}

function initialNewBlogs(newBlogs){
 	//最新发布
 	var newBlogsContainer = $("#ul_new_blogs");
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
 	$.each(tagList,function(index,value){
 		var tagId = value.tagId;
 		var tagName = value.tagName;
 		var tagCount = value.tagCount;
 		tagscloud.append("<a href='category.html?tagId="+tagId+"' class='tagc"+getRandom(1,5)+"'>"+tagName+'('+tagCount+')'+"</a>");
 		
 	})
 	//启动标签特效
	 	  	 var tc = tagcloud(); 
}
//获取随机数
function getRandom(min,max){
	//Math.random()*(上限-下限+1)+下限  
    return parseInt(Math.random() * (max - min + 1) + min); 
}

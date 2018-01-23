	var badgecolor = ["badge-red","badge-orange","badge-green",
					  "badge-yellow","badge-blue","badge-pink",
					  "badge-violet","badge-grey","badge-dark"];
    //新增标签id数组
	var newTagsId = new Array();
	var newTagsStr = new Array();

window.onload=function(){
		$.ajax({
	        type: "post",
	        dataType: "json",
	        url: 'http://localhost:8080/common/load_category_tags.do',
	        data: {},
	        success: function (data) {
	            if (data != "") {
	            	if(data.status===0){
	            		var tagArr = data.data.tagList;
	            		var  categoryArr = data.data.categoryList;

	            		var blogTagDiv = $("#blog-tags");
	            		var categorySelect = $("#blog-categoryId-select");
	            		$.each(tagArr,function(index,value){
								 // blogTagDiv.append("<span class='badge "+badgecolor[getRandom(0,9)]+"' "+id=value.tagId+">"+value.tagName+"</span>");

								 var tagId = value.tagId;
								 var tagName = value.tagName;
								 blogTagDiv.append("<a href='javascript:addFromStoB("+tagId+");'><span id=tagofsystem_"+tagId+" class='badge "+badgecolor[getRandom(0,9)]+"'>"+tagName+"</span></a>&nbsp;");
						});

						$.each(categoryArr,function(index,value){
								if(index==0){
									categorySelect.empty();
									categorySelect.append("<option selected=selected  value='"+value.categoryId+"'>"+value.categoryName+"</option>");

								}
								categorySelect.append(  "<option value='"+value.categoryId+"'>"+value.categoryName+"</option>");
						})
	            	}else{
	            		alert("初始化失败！");
	            	}
	            }
	        }
	     })
	}
	
//添加标签到系统中
 function addTagToSystem(){
 	var addTagDiv = $("#add-tag-div");
 	if(addTagDiv.css("display")=="none"){
 		addTagDiv.show();
 	}
} 

//给博客添加标签
function addFromStoB(id){
 var param = $("#tagofsystem_"+id);
	//先判断是否存在该标签
	if($("#tagOfBlog_"+id).length==0){
		var name = param.text();
		newTagsId.push(id);
		newTagsStr.push(name);
		 $("#tagsofblog").append("<a href='javascript:addFromBtoS("+id+");'><span realId="+id+" id=tagOfBlog_"+id+" class='badge "+badgecolor[getRandom(0,9)]+"'>"+name+"</span></a>&nbsp;");
		param.remove();
	}else{
		param.remove();
	}
}

//给博客删除标签
function addFromBtoS(id){
 var param = $("#tagOfBlog_"+id);
	//先判断是否存在该标签
	if($("#tagofsystem_"+id).length==0){
		  newTagsId.pop(); 
		 newTagsStr.pop();
		var name = param.text();
		 $("#blog-tags").append("<a href='javascript:addFromStoB("+id+");'><span id=tagofsystem_"+id+" class='badge "+badgecolor[getRandom(0,9)]+"'>"+name+"</span></a>&nbsp;");
		param.remove();
	}else{
		//删除数据库中的数据
		$.ajax({
			type: "post",
			dataType: "json",
			url: 'http://localhost:8080/manage/tag/delete.do',
			data: {tagId:id},
			success: function (data) {
			    if (data.status ==0) {
	    			param.remove();
			    }else{
			    	alert("删除失败！");
			    }
			 }
	    });
	}
}

//隐藏标签
$("#hinder-add-tag").click(function(){
	$("#add-tag-div").hide();
})

//给系统添加标签
$("#blog-add-tag").click(function(){
	var newTag = $("#blog-add-input").val();
	if(newTag==null||newTag.length==0){
		alert("请输入有效内容！");
	}else{
		//添加到数据库
		$.ajax({
			type: "post",
			dataType: "json",
			url: 'http://localhost:8080/manage/tag/saveOrUpdate.do',
			data: {tagName:newTag},
			success: function (data) {
			    if (data != "") {
			    	alert(data.status);
			        }
			 }
	    });

	}
})

/*提交表单*/
$("#btn-submit-blog").on("click",function(){
	var title = $("#blog-title-input").val();
	var code = $("#blog-code-select").val();
	var categoryId = $("#blog-categoryId-select").val();
	var content=$("#mdtextarea").text();
	var  summary = content.substr(0,120)+"...";
	var uri = $("#blog-uri-input").val();

	content = editor.getHTML();
    var tags ="";
    $.each(newTagsId,function(index,value){
    	tags += value +" ";
	})
alert(uri);
	var blog={"title":title,"code":code,"categoryId":categoryId,"summary":summary,"content":content,"tagIds":tags,"imgUri":uri};
		$.ajax({
			type: "post",
			dataType: "json",
			url: 'http://localhost:8080/manage/blog/saveOrUpdate.do',
			// contentType: 'application/json;charset=utf-8',
			data: blog,
			success: function (result) {
				alert(result.status);
			    if (result != "") {
			    	alert(data.status);
			        }
			 }
	    });


})




//获取随机数
function getRandom(min,max){
		 //Math.random()*(上限-下限+1)+下限  
    return parseInt(Math.random() * (max - min + 1) + min); 
}
 


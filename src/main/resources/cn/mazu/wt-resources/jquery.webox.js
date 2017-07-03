/**
 * 
 * Plugin: Jquery.webox Developer: Blank Version: 1.0 Beta Update: 2012.07.08
 * 
 */
$
		.extend({
			webox : function(option) {
				var _x, _y, m, allscreen = false;
				if (!option) {
					alert('options can\'t be empty');
					return;
				}
				;
				// if(!option['html']&&!option['iframe']){
				// alert('html attribute and iframe attribute can\'t be both
				// empty');
				// return;
				// };
				option['parent'] = 'webox';
				option['locked'] = 'locked';
				$(document).ready(function(e) {
					// $('.webox').remove();
					// $('.background').remove();
					var width = option['width'] ? option['width'] : 400;
					var height = option['height'] ? option['height'] : 240;
					// $('body').append('<div class="background"
					// style="display:none;"></div><div class="webox"
					// style="width:'+width+'px;height:'+height+'px;display:none;"><div
					// id="inside" style="height:'+height+'px;"><h1 id="locked"
					// onselectstart="return
					// false;">'+(option['title']?option['title']:'webox')+'<a
					// class="span" href="javascript:void(0);"></a></h1>'
					// +(option['iframe']?'<iframe class="w_iframe"
					// src="'+option['iframe']+'" frameborder="0" width="100%"
					// scrolling="yes"
					// style="border:none;overflow-x:hidden;height:'+parseInt(height-30)+'px;"></iframe>':option['html']?option['html']:'')+'</div></div>');
					// $('body').append('<div class="background"
					// style="display:none;"></div><div class="webox"
					// style="width:'+width+'px;height:'+height+'px;display:none;"><div
					// id="inside" style="height:'+height+'px;"><h1 id="locked"
					// onselectstart="return
					// false;">'+(option['title']?option['title']:'webox')+'<a
					// class="span" href="javascript:void(0);"></a></h1>'
					// +(option['html'].html())+'</div></div>');

					/*
					 * if(navigator.userAgent.indexOf('MSIE
					 * 7')>0||navigator.userAgent.indexOf('MSIE 8')>0){
					 * $('.webox').css({'filter':'progid:DXImageTransform.Microsoft.gradient(startColorstr=#55000000,endColorstr=#55000000)'}); }
					 */
					if (option['bgvisibel']) {
						$('.background').fadeTo('slow', 0.3);
						// $('.webox').css({display:'block'});
					}
					;
					$('.webox').css({
						display : 'block'
					});
					// $('#'+option['locked']+' .span').click(function(){
					$("#locked .span").click(function() {
						$('.webox').css({
							display : 'none'
						});
						$('.background').css({
							display : 'none'
						});
					});
					var marginLeft = parseInt(width / 2);
					var marginTop = parseInt(height / 2);
					var winWidth = parseInt($(window).width() / 2);
					var winHeight = parseInt($(window).height() / 2.2);
					var left = winWidth - marginLeft;
					//var top = winHeight - marginTop;
					var top = 80;
					$('.webox').css({
						left : left,
						top : top
					});
					$('.tcontainer').css("max-height","400px");
					// $('#'+option['locked']).mousedown(function(e){
					$('#locked').mousedown(function(e) {
						if (e.which) {
							m = true;
							_x = e.pageX - parseInt($('.webox').css('left'));
							_y = e.pageY - parseInt($('.webox').css('top'));
						}
					}).dblclick(function() {
						if (allscreen) {
							/*$('.webox').css({
								height : height,
								width : width
							});
							$('#inside').height(height);
							$('.tcontainer').css("max-height","100%");*/
							// $('.w_iframe').height(height-30);
							
							$('.webox').css({
							'width' : '898px',
							'height' :'auto',
							top : top,
							left : left
							});
						$('#inside').css("height","auto");
						$('.tcontainer').css("max-height","400px");
						$('#locked .enlarge').css("background-position","0px -16px");
						$('#locked .enlarge').attr("title","最大化");
							/*$('.webox').css({
								left : left,
								top : top
							});*/
							allscreen = false;
						} else {
							allscreen = true;
							var screenHeight = $(window).height();
							var screenWidth = $(window).width();
							$('.webox').css({
								'width' : screenWidth - 18,
								'height' : screenHeight - 18,
								'top' : '0px',
								'left' : '0px'
							});
							$('#inside').height(screenHeight - 20);
							$('.tcontainer').css("max-height",screenHeight - 100);
							$('#locked .enlarge').css("background-position","0px 0px");
							$('#locked .enlarge').attr("title","最小化");
							/*$('.webox').css({
								'width' : '898px',
								'height' :'auto',
								'top' : '0px',
								'left' : '0px'
							});
							$('#inside').css("height","auto");
							$('.tcontainer').css("max-height","400px");*/
							// $('.w_iframe').height(screenHeight-50);
						}
					});
					
					$('#locked .enlarge').click(function(){
						if(!allscreen){
							allscreen = true;
							var screenHeight = $(window).height();
							var screenWidth = $(window).width();
							$('.webox').css({
								'width' : screenWidth - 18,
								'height' : screenHeight - 18,
								'top' : '0px',
								'left' : '0px'
							});
							$('#inside').height(screenHeight - 20);
							$('.tcontainer').css("max-height",screenHeight - 100);
							$(this).attr("style","background-position:0px 0px;");
							$('#locked .enlarge').attr("title","最小化");
						}else{
							$('.webox').css({
								'width' : '898px',
								'height' :'auto',
								top : top,
								left : left
								});
							$('#inside').css("height","auto");
							$('.tcontainer').css("max-height","400px");
							$(this).attr("style","background-position:0px -16px;");
							$('#locked .enlarge').attr("title","最大化");
							allscreen = false;
						}
					});
				}).mousemove(function(e) {
					if (m && !allscreen) {
						var x = e.pageX - _x;
						var y = e.pageY - _y;
						$('.webox').css({
							left : x
						});
						$('.webox').css({
							top : y
						});
					}
				}).mouseup(function() {
					m = false;
				});
				$(window).resize(function() {
					if (allscreen) {
						var screenHeight = $(window).height();
						var screenWidth = $(window).width();
						$('.webox').css({
							'width' : screenWidth - 18,
							'height' : screenHeight - 18,
							'top' : '0px',
							'left' : '0px'
						});
						$('#inside').height(screenHeight - 20);
						// $('.w_iframe').height(screenHeight-50);
					}
				});
				//			$('#'+option['locked']+' .collapse').click(function(){
				$('#locked .collapse').click(function() {
					if ($(this).attr("_num") == "1") {
						$('.tcontainer').slideUp(600);
						$(this).attr("_num", "0");
						$(this)
								.attr(
										"style",
										"background:url(../images/panel_tools.png) no-repeat scroll -32px -16px transparent;");
					} else {
						$('.tcontainer').slideDown(600);
						$(this).attr("_num", "1");
						$(this)
								.attr(
										"style",
										"background:url(../images/panel_tools.png) no-repeat scroll -32px 0px transparent;");
					}
				});
			
			}
		});
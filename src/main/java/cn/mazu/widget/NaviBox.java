package cn.mazu.widget;

import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

/*页脚*/
public class NaviBox extends HtmlTemplate {
	public NaviBox(){
	}
	@Override
	public Widget draw(Template parent, String... args) {
		String text ="<div class='head-v3'>"+
	"<div class='navigation-up'>"+
		"<div class='navigation-inner'>"+
			"<div class='navigation-v3'>"+
				"<ul>"+
					/*"<li class='nav-up-selected-inpage' _t_nav='home'>"+
						"<h2>"+
							"<a href='#'>首页</a>"+
						"</h2>"+
					"</li>"+*/
					"<li class='' _t_nav='product'>"+
							"<a href='#'>云产品</a>"+
					"</li>"+
					"<li class='' _t_nav='wechat'>"+
							"<a href='#'>微信建站</a>"+
					"</li>"+
					"<li class='' _t_nav='solution'>"+
							"<a href='#'>行业解决方案</a>"+
					"</li>"+
					"<li class='' _t_nav='cooperate'>"+
							"<a href='#'>合作伙伴</a>"+
					"</li>"+
					"<li _t_nav='support'>"+
							"<a href='#'>帮助与支持</a>"+
					"</li>"+
				"</ul>"+
			"</div>"+
		"</div>"+
	"</div>"+
	"<div class='navigation-down'>"+
		"<div id='product' class='nav-down-menu menu-1' style='display: none;' _t_nav='product'>"+
			"<div class='navigation-down-inner'>"+
				"<dl style='margin-left: 100px;'>"+
					"<dt>计算机与网络</dt>"+
					"<dd>" +
					"${HighLink}"+
						/*"<a href='javascript:void(0);' onclick='add(\"1\",\"云服务器\", \"${popTable}\")'>云服务器</a>"+*/
					"</dd>"+
					"<dd>"+
						"<a href='javascript:void(0);' onclick='add(\"2\",\"弹性Web引擎\", \"<h1>这里是弹性Web引擎！！！</h1>\")'>弹性Web引擎</a>"+
					"</dd>"+
					"<dd>"+
						"<a href='javascript:void(0);' onclick='add(\"3\",\"负载均衡\",\"\")'>负载均衡</a>"+
					"</dd>"+
				"</dl>"+
				"<dl>"+
					"<dt>存储与CDN</dt>"+
					"<dd>"+
						"<a href='javascript:void(0);' onclick='add(\"4\",\"云数据库\",\"\")'>云数据库</a>"+
					"</dd>"+
					"<dd>"+
						"<a href='javascript:void(0);' onclick='add(\"5\",\"NoSQL高速存储\",\"\")'>NoSQL高速存储</a>"+
					"</dd>"+
					"<dd>"+
						"<a href='javascript:void(0);' onclick='add(\"6\",\"对象存储服务(beta)\",\"\")'>对象存储服务(beta)</a>"+
					"</dd>"+
					"<dd>"+
						"<a href='javascript:void(0);' onclick='add(\"7\",\"CDN\",\"\")'>CDN</a>"+
					"</dd>"+
				"</dl>"+
			"</div>"+
		"</div>"+
		"<div id='solution' class='nav-down-menu menu-3 menu-1' style='display: none;' _t_nav='solution'>"+
			"<div class='navigation-down-inner'>"+
				"<dl style='margin-left: 380px;'>"+
					"<dd>"+
						"<a class='link' href='javascript:void(0);' onclick='add(\"8\",\"微信\",\"\")'>微信</a>"+
					"</dd>"+
				"</dl>"+
				"<dl>"+
					"<dd>"+
						"<a class='link' href='javascript:void(0);' onclick='add(\"9\",\"游戏\",\"\")'>游戏</a>"+
					"</dd>"+
				"</dl>"+
				"<dl>"+
					"<dd>"+
						"<a class='link' href='javascript:void(0);' onclick='add(\"10\",\"移动应用\",\"\")'>移动应用</a>"+
					"</dd>"+
				"</dl>"+
			"</div>"+
		"</div>"+
		"<div id='support' class='nav-down-menu menu-3 menu-1' style='display: none;' _t_nav='support'>"+
			"<div class='navigation-down-inner'>"+
				"<dl style='margin-left: 610px;'>"+
					"<dd>"+
						"<a class='link' href='javascript:void(0);' onclick='add(\"11\",\"资料库\", \"\")'>资料库</a>"+
					"</dd>"+
				"</dl>"+
				"<dl>"+
					"<dd>"+
						"<a class='link' href='javascript:void(0);' onclick='add(\"12\",\"论坛\", \"\")'>论坛</a>"+
					"</dd>"+
				"</dl>"+
				"<dl>"+
					"<dd>"+
						"<a class='link' href='javascript:void(0);' onclick='add(\"13\",\"亿元扶持\", \"\")'>亿元扶持</a>"+
					"</dd>"+
				"</dl>"+
			"</div>"+
		"</div>"+
		"<div id='cooperate' class='nav-down-menu menu-3 menu-1' style='display: none;' _t_nav='cooperate'>"+
			"<div class='navigation-down-inner'>"+
				"<dl style='margin-left: 480px;'>"+
					"<dd>"+
						"<a href='javascript:void(0);' onclick='add(\"14\",\"代理商\", \"\")'>代理商</a>"+
					"</dd>"+
				"</dl>"+
				"<dl>"+
					"<dd>"+
						"<a href='javascript:void(0);' onclick='add(\"15\",\"微信服务商\",\"\")'>微信服务商</a>"+
					"</dd>"+
				"</dl>"+
				"<dl>"+
					"<dd>"+
						"<a href='javascript:void(0);' onclick='add(\"16\",\"创投机构\", \"\")'>创投机构</a>"+
					"</dd>"+
				"</dl>"+
			"</div>"+
		"</div>"+
	"</div>"+
"</div>";

		final Anchor HighLink = new Anchor(new Link(""), "云服务器");
		HighLink.clicked().addListener(HighLink, new Signal.Listener() {
			@Override
			public void trigger() {
				HighLink.doJavaScript("add(\"1\",\"云服务器\",\"<h1>This is CloudServer--------</h1>\")");
			}
		});
		this.bindWidget("HighLink", HighLink);
		
        this.setTemplateText(text,TextFormat.XHTMLUnsafeText);
        this.doJavaScript("$(document).ready(function(){" +
        		"var qcloud={};"+
		"		$('[_t_nav]').hover(function(){"+
		"			var _nav = $(this).attr('_t_nav');"+
		"			clearTimeout( qcloud[ _nav + '_timer' ] );"+
		"			qcloud[ _nav + '_timer' ] = setTimeout(function(){"+
		"			$('[_t_nav]').each(function(){"+
//		"			$(this)[ _nav == $(this).attr('_t_nav') ? 'addClass':'removeClass' ]('nav-up-selected');"+
		"			});"+
		"			$('#'+_nav).stop(true,true).slideDown(200);"+
		"			}, 150);"+
		"		},function(){"+
		"			var _nav = $(this).attr('_t_nav');"+
		"			clearTimeout( qcloud[ _nav + '_timer' ] );"+
		"			qcloud[ _nav + '_timer' ] = setTimeout(function(){"+
//		"			$('[_t_nav]').removeClass('nav-up-selected');"+
			"		$('#'+_nav).stop(true,true).slideUp(200);"+
				"	}, 150);"+
				"});" +
        		"});");
		return this;
	}			
}

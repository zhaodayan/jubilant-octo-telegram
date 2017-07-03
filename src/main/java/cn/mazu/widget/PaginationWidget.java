package cn.mazu.widget;

import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.event.Signal1;
import cn.mazu.widget.event.WMouseEvent;
import cn.mazu.widget.kit.web.interact.InteractWidget;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

public class PaginationWidget extends ContainerWidget {
		public  Integer items_per_page = 1;
		private Integer current_page = 0;
//		private Integer num_display_entries = 4;
//		private Integer num_edge_entries = 2;
		//直接跳到首页和末页,不是上一页和下一页
//		private String prev_text = "上一页";
//		private String next_text = "下一页";
//		private String ellipse_text = "...";
		private Integer maxentries = 7;
//		private boolean prev_show_always = true;
//		private boolean next_show_always = true;
		
		public PaginationWidget(Integer maxentries, Integer current_page,Integer items_per_page){
			this.items_per_page = items_per_page;
			this.maxentries = maxentries;
			this.current_page = current_page;
			drawLinks();
		}
		public int numPages(){
			return (int) Math.ceil(maxentries/(double)items_per_page);
		}
		
//		private int[] getInterval(){
//			int ne_half = (int) Math.ceil(num_display_entries/2);
//			int np = numPages();
//			int upper_limit = np-num_display_entries;
//			int[] result = new int[2];
//			int start = (int) (current_page>ne_half?Math.max(Math.min(current_page-ne_half, upper_limit), 0):0);
//			int end = (int) (current_page>ne_half?Math.min(current_page+ne_half, np):Math.min(num_display_entries, np));
//			result[0]=start;result[1]=end;
//			return result;
//		}
		
		protected void selectPage(int page_id){
			current_page = page_id;
			drawLinks();
		}
		private void drawLinks(){
			this.clear();
//			int[] interval = getInterval();
			int np = numPages();
			
			Template pagi = new Template();
			pagi.setTemplateText("" +
					"<table cellspacing='0' cellpadding='0' border='0'><tbody><tr>" +
			"<td>${pagiFirst}</td>" +
	        "<td>${pagiPrev}</td>" +
	        "<td><div class='pagination-btn-separator'></div></td>"+
	        "<td><span style='padding-left:6px;'>第 ${currentnum} 页</span></td>"+
	        "<td><span style='padding:0 6px;'></span></td>"+
	        "<td><span style='padding-right:6px;'>共 ${pagenum} 页</span></td>"+
	        "<td><div class='pagination-btn-separator'></div></td>"+
	        "<td>${pagiNext}</td>" +
	        "<td>${pagiLast}</td>" +
	        "<td><div class='pagination-btn-separator'></div></td>"+
	    "</tr></tbody></table>"+
	"<div class='pagination-info'>显示记录 ${RecFrom} 至 ${RecTo}   共 ${RecNum} 条记录</div>"+
	"<div style='clear:both;'></div>", TextFormat.XHTMLUnsafeText);
			
			pagi.bindString("currentnum", Integer.valueOf(current_page+1).toString());
			pagi.bindString("pagenum", Integer.valueOf(np).toString());
			pagi.bindString("RecFrom", Integer.valueOf(1+current_page*items_per_page).toString());
			pagi.bindString("RecTo", Integer.valueOf((current_page+1)*items_per_page>maxentries?maxentries:(current_page+1)*items_per_page).toString());
			pagi.bindString("RecNum", Integer.valueOf(maxentries).toString());
			pagi.bindWidget("pagiFirst", createButton(- 1,"pagiFirst"));
			pagi.bindWidget("pagiPrev", createButton(current_page - 1,"pagiPrev"));
			pagi.bindWidget("pagiNext", createButton(current_page + 1,"pagiNext"));
			pagi.bindWidget("pagiLast", createButton(np-1,"pagiLast"));
			pagi.setStyleClass("pagiDiv");
			this.addWidget(pagi);
		}
		
		private Widget createButton(int page_id,String style){
			int np = numPages();
			page_id = page_id < 0 ? 0 : (page_id < np ? page_id : np - 1);
			
			final ContainerWidget a = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_A;
				}
			};
			
			a.setStyleClass("pagibtn");
			final ContainerWidget span = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_SPAN;
				}
			};
			span.setStyleClass(style);
			a.addChild(span);
			if(current_page==0){
				if(style.equals("pagiPrev")||style.equals("pagiFirst")||
						(np==1&&(style.equals("pagiNext")||style.equals("pagiLast")))){
					span.addStyleClass("disabled");
					a.disable();
				}
				
			}else if(current_page==np-1){
				if(style.equals("pagiNext")||style.equals("pagiLast")){
					span.addStyleClass("disabled");
					a.disable();
				}
			}
				a.setAttributeValue("page_id", String.valueOf(page_id));
				a.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
					@Override
					public void trigger(WMouseEvent arg) {
						PaginationWidget.this.selectPage(Integer.valueOf(a.getAttributeValue("page_id")).intValue());
					}
				});
			
			return a;
		}
}

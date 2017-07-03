package cn.mazu.widget;

import cn.mazu.widget.event.Signal1;
import cn.mazu.widget.event.WMouseEvent;
import cn.mazu.widget.kit.web.interact.InteractWidget;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

public class PaginationNew extends ContainerWidget {
		public  Integer items_per_page = 1;
		private Integer current_page = 0;
		private Integer num_display_entries = 4;
		private Integer num_edge_entries = 2;
		//直接跳到首页和末页,不是上一页和下一页
		private String prev_text = "＜";
		private String next_text = "＞";
		private String ellipse_text = "...";
		private Integer maxentries = 7;
		private boolean prev_show_always = true;
		private boolean next_show_always = true;
		
		public PaginationNew(Integer maxentries, Integer current_page,Integer items_per_page){
			this.items_per_page = items_per_page;
			this.maxentries = maxentries;
			this.current_page = current_page;
			drawLinks();
		}
		
		
		public PaginationNew(Integer maxentries, Integer current_page,Integer items_per_page,Boolean flag){
			this.items_per_page = items_per_page;
			this.maxentries = maxentries;
			this.current_page = current_page;
			drawlink2();
		}
		
		public int numPages(){
			return (int) Math.ceil(maxentries/(double)items_per_page);
		}
		
		private int[] getInterval(){
			int ne_half = (int) Math.ceil(num_display_entries/2);
			int np = numPages();
			int upper_limit = np-num_display_entries;
			int[] result = new int[2];
			int start = (int) (current_page>ne_half?Math.max(Math.min(current_page-ne_half, upper_limit), 0):0);
			int end = (int) (current_page>ne_half?Math.min(current_page+ne_half, np):Math.min(num_display_entries, np));
			result[0]=start;result[1]=end;
			return result;
		}
		
		private void appendItem(int page_id,String style) {
			int np = numPages();
			page_id = page_id < 0 ? 0 : (page_id < np ? page_id : np - 1);
			String text = "" + (page_id + 1);
			if(style.equals("prev"))
				text = prev_text;
			else if(style.equals("next"))
				text = next_text;
			final InteractWidget lnk;
			
			if(style.equals("prev")){
				text = prev_text;
//				lnk.setStyleClass("prevpg spanpg");
			}
			else if(style.equals("next")){
				text = next_text;
//				lnk.setStyleClass("nextpg spanpg");
			}
//			lnk = new Text("");
			if (page_id == current_page) {
				lnk = new Text(text);
					lnk.setStyleClass("current spanpg");
			} else {
				lnk = new Text(text);
				lnk.setAttributeValue("page_id", String.valueOf(page_id));
				lnk.setStyleClass("pagingit spanpg");
				lnk.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
					@Override
					public void trigger(WMouseEvent arg) {
						PaginationNew.this.selectPage(Integer.valueOf(lnk.getAttributeValue("page_id")).intValue());
					}
				});
			}
			this.addWidget(lnk);
		}
		protected void selectPage(int page_id){
			current_page = page_id;
			drawLinks();
		}
		private void drawLinks(){
			this.clear();
			int[] interval = getInterval();
			int np = numPages();

			if (!prev_text.equals("") && (current_page > 0 || prev_show_always)) {
				appendItem(current_page - 1, "prev");
			}

			if (interval[0] > 0 && num_edge_entries > 0) {
				int end = Math.min(num_edge_entries, interval[0]);
				for (int i = 0; i < end; i++) {
					appendItem(i, "");
				}
				if (num_edge_entries < interval[0] && !ellipse_text.equals("")) {
					Text ellipse = new Text(ellipse_text);
//					ellipse.setAttributeValue("style", "display: block;float: left;padding: 0.3em 0.5em;margin-right: 5px;line-height:2px;");
					this.addWidget(ellipse);
				}
			}
			for (int i = interval[0]; i < interval[1]; i++)
				appendItem(i, "");
			if (interval[1] < np && num_edge_entries > 0) {
				if ((np-num_edge_entries) > interval[1] && !ellipse_text.equals("")) {
					Text ellipse = new Text(ellipse_text);
					ellipse.setStyleClass("spanpg");
					ellipse.setAttributeValue("style", "border:none;box-shadow:none;background:none;");
					this.addWidget(ellipse);
				}
				int begin = Math.max(np-num_edge_entries, interval[1]);
				for (int i = begin; i < np; i++)
					appendItem(i, "");
			}

			if (!next_text.equals("")
					&& (current_page < np - 1 || next_show_always)) {
				appendItem(current_page + 1, "next");
			}

			maxentries = (maxentries < 0) ? 1 : maxentries;
			items_per_page = (items_per_page < 0) ? 1 : items_per_page;
			
		}
		
		private void drawlink2(){
			this.clear();
			appendItem2(current_page - 1, "prev");
			appendItem2(current_page + 1, "next");
		}
		private void appendItem2(int page_id,String style){
			final InteractWidget lnk = new Text("");
			final int np = numPages();
			page_id = page_id < 0 ? 0 : (page_id < np ? page_id : np - 1);
			if(style.equals("prev")){
				if(current_page==0)
					lnk.setAttributeValue("style", "background-position:0px 0px;margin-right:10px;");
				else
					lnk.setAttributeValue("style", "background-position:-2px 0px;margin-right:10px;");
			}
			else if(style.equals("next")){
				if(current_page==np-1)
					lnk.setAttributeValue("style", "width:13px;background-position:-13px 0px;margin-right:10px;");
				else
					lnk.setAttributeValue("style", "background-position:-12px 0px;");
				
			}
			lnk.setAttributeValue("page_id", String.valueOf(page_id));
			
			lnk.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
				@Override
				public void trigger(WMouseEvent arg) {
					if(lnk.getStyleClass().equals("prev")){
						if(current_page==0)
							lnk.setAttributeValue("page_id","0");
						else
							lnk.setAttributeValue("page_id",(current_page-1)+"");
					}
					if(lnk.getStyleClass().equals("next")){
						if(current_page==np-1)
							lnk.setAttributeValue("page_id", np-1+"");
						else
							lnk.setAttributeValue("page_id", (current_page+1)+"");
						
					}
					PaginationNew.this.selectPage2(Integer.valueOf(lnk.getAttributeValue("page_id")).intValue());
				}
			});
			
		   this.addWidget(lnk);
		}
		
		protected void selectPage2(int page_id){
			current_page = page_id;
			drawlink2();
		}
}

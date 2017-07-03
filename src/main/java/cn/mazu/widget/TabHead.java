package cn.mazu.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.mazu.WApplication;
import cn.mazu.annotation.Bus;
import cn.mazu.mysql.Entity;
import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Notice;
import cn.mazu.storage.entity.InventoryWarn;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.MainMenu;
import cn.mazu.util.Util.NoticeType;
import cn.mazu.util.Util.WorkOrderStatus;
import cn.mazu.utils.AlignmentFlag;
import cn.mazu.webkit.html.DomElement;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Image;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;
import cn.mazu.workorder.entity.WorkOrder;

public class TabHead extends HtmlTemplate {

	@Bus(value = "tabids")
	public String tabids = "";
	@Bus(value = "selid")
	public String selid = "";
	@Bus(value = "nextid")
	public String nextid = "";

	private String paramStri = "";
	private ArrayList list = new ArrayList();
	private Map<String, Boolean> usedcMap; 

	private Entity entity;
	private String invWStr[] = {"货品名称","货品规格","货品编号","数量","预警值"};
	private String workOrderStr[] = {"工单号","品名","数量","交付日期","下单日期",
			"预计交期","实际交期","出货日期","图面","状态"};
	public TabHead() {
		entity = new Entity();
		usedcMap = (Map)WApplication.getInstance().getBus().get("usedcMap");
	}

	/* (non-Javadoc)
	 * @see cn.mazu.widget.HtmlTemplate#draw(cn.mazu.widget.kit.web.interact.Template, java.lang.String[])
	 */
	@Override
	public Widget draw(final Template parent, String... args) {
		try {
			if (tabids.equals(""))
				tabids = "main,";
			if (selid.equals("")){
				selid = "main";
		    }
			final String[] paramArr = tabids.split(",");
			list.clear();

			for (String s : paramArr) {
				list.add(s);
			}

			String text = "<div class=\"mainbox\">"
					+ "<table class='maintbl' cellpadding=\"0\" cellspacing=\"0\" border=\"0\" height=\"auto\" width=\"1260\">"
					+ "<tbody><tr><td><div class=\"main_tab\">"

					+ "<div class=\"main_tab_left\">"
					+ "<div class=\"main_tab_left1\"></div>"
					+ "<div class=\"main_tab_left2\"></div>" + "</div>"

					+ "<div class=\"main_tab_center\">" + " ${tabheads}"
					+ "  </div>" +

					"<div class=\"main_tab_right\">"
					+ "<div class=\"main_tab_right1\"></div>"
					+ "<div class=\"main_tab_right2\"></div>" + "</div>"

					+ "</div></td></tr></tbody></table>"
					+ "<div class=\"main_bottom\"></div>" + "</div>";

			ContainerWidget tabs = new ContainerWidget() {
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_DIV;
				}
			};
			tabs.setId("tabs");
			tabs.setStyleClass("easyui-tabs");
			tabs.setAttributeValue("style", "width:1240px;height:auto;");
			tabs.setAttributeValue("data-options", "tools:'#p-tools'");

			// ---------
			ContainerWidget tabs_panels = new ContainerWidget() {
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_DIV;
				}
			};
			tabs_panels.setAttributeValue("class", "tabs-panels");

			// -------

			for (int i = 0; i < paramArr.length; i++) {
				paramStri = paramArr[i];
				if (nextid.equals(""))
					nextid = "main";

				ContainerWidget tab = new ContainerWidget() {
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_DIV;
					}
				};
				// ------
				ContainerWidget panel = new ContainerWidget() {
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_DIV;
					}
				};
				panel.setAttributeValue("class", "panel");
				// ------
				if (paramStri.equals("main")) {
					tab.setAttributeValue("title", "主页");
					HtmlTemplate template = new HtmlTemplate();
					String html = "${widgetbox1}${widgetbox3}${widgetbox2}";
					//框子中 图标、标题、内容在此处实现
					WidgetBox wb1 = new WidgetBox(drawStoreList("storWrnId"));
					wb1.setAttributeValue("style", "width:608px");
					Image wicon1 = new Image("images/w_icon1.png");
					template.bindWidget("widgetbox1", wb1.draw(wicon1,"库存预警"));
					//
					WidgetBox wb2 = new WidgetBox(drawOrderList("wokOdId"));
					Image wicon2 = new Image("images/w_icon2.png");
					wb2.setAttributeValue("style", "width:1230px");
					template.bindWidget("widgetbox2", wb2.draw(wicon2,"工单管理"));
					//公司公告
					WidgetBox wb3 = new WidgetBox(drawCompNotice("compNotId"));										
					Image wicon3 = new Image("images/w_icon2.png");
					wb3.setAttributeValue("style", "width:608px");
					template.bindWidget("widgetbox3", wb3.draw(wicon3,"公司公告"));
					
					template.setTemplateText(html, TextFormat.XHTMLUnsafeText);
					tab.addWidget(template);
				} else {
					tab.setAttributeValue("title",
							MainMenu.getMainMenuDesc(paramStri));
					if (paramStri.equals(selid)) {
						tab.setAttributeValue("data-options",
								"closable:true,selected:" + i);
						TabContent tcontent = new TabContent(selid);
						tab.addWidget(tcontent.draw(parent, paramStri));
					} else {
						tab.setAttributeValue("data-options", "closable:true");
					}

					// 点击“关闭小叉”
					final LineEdit closeA = new LineEdit();
					closeA.setId(MainMenu.getMainMenuDesc(paramStri) + "cls");
					closeA.hide();
					closeA.clicked().addListener(this, new Signal.Listener() {
						@Override
						public void trigger() {

							String closeId = closeA.getId();
							String closedesc = closeId.replace("cls", "");
							String closename = MainMenu
									.getMainMenuName(closedesc);
							tabids = tabids.replace(closename + ",", "");
							//在list中减去关闭的标签
							for(int i=0;i<list.size();i++){
								String s = list.get(i).toString();
								if(s.equals(closename))
									list.remove(i);
							}
							if (nextid.equals(closename)) {//关闭的是下一个即将被选中的标签
								int indexm = tabids.indexOf(selid)
										+ tabids.substring(tabids.indexOf(selid)).indexOf(",");//选中标签的终点位置
								int indexe = tabids.lastIndexOf(",");//字符串最后一个位置

								if (indexm < indexe) {//在中间
									int index1n = tabids.indexOf(selid);
									String newstr = tabids.substring(index1n+ selid.length() + 1);
									nextid = newstr.substring(0,newstr.indexOf(","));
								
								}else if (indexm == indexe) {//在末尾
									if(list.size()>2) nextid = list.get(list.size() - 2).toString();
									else nextid = "main";
								} 
							}else if (selid.equals(closename)) {// 如果关闭的正好是选中的，则下次选中该标签的上一个标签；否则选中标签保持不变
								selid = nextid;
								
								int indexm = tabids.indexOf(selid)
										+ tabids.substring(tabids.indexOf(selid)).indexOf(",");//选中标签的位置
								int indexe = tabids.lastIndexOf(",");//字符串最后一个位置

								if (indexm < indexe) {//在中间
									int index1 = tabids.indexOf(selid);
									String newstr = tabids.substring(index1
											+ selid.length() + 1);
									nextid = newstr.substring(0,
											newstr.indexOf(","));
								} else if (indexm == indexe) {//在末尾
									if(list.size()>2) nextid = list.get(list.size() - 2).toString();
									else nextid = "main";
								} else
									nextid = "main";
								
							}
							if(!usedcMap.get(selid))
								WApplication.getInstance().setCookie("dataCode", Util.URLEString("___"),60*60);
							else
								WApplication.getInstance().setCookie("dataCode", WApplication.getInstance().getEnvironment().getCookie("pDataCode"),60*60);
							try {
								String url = "/index_page"
										+ URLEncoder.encode("?tabids=" + tabids
												+ "&selid=" + selid
												+ "&nextid=" + nextid, "UTF-8");
								Util.skipPage(url);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					});
					tabs.addWidget(closeA);
					// 选中标签的时候被触发,选中下拉的菜单时间接触发该事件
					final LineEdit seltab = new LineEdit();
					seltab.setId(MainMenu.getMainMenuDesc(paramStri) + "seltab");
					seltab.hide();
					seltab.clicked().addListener(this, new Signal.Listener() {
						@Override
						public void trigger() {

							try {
								String seltabdesc = seltab.getId().replace(
										"seltab", "");
								String selname = MainMenu
										.getMainMenuName(seltabdesc);
								selid = selname;
								if(!usedcMap.get(selid))
			                    	WApplication.getInstance().setCookie("dataCode",Util.URLEString("___"),60*60);
								else
									WApplication.getInstance().setCookie("dataCode",WApplication.getInstance().getEnvironment().getCookie("pDataCode"),60*60);
								// 判断选中的标签是不是在中间
								int indexm = tabids.indexOf(selid)
										+ tabids.substring(
												tabids.indexOf(selid)).indexOf(
												",");//选中标签的位置
								int indexe = tabids.lastIndexOf(",");//字符串最后一个位置

								if (indexm < indexe) {//在中间
									int index1 = tabids.indexOf(selid);
									String newstr = tabids.substring(index1
											+ selid.length() + 1);
									nextid = newstr.substring(0,
											newstr.indexOf(","));
								} else if (indexm == indexe) {//在末尾
									if(list.size()>2) nextid = list.get(list.size() - 2).toString();
									else nextid = "main";
								} else
									nextid = "main";

								String url = "/index_page"
										+ URLEncoder.encode("?tabids=" + tabids
												+ "&selid=" + selid
												+ "&nextid=" + nextid, "UTF-8");
								Util.skipPage(url);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}

						}
					});
					tab.addWidget(seltab);
				}
				// ----
				panel.addWidget(tab);
				tabs_panels.addWidget(panel);
				// ---
			}
			// ---
			tabs.addWidget(tabs_panels);
			// --
			this.setTemplateText(text, TextFormat.XHTMLUnsafeText);
			this.setStyleClass("main");
			this.bindWidget("tabheads", tabs);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	private Widget drawStoreList(String listId){
		Template t = new Template();
		t.setTemplateText("${table}",TextFormat.XHTMLUnsafeText);
		t.setStyleClass("widbox_content");
		t.setId(listId);
		int max_ = entity.getTotalNumberNative(InventoryWarn.class,
				new Object[] { "InventoryWarnQuery"});
		List<EntityObject> stList = entity.getNavListFromGeneral(InventoryWarn.class,
				new Object[] { "InventoryWarnQuery"}, 0, max_);
		
		ContainerWidget tbList = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_TABLE;
			}
		};
		tbList.setStyleClass("tbList");
		if(stList.size()<1){
			return new Text("暂时没有库存预警");
		}else{
			
			ContainerWidget thead= new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_TR;
				}
			};
			for(int i=0;i<5;i++){
				ContainerWidget th= new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TH;
					}
				};
				th.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
				th.addWidget(new Text(invWStr[i]));
				thead.addWidget(th);
			}
			tbList.addWidget(thead);
			for(EntityObject iw:stList){
				InventoryWarn invW = (InventoryWarn) iw;
				ContainerWidget tr= new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TR;
					}
				};
				String invWAttr[] = {invW.getName(),invW.getSpec(),invW.getCode(),invW.getAmt().toString(),invW.getThreshold().toString()};
				for(int i=0;i<5;i++){
					ContainerWidget td= new ContainerWidget(){
						@Override
						public DomElementType getDomElementType() {
							return DomElementType.DomElement_TD;
						}
					};
					td.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
					td.addWidget(new Text(invWAttr[i]));
					tr.addWidget(td);
				}
				tbList.addWidget(tr);
			}
		}
		t.bindWidget("table", tbList);
		return t;
	}
	
	private Widget drawOrderList(String listId){
		Template t = new Template();
		t.setTemplateText("${table}",TextFormat.XHTMLUnsafeText);
		t.setStyleClass("widbox_content");
		t.setId(listId);
		int max_ = entity.getTotalNumber(WorkOrder.class, new Object[]{"like","dataCode","___","and"});
		List<EntityObject> stList = entity.getSubListQBC(WorkOrder.class,
				new Object[]{"like","dataCode","___","and","equal","wostatus",WorkOrderStatus.ACTIVATED,"and"}, 0, max_);
		ContainerWidget tbList = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_TABLE;
			}
		};
		tbList.setStyleClass("tbList");
		
		if(stList.size()<1){
			return new Text(tr("noActivatedWO"));
		}else{
			
			ContainerWidget thead= new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_TR;
				}
			};
			for(int i=0;i<workOrderStr.length;i++){
				ContainerWidget th= new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TH;
					}
				};
				th.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
				th.addWidget(new Text(workOrderStr[i]));
				thead.addWidget(th);
			}
			tbList.addWidget(thead);
			for(EntityObject iw:stList){
				WorkOrder wo = (WorkOrder)iw;
				ContainerWidget tr= new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TR;
					}
				};
				String woNum = String.valueOf(wo.getAmount());
				Date ddate = wo.getDeliveryDate();
				Date odate = wo.getOrderDate();
				Date pdate = wo.getPredictDate();
				Date rdate = wo.getRealDate();
				Date sdate = wo.getShipDate();
				String woAttr[] = {wo.getOrderno(),
						(wo.getName()!=null&&!wo.getName().equals(""))?wo.getName():"",
						(woNum!=null&&!woNum.equals(""))?woNum:""	,
						(ddate!=null)?Util.formatDateyyyyMMdd(ddate):"",(odate!=null)?Util.formatDateyyyyMMdd(odate):"",
						(pdate!=null)?Util.formatDateyyyyMMdd(pdate):"",(rdate!=null)?Util.formatDateyyyyMMdd(rdate):"",
						(sdate!=null)?Util.formatDateyyyyMMdd(sdate):"",
						(wo.getSurface()!=null)?wo.getSurface().getName():"",wo.getWostatus().getName()};
				for(int i=0;i<workOrderStr.length;i++){
					ContainerWidget td= new ContainerWidget(){
						@Override
						public DomElementType getDomElementType() {
							return DomElementType.DomElement_TD;
						}
					};
					td.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
					td.addWidget(new Text(woAttr[i]));
					tr.addWidget(td);
				}
				tbList.addWidget(tr);
			}
		}
		t.bindWidget("table", tbList);
		return t;
	}
	private Widget drawCompNotice(String listId){
		final Template wbcontent = new Template();
		wbcontent.setId(listId);
		wbcontent.setTemplateText("${ul}${win}",TextFormat.XHTMLUnsafeText);
		wbcontent.setStyleClass("widbox_content");
		
		SitemgEntity smgEntity = new SitemgEntity();
		List<Notice> ntLs = smgEntity.getNoticeList(NoticeType.INNERNT);
		if(ntLs.size()<1){
			wbcontent.bindWidget("ul",new Text("暂时没有内部公告发布"));
		}else{
			ContainerWidget ul = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_UL;
				}			
			};
			ul.setStyleClass("widUl");
			for(final Notice n:ntLs){
				ContainerWidget li = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_LI;
					}			
				};
				Text nTitle = new Text(n.getTitle());
				nTitle.setToolTip(n.getTitle());
				nTitle.setStyleClass("titlesp");
				Text nDate = new Text(Util.formatDateyyyyMMdd(n.getPublishDate()));
				nDate.setStyleClass("datesp");
				li.addWidget(nTitle);
				li.addWidget(nDate);
				
				li.clicked().addListener(this, new Signal.Listener() {
					@Override
					public void trigger() {
						final PopBox win = new PopBox(n.getTitle()
								+"<em style='padding-left:10px;font-style: normal;font-weight: normal;font-size: 12px;'>"
								+Util.formatDateyyyyMMdd(n.getPublishDate())+"</em>"){
							@Override
							public Widget createFormTbl(String title, PopBox wbx) {
								Template innerText = new Template();
								innerText.setStyleClass("innerText");
								innerText.setTemplateText(n.getContent(), TextFormat.XHTMLUnsafeText);
								return innerText;
							}
						};
						wbcontent.bindWidget("win", win);
					}
				});
				ul.addWidget(li);
			}
			wbcontent.bindWidget("ul", ul);
		}
		wbcontent.bindString("win", "");
		return wbcontent;
	}
	@Override
	public void updateDom(DomElement element, boolean all) {
		super.updateDom(element, all);
		element.callJavaScript("$(document).ready(function() {"
				+ "$('#tabs').tabs();" + "$('.easyui-datagrid').datagrid();"
				+ "});");
		/*
		 * Iterator<String> it = tabMap.keySet().iterator();
		 * while(it.hasNext()){ String str = it.next(); TabContent tcontent =
		 * new TabContent();
		 * tabMap.get(str).addWidget(tcontent.draw(parent_,str)); }
		 */
	}
}

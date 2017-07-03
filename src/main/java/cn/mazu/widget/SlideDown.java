package cn.mazu.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cn.mazu.WApplication;
import cn.mazu.annotation.Bus;
import cn.mazu.mysql.Entity;
import cn.mazu.sys.entity.AccountPermission;
import cn.mazu.sys.mvc.ModifyPwdView;
import cn.mazu.util.Util;
import cn.mazu.util.Util.MainMenu;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.composite.popup.Icon;
import cn.mazu.widget.kit.composite.popup.StandardButton;
import cn.mazu.widget.kit.composite.popup.WMessageBox;
import cn.mazu.widget.kit.web.interact.Image;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

public class SlideDown extends HtmlTemplate{
	
	@Bus(value="tabids")
	public String tabids = "";
	@Bus(value="selid")
	public String selid = "";
	@Bus(value="nextid")
	public String nextid = "";
	private WMessageBox messageBox;
	private ArrayList list = new ArrayList();
	private Map<String, Boolean> usedcMap = (Map)WApplication.getInstance().getBus().get("usedcMap");
  
	@Override
    public Widget draw(final Template parent, String... args) {
    	try {
			if(tabids.equals(""))
				tabids = "main,";
			if(selid.equals(""))
				selid = "main";
			if(nextid.equals(""))
				nextid = "main";
			String text = 
					"<div class=\"navigation-up\">${logoimg}" +
							"<div class=\"navigation-inner\">" +
									"${main_ul}"+
								"</div>" +
								"${pswdReset}" +							
								"</div>";
			Image img = new Image("images/logo.png");
			img.setAttributeValue("style", "width:103px;height:54px;margin-left:10px;");
			Anchor logo = new Anchor(new Link(Link.Type.InternalPath,"/index_page"), img);
			this.bindWidget("logoimg", logo);
			ModifyPwdView pswdReset = new ModifyPwdView();
			this.bindWidget("pswdReset", pswdReset.draw(null, null));

			
			this.setTemplateText(text,TextFormat.XHTMLUnsafeText);
			
			ContainerWidget main_ul = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					// TODO Auto-generated method stub
					return DomElementType.DomElement_UL;
				}
			};
			main_ul.setStyleClass("navul");
			//获取所有的二级菜单
			String secMenuArr[] = Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("menustr")).split(",");
			Set<MainMenu> parentM = new TreeSet<MainMenu>();
			for (String menuname:secMenuArr){
				parentM.add(MainMenu.getParentMenuByName(menuname.trim()));	
			}
			for(MainMenu m:parentM){
				ContainerWidget main_li = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						// TODO Auto-generated method stub
						return DomElementType.DomElement_LI;
					}
				};
				main_li.setStyleClass("mli");
				Anchor parnet_a = new Anchor(new Link(""),m.desc);
				parnet_a.setStyleClass("mlia");
				main_li.addWidget(parnet_a);
				List<MainMenu> descM = getSecondMenus(m);
				ContainerWidget desc_ul = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_UL;
					}
				};
				for(final MainMenu mm:descM){
					ContainerWidget desc_li = new ContainerWidget(){
			    		@Override
			    		public DomElementType getDomElementType() {
			    			// TODO Auto-generated method stub
			    			return DomElementType.DomElement_LI;
			    		}
			    	};
			    	Anchor li_a = new Anchor(new Link(""),mm.desc);
			    	li_a.clicked().addListener(this,new Signal.Listener() {
							@Override
							public void trigger() {
								try {
				                 String url;
									//打开标签最多不能超过n个
					                 if(!tabids.contains(mm.name)&&tabids.split(",").length>7){
					                	 messageBox = new WMessageBox("提示框",
													"最多只能打开七个标签！", Icon.Information,
													EnumSet.of(StandardButton.Ok));
					                	 messageBox.buttonClicked().addListener(messageBox,
					         					new Signal.Listener() {
					         						public void trigger() {
					         							if (messageBox != null)
					         								messageBox.remove();
					         						}
					         					});
					                	 messageBox.show();
					                	 return;
					                 }else if(!tabids.contains(mm.name)){
					                	 tabids += mm.name+",";
					                 }
				                    selid = mm.name;
				                    if(!usedcMap.get(selid))
				                    	WApplication.getInstance().setCookie("dataCode", Util.URLEString("___"),60*60);
				                    else
										WApplication.getInstance().setCookie("dataCode",WApplication.getInstance().getEnvironment().getCookie("pDataCode"),60*60);
									
				                    final String[] paramArr = tabids.split(",");
				                    list.clear();
				            		for(String s:paramArr){
				            			list.add(s);
				            		}
				                    
				                    if(tabids.split(",").length>1){
				                    	int indexm= tabids.indexOf(selid)+tabids.substring(tabids.indexOf(selid)).indexOf(",");
				                    	int indexe = tabids.lastIndexOf(",");
				                    	if(indexm<indexe){
				                    		int index1 = tabids.indexOf(selid);
				                    		String newstr = tabids.substring(index1+selid.length()+1);
				                    		nextid = newstr.substring(0, newstr.indexOf(","));
			                    		 }else if(indexm==indexe){
											nextid = list.get(list.size()-2).toString();
										}else{
				                    		nextid = "main";
				                    	}
				                    }
				                
									url = "/index_page"+URLEncoder.encode("?tabids="+tabids+"&selid="+selid+"&nextid="+nextid,"UTF-8");
									Util.skipPage(url);								
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							
						}
					  });
			    	desc_li.addWidget(li_a);
			    	desc_ul.addWidget(desc_li);
				}
				main_li.addWidget(desc_ul);
				main_ul.addWidget(main_li);
			}
			this.bindWidget("main_ul",main_ul);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return this;
    }
    //获取该父级菜单下授权的二级菜单集合
	private Entity entity = new Entity();
	private AccountPermission ap  = entity.getCurrentAP();
    private List<MainMenu> getSecondMenus(MainMenu p){
    	List<MainMenu> secMenuList = new ArrayList<MainMenu>();
    	if(ap!=null){
    		String[] menuPerArr = ap.getMenuPermission().split(";");
    		usedcMap.put("main",false);
    		for (String menuPer:menuPerArr){
    			if(MainMenu.getParentMenuByDesc(menuPer.split(":")[0]).name.equals(p.name)){
    				secMenuList.add(MainMenu.getMainMenuByDesc(menuPer.split(":")[0]));
    				if(menuPer.split(":")[1].contains(tr("usedc")))
    					usedcMap.put(MainMenu.getMainMenuByDesc(menuPer.split(":")[0]).name, true);
    				else
    					usedcMap.put(MainMenu.getMainMenuByDesc(menuPer.split(":")[0]).name, false);
    			}
    		}
    	}
    	return secMenuList;
    }
    
}

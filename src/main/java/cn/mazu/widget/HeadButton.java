package cn.mazu.widget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import cn.mazu.util.Util;
import cn.mazu.utils.WLength;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.servlet.WebRequest;
import cn.mazu.webkit.servlet.WebResponse;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Resource;
import cn.mazu.widget.kit.composite.popup.Icon;
import cn.mazu.widget.kit.composite.popup.StandardButton;
import cn.mazu.widget.kit.composite.popup.WMessageBox;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;

/*表格上面的那一排按钮
    显示什么按钮有权限控制
    所以就把所有按钮先都列出在那里，按照权限进行添加*/
public class HeadButton extends HtmlTemplate {
	private Template window = new Template("${win1}");
	private String purid = "";
	private String title;
	private Object obj;
	private WebResponse resp;

	//private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private List<String> authorityList = new ArrayList<String>();

	public HeadButton(String title,String authorityStr){
		this.title = title;
		String[] authorityArr = authorityStr.split(",");
		//String[] authorityArr = getBtnStr().split(",");
		for (String s:authorityArr)
			authorityList.add(s);
	}
	
	/*public HeadButton(String title){
		//String selmenuname = ((Text)getChildWidget("selid")).getText().toString();
		//this(title,"filtrate,view,create,edit,del");
		this(title, getMenuPermission());
//		System.out.println("selid in constructor:"+selid);
	}*/
	
	@Override
	public Widget draw(Template parent, String... args) {
    	String text = "<div class='box auto_height'>"+
    			"	  <div class='box_two'>"+
    			"		<div class='title'>"+this.title+"</div>"+
    			"		<div class='line'></div>"+
    			"			<div id='tb' class='button_calendar'>"+
    			"				<div class='button'>"+
    			"${filtrate}${newView}${newAdd}${newEdit}${newDel}${displayall}${displaySingle}${hideone}${return}${config}${export}${doEnd}"+
    			//"${addupxc}${addupmc}${addupfd}${addupcnc}" +            //筛选、查看、新建，编辑,删除，导出//${displayall}${hideone}
    			"${newIn}${newOut}"+
    			"</div>"+
    			"			</div>"+		    			
    			"	  </div>${win}"+
    			" 	</div>";
    	this.bindString("win", "");
		
		this.setTemplateText(text, TextFormat.XHTMLUnsafeText);
		window.bindString("win1", "");
		this.bindWidget("win", window);
		this.bindString("filtrate", "");
		this.bindString("newView", "");
		this.bindString("newAdd", "");
		this.bindString("newEdit", "");
		this.bindString("newDel", "");
		this.bindString("hideone","");
		this.bindString("displayall", "");
		this.bindString("displaySingle", "");
		this.bindString("return", "");
		this.bindString("config", "");
		this.bindString("export", "");
		this.bindString("doEnd", "");
		/*this.bindString("addupxc", "");
		this.bindString("addupmc", "");
		this.bindString("addupfd", "");
		this.bindString("addupcnc", "");*/
		
		this.bindString("newIn", "");
		this.bindString("newOut", "");
		drawButton();//把所有的按钮都画出来
    	return this;
    }			
		    
    protected void drawButton(){
    	//String authorityStr = getBtnStr();
    	/*String[] authorityArr = getBtnStr().split(",");
    	authorityList.clear();*/
    	/*for (String s:authorityArr)
			authorityList.add(s);*/
    	for (String authstr:authorityList){
    		final Anchor alink = new Anchor();
    		final Text whTxt = new Text();
    		Template iconsp = new Template();
        	iconsp.setStyleClass("icon");
        	alink.addWidget(iconsp);
    		//if(authstr.equals("filtrate")){
        	if(authstr.equals("筛选")){
    			alink.setText("筛选");
        		alink.setStyleClass("mbutton select");
        		this.bindWidget("filtrate", alink);
    		}
    		//if(authstr.equals("view")){
        	if(authstr.equals("查看")){
    			alink.setText("查看");
        		alink.setStyleClass("mbutton check");
        		this.bindWidget("newView", alink);
    		}
    		//if(authstr.equals("create")){
        	if(authstr.equals("新建")){
    			alink.setText("新建");
        		alink.setStyleClass("mbutton add");
        		this.bindWidget("newAdd", alink);
    		}
    		//if(authstr.equals("edit")){
        	if(authstr.equals("编辑")){
    			alink.setText("编辑");
        		alink.setStyleClass("mbutton edit");
        		this.bindWidget("newEdit", alink);
    		}
    		//if(authstr.equals("del")){
        	if(authstr.equals("删除")){
    			alink.setText("删除");
        		alink.setStyleClass("mbutton delete");
        		this.bindWidget("newDel", alink);
    		}
    		//if(authstr.equals("displayall")){
        	if(authstr.equals("显示全部")){
    			alink.setText("显示全部");
    			alink.setStyleClass("mbutton open");
    			this.bindWidget("displayall", alink);
    		}
    		//if(authstr.equals("hideone")){
        	if(authstr.equals("隐藏")){
    			alink.setText("隐藏");
    			alink.setStyleClass("mbutton close");
    			this.bindWidget("hideone", alink);
    		}
    		//if(authstr.equals("return")){
        	if(authstr.equals("返回")){
    			alink.setText("返回");
    			alink.setStyleClass("mbutton back");
    			this.bindWidget("return", alink);
    		}
    		//if(authstr.equals("config")){
        	if(authstr.equals("设置工单")){
    			alink.setText("设置工单");
    			alink.setStyleClass("mbutton config");
    			this.bindWidget("config", alink);
    		}
    		//if(authstr.equals("export")){
        	if(authstr.equals("导出")){
    			alink.setText("导出");
    			alink.setStyleClass("mbutton derive");
    			this.bindWidget("export", alink);
    		}
            //if(authstr.equals("doEnd")){
        	if(authstr.equals("完结")){
            	alink.setText("完结");
            	alink.setStyleClass("mbutton finish");
            	this.bindWidget("doEnd", alink);
            }
    		
    		//if(authstr.equals("newIn")){
        	if(authstr.equals("新建入库")){
    			alink.setText("新建入库");
    			alink.setStyleClass("mbutton storeIn");
    			this.bindWidget("newIn", alink);
    		}
    		//if(authstr.equals("newOut")){
        	if(authstr.equals("新建出库")){
    			alink.setText("新建出库");
    			alink.setStyleClass("mbutton storeOut");
    			this.bindWidget("newOut", alink);
    		}
        	
    		alink.clicked().addListener(this, new Signal.Listener() {//编辑，新建，查看归并为一类
    			@Override
    			public void trigger() {
    				if(alink.getText().toString().equals("筛选")){
    					PopBox filterwin = new PopBox(alink.getText().toString()){
    						@Override
    						public Widget createFormTbl(String title,
    								PopBox wbx) {
    							return generateFilterForm(title,wbx);
    						}
    					};
    					window.bindWidget("win1", filterwin);
    				}else if(alink.getText().toString().equals("返回")){
    					drawPreviousTbl();
    				}else if(alink.getText().toString().equals("显示全部")){
    					drawAllData();
    				}else if(getDeliveryObj()==null&&(alink.getText().toString().equals("隐藏")
    						||alink.getText().toString().equals("编辑")||alink.getText().toString().equals("查看")
    						||alink.getText().toString().equals("删除")||alink.getText().toString().equals("设置工单")
    						||alink.getText().toString().equals("导出")||alink.getText().toString().equals("完结"))){
    					Util.infoMessage(tr("Information Please").toString(), "请选中一条记录");
    				}else if(getDeliveryObj()!=null&&alink.getText().toString().equals("删除")){
    					final WMessageBox msbox = new WMessageBox(tr("Information Please").toString(),"确认删除这条记录？",Icon.Information,EnumSet.of(StandardButton.Ok));
    					msbox.setWidth(new WLength(200));
    					PushButton cancel = new PushButton("取消");
    					cancel.setAttributeValue("style", "margin-left:30px;");
    					msbox.addButton(cancel, null);
    					cancel.clicked().addListener(null,	new Signal.Listener() {
    						@Override
    						public void trigger() {
    							if (msbox != null)
    								msbox.remove();
    						}
    					});
    					msbox.buttonClicked().addListener(null,new Signal.Listener() {
    						public void trigger() {
    							removeFromTbl();
    							
    							if (msbox != null)
    								msbox.remove();
    						}
    					});
    			    	msbox.show();
    					
    				}else if(getDeliveryObj()!=null&&alink.getText().toString().equals("隐藏")){
    					hideSelectedData();
    				}else if(getDeliveryObj()!=null&&alink.getText().toString().equals("设置工单")){
    					PopBox win0 = new PopBox(alink.getText().toString()){
    						@Override
    						public Widget createFormTbl(String title,
    								PopBox wbx) {
    							return configWorkOrder(title,wbx);
    						}
    					};
    					window.bindWidget("win1", win0);
    				}else if(getDeliveryObj()!=null&&alink.getText().toString().equals("导出")){
    					alink.setResource(new Resource() {
    						@Override
    						protected void handleRequest(WebRequest request, WebResponse response)
    								throws IOException {
    							exportDoc(response);
    						}
    					});	
    				}else if(alink.getText().toString().equals("完结")){
    					endSel();
    				}else{
    					PopBox win0 = new PopBox(alink.getText().toString()){
    						@Override
    						public Widget createFormTbl(String title,
    								PopBox wbx) {
    							return generateSimpleForm(title,wbx);
    						}
    					};
    					window.bindWidget("win1", win0);
    				}
    			}
    		});
    	}
	}

    public Widget generateSimpleForm(String title,PopBox pb){//一般性新增、编辑，查看，新建入库，新建出库
    	return null;
    }
    
    public Widget generateFilterForm(String title,PopBox pb){//筛选
    	return null;
    }
    /*public void drawUplayer(){		    
    	System.out.println("hello");
    }*/
    
	public String getPurid() {
		return purid;
	}
	public void setPurid(String purid) {
		this.purid = purid;
	}
	public Widget crateNewForm(){
		return null;
	}
	/*//点击过滤按钮
	public Widget generateFiltrate(){
		return null;
	}
	//点击新建按钮
	public Widget generateNew(PopBox wbx){
		return null;
	}
	//点击编辑
	public Widget generateEdit(PopBox wbx){
		return null;
	}
	public Widget generateView(PopBox wbx){
		return null;
	}*/
	//delivery the selected obj
	public void setDeliveryObj(Object obj){
		this.obj = obj;
	}
	public Object getDeliveryObj(){
		return this.obj;
	}
	public void removeFromTbl(){
	};
	//隐藏选中的记录
	public void hideSelectedData(){}
	//显示全部
	public void drawAllData(){}
	//画之前的表格，即【返回】
	public void drawPreviousTbl(){
	};
	//设置工单
	public Widget configWorkOrder(String title,PopBox pb){
		return null;
	}
	//导出工单
	public Object exportDoc(WebResponse resp){
		return null;
	}
	public void endSel(){
		
	}
	
}

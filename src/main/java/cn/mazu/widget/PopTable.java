package cn.mazu.widget;

import java.util.List;

import cn.mazu.WApplication;
import cn.mazu.util.Util;
import cn.mazu.utils.AlignmentFlag;
import cn.mazu.webkit.html.DomElement;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.script.JavaScriptObjectType;
import cn.mazu.webkit.script.JavaScriptScope;
import cn.mazu.webkit.script.WJavaScriptPreamble;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.CheckBox;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

public class PopTable extends HtmlTemplate {
//	private Template this = new Template();
	public static String treaid = "";
	public PopTable(){
//		initJs();
		this.doJavaScript("$('#dga').datagrid();");
	}

	@Override
	public Widget draw(Template parent, String... args) {
		    	String text = 
		    			""+
		    			"			<table id='dga' class='easyui-datagrid' width='auto' "+
		    			"			data-options=\"rownumbers:false,pagination:false,fitColumns:false,singleSelect:true,method:'get',striped:true,left:'150px',top:'20px'\">"+
		    			"            <thead>"+
		    			"              <tr>"+
		    			"<th data-options=\"field:'Fa',checkbox:true,align:'center'\">操作</th>"+
		    			"                <th data-options=\"field:'Fe',width:$(this).width() * 0.15,align:'center'\" >上传时间</th>"+
		    			"                <th data-options=\"field:'Ff',width:$(this).width() * 0.15,align:'center'\" >下载次数</th>"+
		    			"                <th data-options=\"field:'Fb',width:$(this).width() * 0.15,align:'center'\">素材名称</th>"+
		    			"                <th data-options=\"field:'Fc',width:$(this).width() * 0.15,align:'center'\">素材描述</th>"+
		    			"                <th data-options=\"field:'Fd',width:$(this).width() * 0.15,align:'center'\">素材分类</th>"+
		    			"              </tr>"+
		    			"            </thead><tbody>" +
		    			"<tr><td></td><td>11</td><td>12</td><td>13</td><td>14</td><td>15</td></tr>"+
		    			"<tr><td></td><td>21</td><td>22</td><td>23</td><td>24</td><td>25</td></tr>"+
		    			"<tr><td></td><td>31</td><td>32</td><td>33</td><td>34</td><td>25</td></tr>"+
//		    			"${tbody}"+
		    			"             </tbody> "+
		    			"          </table>"+
		    			"	  ";
				/*TreasureEntity treaEntity = new TreasureEntity();
				List<Treasure> trlist = treaEntity.getAllTrea();
				ContainerWidget tbody = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TBODY;
					}
				};
				if(trlist.size()==0){
					System.out.println("trlist.size=====000000------------");
				}else{
					//for(Treasure t:trlist){
					for(int j=0;j<trlist.size();j++){
						final Treasure t = trlist.get(j);
						final ContainerWidget tr = new ContainerWidget(){
							@Override
							public DomElementType getDomElementType() {
								return DomElementType.DomElement_TR;
							}
						};
						tr.setId("seltrr"+j);
						tr.clicked().addListener(this, new Signal.Listener() {
							@Override
							public void trigger() {
							  System.out.println("selected id is:"+t.getId());
							  treaid = t.getId();
							}
						});
						for(int i=0;i<6;i++){
							ContainerWidget td = new ContainerWidget(){
								@Override
								public DomElementType getDomElementType() {
									return DomElementType.DomElement_TD;
								}
							};
							td.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
							if(i==1){
								Template tcont = new Template(t.getTreasureName()==null||t.getTreasureName().equals("")?"":t.getTreasureName());
								td.addWidget(tcont);
							}else if(i==2){
								Template tcont = new Template(t.getTreasureDesc()==null||t.getTreasureDesc().equals("")?"":t.getTreasureDesc());
								td.addWidget(tcont);
							}else if(i==3){
								Template tcont = new Template(t.getTreasureTag()==null||t.getTreasureTag().equals("")?"":t.getTreasureTag().name);
								td.addWidget(tcont);
							}else if(i==4){
								Template tcont = new Template(Util.formatDateyyyyMMdd(t.getGenerateDate()));
								td.addWidget(tcont);
							}else if(i==5){
								Template tcont = new Template(t.getDowncount().toString());
								td.addWidget(tcont);
								CheckBox ck = new CheckBox();
								ck.setText("kin");
								
								CheckBox ck2 = new CheckBox();
								ck2.setText("kin2");
								
								td.addWidget(ck);
								td.addWidget(ck2);
							}
							
							tr.addWidget(td);
						}
						tbody.addWidget(tr);
					}
				}
				this.bindWidget("tbody", tbody);*/
				this.setTemplateText(text, TextFormat.XHTMLUnsafeText);
		    					return this;
		    }
	/*@Override
	public void updateDom(DomElement element, boolean all) {
		super.updateDom(element, all);
		element.callJavaScript("$(document).ready(function() {" +
				"$('#dga').datagrid();" +
	            "});");
	}*/
}

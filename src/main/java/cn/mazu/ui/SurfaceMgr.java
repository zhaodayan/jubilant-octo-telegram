package cn.mazu.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.mazu.WApplication;
import cn.mazu.base.entity.GeneralElement;
import cn.mazu.drawing.entity.Drawing;
import cn.mazu.mysql.Entity;
import cn.mazu.surface.entity.Surface;
import cn.mazu.surface.entity.SurfaceDetail;
import cn.mazu.surface.mvc.SurfaceForm;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.WidgetUtil;
import cn.mazu.utils.Side;
import cn.mazu.utils.WLength;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PaginationWidget;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.form.SelectionBox;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;
import cn.mazu.widget.validator.DoubleValidator;
import cn.mazu.widget.validator.IntValidator;

public class SurfaceMgr extends MiddleTpl {
	private Entity dbEntity;
	private String gecode = "", surfacecode = "";
	private Map<String,Widget> detailCodeMap = new HashMap<String,Widget>();
	private HeadButton hb;
	private Surface selSurface;
	

	public SurfaceMgr() {
		super("cn.mazu.surface.entity.Surface", new Object[]{"like","dataCode",
				 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
		dbEntity = getDBEntity();
	}

	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		hb = getHeadButton();
		selSurface = hb.getDeliveryObj() == null ? new Surface()
				: (Surface) hb.getDeliveryObj();
		return new SurfaceForm(title, selSurface, p, SurfaceMgr.this).draw(getMyParent(),
				null);
	}

	// field,field字段;fieldVal,get方法得到的field的值
	@Override
	public Widget generateDetailWidget(Field field, Map fMap,EntityObject deo,EntityObject eo) {
		Object fieldVal = null;
		fieldVal = WidgetUtil.getValFromProperty(deo, field);
		final EntityObject fdeo = deo;
		surfacecode = ((Surface) getHeadButton().getDeliveryObj()).getCode().toString();
		Widget w = null;
		if (field.getName().equals("itemName")
				|| field.getName().equals("description")
				|| field.getName().equals("material")
				|| field.getName().equals("drawing")) {
			w = new LineEdit();
			w.setAttributeValue("style",
					"background: #f1f2f3;text-align: center;");
			((LineEdit) w).setText(fieldVal == null ? "" : fieldVal.toString());
		}else if(field.getName().equals("amount")||field.getName().equals("itemNo")){
			w = new LineEdit();
			w.setAttributeValue("style",
					"background: #f1f2f3;text-align: center;width:20px;");
			DoubleValidator iv = new DoubleValidator();
			((LineEdit)w).setValidator(iv);
			((LineEdit)w).setMaxLength(7);
			((LineEdit) w).setText(fieldVal == null ? "0" : fieldVal.toString());
			
		} else if (field.getName().equals("generalElement")) {
			GeneralElement sd = ((SurfaceDetail)deo).getGeneralElement();
			String code = sd ==null?"选择零件":sd.getName()+"-"+sd.getCode();
			Anchor a = new Anchor(new Link(""), code);
			a.setStyleClass("aclick ");
			a.clicked().addListener(this, new Signal.Listener() {
				@Override
				public void trigger() {
					PopBox win = new PopBox("选择零件及图纸") {
						@Override
						public Widget createFormTbl(String title, PopBox wbx) {
							try {
								return createSelectDrawingForm(wbx,fdeo);
							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}
						}
					};
					SurfaceMgr.this.bindWidget("win1", win);
					drawGrid();
					SurfaceMgr.this.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+getPaginationStart()*getPaginationMax()+"});");
				}
			});
			w = a;
			detailCodeMap.put(deo.id+"-a",a);
		} else if (field.getName().equals("detailcode")) {
			LineEdit detailCodeLE = new LineEdit();
			detailCodeLE.setAttributeValue("style",
					"background: #f1f2f3;text-align: center;");
			detailCodeLE.setText(fieldVal == null ? "": fieldVal.toString());
			w = detailCodeLE;
			detailCodeMap.put(deo.id,detailCodeLE);
		}
		fMap.put(field, w);
		return w;
	}
	
	private SelectionBox select_drawing = new SelectionBox();//选中某一零件后，该零件下的所有图纸
	private SelectionBox select_widget = new SelectionBox();//零件下拉框
	private LineEdit select_widget_input = new LineEdit();
	private LineEdit select_drawing_input = new LineEdit();
	private Map<String,GeneralElement> generalElementMap = new HashMap<String, GeneralElement>();
	private List<EntityObject> elements = getDBEntity().getSubListQBC(GeneralElement.class,new Object[]{"like","dataCode",
		 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"},-1,-1);
	private Widget createSelectDrawingForm(final PopBox box,final EntityObject deo) {
		final Template tpl = new Template();
		String ttpl = "<div class=\"tcontainer\"><table class=\"form_tb\">"
				+ "<tr><td>名称</td><td>${select_widget_input}${filterW}</td>"
				+ "<td></td><td>${select_drawing_input}</td></tr>"
				+ "<tr>"
				+ "<td>零件</td><td>${select_widget}</td>"
				+ "<td>图纸或规格</td><td>${select_drawing}</td>"
				+ "</tr></table></div><div class=\"box_button\">${saveBtn}</div>";
		tpl.setTemplateText(ttpl, TextFormat.XHTMLUnsafeText);
		generalElementMap.clear();
		select_widget.clear();
		String code = "";
		if(((SurfaceDetail)deo).getGeneralElement()!=null)
			code = ((SurfaceDetail)deo).getGeneralElement().getName()+"-"+((SurfaceDetail)deo).getGeneralElement().getCode();
		int index = 0;
		select_widget.setCurrentIndex(index);
		for (EntityObject eo : elements) {
			GeneralElement el = (GeneralElement)eo;
			select_widget.addItem(el.getName() + "-" + el.getCode());
			generalElementMap.put(el.getCode(), el);
			if((el.getName() + "-" + el.getCode()).equals(code))
				select_widget.setCurrentIndex(index);
			index++;
		}
		select_widget.setMargin(new WLength(10), EnumSet.of(Side.Right));
		select_widget.activated().addListener(this, new Signal.Listener() {
			public void trigger() {
				initSelectDrawing(tpl,deo);
				select_widget_input.setValueText(select_widget.getCurrentText()
						.toString());
			}
		});
		initSelectDrawing(tpl,deo);
		PushButton filterW = new PushButton(tr("search"));
		filterW.setStyleClass("editcls");
		filterW.setAttributeValue("style", "vertical-align:top");
		filterW.clicked().addListener(this,new Signal.Listener() {
			@Override
			public void trigger() {
				select_widget.clear();
				String txtInput = select_widget_input.getText().trim();
				elements = getDBEntity().getSubListQBC(GeneralElement.class,new Object[]{
					       "like","dataCode",
					       Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and",
					       "like","name","%"+txtInput+"%","and","like","code","%"+txtInput+"%","or"},-1,-1);
				for (EntityObject eo : elements) {
					GeneralElement el = (GeneralElement)eo;
					select_widget.addItem(el.getName() + "-" + el.getCode());
					generalElementMap.put(el.getCode(), el);
					
				}
			}
		});
		tpl.bindWidget("filterW", filterW);
		tpl.bindWidget("select_widget", select_widget);
		tpl.bindWidget("select_widget_input", select_widget_input);
		tpl.bindWidget("select_drawing_input", select_drawing_input);
		Anchor saveBtn = new Anchor(new Link(""), "保存");
		saveBtn.setStyleClass("close");
		tpl.bindWidget("saveBtn", saveBtn);
		saveBtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				box.setAttributeValue("style", "display:none");
				String select_widget_code = select_widget_input.getValueText().toString();
				String select_drawing_code = select_drawing_input.getValueText().toString();
				((LineEdit)detailCodeMap.get(deo.getId())).setValueText(select_drawing_code);
				((Anchor)detailCodeMap.get(deo.getId()+"-a")).setText(select_widget_code);
			}
		});
		return tpl;
	}

	private void initSelectDrawing(final Template tpl,final EntityObject deo) {
		select_drawing = new SelectionBox();
		select_drawing.setCurrentIndex(0);
		String[] arr = select_widget.getCurrentText().toString().split("-");
		//System.out.println("select_widget:"+select_widget.getCurrentText());
		
		//GeneralElement data = generalElementMap.get(arr.length==1?arr[0]:(arr.length==2?arr[1]:(arr.length==3?arr[2]:"")));
		
		if (arr.length >= 2) {
			String detailCode = ((SurfaceDetail)deo).getDetailcode();
			String code = arr.length==2?arr[1]:(arr.length==3?arr[2]:"");
			//System.out.println("code:"+code);
			GeneralElement ge = generalElementMap.get(code);
			//GeneralElement ge = (GeneralElement)generalElementMap.get(arr[1]);
			int index = 0;
			for (Drawing drawing :getDBEntity().getDrawingOfWidget(ge)) {
				select_drawing.addItem(drawing.getCode());
				if(detailCode.equals(drawing.getCode()))
						select_drawing.setCurrentIndex(index);
				index++;
			}
			//System.out.println("index:"+index);
			select_drawing.setMargin(new WLength(10), EnumSet.of(Side.Right));
			select_drawing.activated().addListener(null, new Signal.Listener() {
				public void trigger() {
					select_drawing_input.setValueText(select_drawing
							.getCurrentText().toString());
				}
			});
		}
		select_widget_input.setValueText(select_widget.getCurrentText()
				.toString());
		select_drawing_input.setValueText(select_drawing.getCurrentText()
				.toString());
		tpl.bindWidget("select_drawing", select_drawing);
	}

	@Override
	public boolean setPropertyFromWidget(EntityObject deo, Map fMap,EntityObject eo) {
		//try {
			for (Iterator iterator = fMap.entrySet().iterator(); iterator
					.hasNext();) {
				Map.Entry<Field, FormWidget> fw = (Entry<Field, FormWidget>) iterator
						.next();
				Field setField = fw.getKey();
				Object setWidget = fw.getValue();
                Object setval = null;
				if (setField.getType().getName().equals("java.lang.String")) {
					setval = ((LineEdit) setWidget).getText();
				} else if (setField.getType().getName().equals("java.lang.Short")) {
					Short d = Short.valueOf(((LineEdit) setWidget).getText());
					setval = d;
				} else if (setField.getType().getName().equals("java.lang.Double")) {
					Double d = Double.valueOf(((LineEdit) setWidget).getText());
					setval = d;
				}else if (setField.getType().getName()
						.equals("cn.mazu.base.entity.GeneralElement")) {
					 String[] code = ((Anchor)detailCodeMap.get(deo.getId()+"-a")).getText().toString().split("-");
					//GeneralElement data =  generalElementMap.get(code.length>1?code[1]:code[0]);
					GeneralElement data = generalElementMap.get(code.length==1?code[0]:(code.length==2?code[1]:(code.length==3?code[2]:"")));
					setval = data;
				}
				WidgetUtil.setValForProperty(deo, setField, setval);
			}
			WidgetUtil.setValForProperty(deo,eo.getClass().getSimpleName(),eo);
			return dbEntity.saveOrUpdate(deo);
	}
    @Override
	public boolean validatePassed(Map fMap,EntityObject deo){
    	//清理上次的提示
		boolean retValue = true;
		for (Iterator iterator = fMap.entrySet().iterator(); iterator
				.hasNext();) {
			Map.Entry<Field, FormWidget> fw = (Entry<Field, FormWidget>) iterator
					.next();
			Field setField = fw.getKey();
			Object setWidget = fw.getValue();
			
			if (setField.getType().getName().equals("java.lang.String")&&!setField.getName().equals("material")) {
				Text vinfo = new Text();
				vinfo.setAttributeValue("style", "color:red");
				ContainerWidget cw = (ContainerWidget)((LineEdit) setWidget).getParent();
				if(cw.getChildren().size()-1!=cw.getIndexOf(((LineEdit) setWidget))){
					Widget previousInfo = cw.getWidget(cw.getIndexOf(((LineEdit) setWidget))+1);
					//previousInfo.setParent(null);//第一次判断时是没有提示信息组件的，清理上次的提示信息
					cw.removeWidget(previousInfo);
				}
				if(((LineEdit) setWidget).getText().trim().equals("")){//图号可以为不填
					vinfo.setText(tr("is null"));
					cw.insertWidget(cw.getIndexOf(((LineEdit) setWidget))+1, vinfo);
					retValue = false;
				}
			}else if(setField.getType().getName().equals("java.lang.Short")){
				Text vinfo = new Text();
				vinfo.setAttributeValue("style", "color:red");
				ContainerWidget cw = (ContainerWidget)((LineEdit) setWidget).getParent();
				if(cw.getChildren().size()-1!=cw.getIndexOf(((LineEdit) setWidget))){
					Widget previousInfo = cw.getWidget(cw.getIndexOf(((LineEdit) setWidget))+1);
					//previousInfo.setParent(null);//第一次判断时是没有提示信息组件的，清理上次的提示信息
					cw.removeWidget(previousInfo);
				}
				Integer d = Integer.valueOf(((LineEdit) setWidget).getText());
				if(d==0){//为空
					vinfo.setText(tr("is null"));
					retValue = false;
				}else if(d<Short.MIN_VALUE){
					vinfo.setText(tr("too small"));
					retValue = false;
				}else if(d>Short.MAX_VALUE){
					vinfo.setText(tr("too large"));
					retValue = false;
				}
				cw.insertWidget(cw.getIndexOf(((LineEdit) setWidget))+1, vinfo);
			}else if(setField.getType().getName().equals("cn.mazu.base.entity.GeneralElement")){
				Text vinfo = new Text();
				vinfo.setAttributeValue("style", "color:red");
				 String name = ((Anchor)detailCodeMap.get(deo.getId()+"-a")).getText().toString().trim();
				 ContainerWidget cw = (ContainerWidget)((Anchor)detailCodeMap.get(deo.getId()+"-a")).getParent();
				 if(cw.getChildren().size()-1!=cw.getIndexOf((Anchor)detailCodeMap.get(deo.getId()+"-a"))){
						Widget previousInfo = cw.getWidget(cw.getIndexOf((Anchor)detailCodeMap.get(deo.getId()+"-a"))+1);
						cw.removeWidget(previousInfo);
					}
				 if(name.equals("")||name.equals("选择零件")){
					 vinfo.setText(tr("is null"));
						retValue = false;
				 }
				 cw.insertWidget(cw.getIndexOf((Anchor)detailCodeMap.get(deo.getId()+"-a"))+1, vinfo);
			}
		}
		return retValue;
	}
}

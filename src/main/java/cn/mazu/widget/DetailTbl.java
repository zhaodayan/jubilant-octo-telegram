/*package cn.mazu.widget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.mazu.annotation.DisplayField;
import cn.mazu.mysql.Entity;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.utils.AlignmentFlag;
import cn.mazu.utils.WLength;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.composite.popup.Icon;
import cn.mazu.widget.kit.composite.popup.StandardButton;
import cn.mazu.widget.kit.composite.popup.WMessageBox;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

弹出的明细-合同管理，图面管理

public class DetailTbl extends MiddleTpl {
	private Entity dbEntity;
	private EntityObject deo;
	private List<Field> detailFieldList;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public DetailTbl() {
		dbEntity = getDBEntity();
	}
    @Override
    protected Widget generateDetailForm(final String title, final PopBox wbx,final EntityObject eo, String dename) {
	   String htmlText  = "<div class=\"tcontainer\"><table class='detailtb'>" +
	                      "${thead}"+
	                      "${tbody}</table></div>${box_button}";
	Template formtbl = new Template();
	formtbl.setTemplateText(htmlText, TextFormat.XHTMLUnsafeText);
	//绑定表头
	formtbl.bindWidget("thead", getDetailHead(dename));
	
	Template box_button = new Template();		
	box_button.setStyleClass("box_button");
	box_button.setTemplateText("${cancelbtn}", TextFormat.XHTMLUnsafeText);
	//找到明细类中引用的实体主类的属性名称
	Field[] df_ar = deCls.getFields();
	//对Arrays.asList(df_ar)又包装一层new ArrayList,若不包装，直接在下面removeAll，由于df_ar作为数组长度一定，Arrays.asList(...)后长度仍定，所以要多套一层
	List<Field> detailAllFieldList = new ArrayList<Field>(Arrays.asList(df_ar));
	detailAllFieldList.removeAll(detailFieldList);
	Object[] objDetailArr = new Object[]{"1","1"};
	for (Field f:detailAllFieldList){
		 if(eo.getClass().isAssignableFrom(f.getType())){
			 objDetailArr[0] = f.getName()+".id";
			 objDetailArr[1] = eo.getId();
			 logger.error("objDetailArr[0]:"+objDetailArr[0]+"\t objDetailArr[1]:"+objDetailArr[1]);
			 break;
		 }
	}
	List<EntityObject> detailList = dbEntity.getSubListHQL(deCls, objDetailArr, -1, -1);//查询明细类的记录
	ContainerWidget tbody = new ContainerWidget(){//模板体
		@Override
		public DomElementType getDomElementType() {
			return DomElementType.DomElement_TBODY;
		}
	};
  for (int i=0;i<=detailList.size();i++){//逐行显示
	final Map<Field,FormWidget> fieldWMap = new HashMap<Field,FormWidget>();
	final ContainerWidget tr = new ContainerWidget(){
		@Override
		public DomElementType getDomElementType() {
			return DomElementType.DomElement_TR;
		}
	};

	try {
		deo = (EntityObject) (detailList.size()==i?deCls.newInstance():detailList.get(i));//操作或顯示的明細實體
	} catch (Exception e1) {
		e1.printStackTrace();
	} //取到当前明细，若无则新建
	for(int j=0;j<detailFieldList.size();j++){//逐列显示
		final Field detailField = detailFieldList.get(j);//取到字段field
		
		ContainerWidget td = new ContainerWidget(){//画一个td
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_TD;
			}
		};
		td.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);//设置td的对齐方式
		if(((DisplayField)detailField.getAnnotation(DisplayField.class)).cname().equals("操作")){//操作字段的内容是【保存】和【删除】两个按钮
			Template tcont = new Template();
			tcont.setTemplateText("${savebtn}${deletebtn}", TextFormat.XHTMLUnsafeText);
			PushButton saveBtn = new PushButton("保存");
			saveBtn.setAttributeValue("style", "margin-right:10px;");
			saveBtn.setStyleClass("pushbutton");
			saveBtn.clicked().addListener(this, new Signal.Listener() {//點擊【保存】按鈕
				@Override
				public void trigger() {
					    String setObj = "set"+eo.getClass().getSimpleName().substring(0,1).toUpperCase()+eo.getClass().getSimpleName().substring(1);
					    Method setObjM = null,setNativeM = null;
						try {
							 for(Iterator iterator = fieldWMap.entrySet().iterator();iterator.hasNext();){
						    	 Map.Entry<Field,FormWidget> fw = (Entry<Field, FormWidget>) iterator.next();
						    	 Field setField = fw.getKey();
						    	 String setNative = "set"+setField.getName().substring(0, 1).toUpperCase()+setField.getName().substring(1);
						    	 logger.error("setNative:"+setNative+"\t nativeVal:"+fw.getValue().getValueText());
						    	 Object nativeVal = setField.getType().cast(fw.getValue().getValueText());
						    	 setNativeM = deo.getClass().getMethod(setNative,setField.getType());
						    	 setNativeM.invoke(deo, nativeVal);
						    }
							setPrimitiveProperty();
							setObjM = deo.getClass().getMethod(setObj, eo.getClass());
							setObjM.invoke(deo, eo);
							if(dbEntity.saveOrUpdate(deo)){
								wbx.draw(title);
								Util.infoMessage("", "保存成功");
							}else
								Util.infoMessage("", "保存失败");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
				}
			});
			
			PushButton deleteBtn = new PushButton("删除");
			deleteBtn.setStyleClass("pushbutton");
			deleteBtn.clicked().addListener(this, new Signal.Listener() {
				@Override
				public void trigger() {
					final WMessageBox msbox = new WMessageBox("Information Please","确认删除这条记录？",Icon.Information,EnumSet.of(StandardButton.Ok));
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
							if(dbEntity.removeEO(deo)){
								wbx.draw(title);
							}else
								Util.infoMessage("Information Please", "删除失败");
							
							if (msbox != null)
								msbox.remove();
							
						}
					});
			    	msbox.show();
				}
			});
			tcont.bindWidget("savebtn", saveBtn);
			tcont.bindWidget("deletebtn", deleteBtn);
			td.addWidget(tcont);
		}else{
			try {
				FormWidget le = new LineEdit();
				le.setAttributeValue("style", "background: #f1f2f3;text-align: center;");
				String getName = "get"+detailField.getName().substring(0, 1).toUpperCase()+detailField.getName().substring(1);
				Method m = deo.getClass().getMethod(getName);
				Object retval = m.invoke(deo);
				logger.error("detailField:"+detailField+"\t retval:"+retval);
				//le.setValueText(retval==null?"":retval.toString());
				td.addWidget(getFieldWidget(detailField,retval));
				//td.addWidget(le);
				//fieldWMap.put(detailField, le);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		tr.addWidget(td);
			
	}
	tbody.addWidget(tr);
}

	formtbl.bindWidget("tbody", tbody);
	
	Anchor cancelbtn = new Anchor(new Link(""), "关闭");
	cancelbtn.setStyleClass("close");
	cancelbtn.clicked().addListener(this,new Signal.Listener() {
		@Override
		public void trigger() {
			wbx.setAttributeValue("style", "display:none");
		}
	});
	box_button.bindWidget("cancelbtn", cancelbtn);
		
	formtbl.bindWidget("box_button", box_button);
	return formtbl;
}
    private Widget getFieldWidget(Field field,Object fieldVal){
    	String fieldType = field.getType().getName();
    	Widget fieldWidget = null;
    	if(fieldType.equals("java.lang.String")||fieldType.equals("short")){//文本 和数值
    		fieldWidget = new LineEdit(fieldVal==null?"":fieldVal.toString());
    		fieldWidget.setAttributeValue("style", "background: #f1f2f3;text-align: center;");
    	}else if(fieldType.equals("java.util.Date")){//日期
    		fieldWidget = new DateEdit();
    		((DateEdit)fieldWidget).setFormat("yyyy-MM-dd");
    	}
        return fieldWidget;    	
    } 
    //画明细的表头
    private Widget getDetailHead(String dename){
		detailFieldList = new ArrayList<Field>();
		try{
		  deCls= Class.forName(dename);
		}catch(Exception e){
			e.printStackTrace();
		}
		Field[] fieldArr = deCls.getFields();
		ContainerWidget thead = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_THEAD;
			}
		};
		//序号归并到实体类的一列，不做例外处理
		for (Field field:fieldArr){
			DisplayField df = field.getAnnotation(DisplayField.class);
			if(df!=null){
				ContainerWidget th = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TH;
					}
				};
				th.addWidget(new Text(df.cname()));
				thead.addWidget(th);
				detailFieldList.add(field);
			}
				
		}
		return thead;
	}   
    
    public void setPrimitiveProperty(){
    	
    }
}
*/
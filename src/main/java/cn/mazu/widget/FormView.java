package cn.mazu.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.WApplication;
import cn.mazu.utils.LocaleUtils;
import cn.mazu.utils.ValidationStyleFlag;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.web.form.AbstractToggleButton;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.validator.DoubleValidator;
import cn.mazu.widget.validator.IntValidator;
import cn.mazu.widget.validator.RegExpValidator;
import cn.mazu.widget.validator.Validator;

public class FormView extends HtmlTemplate {
	private static Logger logger = LoggerFactory.getLogger(FormView.class);
	
	private Map<String, FieldData> fields_;
	
	public FormView() {
		this.fields_ = new HashMap<String, FormView.FieldData>();
		this.init();
	}
	
	private void init() {
		this.addFunction("id", Functions.id);
		this.addFunction("tr", Functions.tr);
		this.addFunction("block", Functions.block);
	}
	
	public void updateModel(FormModel model){
		List<String> fields = model.getFields();
		for(int i = 0; i < fields.size(); ++i){
			String field = fields.get(i);
			this.updateModelField(model,field);
		}
		
	}
	
	public void updateModelField(FormModel model, String field) {
		Widget edit = this.resolveWidget(field);
		FormWidget fedit = (edit instanceof FormWidget ? (FormWidget)edit : null);
		if(fedit != null)
			this.updateModelValue(model, field, fedit);
		else
			this.updateModelValue(model, field, edit);
	}
	
	protected void updateModelValue(FormModel model, String field,
			FormWidget fedit) {
		if(this.updateModelValue(model, field, (Widget)fedit))
			return;
		AbstractToggleButton b = ((fedit) instanceof AbstractToggleButton ? (AbstractToggleButton)(fedit) : null);
		
		if(b != null){
			model.setValue(field, b.isChecked());
		} else {
			//model.setValue(field, fedit.getValueText());
			try {
				Double strTdob = Double.valueOf(fedit.getValueText().replace(",", "")).doubleValue();
				model.setValue(field, fedit.getValueText().replace(",", ""));
//				System.out.println("number data type");
			} catch (NumberFormatException e) {
				model.setValue(field, fedit.getValueText());
//				System.out.println("string data type");
//				e.printStackTrace();
			}
		}
	}
	
	protected boolean updateModelValue(FormModel model, String field, Widget edit){
		FieldData fd = this.fields_.get(field);
		if(fd != null){
			if(fd.updateFunctions != null){
				fd.updateFunctions.updateModelValue(model, field, edit);
				return true;
			}
		}
		return false;
	}
	
	public void setFormWidget(String field, Widget formWidget, FieldView fieldView){
		FieldData i = this.fields_.get(field);
		if(!(i!=null))
			this.fields_.put(field, new FieldData());
		this.fields_.get(field).formWidget = formWidget;
		this.fields_.get(field).updateFunctions = fieldView;
		this.bindWidget(field, formWidget);
	}
	
	public void updateView(FormModel model) {
		List<String> fields = model.getFields();
		for(int i = 0; i < fields.size(); i++){
			String field = fields.get(i);
			this.updateViewField(model,field);
		}
	}
	
	private void updateViewField(FormModel model, String field) {
		final String var = field;
		if(model.isVisible(field)){
			this.setCondition("if:"+var, true);
			Widget edit = this.resolveWidget(var);
			if(!(edit != null)){
				edit = this.createFormWidget(field);
				if(!(edit != null)){
					/*logger.error(new StringWriter().append("updateViewField: createFormWidget('")
							.append(field).append("') return 0").toString());*/
					return;
				}
				this.bindWidget(var, edit);
			}/*else if(var.equals("desc-field")){
				edit = this.createFormWidget(field);
				this.bindWidget(var, edit);
			}*/
			FormWidget fedit = ((edit) instanceof FormWidget) ? (FormWidget)edit : null;
			if(fedit != null){
				if(fedit.getValidator() != model.getValidator(field)&& model.getValidator(field)!=null){
					fedit.setValidator(model.getValidator(field));
				}
				this.updateViewValue(model, field, fedit);
			}else
				this.updateViewValue(model, field, edit);
			Text info = (Text) this.resolveWidget(var + "-info");
			if(!(info != null)){
				info = new Text();
				this.bindWidget(var + "-info", info);
			}
			this.bindString(var + "-label", model.label(field));
			Validator.Result v = model.getValidation(field);
			//info.setText(v.getMessage());
			this.indicateValidation(field, model.isValidated(field), info,
					edit, v);
			//System.out.println("field:"+field+"model.isReadOnly(field):"+model.isReadOnly(field));
			//edit.setDisabled(model.isReadOnly(field));
		}else{
			this.setCondition("if:"+var, false);
			this.bindEmpty(var);
			this.bindEmpty(var+"-info");
		}

	}
	protected void indicateValidation(String field, boolean validated, Text info, Widget edit, Validator.Result validation){
		info.setText(validation.getMessage());
		if(validated){
			WApplication.getInstance().getTheme().applyValidationStyle(edit, validation, ValidationStyleFlag.ValidationAllStyles);
			info.toggleStyleClass("Wt-error",validation.getState() != Validator.State.Valid,true);
		}else{
			WApplication.getInstance().getTheme().applyValidationStyle(edit, validation, ValidationStyleFlag.ValidationNoStyle);
			info.removeStyleClass("Wt-error",true);
		}
	}
	protected void updateViewValue(FormModel model, String field, FormWidget edit){
		if(this.updateViewValue(model, field, (Widget)edit)){
			return;
		}
		AbstractToggleButton b =((edit) instanceof AbstractToggleButton ? (AbstractToggleButton)edit : null);
		if(b != null){
			Object v = model.getValue(field);
			if(!(v!=null) || (Boolean)v == false)
				b.setChecked(false);
			else
				b.setChecked(true);
		}else{
			if(edit.getValueText()!=null&&!edit.getValueText().equals(""))//��ʼ���󣬲���null����""
				edit.setValueText(edit.getValueText());
			else
				edit.setValueText(model.valueText(field));
		}
	}
	protected boolean updateViewValue(FormModel model, String field, Widget edit) {
		FieldData fi = this.fields_.get(field);
		if(fi != null){
			if(fi.updateFunctions != null){
				fi.updateFunctions.updateViewValue();
				return true;
			}
		}
		return false;
	}

	protected Widget createFormWidget(String field) {
		return null;
	}

	public static class FieldData {
		private static Logger logger = LoggerFactory.getLogger(FieldData.class);
		
		public Widget formWidget;
		public FieldView updateFunctions;
		public FieldData(){
			this.formWidget = null;
		}
	}
	
	public static interface FieldView{
		public void updateViewValue();
		public void updateModelValue(FormModel model, String field, Widget edit);
	}
	//added20141027
	public boolean validationPassed(FormModel model){
		   updateView(model);
		   List<String> fieldsList = model.getFields();
		   /*for (String fieldName:fieldsList){
			   Validator fieldValidator = model.getValidator(fieldName);
			  // Object fieldValue = model.getValue(fieldName);
			   if(fieldValidator!=null){
				   this.bindString(fieldName+"-info", "");
			   }
		   }*/
		   boolean retval = true;
		   for (String fieldName:fieldsList){
			   Validator fieldValidator = model.getValidator(fieldName);
			   Object fieldValue = model.getValue(fieldName);
			   if(fieldValidator!=null){
				   //System.out.println("fieldname:"+fieldName+"\tfieldValue:"+fieldValue);
				   if(fieldValue==null||fieldValue!=null&&fieldValue.toString().trim().equals("")){
					   this.bindString(fieldName+"-info","<span class='span'>"+tr("is null")+"</span>");
					   retval = false;
				   }else if(IntValidator.class.isAssignableFrom(fieldValidator.getClass())){
					   int i = 0;
						try {
							i = LocaleUtils.toInt(LocaleUtils.getCurrentLocale(),fieldValue.toString());
						} catch (NumberFormatException e) {
							this.bindString(fieldName+"-info","<span class='span'>"+tr("badform")+"</span>");
							 retval = false;
						}
						   if(i>((IntValidator)model.getValidator(fieldName)).getTop()){
							  this.bindString(fieldName+"-info", 
									  "<span class='span'>"
											  +((IntValidator)model.getValidator(fieldName)).getInvalidTooLargeText()
											  +"</span>");
							  retval = false;
						   }else if(i<((IntValidator)model.getValidator(fieldName)).getBottom()){
							    this.bindString(fieldName+"-info",
							    		 "<span class='span'>"
							    		+((IntValidator)model.getValidator(fieldName)).getInvalidTooSmallText()
							    		 +"</span>");
							  retval = false;
						   }
				   }else if(DoubleValidator.class.isAssignableFrom(fieldValidator.getClass())){
					   double d = 0.0;
					   boolean convertFlag = true;//Double的最小值<0.0
					   try {
							d = LocaleUtils.toDouble(LocaleUtils.getCurrentLocale(),fieldValue.toString());
						} catch (NumberFormatException e) {
							this.bindString(fieldName+"-info","<span class='span'>"+tr("badform")+"</span>");
							 convertFlag = false;
							 retval = false;
						}
						   if(d>((DoubleValidator)model.getValidator(fieldName)).getTop()&&convertFlag){
							    this.bindString(fieldName+"-info", 
							    		 "<span class='span'>"+
							    		((DoubleValidator)model.getValidator(fieldName)).getInvalidTooLargeText()
							    		+"</span>");
							  retval = false;
						   }else if(d<((DoubleValidator)model.getValidator(fieldName)).getBottom()&&convertFlag){
							     this.bindString(fieldName+"-info",
							    		 "<span class='span'>"+
							    		 ((DoubleValidator)model.getValidator(fieldName)).getInvalidTooSmallText()
							    		 +"</span>");
							  retval = false;
						   }
				   }else if(RegExpValidator.class.isAssignableFrom(fieldValidator.getClass())){
					   Pattern pattern = Pattern.compile(((RegExpValidator)fieldValidator).getRegExp());
					   if(!pattern.matches(((RegExpValidator)fieldValidator).getRegExp(), fieldValue.toString())){
						   this.bindString(fieldName+"-info",
								   "<span class='span'>"+
								   ((RegExpValidator)fieldValidator).getInvalidNoMatchText()+"</span>");
						   retval = false;
					   }
				   }
			   }
		   }
		       return retval;
	}
	  protected boolean busiValidated() {
		  return true;
	}
}

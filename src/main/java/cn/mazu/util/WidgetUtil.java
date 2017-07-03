package cn.mazu.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import cn.mazu.widget.kit.model.ItemDataRole;
import cn.mazu.widget.kit.model.StringListModel;
import cn.mazu.widget.kit.web.form.ComboBox;

//组件中被写滥的方法，归拢一下
public class WidgetUtil {
    //查询数据库得出列表值来生成下拉列表
	public static ComboBox initSelFromList(List<EntityObject> eList,String fieldNames){
		ComboBox cb = new ComboBox();
		StringListModel slm = new StringListModel();
		slm.addString("请选择");
		slm.setData(0, 0,null,ItemDataRole.UserRole);
		for (EntityObject eo:eList){
			String fieldName = "";
			for(String singleField:fieldNames.split(",")){
				fieldName += getValFromProperty(eo,singleField)+"-";
			}
			int seperatorIndex = fieldName.lastIndexOf("-");
			if(seperatorIndex>0)fieldName = fieldName.substring(0,seperatorIndex);
			slm.addString(fieldName);
			slm.setData(eList.indexOf(eo)+1, 0,eo,ItemDataRole.UserRole);
		}
	    cb.setModel(slm);
	    return cb;
	}
	//根据枚举值生成下拉列表
	public static ComboBox initSelFromEnum(Class<? extends Enum> cls){
		ComboBox cb = new ComboBox();
		StringListModel slm = new StringListModel();
		slm.addString("请选择");
		slm.setData(0, 0,null,ItemDataRole.UserRole);
		Enum[] eConsArr = cls.getEnumConstants();
		for (int i=0;i<eConsArr.length;i++){
			Enum ec = eConsArr[i];
			slm.addString(ec.toString());
			slm.setData(i+1,0,ec,ItemDataRole.UserRole);
		}
		cb.setModel(slm);
		return cb;
	}
	//获取下拉选中的对象
	public static Object getSelEO(ComboBox combo){
		StringListModel slm = (StringListModel)combo.getModel();
		int selectedRow = combo.getCurrentIndex();
		return slm.getData(selectedRow,0,ItemDataRole.UserRole);
	}
	public static String getSelDisplayStr(ComboBox cmobo){
		StringListModel slm = (StringListModel)cmobo.getModel();
		int selectedRow = cmobo.getCurrentIndex();
		return slm.getData(selectedRow,0,ItemDataRole.DisplayRole).toString();
	}
	//调用getter获取属性值
	public static Object getValFromProperty(EntityObject eo,Field field){
		/*Method m = null;
			try {
				m = eo.getClass().getMethod(gettersOrSetters(field,"get"));
				return m.invoke(eo);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		return "";*/
		return getValFromProperty(eo,field.getName());
	}
	public static Object getValFromProperty(EntityObject eo,String fieldName){
		Method m = null;
			try {
				m = eo.getClass().getMethod(gettersOrSetters(fieldName,"get"));
				return m.invoke(eo);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		return "";
	}
	//调用setter设定属性值
	public static void setValForProperty(EntityObject eo,String fieldName,Object setval){
		Method m = null;
		try{
			m = eo.getClass().getMethod(gettersOrSetters(fieldName,"set"),setval.getClass()); 
			m.invoke(eo, setval);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void setValForProperty(EntityObject eo,Field field,Object setval){
		setValForProperty(eo,field.getName(),setval);
		/*Method m = null;
		try{
			m = eo.getClass().getMethod(gettersOrSetters(field,"set"),setval.getClass());
			m.invoke(eo, setval);
		}catch(Exception e){
			e.printStackTrace();
		}*/
	}
	//拼接getPropertyname/setPropertyname
	public static String gettersOrSetters(Field field,String type){
		return gettersOrSetters(field.getName(),type);
	}
	public static String gettersOrSetters(String fieldName,String type){
		return type+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
	}
}

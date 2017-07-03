package cn.mazu.storage.mvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import cn.mazu.mysql.Entity;
import cn.mazu.storage.entity.Inventory;
import cn.mazu.util.Util.StoreCategory;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.DateValidator;
import cn.mazu.widget.validator.DoubleValidator;
import cn.mazu.widget.validator.Validator;

public class InventoryInitialModel extends FormModel{
    public static final String eid = "id-field";
    //品名
    public static final String itemname = "name-field";
    //初始数量
    public static final String itemnum = "num-field";
    //规格
    public static final String specification = "spec-field";
    //供货商
    public static final String supplier = "supplier-field";
    //计量单位
    public static final String itemunit = "unit-field";
    //货位--仓别
    //public static final String allocation = "allocate-field";
//    public static final String storecategory = "sc-field";
    //材质
    public static final String texture = "texture-field";
    //盘点日期
    public static final String checkdate = "cdate-field";
    //货号
    public static final String itemcode = "code-field";
    //预警值
    public static final String threshold = "threshold-field";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public InventoryInitialModel() {
		Validator generalV = new Validator();
		DateValidator dv = new DateValidator();
		
		/*DoubleValidator itemnumResult = new DoubleValidator();
		itemnumResult.setRange(Double.MIN_VALUE, Double.MAX_VALUE);
		itemnumResult.setInvalidTooLargeText(tr("too large"));
		itemnumResult.setInvalidTooSmallText(tr("too small"));*/
		
		DoubleValidator thresholdResult = new DoubleValidator();
		thresholdResult.setRange(0, Double.MAX_VALUE);
		thresholdResult.setInvalidTooLargeText(tr("too large"));
		thresholdResult.setInvalidTooSmallText(tr("too small"));
		
		this.addField(eid);
		
//		this.addField(storecategory);
//		this.setValidator(allocation, generalV);
		
		this.addField(checkdate);
		this.setValidator(checkdate, dv);
		
		this.addField(itemname);
		this.setValidator(itemname, generalV);
		
		this.addField(itemnum);
		this.setValidator(itemnum, thresholdResult);
		
		this.addField(threshold);
		this.setValidator(threshold, thresholdResult);
		
		this.addField(itemunit);
//		this.setValidator(itemunit, generalV);
		
		this.addField(specification);
		this.setValidator(specification, generalV);
		
		this.addField(supplier);
//		this.setValidator(supplier, generalV);
		
		this.addField(texture);
//		this.setValidator(texture, generalV);
		
		this.addField(itemcode);
		this.setValidator(itemcode, generalV);
	}
	public boolean saveOrUpdate(Entity entity,Inventory inventory){
		inventory.setUnit(this.getValue(itemunit).toString());
		//inventory.setAllocation((StoreCategory)this.getValue(allocation));
		//inventory.setStoreCategory((StoreCategory)this.getValue(storecategory));
		inventory.setSupplier(this.getValue(supplier).toString());
        inventory.setSpec(this.getValue(specification).toString());
        inventory.setName(this.getValue(itemname).toString());
        inventory.setTexture(this.getValue(texture).toString());
        inventory.setAmount(Double.valueOf(this.getValue(itemnum).toString()));
        inventory.setThreshold(Double.valueOf(this.getValue(threshold).toString()));
        try {
			inventory.setCheckDate(sdf.parse(this.getValue(checkdate).toString()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        inventory.setCode(this.getValue(itemcode).toString());
        
//        Transaction t = entity.getDatabase().startTransaction();
        try{
        	inventory.setCheckDate(sdf.parse(this.getValue(checkdate).toString()));
        /*	entity.getEntityManager().merge(inventory);
        	t.commit();*/
        }catch(Exception e){
//        	t.rollback();
        	e.printStackTrace();
        }
        return entity.saveOrUpdate(inventory);
	}

}

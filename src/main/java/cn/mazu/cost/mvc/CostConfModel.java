package cn.mazu.cost.mvc;

import cn.mazu.cost.entity.CostConf;
import cn.mazu.mysql.Entity;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.DateValidator;
import cn.mazu.widget.validator.DoubleValidator;
import cn.mazu.widget.validator.Validator;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.hibernate.ejb.criteria.ValueHandlerFactory.ShortValueHandler;

public class CostConfModel extends FormModel
{
  public static final String cnccost = "cncc-field";
  public static final String fdcost = "fdc-field";
  public static final String mccost = "mcc-field";
  public static final String xccost = "xcc-field";
  public static final String id = "id-field";
  public static final String startDate = "startd-field";
  public static final String endDate = "endd-field";

  public CostConfModel()
  {
//    Validator generalVali = new Validator();
    DoubleValidator costResult = new DoubleValidator();
	costResult.setRange(Double.MIN_VALUE, Double.MAX_VALUE);
	costResult.setInvalidTooLargeText(tr("too large"));
	costResult.setInvalidTooSmallText(tr("too small"));
    
    addField("cncc-field");
    addField("fdc-field");
    addField("mcc-field");
    addField("xcc-field");
    setValidator("cncc-field", costResult);
    setValidator("fdc-field", costResult);
    setValidator("mcc-field", costResult);
    setValidator("xcc-field", costResult);
    addField("startd-field");
    DateValidator dv = new DateValidator();
    setValidator("startd-field", dv);
    addField("endd-field");
    setValidator("endd-field", dv);
    addField("id-field");
  }
  public boolean saveOrUpdate(Entity entity, CostConf cc) {
    cc.setCnccost(Double.valueOf(getValue("cncc-field").toString()));
    cc.setXccost(Double.valueOf(getValue("xcc-field").toString()));
    cc.setMccost(Double.valueOf(getValue("mcc-field").toString()));
    cc.setFdcost(Double.valueOf(getValue("fdc-field").toString()));
    try {
      cc.setStartDate(entity.getSdf().parse(getValue("startd-field").toString()));
      cc.setEndDate(entity.getSdf().parse(getValue("endd-field").toString()));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return entity.saveOrUpdate(cc);
  }
}
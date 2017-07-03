package cn.mazu.cost.mvc;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.mazu.cost.entity.CostConf;
import cn.mazu.ui.CostConfMgr;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;

//工时成本配置
public class CostConfForm extends FormView{
   private CostConfMgr ccMgr;
   private String title;
   private CostConf cc;
   private PopBox ccPb;
   private CostConfModel ccModel;
   private Map<Date, Date> availablePeriod = new HashMap();
   public CostConfForm(CostConfMgr ccMgr,String title,PopBox ccPb,CostConf cc) {
	  this.ccMgr = ccMgr;
      this.title = title; 
	  this.ccPb = ccPb;
	  this.ccModel = new CostConfModel();
	  this.cc = cc;
	  if(!title.equals("新建"))
		  updateClientView();
	  else
		 this.cc = new CostConf();
   }
   
   @Override
	public Widget draw(Template parent, String... args) {
	    this.setTemplateText(tr("costconf-form"), TextFormat.XHTMLUnsafeText);
	    Anchor submitbtn = new Anchor(new Link(""), "保存");
		submitbtn.setStyleClass("close");
		submitbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				CostConfForm.this.updateModel(ccModel);
				if(validationPassed(ccModel)&&busiValidated()){
					if(ccModel.saveOrUpdate(ccMgr.getDBEntity(),cc)){
						if(ccMgr.getSelectTr()!=null&&!ccMgr.getSelectTr().getId().equals(cc.getId())||ccMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							ccMgr.drawGrid();
							ccMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+ccMgr.getPaginationStart()*ccMgr.getPaginationMax()+"});");
						}else if(ccMgr.getSelectTr()!=null&&ccMgr.getSelectTr().getId().equals(cc.getId())){//选中某一行，确实执行的是【编辑】操作
							ccMgr.refreshSelTr(cc);
						}
					}else{
						Util.infoMessage(tr("Information Please").toString(), "失败");
					}
					ccPb.setAttributeValue("style", "display:none");
				}
			}
		});
		Anchor closebtn = new Anchor(new Link(""), "关闭");
		closebtn.setStyleClass("close");
		closebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				ccPb.setAttributeValue("style", "display:none");
			}
		});
		if(title.equals("查看")){
			this.bindString("submit", "");
		}else
			this.bindWidget("submit", submitbtn);
		this.bindWidget("close", closebtn);
		this.updateView(ccModel);
		bindString("busiinfo", "");
		return this;
	}
    @Override
	protected Widget createFormWidget(String field) {
	    Widget l = new LineEdit();
	    if(field.equals(ccModel.id))
	    	l.hide();
	    else if ((field.equals("startd-field")) || (field.equals("endd-field"))) {
	        l = new DateEdit();
	        ((DateEdit)l).setFormat("yyyy-MM-dd");
	      }
		return l;
	}
    private void updateClientView(){
    	ccModel.setValue(ccModel.cnccost, cc.getCnccost());
    	ccModel.setValue(ccModel.fdcost, cc.getFdcost());
    	ccModel.setValue(ccModel.mccost, cc.getMccost());
    	ccModel.setValue(ccModel.xccost, cc.getXccost());
    	ccModel.setValue("startd-field", Util.formatDateyyyyMMdd(cc.getStartDate()));
        ccModel.setValue("endd-field",Util.formatDateyyyyMMdd(cc.getEndDate()));
    }
    //@Override
    protected boolean busiValidated()
    {
      bindString("busiinfo", "");
      List<EntityObject> apList = this.ccMgr.getPlist();
      apList.remove(cc);
      for (EntityObject eo : apList) {
        CostConf cc = (CostConf)eo;
        this.availablePeriod.put(cc.startDate, cc.endDate);
      }
        Date startDate = new Date();
		try {
			startDate = this.ccMgr.getDBEntity().getSdf().parse(this.ccModel.getValue("startd-field").toString());
		} catch (ParseException e) {
//			e.printStackTrace();
			this.bindString("startd-field-info","<span class='span'>"+tr("is null")+"</span>");
			return false;
		}
        Date endDate = new Date();
		try {
			endDate = this.ccMgr.getDBEntity().getSdf().parse(this.ccModel.getValue("endd-field").toString());
		} catch (ParseException e) {
			this.bindString("endd-field-info","<span class='span'>"+tr("is null")+"</span>");
			return false;
		}
        if (!endDate.after(startDate)) {
          this.bindString("busiinfo", "<span class='span'>"+ tr("start date lthan end date")+"</span>");
          return false;
        }
        for (Iterator iterator = this.availablePeriod.entrySet().iterator(); iterator.hasNext(); ) {
          Map.Entry dateEntry = (Map.Entry)iterator.next();
          if ((!endDate.before((Date)dateEntry.getKey())) && (!startDate.after((Date)dateEntry.getValue()))) {
           this.bindString("busiinfo", "<span class='span'>"+ tr("date overlap")+"</span>");
            return false;
          }
        }
      return true;
    }
}

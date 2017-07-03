package cn.mazu.sitemg.mvc;



import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Product;
import cn.mazu.ui.Productinfo;
import cn.mazu.util.Util;
import cn.mazu.util.Util.RapidOrNot;
import cn.mazu.util.WidgetUtil;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.ImageMgr;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class ProductView extends FormView{
	private Product p;
	private SitemgEntity smgEntity;
	private ProductModel pModel;
	private PopBox pb;
	private Template img = new Template();
	private Productinfo sdMgr;
	private String type = "";
	
	private ComboBox rapidOrNot = new ComboBox();
//	private StringListModel rONModel = new StringListModel(rapidOrNot);
	    
	public ProductView(Product p,PopBox pb,Productinfo sdMgr,String type){
		pModel = new ProductModel();
		smgEntity = new SitemgEntity();
		this.p = p;
		this.pb = pb;
		this.sdMgr = sdMgr;
		this.type = type;
		img.setTemplateText("<div class='' style='float:left;'>${imgmgr}</div>",TextFormat.XHTMLUnsafeText);
		if(p!=null&&!type.equals("新建")){
			updateContactView();
		}else
			this.p = new Product();
	}

	@Override
	public Widget draw(final Template parent, final String... args) {
		/*rONModel.addString("请选择");
		rONModel.setData(0, 0, null,ItemDataRole.UserRole);
		RapidOrNot data_arr[] ={RapidOrNot.RAPIDNO,RapidOrNot.RAPIDYES};
		for(int j=0;j<data_arr.length;j++){
			rONModel.addString(data_arr[j].name);
			rONModel.setData(j+1, 0, data_arr[j], ItemDataRole.UserRole);
		}
		rapidOrNot.setModel(rONModel);*/
		rapidOrNot = WidgetUtil.initSelFromEnum(RapidOrNot.class);
		
		this.setTemplateText(tr("form-prodct"), TextFormat.XHTMLUnsafeText);
		this.updateView(pModel);
		Anchor savebtn = new Anchor(new Link(""), "保存");
		savebtn.setStyleClass("close");
		savebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				ProductView.this.updateModel(pModel);
				if(validationPassed(pModel)){
/*					if(pModel.saveOrUpdate(sdMgr.getDBEntity(),p)){
						sdMgr.draw(parent, null);
						sdMgr.doJavaScript("$('#bctable').datagrid();");
					}
*/					if(pModel.saveOrUpdate(sdMgr.getDBEntity(),p)){
						if(sdMgr.getSelectTr()!=null&&!sdMgr.getSelectTr().getId().equals(p.getId())||sdMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							sdMgr.drawGrid();
							sdMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+sdMgr.getPaginationStart()*sdMgr.getPaginationMax()+"});");
						}else if(sdMgr.getSelectTr()!=null&&sdMgr.getSelectTr().getId().equals(p.getId())){//选中某一行，确实执行的是【编辑】操作
							sdMgr.refreshSelTr(p);
						}
					}else{
						Util.infoMessage(tr("Information Please").toString(), "失败");
					}

					
					pb.setAttributeValue("style", "display:none");
				}
			}
		});
		if(type.equals("查看")){
			this.bindString("submit", "");
		}else
			this.bindWidget("submit", savebtn);
		Anchor closebtn = new Anchor(new Link(""), "关闭");
		closebtn.setStyleClass("close");
		closebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				pb.setAttributeValue("style", "display:none");
			}
		});
		this.bindWidget("close", closebtn);
		return this;
	}

	@Override
	protected Widget createFormWidget(String field) {
		Widget result = null;
		if(field.equals(ProductModel.namefield))
			result = new LineEdit();
		else if(field.equals(ProductModel.introfield))
			result = new TextArea();
		else if(field.equals(ProductModel.imguploadfield)){
			img.bindWidget("imgmgr",new ImageMgr(p.getId(),"product"));
			result = img;
		}else if(field.equals(ProductModel.rapidfield)){
			result = rapidOrNot;
		}
		return result;
	}
	
	@Override
	public void updateModelField(FormModel model, String field) {
		if(field.equals(ProductModel.rapidfield)){
	    	pModel.setValue(pModel.rapidfield,WidgetUtil.getSelEO(rapidOrNot));
		}else 
	    	   super.updateModelField(model, field);
	}
	
	private void updateContactView(){
		pModel.setValue(pModel.introfield, p.getPintro());
		pModel.setValue(pModel.namefield, p.getPname());
		pModel.setValue(pModel.rapidfield,p.getRapidOrNot().name);
	}
}

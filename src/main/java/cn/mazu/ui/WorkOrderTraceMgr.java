package cn.mazu.ui;

import cn.mazu.WApplication;
import cn.mazu.util.Util;
import cn.mazu.widget.MiddleTpl;

public class WorkOrderTraceMgr extends MiddleTpl {
    public WorkOrderTraceMgr() {
	   super("cn.mazu.workorder.entity.WorkOrderTraceDetail",new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
	   this.bindEmpty("win1");
	}
}

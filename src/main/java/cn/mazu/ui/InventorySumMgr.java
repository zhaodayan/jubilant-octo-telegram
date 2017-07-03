package cn.mazu.ui;

import cn.mazu.widget.MiddleTpl;

//" where b.book_date<=now() "+ 去掉了统计时间限制,无流水账记录的库存初始化信息也要被查询出来        			
public class InventorySumMgr extends MiddleTpl {
    public InventorySumMgr() {
         super("cn.mazu.storage.entity.InventorySum",
        	new Object[]{"InventorySumQuery"       		 
         });
         setNavSqlFlag(true);
    }
}



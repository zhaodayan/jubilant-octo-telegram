package cn.mazu.ui;

import cn.mazu.widget.MiddleTpl;

public class InventoryWarnMgr extends MiddleTpl
{
  public InventoryWarnMgr()
  {
    super("cn.mazu.storage.entity.InventoryWarn", 
      new Object[] { "InventoryWarnQuery" });
    setNavSqlFlag(true);
  }
}
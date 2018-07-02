package org.jboss.seam.example.webassoc.mny;

import javax.ejb.Local;

@Local
public interface AcctRecordHome
{

   // Methods from AcctRecordHomeImpl
   public AcctRecord initAcctRecord();

   public void ejbRemove();

   // Methods from EntityHome and Home
   public Object getId();

   public void setId(Object id);

   public String persist();

   public String update();

   public String remove();

   public boolean isManaged();

   public void create();

   public Object getInstance();

   // ================================
   // Business operations
   // ================================
}
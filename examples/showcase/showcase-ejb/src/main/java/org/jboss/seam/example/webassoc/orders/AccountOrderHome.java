package org.jboss.seam.example.webassoc.orders;

import java.util.List;

import javax.ejb.Local;

@Local
public interface AccountOrderHome
{

   // =================================
   // Methods from AccountOrderHomeImpl
   // =================================
   public AccountOrder initAccountOrder();

   public void ejbRemove();

   // =================================
   // Methods from EntityHome and Home
   // =================================
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
   // List of all valid orders
   public List<AccountOrder> getAllOrders();

}
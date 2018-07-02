package org.jboss.seam.example.webassoc.orders;

import java.util.List;

import javax.ejb.Local;

@Local
public interface GuideOrderHome
{

   // =================================
   // Methods from AssocMemberHomeImpl
   // =================================
   public GuideOrder initGuideOrder();

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
   public List<GuideOrder> getAllOrders();

}
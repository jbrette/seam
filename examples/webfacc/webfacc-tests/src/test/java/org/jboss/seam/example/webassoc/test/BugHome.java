package org.jboss.seam.example.webassoc.test;

import javax.ejb.Local;

@Local
public interface BugHome
{

   // Methods from BugHomeImpl
   public Bug initBug();

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
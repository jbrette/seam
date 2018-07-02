package org.jboss.seam.example.webassoc.mail;

import javax.ejb.Local;

@Local
public interface MailingHome
{

   // Methods from MailingHomeImpl
   public Mailing initMailing();

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

   // Additional business method
   public String subscribe();

   public String mail();

   public String unsubscribe();

}
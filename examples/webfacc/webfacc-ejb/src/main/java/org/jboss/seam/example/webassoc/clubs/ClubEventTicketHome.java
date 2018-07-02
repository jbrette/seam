package org.jboss.seam.example.webassoc.clubs;

import javax.ejb.Local;

@Local
public interface ClubEventTicketHome
{

   // Methods from ClubEventTicketHomeImpl
   public ClubEventTicket initClubEventTicket();

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
   public String register();

   public String unregister();

}
package org.jboss.seam.example.webassoc.clubs;

import javax.ejb.Local;

@Local
public interface ClubEventRegistrationHome
{

   // Methods from ClubEventRegistrationHomeImpl
   public ClubEventRegistration initClubEventRegistration();

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

   public String register_me();

   public String unregister_me();

   // recompute the total participation
   public String updateAndClose();

   // confirm the registration
   public String emailRegistration();

   // enter the waiting list
   public String enterWaitingList();

   // exit the waiting list
   public String exitWaitingList();

   // confirm the registration
   public String confirmRegistration();

   // cancel the registration
   public String cancelRegistration();

   // payment email
   public String emailPayment();

   //
   public String deleteTicket();

}
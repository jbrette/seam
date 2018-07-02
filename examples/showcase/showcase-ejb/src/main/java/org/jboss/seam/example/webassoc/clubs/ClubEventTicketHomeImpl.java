package org.jboss.seam.example.webassoc.clubs;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import javax.ejb.Remove;
import javax.ejb.Stateful;

@Name("clubEventTicketHome")
@Stateful
public class ClubEventTicketHomeImpl
        extends EntityHome<ClubEventTicket>
        implements ClubEventTicketHome
{

   private static final long serialVersionUID = 4065230436863767098L;
   @In(required = false)
   private ClubEventRegistration clubEventRegistration;
   @RequestParameter("paramClubEventRegistrationId")
   private Long registrationId;
   @In(required = false)
   private AssocMember authenticatedMember;
   @In
   private FacesMessages facesMessages;

   @Factory("clubEventTicket")
   public ClubEventTicket initClubEventTicket()
   {

      return getInstance();

   }

   protected ClubEventTicket createInstance()
   {

      ClubEventTicket ticket = new ClubEventTicket();
      ClubEventRegistration theRegistration = null;

      if ((clubEventRegistration == null) && (registrationId != null))
      {

         theRegistration =
            getEntityManager().find(ClubEventRegistration.class,
               registrationId);

      }
      else
      {

         theRegistration = clubEventRegistration;

      }

      if (theRegistration != null)
      {

         ticket.setRegistration(theRegistration);
         theRegistration.getTickets().add(ticket);
         theRegistration.resetFirstNames();
         ticket.setLastName(theRegistration.getMember().getAssocName());

      }
      else
      {

         ticket.setLastName("LastName");

      }

      ticket.setFirstName("FirstName");
      ticket.setFKind(ClubEventTicket.FKind.kidNonMember);

      return ticket;

   }

   // Additional business method
   public String register()
   {

      if ((clubEventRegistration == null)
         || (clubEventRegistration.getEvent() == null)
         || (clubEventRegistration.getEvent().getStatus()
            == ClubEvent.CEState.closed))
      {

         facesMessages.add(
            "Event is closed to registration and can not be modified");

         return "regclosed";

      }

      String res = persist();

      // Let's update the registration
      ClubEventRegistration reg = getInstance().getRegistration();
      reg.updateAmount();
      getEntityManager().persist(reg);

      // Let's update the event
      ClubEvent tmpe = reg.getEvent();
      tmpe.updateAmount();
      getEntityManager().persist(tmpe);

      return res;

   }

   public String unregister()
   {

      ClubEventTicket ticket = getInstance();
      ClubEventRegistration reg = ticket.getRegistration();

      if (reg.getEvent().getStatus() == ClubEvent.CEState.closed)
      {

         facesMessages.add(
            "Event is closed to registration and can not be modified");

         return "regclosed";

      }

      String res = remove();

      // Let's update the registration
      reg = getInstance().getRegistration();
      reg.updateAmount();

      if (reg.getTickets().size() == 0)
      {

         // Nobody left. Let's cancel everything
         reg.cancelRegistration(authenticatedMember);

      }

      getEntityManager().persist(reg);

      // Let's update the event
      ClubEvent tmpe = reg.getEvent();
      tmpe.updateAmount();
      getEntityManager().persist(tmpe);

      return res;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
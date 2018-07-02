package org.jboss.seam.example.webassoc.clubs;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.example.webassoc.mail.MailProcessor;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;

@Name("clubEventRegistrationHome")
@Stateful
public class ClubEventRegistrationHomeImpl
        extends EntityHome<ClubEventRegistration>
        implements ClubEventRegistrationHome
{

   private static final long serialVersionUID = 328076026237924677L;
   @In(required = false)
   private ClubEvent clubEvent;
   @In(required = false)
   private AssocMember assocMember;
   @In(required = false)
   private AssocMember authenticatedMember;
   @In
   private FacesMessages facesMessages;
   @Logger
   private Log log;
   @In(create = true)
   private MailProcessor mailProcessor;
   @RequestParameter
   private Long ticketId;

   @Factory("clubEventRegistration")
   public ClubEventRegistration initClubEventRegistration()
   {

      return getInstance();

   }

   protected ClubEventRegistration createInstance()
   {

      ClubEventRegistration reg = new ClubEventRegistration();

      if (clubEvent != null)
      {

         reg.setEvent(clubEvent);
         clubEvent.getRegistrations().add(reg);

      }

      AssocMember tmpRegisteredMemberHack = null;
      AssocMember tmpRegisteringMemberHack = null;

      if (assocMember != null)
      {

         tmpRegisteredMemberHack = assocMember;

      }
      else if (authenticatedMember != null)
      {

         Query hack =
            getEntityManager().createQuery(
               "SELECT c FROM AssocMember c WHERE c.id = :authenticatedAssocId ");
         hack.setParameter("authenticatedAssocId",
            authenticatedMember.getId());
         tmpRegisteredMemberHack = (AssocMember)hack.getSingleResult();

      }

      if (tmpRegisteredMemberHack != null)
      {

         reg.setMember(tmpRegisteredMemberHack);
         tmpRegisteredMemberHack.getEvents().add(reg);

      }

      {

         Query hack =
            getEntityManager().createQuery(
               "SELECT c FROM AssocMember c WHERE c.id = :authenticatedAssocId ");
         hack.setParameter("authenticatedAssocId",
            ((authenticatedMember != null) ? authenticatedMember.getId()
                                           : Cts.PresidentIds.AssocMember));
         tmpRegisteringMemberHack = (AssocMember)hack.getSingleResult();
         reg.setRegisteringMember(tmpRegisteringMemberHack);

      }

      reg.setRegistrationDate(Calendar.getInstance().getTime());

      if ((clubEvent != null) && (clubEvent.getAcceptNewRegistration()))
      {

         reg.setStatus(ClubEventRegistration.CERState.registered);

      }
      else
      {

         reg.setStatus(ClubEventRegistration.CERState.waiting);

      }

      reg.setAdultMemberNb(new Integer(0));
      reg.setAdultNonMemberNb(new Integer(0));
      reg.setKidMemberNb(new Integer(0));
      reg.setKidNonMemberNb(new Integer(0));
      reg.setAmount(new Integer(0));
      reg.setPaid(Boolean.FALSE);
      reg.setPaymentInfo("");
      reg.setRefunded(Boolean.FALSE);
      reg.setRefundInfo("");

      return reg;

   }

   // ====================================================
   // Business operations
   // ====================================================
   public String unregister()
   {

      ClubEventRegistration reg = getInstance();

      if (reg.getEvent().getStatus() == ClubEvent.CEState.closed)
      {

         String msg =
            "Event is closed to registration and can not be modified";
         log.info(msg);
         facesMessages.add(msg);

         return "regclosed";

      }

      return remove();

   }

   public long countRegistration(AssocMember searchedMember,
      ClubEvent searchedEvent)
   {

      Query check =
         getEntityManager().createQuery(
            "SELECT DISTINCT COUNT(c.id) FROM ClubEventRegistration c WHERE c.member = :currentMember and c.event = :currentEvent");
      check.setParameter("currentMember", searchedMember);
      check.setParameter("currentEvent", searchedEvent);

      long count = (Long)check.getSingleResult();

      return count;

   }

   private String checkAndregister(boolean checkFirstForExistingRegistration)
   {

      ClubEventRegistration reg = getInstance();

      if (reg.getEvent().getStatus() == ClubEvent.CEState.closed)
      {

         String msg =
            "Event is closed to registration and can not be modified";
         log.info(msg);
         facesMessages.add(msg);

         return "regclosed";

      }

      if (checkFirstForExistingRegistration)
      {

         long count = countRegistration(reg.getMember(), reg.getEvent());

         if (count != 0)
         {

            String msg =
               reg.getMember().getAssocName()
               + " is already registered for event "
               + reg.getEvent().getTitle() + ". Please click the edit link";
            log.info(msg);
            facesMessages.add(msg);

            return "alreadyregistered";

         }

      }

      String res = persist();
      reg.addDefaultTickets(getEntityManager());
      reg.updateAmount();
      getEntityManager().persist(reg);

      ClubEvent tmpEvent = reg.getEvent();
      tmpEvent.updateAmount();
      getEntityManager().persist(tmpEvent);

      return res;

   }

   public String register()
   {

      return checkAndregister(true);

   }

   public String register_me()
   {

      if ((clubEvent != null) && (authenticatedMember != null))
      {

         long count = countRegistration(authenticatedMember, clubEvent);

         if (count != 0)
         {

            String msg =
               authenticatedMember.getAssocName()
               + " is already registered for event " + clubEvent.getTitle()
               + ". Please click the edit link";
            log.info(msg);
            facesMessages.add(msg);

            return "alreadyregistered";

         }
         else
         {

            return checkAndregister(false);

         }

      }
      else
      {

         return checkAndregister(true);

      }

   }

   public String unregister_me()
   {

      return unregister();

   }

   public String update()
   {

      updateAmount();

      return super.update();

   }

   public String updateAndClose()
   {

      updateAmount();
      super.update();

      return "updatedAndClosed";

   }

   public String updateAmount()
   {

      ClubEventRegistration reg = getInstance();

      if (reg.getEvent().getStatus() == ClubEvent.CEState.closed)
      {

         String msg =
            "Event is closed to registration and can not be modified";
         log.info(msg);
         facesMessages.add(msg);

         return "regclosed";

      }

      reg.updateAmount();
      getEntityManager().persist(reg);

      ClubEvent tmpe = reg.getEvent();
      tmpe.updateAmount();
      getEntityManager().persist(tmpe);

      return "amountUpdated";

   }

   public String emailRegistration()
   {

      ClubEventRegistration reg = getInstance();

      if (reg.getEvent().getStatus() == ClubEvent.CEState.closed)
      {

         String msg =
            "Event is closed to registration and can not be modified";
         log.info(msg);
         facesMessages.add(msg);

         return "regclosed";

      }

      reg.updateAmount();
      getEntityManager().persist(reg);
      mailProcessor.sendEMailNow("/mailing/registration.xhtml", reg.getEvent(),
         reg, reg.getDrivingDirectionLink());

      return "regopened";

   }

   public String confirmRegistration()
   {

      ClubEventRegistration reg = getInstance();

      if (reg.getEvent().getStatus() == ClubEvent.CEState.closed)
      {

         String msg =
            "Event is closed to registration and can not be modified";
         log.info(msg);
         facesMessages.add(msg);

         return "regclosed";

      }

      reg.confirmRegistration(authenticatedMember);
      getEntityManager().persist(reg);
      mailProcessor.sendEMailNow("/mailing/confirmation.xhtml", reg.getEvent(),
         reg, reg.getDrivingDirectionLink());

      return "regopened";

   }

   public String cancelRegistration()
   {

      ClubEventRegistration reg = getInstance();

      if (reg.getEvent().getStatus() == ClubEvent.CEState.closed)
      {

         String msg =
            "Event is closed to registration and can not be modified";
         log.info(msg);
         facesMessages.add(msg);

         return "regclosed";

      }

      reg.cancelRegistration(authenticatedMember);
      getEntityManager().persist(reg);
      mailProcessor.sendEMailNow("/mailing/cancellation.xhtml", reg.getEvent(),
         reg, reg.getDrivingDirectionLink());

      return "regopened";

   }

   public String enterWaitingList()
   {

      ClubEventRegistration reg = getInstance();

      if (reg.getEvent().getStatus() == ClubEvent.CEState.closed)
      {

         String msg =
            "Event is closed to registration and can not be modified";
         log.info(msg);
         facesMessages.add(msg);

         return "regclosed";

      }

      reg.enterWaitingList();
      getEntityManager().persist(reg);
      mailProcessor.sendEMailNow("/mailing/enterWaitingList.xhtml",
         reg.getEvent(), reg, reg.getDrivingDirectionLink());

      return "regopened";

   }

   public String exitWaitingList()
   {

      ClubEventRegistration reg = getInstance();

      if (reg.getEvent().getStatus() == ClubEvent.CEState.closed)
      {

         String msg =
            "Event is closed to registration and can not be modified";
         log.info(msg);
         facesMessages.add(msg);

         return "regclosed";

      }

      reg.exitWaitingList();
      getEntityManager().persist(reg);
      mailProcessor.sendEMailNow("/mailing/exitWaitingList.xhtml",
         reg.getEvent(), reg, reg.getDrivingDirectionLink());

      return "regopened";

   }

   public String emailPayment()
   {

      ClubEventRegistration reg = getInstance();
      mailProcessor.sendEMailNow("/mailing/payment.xhtml", reg.getEvent(), reg,
         reg.getDrivingDirectionLink());

      return "regopened";

   }

   //
   public String deleteTicket()
   {

      if ((ticketId == null))
      {

         String msg = "No Ticket selected";
         FacesMessages.instance().add(msg);
         log.info(msg);

         return "";

      }

      ClubEventRegistration reg = getInstance();

      if (reg.getEvent().getStatus() == ClubEvent.CEState.closed)
      {

         String msg =
            "Event is closed to registration and can not be modified";
         log.info(msg);
         facesMessages.add(msg);

         return "regclosed";

      }
      else
      {

         List<ClubEventTicket> tickets = reg.getTickets();

         for (ClubEventTicket ticket : tickets)
         {

            if (ticket.getId().equals(ticketId))
            {

               String msg =
                  "Ticket for [" + ticket.getFirstName() + "]["
                  + ticket.getLastName() + "] deleted";
               log.info(msg);
               facesMessages.add(msg);
               reg.getTickets().remove(ticket);
               getEntityManager().remove(ticket);

               // Let's update the registration
               reg.updateAmount();

               if (reg.getTickets().size() == 0)
               {

                  // Nobody left. Let's cancel everything
                  cancelRegistration();

               }

               getEntityManager().persist(reg);

               // Let's update the event
               ClubEvent tmpe = reg.getEvent();
               tmpe.updateAmount();
               getEntityManager().persist(tmpe);

               return "ticketDeleted";

            }

         }

         String msg = "Ticket with id [" + ticketId + "] not found";
         log.info(msg);
         facesMessages.add(msg);

         return "";

      }

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
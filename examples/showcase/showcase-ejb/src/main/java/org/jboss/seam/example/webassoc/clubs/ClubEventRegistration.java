package org.jboss.seam.example.webassoc.clubs;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.ClubMembership;
import org.jboss.seam.example.webassoc.mship.FamilyMember;
import org.jboss.seam.example.webassoc.util.Cts;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Name("clubEventRegistration")
@Table(
   name = "ClubEventRegistration",
   uniqueConstraints =
      {@UniqueConstraint(columnNames = {"member_id", "event_id"})}
)
public class ClubEventRegistration
        implements Serializable
{

   private static final long serialVersionUID = 4347090486388214142L;

   // =================================
   // CERState Definition
   // =================================
   public enum CERState
   {

      registered(0), confirmed(1), waiting(2), cancelled(3);

      private int id;

      private CERState(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }
   ;

   // =================================
   // Members
   // =================================
   private Long m_id;
   private AssocMember m_member;
   private ClubEvent m_event;
   private List<ClubEventTicket> m_tickets = new ArrayList<ClubEventTicket>();
   private AssocMember m_registeringMember;
   private AssocMember m_confirmingMember;
   private AssocMember m_cancellingMember;
   private Date m_registrationDate;
   private Date m_confirmationDate;
   private CERState m_status;
   private Date m_cancellationDate;
   private Integer m_adultMemberNb;
   private Integer m_kidMemberNb;
   private Integer m_adultNonMemberNb;
   private Integer m_kidNonMemberNb;
   private Integer m_amount = new Integer(0);
   private String m_paymentInfo;
   private boolean m_paid;
   private String m_refundInfo;
   private boolean m_refunded;
   private String m_notes;

   // =================================
   // Transient fields
   // =================================
   private transient String m_firstNames = null;

   // =================================
   // Getter/Setter
   // =================================
   @Id @GeneratedValue
   public Long getId()
   {

      return m_id;

   }

   public void setId(Long id)
   {

      this.m_id = id;

   }

   @NotNull
   public Date getRegistrationDate()
   {

      return m_registrationDate;

   }

   public void setRegistrationDate(Date registrationDate)
   {

      this.m_registrationDate = registrationDate;

   }

   public CERState getStatus()
   {

      return m_status;

   }

   public void setStatus(CERState status)
   {

      this.m_status = status;

   }

   @Transient
   public boolean getConfirmed()
   {

      return m_status == CERState.confirmed;

   }

   @Transient
   public boolean getCancelled()
   {

      return m_status == CERState.cancelled;

   }

   @Transient
   public boolean getWaitingList()
   {

      return m_status == CERState.waiting;

   }

   @Transient
   public boolean getRegistered()
   {

      return m_status == CERState.registered;

   }

   @Transient
   public boolean getMemberCanAddTicket()
   {

      return (((m_status == CERState.registered)
               || (m_status == CERState.waiting))
            && (getEvent().getAcceptNewRegistration()));

   }

   @Transient
   public boolean getMemberCanRemoveTicket()
   {

      return ((m_status == CERState.registered)
            || (m_status == CERState.waiting));

   }

   @Transient
   public boolean getMemberCanModifyTicket()
   {

      return ((m_status == CERState.registered)
            || (m_status == CERState.waiting));

   }

   public Date getConfirmationDate()
   {

      return m_confirmationDate;

   }

   public void setConfirmationDate(Date confirmationDate)
   {

      this.m_confirmationDate = confirmationDate;

   }

   public Date getCancellationDate()
   {

      return m_cancellationDate;

   }

   public void setCancellationDate(Date cancellationDate)
   {

      this.m_cancellationDate = cancellationDate;

   }

   @NotNull
   public Integer getAdultMemberNb()
   {

      return m_adultMemberNb;

   }

   public void setAdultMemberNb(Integer nb)
   {

      this.m_adultMemberNb = nb;

   }

   @NotNull
   public Integer getAdultNonMemberNb()
   {

      return m_adultNonMemberNb;

   }

   public void setAdultNonMemberNb(Integer nb)
   {

      this.m_adultNonMemberNb = nb;

   }

   @NotNull
   public Integer getKidMemberNb()
   {

      return m_kidMemberNb;

   }

   public void setKidMemberNb(Integer nb)
   {

      this.m_kidMemberNb = nb;

   }

   @NotNull
   public Integer getKidNonMemberNb()
   {

      return m_kidNonMemberNb;

   }

   public void setKidNonMemberNb(Integer nb)
   {

      this.m_kidNonMemberNb = nb;

   }

   public void setAmount(Integer amount)
   {

      this.m_amount = amount;

   }

   public Integer getAmount()
   {

      return m_amount;

   }

   public void setPaymentInfo(String paymentInfo)
   {

      this.m_paymentInfo = paymentInfo;

   }

   public String getPaymentInfo()
   {

      return m_paymentInfo;

   }

   public void setPaid(boolean paid)
   {

      this.m_paid = paid;

   }

   public boolean isPaid()
   {

      return m_paid;

   }

   public void setRefundInfo(String refundInfo)
   {

      this.m_refundInfo = refundInfo;

   }

   public String getRefundInfo()
   {

      return m_refundInfo;

   }

   public void setRefunded(boolean refunded)
   {

      this.m_refunded = refunded;

   }

   public boolean isRefunded()
   {

      return m_refunded;

   }

   public void setNotes(String notes)
   {

      this.m_notes = notes;

   }

   public String getNotes()
   {

      return m_notes;

   }

   // =================================
   // Relationship
   // =================================
   @NotNull @ManyToOne
   public AssocMember getMember()
   {

      return m_member;

   }

   public void setMember(AssocMember member)
   {

      this.m_member = member;

   }

   @NotNull @ManyToOne
   public ClubEvent getEvent()
   {

      return m_event;

   }

   public void setEvent(ClubEvent event)
   {

      this.m_event = event;

   }

   @OneToMany(mappedBy = "registration", cascade = CascadeType.REMOVE)
   @OrderBy("firstName")
   public List<ClubEventTicket> getTickets()
   {

      return m_tickets;

   }

   public void setTickets(List<ClubEventTicket> tickets)
   {

      this.m_tickets = tickets;

   }

   public void addDefaultTickets(EntityManager em)
   {

      boolean isClassicMember = m_member.isClassic();
      List<FamilyMember> fmembers = m_member.getFamilyMembers();
      List<FamilyMember> clubmembers = new ArrayList<FamilyMember>();
      ClubEvent theEvent = getEvent();

      for (FamilyMember fmember : fmembers)
      {

         // Let's look for the family member which are registered
         // to that club or activity
         List<ClubMembership> memberships = fmember.getMemberships();

         for (ClubMembership membership : memberships)
         {

            if (membership.getClub().equals(m_event.getClub()))
            {

               if ((theEvent.getAdultQuota() != 0) && fmember.isAdult())
               {

                  clubmembers.add(fmember);

               }
               else if ((theEvent.getKidQuota() != 0)
                  && (fmember.getFKind() == FamilyMember.FKind.kid))
               {

                  clubmembers.add(fmember);

               }

            }

         }

      }

      if (clubmembers.size() == 0)
      {

         // AssocMember did not took the time to register invidual familly
         // member to the club. Let's create a default ticket for all the
         // family member
         for (FamilyMember fmember : fmembers)
         {

            if ((theEvent.getAdultQuota() != 0) && fmember.isAdult())
            {

               clubmembers.add(fmember);

            }
            else if ((theEvent.getKidQuota() != 0)
               && (fmember.getFKind() == FamilyMember.FKind.kid))
            {

               clubmembers.add(fmember);

            }

         }

      }

      for (FamilyMember fmember : clubmembers)
      {

         ClubEventTicket ticket = new ClubEventTicket();
         ticket.setFirstName(fmember.getFirstName());
         ticket.setLastName(fmember.getLastName());

         if (isClassicMember)
         {

            if (fmember.isAdult())
            {

               ticket.setFKind(ClubEventTicket.FKind.adultMember);

            }
            else
            {

               ticket.setFKind(ClubEventTicket.FKind.kidMember);

            }

         }
         else
         {

            if (fmember.isAdult())
            {

               ticket.setFKind(ClubEventTicket.FKind.adultNonMember);

            }
            else
            {

               ticket.setFKind(ClubEventTicket.FKind.kidNonMember);

            }

         }

         ticket.setRegistration(this);
         getTickets().add(ticket);
         resetFirstNames();
         em.persist(ticket);

      }

   }

   @ManyToOne
   public AssocMember getRegisteringMember()
   {

      return m_registeringMember;

   }

   public void setRegisteringMember(AssocMember member)
   {

      this.m_registeringMember = member;

   }

   @ManyToOne
   public AssocMember getConfirmingMember()
   {

      return m_confirmingMember;

   }

   public void setConfirmingMember(AssocMember member)
   {

      this.m_confirmingMember = member;

   }

   @ManyToOne
   public AssocMember getCancellingMember()
   {

      return m_cancellingMember;

   }

   public void setCancellingMember(AssocMember member)
   {

      this.m_cancellingMember = member;

   }

   // ================================
   // Recompute the registration link
   // ================================
   public void updateAmount()
   {

      m_adultMemberNb = 0;
      m_adultNonMemberNb = 0;
      m_kidMemberNb = 0;
      m_kidNonMemberNb = 0;
      m_amount = 0;

      List<ClubEventTicket> tickets = getTickets();

      for (ClubEventTicket ticket : tickets)
      {

         if (ticket.getFKind() == ClubEventTicket.FKind.adultMember)
         {

            m_adultMemberNb++;
            m_amount += m_event.getAdultMemberFee();

         }
         else if (ticket.getFKind() == ClubEventTicket.FKind.adultNonMember)
         {

            m_adultNonMemberNb++;
            m_amount += m_event.getAdultNonMemberFee();

         }
         else if (ticket.getFKind() == ClubEventTicket.FKind.kidMember)
         {

            m_kidMemberNb++;
            m_amount += m_event.getKidMemberFee();

         }
         else if (ticket.getFKind() == ClubEventTicket.FKind.kidNonMember)
         {

            m_kidNonMemberNb++;
            m_amount += m_event.getKidNonMemberFee();

         }
         else if (ticket.getFKind() == ClubEventTicket.FKind.free)
         {

         }

      }

   }

   public void confirmRegistration(AssocMember confirmingMember)
   {

      updateAmount();
      setConfirmationDate(Calendar.getInstance().getTime());
      setConfirmingMember(confirmingMember);
      setStatus(CERState.confirmed);

   }

   public void cancelRegistration(AssocMember cancellingMember)
   {

      updateAmount();
      setCancellationDate(Calendar.getInstance().getTime());
      setCancellingMember(cancellingMember);
      setStatus(CERState.cancelled);

   }

   public void enterWaitingList()
   {

      updateAmount();
      setStatus(CERState.waiting);

   }

   public void exitWaitingList()
   {

      updateAmount();
      setStatus(CERState.registered);

   }

   // ================================
   // Recompute the registration link
   // ================================
   @Transient
   public String getDrivingDirectionLink()
   {

      String googleMap = "http://maps.google.com/maps";
      ClubEvent theEvent = getEvent();
      String daddr = null;

      if (theEvent.getAddress() != null)
      {

         daddr =
            "daddr=" + theEvent.getAddress().replaceFirst(" ", "+") + ","
            + theEvent.getCity().replaceFirst(" ", "+") + ","
            + theEvent.getState().replaceFirst(" ", "+") + ","
            + theEvent.getZip().replaceFirst(" ", "+");

      }
      else
      {

         daddr =
            "daddr=" + theEvent.getCity().replaceFirst(" ", "+") + ","
            + theEvent.getState().replaceFirst(" ", "+") + ","
            + theEvent.getZip().replaceFirst(" ", "+");

      }

      String saddr =
         "saddr=" + getMember().getAddress().replaceFirst(" ", "+") + ","
         + getMember().getCity().replaceFirst(" ", "+") + ","
         + getMember().getState().replaceFirst(" ", "+") + ","
         + getMember().getZip().replaceFirst(" ", "+");

      return googleMap + "?" + daddr + "&" + saddr;

   }

   @Transient
   public String getCancellationLink()
   {

      ExternalContext extCtxt =
         FacesContext.getCurrentInstance().getExternalContext();
      String registrationLink =
         ((javax.servlet.http.HttpServletRequest)extCtxt.getRequest())
         .getRequestURL().toString();

      // Temporary ugly hack
      registrationLink =
         "http://" + Cts.IPs.UGLYSERVERNAME + "/" + Cts.IPs.UGLYWARNAME
         + "/editClubEventRegistration.seam";
      registrationLink =
         registrationLink
         + ((registrationLink.indexOf("?") != -1) ? "&pagesClubId="
                                                  : "?pagesClubId=")
         + getEvent().getClub().getId();
      registrationLink =
         registrationLink
         + ((registrationLink.indexOf("?") != -1) ? "&pagesClubEventId="
                                                  : "?pagesClubEventId=")
         + getEvent().getId();
      registrationLink =
         registrationLink
         + ((registrationLink.indexOf("?") != -1)
            ? "&pagesClubEventRegistrationId="
            : "?pagesClubEventRegistrationId=") + getId();

      return registrationLink;

   }

   @Transient
   public String getFirstNames()
   {

      if (m_firstNames != null)
      {

         return m_firstNames;

      }

      m_firstNames = "";

      for (ClubEventTicket ticket : getTickets())
      {

         m_firstNames += ticket.getFirstName() + " ";

      }

      return m_firstNames;

   }

   @Transient
   public void resetFirstNames()
   {

      m_firstNames = null;

   }

   @Transient
   public String getCssStyleName()
   {

      if (m_status == CERState.confirmed)
      {

         return "confirmed";

      }
      else if (m_status == CERState.cancelled)
      {

         return "cancelled";

      }
      else if (m_status == CERState.waiting)
      {

         return "waiting";

      }
      else
      {

         return "registered";

      }

   }

}
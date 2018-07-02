package org.jboss.seam.example.webassoc.mail;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mship.AssocMember;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Name("mailingControlerMembership")
@Table(
   name = "Mailing",
   uniqueConstraints =
      {@UniqueConstraint(columnNames = {"member_id", "mailingController_id"})}
)
public class Mailing
        implements Serializable
{

   private static final long serialVersionUID = -2027439651759750590L;

   // =================================
   // Kind Definition
   // =================================
   /**
    * notSent: Mailing not sent yet
    * announcement: Event has been announced
    * reminder: First Reminder of the event
    * secondReminder: Second Reminder of the event
    * dontForget: Don't forget the event is tomorrow
    * sent: Mailing has be sent and must not be sent again
    */
   public enum MState
   {

      notSent(0), announced(1), reminded(2), remindedAgain(3),
      dontForgetSent(4), sent(5);

      private int id;

      private MState(int id)
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
   private MailingController m_mailingControler;
   private MState m_state;

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

   @Transient
   public boolean isSent()
   {

      return m_state.equals(MState.sent);

   }

   public void setState(MState sent)
   {

      this.m_state = sent;

   }

   public MState getState()
   {

      return m_state;

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
   public MailingController getMailingController()
   {

      return m_mailingControler;

   }

   public void setMailingController(MailingController mailingControler)
   {

      this.m_mailingControler = mailingControler;

   }

}
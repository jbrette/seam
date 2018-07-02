package org.jboss.seam.example.webassoc.vote;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.util.Cts;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Name("ballot")
@Table(
   name = "Ballot",
   uniqueConstraints =
      {@UniqueConstraint(columnNames = {"member_id", "vote_id"})}
)
public class Ballot
        implements Serializable
{

   private static final long serialVersionUID = -7557905976534632451L;

   // =================================
   // Kind Definition
   // =================================
   public enum BRes
   {

      notCast(0), yea(1), nay(2), blank(3);

      private int id;

      private BRes(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }

   /**
    * notSent: Mailing not sent yet
    * sent: Mailing has be sent and must not be sent again
    */
   public enum MState
   {

      notSent(0), sent(1), newOne(2);

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
   private Vote m_vote;
   private BRes m_res;
   private Date m_castingDate;
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

   public BRes getRes()
   {

      return m_res;

   }

   public void setRes(BRes res)
   {

      m_res = res;

   }

   public Date getCastingDate()
   {

      return m_castingDate;

   }

   public void setCastingDate(Date castingDate)
   {

      m_castingDate = castingDate;

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
   public Vote getVote()
   {

      return m_vote;

   }

   public void setVote(Vote vote)
   {

      this.m_vote = vote;

   }

   @Transient
   public boolean isBlocked()
   {

      return ((m_castingDate != null)
            || (m_vote.getEndDate().getTime()
               < Calendar.getInstance().getTime().getTime()));

   }

   @Transient
   public String getBallotCastingLink()
   {

      ExternalContext extCtxt =
         FacesContext.getCurrentInstance().getExternalContext();
      String registrationLink =
         ((javax.servlet.http.HttpServletRequest)extCtxt.getRequest())
         .getRequestURL().toString();

      // Temporary ugly hack
      registrationLink =
         "http://" + Cts.IPs.UGLYSERVERNAME + "/" + Cts.IPs.UGLYWARNAME
         + "/castBallot.seam";
      registrationLink =
         registrationLink
         + ((registrationLink.indexOf("?") != -1) ? "&pagesVoteId="
                                                  : "?pagesVoteId=")
         + getVote().getId();
      registrationLink =
         registrationLink
         + ((registrationLink.indexOf("?") != -1) ? "&pagesBallotId="
                                                  : "?pagesBallotId=")
         + getId();

      return registrationLink;

   }

}
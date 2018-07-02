package org.jboss.seam.example.webassoc.vote;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.Cts;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@Name("vote")
public class Vote
        implements Serializable
{

   private static final long serialVersionUID = 2516080509819411419L;

   // =================================
   // Kind Definition
   // =================================
   public enum VKind
   {

      allmembers(1), committeemembers(2);

      private int id;

      private VKind(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }

   // =================================
   // Sending State Definition
   // =================================
   /**
    * notSent: Mailing not sent yet
    * announcement: Event has been announced
    * reminder: First Reminder of the event
    * secondReminder: Second Reminder of the event
    * dontForget: Don't forget the event is tomorrow
    * sent: Mailing has be sent and must not be sent again
    */
   public enum VState
   {

      initialState(0), announcementStarted(1), announcementEnded(2),
      finalState(3);

      private int id;

      private VState(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }

   // =================================
   // Members
   // =================================
   private Long m_id;
   private String m_title;
   private Date m_startDate;
   private Date m_endDate;
   private String m_content;
   private List<Ballot> m_ballots = new ArrayList<Ballot>();
   private List<VoteAttachment> m_attachments =
      new ArrayList<VoteAttachment>();
   private VKind m_kind;
   private int m_resultYea;
   private int m_resultNay;
   private int m_resultBlank;
   private int m_resultCast;
   private VState m_sendingState;

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

      m_id = id;

   }

   @Length(max = 50)
   public String getTitle()
   {

      return m_title;

   }

   public void setTitle(String name)
   {

      m_title = name;

   }

   @Lob
   public String getContent()
   {

      return m_content;

   }

   public void setContent(String description)
   {

      if ((description != null) && (description.length() == 0))
      {

         // JEB: Protect against HIBERNATE-MYSQL CLOB issue
         m_content = " ";

      }
      else
      {

         m_content = description;

      }

   }

   @Transient
   public String getARefCheckedContent()
   {

      String content = getContent();
      content = content.replaceAll("href=\"http", "FOO");
      content = content.replaceAll("href=\"mailto", "BAR");
      content =
         content.replaceAll("href=\"",
            "href=\"http://" + Cts.IPs.UGLYSERVERNAME + "/"
            + Cts.IPs.UGLYWARNAME + "/");
      content = content.replaceAll("FOO", "href=\"http");
      content = content.replaceAll("BAR", "href=\"mailto");

      return content;

   }

   @NotNull
   public Date getStartDate()
   {

      return m_startDate;

   }

   public void setStartDate(Date startDate)
   {

      m_startDate = startDate;

   }

   @NotNull
   public Date getEndDate()
   {

      return m_endDate;

   }

   public void setEndDate(Date endDate)
   {

      m_endDate = endDate;

   }

   /**
    * Request to run to create the mailing list
    *
    * @return
    */
   public VKind getVKind()
   {

      return m_kind;

   }

   public void setVKind(VKind kind)
   {

      m_kind = kind;

   }

   public void setResultYea(int resultYea)
   {

      this.m_resultYea = resultYea;

   }

   public int getResultYea()
   {

      return m_resultYea;

   }

   public void setResultNay(int resultNay)
   {

      this.m_resultNay = resultNay;

   }

   public int getResultNay()
   {

      return m_resultNay;

   }

   public void setResultBlank(int resultBlank)
   {

      this.m_resultBlank = resultBlank;

   }

   public int getResultBlank()
   {

      return m_resultBlank;

   }

   public void setResultCast(int resultCast)
   {

      this.m_resultCast = resultCast;

   }

   public int getResultCast()
   {

      return m_resultCast;

   }

   public VState getSendingState()
   {

      return m_sendingState;

   }

   public void setSendingState(VState sending)
   {

      this.m_sendingState = sending;

   }

   // ============================================
   // Announcement control
   // ============================================
   @Transient
   public boolean isReadyForAnnouncement()
   {

      VState sendingState = getSendingState();

      return ((sendingState.equals(VState.initialState))
            || (sendingState.equals(VState.announcementEnded)));

   }

   // ================================
   // Recompute the total data
   // ================================
   public void updateResults()
   {

      int nbBallot = m_ballots.size();
      int nbCast = 0;
      int nbYea = 0;
      int nbNay = 0;
      int nbBlank = 0;

      for (Ballot theBallot : m_ballots)
      {

         if (theBallot.getRes() == Ballot.BRes.yea)
         {

            nbCast++;
            nbYea++;

         }
         else if (theBallot.getRes() == Ballot.BRes.nay)
         {

            nbCast++;
            nbNay++;

         }
         else if (theBallot.getRes() == Ballot.BRes.blank)
         {

            nbCast++;
            nbBlank++;

         }

      }

      m_resultYea = ((nbYea * 100) / nbCast);
      m_resultNay = ((nbNay * 100) / nbCast);
      m_resultBlank = ((nbBlank * 100) / nbCast);
      m_resultCast = ((nbCast * 100) / nbBallot);

   }

   // =================================
   // Relationship
   // =================================
   @OneToMany(mappedBy = "vote", cascade = CascadeType.REMOVE)
   public List<Ballot> getBallots()
   {

      return m_ballots;

   }

   public void setBallots(List<Ballot> ballots)
   {

      m_ballots = ballots;

   }

   @OneToMany(mappedBy = "vote", cascade = CascadeType.REMOVE)
   public List<VoteAttachment> getAttachments()
   {

      return m_attachments;

   }

   public void setAttachments(List<VoteAttachment> attachments)
   {

      m_attachments = attachments;

   }

}
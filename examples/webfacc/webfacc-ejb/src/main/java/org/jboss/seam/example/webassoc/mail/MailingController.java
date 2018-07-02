package org.jboss.seam.example.webassoc.mail;

import org.hibernate.validator.Length;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mail.Mailing;
import org.jboss.seam.example.webassoc.util.Cts;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@Name("mailingController")
@Inheritance(strategy = InheritanceType.JOINED)
public class MailingController
        implements Serializable
{

   private static final long serialVersionUID = 2212390746857154554L;

   // =================================
   // Kind Definition
   // =================================
   public enum RKind
   {

      testmembers(0), validentries(1), allmembers(2),
      nonlatebulletinmembers(3), latebulletinmembers(4),
      otherbulletinmembers(5), nonlatemembers(6), latemembers(7),
      guestmembers(8), sponsors(9), advertisers(10), partnerorgs(11),
      committeemembers(12), kid_0_3_Members(13), kid_3_6_Members(14),
      kid_6_10_Members(15), kid_10_15_Members(16), kid_15_18_Members(17),
      kid_18_20_Members(18), kid_21_plus_Members(19), clubMembers(20);

      private int id;

      private RKind(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }

   // =================================
   // Kind Definition
   // =================================
   public enum NKind
   {

      newsLetter(0), internal(1), eventAnnouncement(2);

      private int id;

      private NKind(int id)
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
   public enum MCState
   {

      initialState(0), announcementStarted(1), announcementEnded(2),
      reminderedStarted(3), reminderedEnded(4), secondReminderStarted(5),
      secondReminderEnded(6), dontForgetStarted(7), dontForgetEnded(8),
      finalState(9);

      private int id;

      private MCState(int id)
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
   private Date m_date;
   private String m_content;
   private List<Mailing> m_mailings = new ArrayList<Mailing>();
   private List<MailAttachment> m_attachments =
      new ArrayList<MailAttachment>();
   private String m_templateFile;
   private RKind m_mailingListBuildRequest;
   private NKind m_kind;
   private MCState m_sendingState;

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

   // @NotNull
   public Date getDate()
   {

      return m_date;

   }

   public void setDate(Date commentDate)
   {

      m_date = commentDate;

   }

   /**
    * Path to the template to use for the email
    *
    * @param templateFile
    */
   public String getTemplateFile()
   {

      return m_templateFile;

   }

   public void setTemplateFile(String templateFile)
   {

      m_templateFile = templateFile;

   }

   /**
    * Request to run to create the mailing list
    *
    * @return
    */
   public RKind getMailingListBuildRequest()
   {

      return m_mailingListBuildRequest;

   }

   public void setMailingListBuildRequest(RKind mailingListBuildRequest)
   {

      m_mailingListBuildRequest = mailingListBuildRequest;

   }

   /**
    * Request to run to create the mailing list
    *
    * @return
    */
   public NKind getNKind()
   {

      return m_kind;

   }

   public void setNKind(NKind kind)
   {

      m_kind = kind;

   }

   public MCState getSendingState()
   {

      return m_sendingState;

   }

   public void setSendingState(MCState sendingState)
   {

      this.m_sendingState = sendingState;

   }

   // =================================
   // Relationship
   // =================================
   @OneToMany(mappedBy = "mailingController", cascade = CascadeType.REMOVE)
   public List<Mailing> getMailings()
   {

      return m_mailings;

   }

   public void setMailings(List<Mailing> mailings)
   {

      m_mailings = mailings;

   }

   @OneToMany(mappedBy = "mailingController", cascade = CascadeType.REMOVE)
   public List<MailAttachment> getAttachments()
   {

      return m_attachments;

   }

   public void setAttachments(List<MailAttachment> attachments)
   {

      m_attachments = attachments;

   }

   @Transient
   public MailAttachment getFirstAttachment()
   {

      if (m_attachments.size() > 0)
      {

         return m_attachments.get(0);

      }
      else
      {

         MailAttachment tmp = new MailAttachment();
         tmp.setFileName("EMPTY");
         tmp.setData(new byte[0]);
         tmp.setContentType("image/jpeg");

         return tmp;

      }

   }

}
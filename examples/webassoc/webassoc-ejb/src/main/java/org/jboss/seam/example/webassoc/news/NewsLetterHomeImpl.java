package org.jboss.seam.example.webassoc.news;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.example.webassoc.mail.MailAttachment;
import org.jboss.seam.example.webassoc.mail.MailProcessor;
import org.jboss.seam.example.webassoc.mail.Mailing;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.AssocMemberHome;
import org.jboss.seam.example.webassoc.mship.FamilyMember;
import org.jboss.seam.example.webassoc.mship.FamilyMemberHome;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;

@Name("newsLetterHome")
@Stateful
public class NewsLetterHomeImpl
        extends EntityHome<NewsLetter>
        implements NewsLetterHome
{

   private static final long serialVersionUID = -1201171200802938517L;
   @Logger
   private Log log;
   @In(create = true)
   private MailProcessor mailProcessor;
   @In(required = false)
   private AssocMember authenticatedMember;
   @In(create = true)
   private AssocMemberHome assocMemberHome;
   @In(create = true)
   private FamilyMemberHome familyMemberHome;
   @In
   private FacesMessages facesMessages;
   @RequestParameter
   private Integer attachmentId;
   @RequestParameter
   private Long mailingId;

   // Image fields
   private byte[] m_uploadedData;
   private String m_uploadedContentType;
   private String m_uploadedFileName;

   @Factory("newsLetter")
   public NewsLetter initNewsLetter()
   {

      return getInstance();

   }

   protected NewsLetter createInstance()
   {

      NewsLetter res = new NewsLetter();
      res.setTitle("The Title of EMAIL");
      res.setContent("<b>New NewsLetter</b>");
      res.setDate(Calendar.getInstance().getTime());
      res.setNKind(NewsLetter.NKind.newsLetter);
      res.setMailingListBuildRequest(NewsLetter.RKind.testmembers);
      res.setTemplateFile("/mailing/simplemail.xhtml");
      res.setSendingState(NewsLetter.MCState.initialState);

      return res;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

   @SuppressWarnings("unchecked")
   public String buildMailingList()
   {

      NewsLetter newsLetter = getInstance();
      Query assocMbQuery = null;
      Query famMbQuery = null;

      if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.testmembers)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.testmembers);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.validentries)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.validentries);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.nonlatebulletinmembers)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(
               AssocMemberHome.RKind.nonlatebulletinmembers);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.latebulletinmembers)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(
               AssocMemberHome.RKind.latebulletinmembers);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.otherbulletinmembers)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(
               AssocMemberHome.RKind.otherbulletinmembers);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.allmembers)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.allmembers);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.nonlatemembers)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.nonlatemembers);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.latemembers)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.latemembers);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.guestmembers)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.guestmembers);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.sponsors)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.sponsors);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.advertisers)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.advertisers);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.partnerorgs)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.partnerorgs);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.committeemembers)
      {

         assocMbQuery =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.committeemembers);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.kid_0_3_Members)
      {

         famMbQuery =
            familyMemberHome.buildQuery(
               FamilyMemberHome.RKind.kid_0_3_Members);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.kid_3_6_Members)
      {

         famMbQuery =
            familyMemberHome.buildQuery(
               FamilyMemberHome.RKind.kid_3_6_Members);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.kid_6_10_Members)
      {

         famMbQuery =
            familyMemberHome.buildQuery(
               FamilyMemberHome.RKind.kid_6_10_Members);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.kid_10_15_Members)
      {

         famMbQuery =
            familyMemberHome.buildQuery(
               FamilyMemberHome.RKind.kid_10_15_Members);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.kid_15_18_Members)
      {

         famMbQuery =
            familyMemberHome.buildQuery(
               FamilyMemberHome.RKind.kid_15_18_Members);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.kid_18_20_Members)
      {

         famMbQuery =
            familyMemberHome.buildQuery(
               FamilyMemberHome.RKind.kid_18_20_Members);

      }
      else if (newsLetter.getMailingListBuildRequest()
         == NewsLetter.RKind.kid_21_plus_Members)
      {

         famMbQuery =
            familyMemberHome.buildQuery(
               FamilyMemberHome.RKind.kid_21_plus_Members);

      }
      else
      {

         assocMbQuery = null;
         famMbQuery = null;

      }

      List<AssocMember> members = null;

      if ((assocMbQuery == null) && (famMbQuery == null))
      {

         members = new ArrayList<AssocMember>();

      }
      else if ((assocMbQuery == null) && (famMbQuery != null))
      {

         members = new ArrayList<AssocMember>();

         List<FamilyMember> fmembers = famMbQuery.getResultList();

         for (FamilyMember fmember : fmembers)
         {

            members.add(fmember.getAssocMember());

         }

      }
      else if ((assocMbQuery != null) && (famMbQuery == null))
      {

         members = assocMbQuery.getResultList();

      }

      Query alreadyInMailingList =
         getEntityManager().createQuery(
            "select m from Mailing m where m.member = :member and m.mailingController = :newsLetter");

      for (AssocMember member : members)
      {

         if (member.getEMailValid())
         {

            alreadyInMailingList.setParameter("member", member);
            alreadyInMailingList.setParameter("newsLetter", newsLetter);

            List<Mailing> found = alreadyInMailingList.getResultList();

            if (found.size() == 0)
            {

               Mailing mailing = new Mailing();
               mailing.setMember(member);
               mailing.setMailingController(newsLetter);
               mailing.setState(Mailing.MState.notSent);
               newsLetter.getMailings().add(mailing);
               member.getMailings().add(mailing);
               getEntityManager().persist(mailing);

            }

         }
         else
         {

            log.info("Member " + member.getAssocName()
               + " does not have a valid EMail. skipping");

         }

      }

      getEntityManager().persist(newsLetter);

      return "";

   }

   public String sendTestEmail()
   {

      NewsLetter newsLetter = getInstance();
      mailProcessor.sendNewsLetter2Me(Cts.Delays.NEWS_LETTER,
         newsLetter.getId(), authenticatedMember.getId());

      return "";

   }

   public String send2MailingList()
   {

      NewsLetter newsLetter = getInstance();
      NewsLetter.MCState currState = newsLetter.getSendingState();

      if (!((currState.equals(NewsLetter.MCState.initialState))
            || (currState.equals(NewsLetter.MCState.announcementEnded))))
      {

         String msg = newsLetter.getTitle() + " still in state " + currState;
         facesMessages.add(msg);

         return "wrongstate";

      }
      else
      {

         try
         {

            QuartzTriggerHandle res =
               mailProcessor.scheduleSendNewsLetter2MailingList(
                  Cts.Delays.NEWS_LETTER, newsLetter.getId());
            log.info("sendNewsLetter2MailingList returned " + res.toString());

         }
         catch (Exception ex)
         {

            log.info("sendNewsLetter2MailingList did not returned");

         }

      }

      return "";

   }

   public String emptyMailingList()
   {

      NewsLetter newsLetter = getInstance();
      Query mailingQuery =
         getEntityManager().createQuery(
            "delete from Mailing c where c.mailingController = :newsLetter and c.state = :state");
      mailingQuery.setParameter("newsLetter", newsLetter);
      mailingQuery.setParameter("state", Mailing.MState.notSent);
      mailingQuery.executeUpdate();
      newsLetter.setMailings(new ArrayList<Mailing>());
      getEntityManager().persist(newsLetter);

      return "";

   }

   public String markAsSent()
   {

      NewsLetter newsLetter = getInstance();
      Query mailingQuery =
         getEntityManager().createQuery(
            "update Mailing c set c.state = :state where c.mailingController = :newsLetter");
      mailingQuery.setParameter("state", Mailing.MState.sent);
      mailingQuery.setParameter("newsLetter", newsLetter);
      mailingQuery.executeUpdate();
      newsLetter.setSendingState(NewsLetter.MCState.finalState);
      getEntityManager().persist(newsLetter);

      return "";

   }

   // Used to tag as sent a newsLetter
   public String toggleToSent()
   {

      return toggleState(Boolean.TRUE);

   }

   // Used to tag as unsent
   public String toggleToUnsent()
   {

      return toggleState(Boolean.FALSE);

   }

   //
   public String toggleState(Boolean newState)
   {

      if ((mailingId == null))
      {

         String msg = "No Mailing selected";
         FacesMessages.instance().add(msg);
         log.info(msg);

         return "failed";

      }

      Mailing foundMailing = getEntityManager().find(Mailing.class, mailingId);

      if (foundMailing != null)
      {

         foundMailing.setState((newState ? Mailing.MState.sent
                                         : Mailing.MState.notSent));
         getEntityManager().persist(foundMailing);

         return "toggled";

      }
      else
      {

         return "failed";

      }

   }

   //
   public String deleteMailing()
   {

      if ((mailingId == null))
      {

         String msg = "No Mailing selected";
         FacesMessages.instance().add(msg);
         log.info(msg);

         return "failed";

      }

      NewsLetter reg = getInstance();
      List<Mailing> mailings = reg.getMailings();

      for (Mailing mailing : mailings)
      {

         if (mailing.getId().equals(mailingId))
         {

            String msg =
               "Mailing for [" + mailing.getMember().getAssocName()
               + "] deleted";
            log.info(msg);
            reg.getMailings().remove(mailing);
            getEntityManager().remove(mailing);
            getEntityManager().persist(reg);

            return "mailingDeleted";

         }

      }

      return "failed";

   }

   public void setUploadedData(byte[] uploadedData)
   {

      m_uploadedData = uploadedData;

   }

   public void setUploadedContentType(String contentType)
   {

      m_uploadedContentType = contentType;

   }

   public void setUploadedFileName(String uploadedFileName)
   {

      String[] tmp =
         ((uploadedFileName != null) ? uploadedFileName.split("/")
                                     : new String[] {""});
      m_uploadedFileName = tmp[tmp.length - 1];

   }

   public String uploadAttachment()
   {

      if ((m_uploadedData == null) || (m_uploadedData.length == 0))
      {

         FacesMessages.instance().add("No Attachment selected");

         return "";

      }
      else
      {

         NewsLetter newsLetter = getInstance();
         MailAttachment doc = new MailAttachment();
         doc.setMailingController(newsLetter);
         doc.setData(m_uploadedData);
         doc.setContentType(m_uploadedContentType);
         doc.setFileName(m_uploadedFileName);
         doc.updateThumbnail();
         newsLetter.getAttachments().add(doc);
         getEntityManager().persist(doc);
         m_uploadedData = null;
         m_uploadedContentType = null;
         m_uploadedFileName = null;

         return "attachmentUploaded";

      }

   }

   public String deleteAttachment()
   {

      if ((attachmentId == null))
      {

         FacesMessages.instance().add("No Attachment selected");

         return "";

      }
      else
      {

         NewsLetter newsLetter = getInstance();
         List<MailAttachment> attachments = newsLetter.getAttachments();

         for (MailAttachment attachment : attachments)
         {

            if (attachment.getImageId().equals(attachmentId))
            {

               newsLetter.getAttachments().remove(attachment);
               getEntityManager().remove(attachment);
               getEntityManager().persist(newsLetter);

               return "attachmentDeleted";

            }

         }

         return "";

      }

   }

}
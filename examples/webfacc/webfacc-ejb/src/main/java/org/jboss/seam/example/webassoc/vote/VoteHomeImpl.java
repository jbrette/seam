package org.jboss.seam.example.webassoc.vote;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.example.webassoc.mail.MailProcessor;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.AssocMemberHome;
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

@Name("voteHome")
@Stateful
public class VoteHomeImpl
        extends EntityHome<Vote>
        implements VoteHome
{

   private static final long serialVersionUID = 6693036930628707715L;
   @Logger
   private Log log;
   @In(create = true)
   private MailProcessor mailProcessor;
   @In(required = false)
   private AssocMember authenticatedMember;
   @In(create = true)
   private AssocMemberHome assocMemberHome;
   @RequestParameter
   private Integer attachmentId;
   @In
   private FacesMessages facesMessages;

   // Image download fields
   private byte[] m_uploadedData;
   private String m_uploadedContentType;
   private String m_uploadedFileName;

   @Factory("vote")
   public Vote initVote()
   {

      return getInstance();

   }

   protected Vote createInstance()
   {

      Vote res = new Vote();
      res.setTitle("Object of the vote");
      res.setContent("<b>New Vote</b>");

      Calendar cal = Calendar.getInstance();
      res.setStartDate(cal.getTime());
      res.setEndDate(cal.getTime());
      res.setVKind(Vote.VKind.committeemembers);
      res.setResultBlank(0);
      res.setResultYea(0);
      res.setResultNay(0);
      res.setResultCast(0);

      // res.setBallotListBuildRequest(Vote.RKind.allmembers);
      res.setSendingState(Vote.VState.initialState);

      return res;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

   @SuppressWarnings("unchecked")
   public String buildBallotList()
   {

      Vote vote = getInstance();
      Query query = null;

      if (vote.getVKind() == Vote.VKind.allmembers)
      {

         query = assocMemberHome.buildQuery(AssocMemberHome.RKind.allmembers);

      }
      else if (vote.getVKind() == Vote.VKind.committeemembers)
      {

         query =
            assocMemberHome.buildQuery(AssocMemberHome.RKind.committeemembers);

      }
      else
      {

         query = null;

      }

      List<AssocMember> members = null;

      if (query == null)
      {

         members = new ArrayList<AssocMember>();

      }
      else if (query != null)
      {

         members = query.getResultList();

      }

      Query alreadyInBallotList =
         getEntityManager().createQuery(
            "select b from Ballot b where b.member = :member and b.vote = :vote");

      for (AssocMember member : members)
      {

         if (member.getEMailValid())
         {

            alreadyInBallotList.setParameter("member", member);
            alreadyInBallotList.setParameter("vote", vote);

            List<Ballot> found = alreadyInBallotList.getResultList();

            if (found.size() == 0)
            {

               Ballot ballot = new Ballot();
               ballot.setMember(member);
               ballot.setVote(vote);
               ballot.setCastingDate(null);
               ballot.setRes(Ballot.BRes.notCast);
               ballot.setState(Ballot.MState.notSent);
               vote.getBallots().add(ballot);
               member.getBallots().add(ballot);
               getEntityManager().persist(ballot);

            }

         }
         else
         {

            log.info("Member " + member.getAssocName()
               + " does not have a valid EMail. skipping");

         }

      }

      getEntityManager().persist(vote);

      return "";

   }

   public String emptyBallotList()
   {

      Vote vote = getInstance();
      Query ballotQuery =
         getEntityManager().createQuery(
            "delete from Ballot b where b.vote = :vote and b.res = :res");
      ballotQuery.setParameter("vote", vote);
      ballotQuery.setParameter("res", Ballot.BRes.notCast);
      ballotQuery.executeUpdate();
      vote.setBallots(new ArrayList<Ballot>());
      getEntityManager().persist(vote);

      return "";

   }

   public String markAsSent()
   {

      Vote vote = getInstance();
      Query mailingQuery =
         getEntityManager().createQuery(
            "update Ballot b set b.state = :state where b.vote = :vote");
      mailingQuery.setParameter("state", Ballot.MState.sent);
      mailingQuery.setParameter("vote", vote);
      mailingQuery.executeUpdate();
      vote.setSendingState(Vote.VState.finalState);
      getEntityManager().persist(vote);

      return "";

   }

   public String sendTestEmail()
   {

      Vote vote = getInstance();
      mailProcessor.sendVote2Me(1000, vote.getId(),
         authenticatedMember.getId());

      return "";

   }

   public String announceVote()
   {

      Vote vote = getInstance();
      Vote.VState currState = vote.getSendingState();

      if (!((currState.equals(Vote.VState.initialState))
            || (currState.equals(Vote.VState.announcementEnded))))
      {

         String msg = vote.getTitle() + " still in state " + currState;
         facesMessages.add(msg);

         return "wrongstate";

      }
      else
      {

         try
         {

            QuartzTriggerHandle res =
               mailProcessor.announceVote(Cts.Delays.VOTE, vote.getId());
            log.info("announceVote returned " + res.toString());

         }
         catch (Exception ex)
         {

            log.info("announceVote did not returned");

         }

         return "";

      }

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

         Vote vote = getInstance();
         VoteAttachment doc = new VoteAttachment();
         doc.setVote(vote);
         doc.setData(m_uploadedData);
         doc.setContentType(m_uploadedContentType);
         doc.setFileName(m_uploadedFileName);
         doc.updateThumbnail();
         vote.getAttachments().add(doc);
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

         Vote vote = getInstance();
         List<VoteAttachment> attachments = vote.getAttachments();

         for (VoteAttachment attachment : attachments)
         {

            if (attachment.getImageId().equals(attachmentId))
            {

               vote.getAttachments().remove(attachment);
               getEntityManager().remove(attachment);
               getEntityManager().persist(vote);

               return "attachmentDeleted";

            }

         }

         return "";

      }

   }

   public String updateResults()
   {

      Vote vote = getInstance();

      if (vote.getEndDate().getTime()
         < Calendar.getInstance().getTime().getTime())
      {

         vote.updateResults();
         getEntityManager().persist(vote);

         return "updated";

      }
      else
      {

         return "unchanged";

      }

   }

}
package org.jboss.seam.example.webassoc.mail;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Duration;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.FinalExpiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.example.webassoc.article.Article;
import org.jboss.seam.example.webassoc.classified.Classified;
import org.jboss.seam.example.webassoc.clubs.ClubEvent;
import org.jboss.seam.example.webassoc.clubs.ClubEventRegistration;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.news.NewsLetter;
import org.jboss.seam.example.webassoc.orders.AccountOrder;
import org.jboss.seam.example.webassoc.orders.GuideOrder;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.example.webassoc.vote.Ballot;
import org.jboss.seam.example.webassoc.vote.Vote;
import org.jboss.seam.example.webassoc.yellowpages.YellowPage;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import javax.faces.FacesException;

import javax.jms.JMSException;
import javax.jms.QueueSender;
import javax.jms.QueueSession;

import javax.mail.MessagingException;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

@Stateless
@Name("mailProcessor")
public class MailProcessorImpl
        implements MailProcessor
{

   private final static int RETRY_COUNT = 5;
   private final static String SMTP_MAGIC_ERROR = "response: 421";
   private final static boolean REALLY_SENT_MAIL = true;
   @Logger
   private Log log;
   @In
   private FacesMessages facesMessages;
   @In
   EntityManager entityManager;
   @In(required = false)
   QuartzTriggerHandle timer;
   @In
   Events events;
   @In
   private QueueSender mailingQueueSender;
   @In
   private QueueSession queueSession;

   /**
    * Utility method to trace the completion of the sending
    */
   private void successfulSending(String msgName, AssocMember amember)
   {

      facesMessages.add("Sent e-mail successfully to ["
         + amember.getAssocName() + "][" + amember.getEmail() + "]");
      log.info("Sent " + msgName + " e-mail successfully to ["
         + amember.getAssocName() + "][" + amember.getEmail() + "]");

   }

   private void successfulScheduling(String msgName, AssocMember amember)
   {

      facesMessages.add("Scheduled e-mail successfully to ["
         + amember.getAssocName() + "][" + amember.getEmail() + "]");
      log.info("Scheduled " + msgName + " e-mail successfully to ["
         + amember.getAssocName() + "][" + amember.getEmail() + "]");

   }

   private void failureToSent(String msgName, AssocMember amember, Exception e)
   {

      facesMessages.add("Sending e-mail failed for [" + amember.getAssocName()
         + "][" + amember.getEmail() + "]");
      log.error("Error sending " + msgName + " e-mail for ["
         + amember.getAssocName() + "][" + amember.getEmail() + "]", e);

   }

   private void failureToSchedule(String msgName, AssocMember amember,
      Exception e)
   {

      facesMessages.add("Scheduling e-mail failed for ["
         + amember.getAssocName() + "][" + amember.getEmail() + "]");
      log.error("Error scheduling " + msgName + " e-mail for ["
         + amember.getAssocName() + "][" + amember.getEmail() + "]", e);

   }

   private void failureToSchedule(String msgName, Long voteId, Exception e)
   {

      log.error("Error scheduling " + msgName + " e-mail for [" + voteId + "]",
         e);

   }

   /**
    * Try to work around the connect exception sent by the smtp server
    */
   private int handleFacesException(String msgName, AssocMember amember,
      Exception e, int remainingRetries)
   {

      if ((e.getCause() != null)
         && (e.getCause() instanceof MessagingException)
         && (e.getMessage() != null)
         && (e.getMessage().indexOf(SMTP_MAGIC_ERROR) != -1))
      {

         remainingRetries--;

         if (remainingRetries != 0)
         {

            try
            {

               Thread.sleep(10000);

            }
            catch (InterruptedException e1)
            {

               //
            }

         }
         else
         {

            facesMessages.add("Sending e-mail failed for ["
               + amember.getAssocName() + "][" + amember.getEmail() + "]");
            log.error("Error sending " + msgName + " e-mail for ["
               + amember.getAssocName() + "][" + amember.getEmail() + "]");

         }

      }
      else
      {

         facesMessages.add("Sending e-mail failed for ["
            + amember.getAssocName() + "][" + amember.getEmail() + "]");
         log.error("Error sending " + msgName + " e-mail for ["
            + amember.getAssocName() + "][" + amember.getEmail() + "]", e);
         remainingRetries = 0;

      }

      return remainingRetries;

   }

   //================================================================================
   // Alerts
   //================================================================================
   @Transactional(TransactionPropagationType.REQUIRED)
   public void sendAlert(String email, String msgTitle)
   {

      try
      {

         Contexts.getEventContext().set("objectcreatoremail", email);
         Contexts.getEventContext().set("msgtitle", msgTitle);
         Contexts.getEventContext().set("donotreply", Cts.DO_NOT_REPLY);
         Contexts.getEventContext().set("projectname", Cts.PROJECT_NAME);
         Renderer.instance().render("/mailing/newobjectalert.xhtml");
         facesMessages.add("Sent e-mail successfully to [" + email + "]");
         log.info("Sent " + msgTitle + " e-mail successfully to [" + email
            + "]");

      }
      catch (FacesException e)
      {

         facesMessages.add("Sending e-mail failed for [" + email + "]");
         log.error("Error sending " + msgTitle + " e-mail for [" + email + "]",
            e);

      }
      catch (Exception e)
      {

         facesMessages.add("Sending e-mail failed for [" + email + "]");
         log.error("Error sending " + msgTitle + " e-mail for [" + email + "]",
            e);

      }

   }

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewAccountOrderAlert(Long newObjectId)
   {

      AccountOrder newObject =
         entityManager.find(AccountOrder.class, newObjectId);
      sendAlert(newObject.getEmail(),
         "Account Request For [" + newObject.getBuyerName() + "] : "
         + newObject.getPrice());

   }

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewGuideOrderAlert(Long newObjectId)
   {

      GuideOrder newObject = entityManager.find(GuideOrder.class, newObjectId);
      sendAlert(newObject.getEmail(),
         "Guide Order For [" + newObject.getBuyerName() + "]");

   }

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewClassifiedAlert(Long newObjectId)
   {

      Classified newObject = entityManager.find(Classified.class, newObjectId);
      sendAlert(newObject.getPublisher().getEmail(),
         "Classified [" + newObject.getTitle() + "]");

   }

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewArticleAlert(Long newObjectId)
   {

      Article newObject = entityManager.find(Article.class, newObjectId);
      sendAlert(newObject.getAuthor().getEmail(),
         "Article [" + newObject.getTitle() + "]");

   }

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewYellowPageAlert(Long newObjectId)
   {

      YellowPage newObject = entityManager.find(YellowPage.class, newObjectId);
      sendAlert(newObject.getReferredBy().getEmail(),
         "YellowPage [" + newObject.getName() + "]");

   }

   //================================================================================
   // ClubEvent HANDLING
   //================================================================================
   /**
    *
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendEventAnnouncement2Me(@Duration
      long delay, Long clubEventId, Long amemberId, String registrationLink)
   {

      ClubEvent clubEvent = entityManager.find(ClubEvent.class, clubEventId);
      AssocMember amember = entityManager.find(AssocMember.class, amemberId);
      sendClubEvent2OneEntry(clubEvent, null, amember, registrationLink);

   }

   /**
   *
   */
   @SuppressWarnings("unchecked")
   public List<Mailing> selectClubEventMailingsToSent(ClubEvent clubEvent,
      Mailing.MState targetState)
   {

      // Let's select the ballots to send.
      // List<Mailing> mailingList = clubEvent.getMailings();
      Query mailingQuery =
         entityManager.createQuery(
            "select c from Mailing c where c.mailingController = :clubEvent and c.state != :state");
      mailingQuery.setParameter("clubEvent", clubEvent);
      mailingQuery.setParameter("state", targetState);

      List<Mailing> mailingList = mailingQuery.getResultList();

      // There is no ballot left to sent
      if (mailingList.size() == 0)
      {

         log.info("No mailing left to send for [" + clubEvent.getTitle()
            + "]");

      }

      return mailingList;

   }

   /**
    *
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public QuartzTriggerHandle scheduleSendEventAnnouncement2MailingList(
      @Duration
      long delay, Long clubEventId, String registrationLink, int kind)
   {

      ClubEvent clubEvent = entityManager.find(ClubEvent.class, clubEventId);
      MailProcessorCmd.CMDOrder[] orders = null;
      Mailing.MState targetState = null;

      if (kind == 0)
      {

         orders =
            new MailProcessorCmd.CMDOrder[]
            {
               MailProcessorCmd.CMDOrder.setClubEventToAnnouncing,
               MailProcessorCmd.CMDOrder.setClubEventToAnnounced
            };
         targetState = Mailing.MState.announced;

      }
      else if (kind == 1)
      {

         orders =
            new MailProcessorCmd.CMDOrder[]
            {
               MailProcessorCmd.CMDOrder.setClubEventToReminding,
               MailProcessorCmd.CMDOrder.setClubEventToReminded
            };
         targetState = Mailing.MState.reminded;

      }
      else if (kind == 2)
      {

         orders =
            new MailProcessorCmd.CMDOrder[]
            {
               MailProcessorCmd.CMDOrder.setClubEventToRemindingAgain,
               MailProcessorCmd.CMDOrder.setClubEventToRemindedAgain
            };
         targetState = Mailing.MState.remindedAgain;

      }
      else if (kind == 3)
      {

         orders =
            new MailProcessorCmd.CMDOrder[]
            {
               MailProcessorCmd.CMDOrder.setClubEventToRemindingFinal,
               MailProcessorCmd.CMDOrder.setClubEventToRemindedFinal
            };
         targetState = Mailing.MState.dontForgetSent;

      }
      else
      {

         targetState = Mailing.MState.sent;

      }

      // Let's select the ballots to send.
      // List<Mailing> mailingList = clubEvent.getMailings();
      List<Mailing> mailingList =
         selectClubEventMailingsToSent(clubEvent, targetState);

      // There is no ballot left to sent
      if (mailingList.size() == 0)
      {

         return timer;

      }

      try
      {

         mailingQueueSender.send(queueSession.createObjectMessage(
               new MailProcessorCmd(orders[0], clubEventId)));

      }
      catch (JMSException e1)
      {

         failureToSchedule("ClubEvent", clubEventId, e1);

      }

      for (Mailing mailing : mailingList)
      {

         // Already filtered in the selectClubEventMailingsToSent but can not hurt
         if (!mailing.getState().equals(targetState))
         {

            try
            {

               mailingQueueSender.send(queueSession.createObjectMessage(
                     mailing));
               successfulScheduling("ClubEvent", mailing.getMember());

            }
            catch (Exception e)
            {

               failureToSchedule("ClubEvent", mailing.getMember(), e);

            }

         }

      }

      try
      {

         mailingQueueSender.send(queueSession.createObjectMessage(
               new MailProcessorCmd(orders[1], clubEventId)));

      }
      catch (JMSException e1)
      {

         failureToSchedule("ClubEvent", clubEventId, e1);

      }

      return timer;

   }

   @Transactional(TransactionPropagationType.REQUIRED)
   public void sendClubEvent2OneEntry(ClubEvent sClubEvent, Mailing mailing,
      AssocMember sMember, String registrationLink)
   {

      AssocMember amember = null;
      ClubEvent clubEvent = null;

      if (sMember == null)
      {

         mailing = entityManager.merge(mailing);
         clubEvent = (ClubEvent)mailing.getMailingController();
         amember = mailing.getMember();

      }
      else
      {

         clubEvent = sClubEvent;
         amember = sMember;

      }

      if (registrationLink == null)
      {

         registrationLink = clubEvent.getRegistrationLink();

      }

      String mailingTemplateName = clubEvent.getTemplateFile();
      List<MailAttachment> attachments = clubEvent.getAttachments();

      for (MailAttachment attachment : attachments)
      {

         String info = "";
         info += " FileName: " + attachment.getFileName();
         info += " Type: " + attachment.getContentType();
         info += " Size: " + attachment.getData().length;
         log.info(info);

      }

      int remainingRetries = RETRY_COUNT;

      while (remainingRetries > 0)
      {

         try
         {

            Contexts.getEventContext().set("amember", amember);
            Contexts.getEventContext().set("clubEvent", clubEvent);
            Contexts.getEventContext().set("registrationLink",
               registrationLink);
            Contexts.getEventContext().set("donotreply", Cts.DO_NOT_REPLY);
            Contexts.getEventContext().set("projectname", Cts.PROJECT_NAME);

            if (REALLY_SENT_MAIL)
            {

               Renderer.instance().render(mailingTemplateName);

            }

            successfulSending("ClubEvent", amember);
            remainingRetries = 0;
            saveClubEventMailingState(clubEvent, mailing);

         }
         catch (FacesException e)
         {

            remainingRetries =
               handleFacesException("ClubEvent", amember, e, remainingRetries);

         }
         catch (Exception e)
         {

            failureToSent("ClubEvent", amember, e);
            remainingRetries = 0;

         }

      }

   }

   /**
    * The following function could also be invoked the following way
    * Events.instance().raiseAsynchronousEvent("saveClubEventMailingState", clubEvent, mailing);
    */
   @Observer("org.jboss.seam.example.webassoc.saveClubEventMailingState")
   @Transactional(TransactionPropagationType.REQUIRED)
   public void saveClubEventMailingState(ClubEvent clubEvent, Mailing mailing)
   {

      if ((mailing != null) && (clubEvent != null))
      {

         ClubEvent.MCState state = clubEvent.getSendingState();

         if (state.equals(ClubEvent.MCState.announcementStarted))
         {

            mailing.setState(Mailing.MState.announced);

         }
         else if (state.equals(ClubEvent.MCState.reminderedStarted))
         {

            mailing.setState(Mailing.MState.reminded);

         }
         else if (state.equals(ClubEvent.MCState.secondReminderStarted))
         {

            mailing.setState(Mailing.MState.remindedAgain);

         }
         else if (state.equals(ClubEvent.MCState.dontForgetStarted))
         {

            mailing.setState(Mailing.MState.dontForgetSent);

         }
         else
         {

            mailing.setState(Mailing.MState.sent);

         }

         entityManager.persist(mailing);
         entityManager.persist(clubEvent);

      }

   }

   /**
    * Send the email regarding a unique event registration
    *
    * @param templateName
    * @param clubEvent
    * @param registration
    * @param directionLink
    */
   @Transactional(TransactionPropagationType.NEVER)
   public void sendEMailNow(String templateName, ClubEvent clubEvent,
      ClubEventRegistration registration, String directionLink)
   {

      int remainingRetries = RETRY_COUNT;

      while (remainingRetries > 0)
      {

         try
         {

            Contexts.getEventContext().set("clubEvent", clubEvent);
            Contexts.getEventContext().set("registration", registration);
            Contexts.getEventContext().set("directionLink", directionLink);
            Contexts.getEventContext().set("donotreply", Cts.DO_NOT_REPLY);
            Contexts.getEventContext().set("projectname", Cts.PROJECT_NAME);

            if (REALLY_SENT_MAIL)
            {

               Renderer.instance().render(templateName);

            }

            successfulSending("ClubEvent", registration.getMember());
            remainingRetries = 0;

         }
         catch (FacesException e)
         {

            remainingRetries =
               handleFacesException("NewsLetter", registration.getMember(), e,
                  remainingRetries);

         }
         catch (Exception e)
         {

            failureToSent("ClubEvent", registration.getMember(), e);
            remainingRetries = 0;

         }

      }

   }

   //================================================================================
   // NewsLetter HANDLING
   //================================================================================
   /**
    * Sends the same mail to a unique entry
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewsLetter2Me(@Duration
      long delay, Long newsLetterId, Long amemberId)
   {

      NewsLetter newsLetter =
         entityManager.find(NewsLetter.class, newsLetterId);
      AssocMember amember = entityManager.find(AssocMember.class, amemberId);
      sendNewsLetter2OneEntry(newsLetter, null, amember);

   }

   /**
    *
    */
   @SuppressWarnings("unchecked")
   public List<Mailing> selectNewsLetterMailingsToSent(NewsLetter newsLetter,
      Mailing.MState targetState)
   {

      // Let's select the ballots to send.
      // List<Mailing> mailingList = newsLetter.getMailings();
      Query mailingQuery =
         entityManager.createQuery(
            "select c from Mailing c where c.mailingController = :newsLetter and c.state != :state");
      mailingQuery.setParameter("newsLetter", newsLetter);
      mailingQuery.setParameter("state", targetState);

      List<Mailing> mailingList = mailingQuery.getResultList();

      // There is no ballot left to sent
      if (mailingList.size() == 0)
      {

         log.info("No mailing left to send for [" + newsLetter.getTitle()
            + "]");
         newsLetter.setSendingState(NewsLetter.MCState.finalState);
         entityManager.persist(newsLetter);

      }

      return mailingList;

   }

   /**
    * Sends the same mail to all the entries in
    * the mailing list
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public QuartzTriggerHandle scheduleSendNewsLetter2MailingList(
      @Duration
      long delay, Long newsLetterId)
   {

      NewsLetter newsLetter =
         entityManager.find(NewsLetter.class, newsLetterId);
      log.info("scheduleSendNewsLetter2MailingList for ["
         + newsLetter.getTitle() + "]");

      MailProcessorCmd.CMDOrder[] orders =
         new MailProcessorCmd.CMDOrder[]
         {
            MailProcessorCmd.CMDOrder.setNewsLetterToSending,
            MailProcessorCmd.CMDOrder.setNewsLetterToSent
         };
      Mailing.MState targetState = Mailing.MState.sent;

      // Let's select the ballots to send.
      // List<Mailing> mailingList = newsLetter.getMailings();
      List<Mailing> mailingList =
         selectNewsLetterMailingsToSent(newsLetter, targetState);

      // There is no ballot left to sent
      if (mailingList.size() == 0)
      {

         return timer;

      }

      // Let's proceed with sending the emails.
      try
      {

         mailingQueueSender.send(queueSession.createObjectMessage(
               new MailProcessorCmd(orders[0], newsLetterId)));

      }
      catch (JMSException e1)
      {

         failureToSchedule("NewsLetter", newsLetterId, e1);

      }

      for (Mailing mailing : mailingList)
      {

         // Already filtered in the selectNewsLetterMailingsToSent but can not hurt
         if (!mailing.getState().equals(targetState))
         {

            try
            {

               mailingQueueSender.send(queueSession.createObjectMessage(
                     mailing));
               successfulScheduling("NewsLetter", mailing.getMember());

            }
            catch (Exception e)
            {

               failureToSchedule("NewsLetter", mailing.getMember(), e);

            }

         }

      }

      try
      {

         mailingQueueSender.send(queueSession.createObjectMessage(
               new MailProcessorCmd(orders[1], newsLetterId)));

      }
      catch (JMSException e1)
      {

         failureToSchedule("NewsLetter", newsLetterId, e1);

      }

      return timer;

   }

   @Transactional(TransactionPropagationType.REQUIRED)
   public void sendNewsLetter2OneEntry(NewsLetter sNewsLetter, Mailing mailing,
      AssocMember sMember)
   {

      AssocMember amember = null;
      NewsLetter newsLetter = null;

      if (sMember == null)
      {

         mailing = entityManager.merge(mailing);
         newsLetter = (NewsLetter)mailing.getMailingController();
         amember = mailing.getMember();

      }
      else
      {

         newsLetter = sNewsLetter;
         amember = sMember;

      }

      String mailingTemplateName = newsLetter.getTemplateFile();
      List<MailAttachment> attachments = newsLetter.getAttachments();

      for (MailAttachment attachment : attachments)
      {

         String info = "";
         info += " FileName: " + attachment.getFileName();
         info += " Type: " + attachment.getContentType();
         info += " Size: " + attachment.getData().length;
         // log.info(info);

      }

      int remainingRetries = RETRY_COUNT;

      while (remainingRetries > 0)
      {

         try
         {

            Contexts.getEventContext().set("amember", amember);
            Contexts.getEventContext().set("newsLetter", newsLetter);
            Contexts.getEventContext().set("donotreply", Cts.DO_NOT_REPLY);
            Contexts.getEventContext().set("projectname", Cts.PROJECT_NAME);

            if (REALLY_SENT_MAIL)
            {

               Renderer.instance().render(mailingTemplateName);

            }

            successfulSending("NewsLetter", amember);
            remainingRetries = 0;
            saveNewsLetterMailingState(newsLetter, mailing);

         }
         catch (FacesException e)
         {

            remainingRetries =
               handleFacesException("NewsLetter", amember, e,
                  remainingRetries);

         }
         catch (Exception e)
         {

            failureToSent("NewsLetter", amember, e);
            remainingRetries = 0;

         }

      }

   }

   /**
    * The following function could also be invoked the following way
    * Events.instance().raiseAsynchronousEvent("saveNewsLetterMailingState", newsLetter, mailing);
    */
   @Observer("org.jboss.seam.example.webassoc.saveNewsLetterMailingState")
   @Transactional(TransactionPropagationType.REQUIRED)
   public void saveNewsLetterMailingState(NewsLetter newsLetter,
      Mailing mailing)
   {

      if ((mailing != null) && (newsLetter != null))
      {

         mailing.setState(Mailing.MState.sent);
         entityManager.persist(mailing);
         entityManager.persist(newsLetter);

      }

   }

   //================================================================================
   // VOTE HANDLING
   //================================================================================
   /**
    * Sends the same mail to a unique entry
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendVote2Me(@Duration
      long delay, Long voteId, Long memberId)
   {

      Vote vote = entityManager.find(Vote.class, voteId);
      AssocMember amember = entityManager.find(AssocMember.class, memberId);
      Query ballotQuery =
         entityManager.createQuery(
            "select b from Ballot b where b.vote = :vote and b.member = :member");
      ballotQuery.setParameter("vote", vote);
      ballotQuery.setParameter("member", amember);
      sendOneBallot(vote, ((Ballot)(ballotQuery.getSingleResult())), amember);

   }

   /**
   *
   */
   @SuppressWarnings("unchecked")
   public List<Ballot> selectBallotsToSent(Vote vote,
      Ballot.MState targetState)
   {

      Query ballotQuery =
         entityManager.createQuery(
            "select b from Ballot b where b.vote = :vote and b.state != :state");
      ballotQuery.setParameter("vote", vote);
      ballotQuery.setParameter("state", targetState);

      List<Ballot> ballots = ballotQuery.getResultList();

      // There is no ballot left to sent
      if (ballots.size() == 0)
      {

         log.info("No ballots left to send for [" + vote.getTitle() + "]");
         vote.setSendingState(Vote.VState.finalState);
         entityManager.persist(vote);

      }

      return ballots;

   }

   /**
    * Sends the same mail to all the entries in
    * the mailing list
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public QuartzTriggerHandle announceVote(@Duration
      long delay, Long voteId)
   {

      Vote vote = entityManager.find(Vote.class, voteId);
      log.info("announceVote for [" + vote.getTitle() + "]");

      MailProcessorCmd.CMDOrder[] orders =
         new MailProcessorCmd.CMDOrder[]
         {
            MailProcessorCmd.CMDOrder.setVoteToSending,
            MailProcessorCmd.CMDOrder.setVoteToSent
         };
      Ballot.MState targetState = Ballot.MState.sent;

      // Let's select the ballots to send.
      // List<Ballot> ballots = vote.getBallots();
      List<Ballot> ballots = selectBallotsToSent(vote, targetState);

      // There is no ballot left to sent
      if (ballots.size() == 0)
      {

         return timer;

      }

      // Proceed with sending the ballots
      try
      {

         mailingQueueSender.send(queueSession.createObjectMessage(
               new MailProcessorCmd(orders[0], voteId)));

      }
      catch (JMSException e1)
      {

         failureToSchedule("Vote", voteId, e1);

      }

      for (Ballot ballot : ballots)
      {

         // Already filtered in the selectBallotToSent but can not hurt
         if (!ballot.getState().equals(targetState))
         {

            try
            {

               mailingQueueSender.send(queueSession.createObjectMessage(
                     ballot));
               successfulScheduling("Ballot", ballot.getMember());

            }
            catch (Exception e)
            {

               failureToSchedule("Ballot", ballot.getMember(), e);

            }

         }

      }

      try
      {

         mailingQueueSender.send(queueSession.createObjectMessage(
               new MailProcessorCmd(orders[1], voteId)));

      }
      catch (JMSException e1)
      {

         failureToSchedule("Vote", voteId, e1);

      }

      return timer;

   }

   @Transactional(TransactionPropagationType.REQUIRED)
   public void sendOneBallot(Vote sVote, Ballot ballot, AssocMember sMember)
   {

      AssocMember amember = null;
      Vote vote = null;

      if (sMember == null)
      {

         ballot = entityManager.merge(ballot);
         vote = ballot.getVote();
         amember = ballot.getMember();

      }
      else
      {

         vote = sVote;
         amember = sMember;

      }

      int remainingRetries = RETRY_COUNT;

      while (remainingRetries > 0)
      {

         try
         {

            Contexts.getEventContext().set("aballot", ballot);
            Contexts.getEventContext().set("donotreply", Cts.DO_NOT_REPLY);
            Contexts.getEventContext().set("projectname", Cts.PROJECT_NAME);

            if (REALLY_SENT_MAIL)
            {

               Renderer.instance().render("/mailing/voteannouncement.xhtml");

            }

            successfulSending("Ballot", amember);
            remainingRetries = 0;
            saveBallotMailingState(vote, ballot);

         }
         catch (FacesException e)
         {

            remainingRetries =
               handleFacesException("Ballot", ballot.getMember(), e,
                  remainingRetries);

         }
         catch (Exception e)
         {

            failureToSent("Ballot", ballot.getMember(), e);
            remainingRetries = 0;

         }

      }

   }

   /**
    * The following function could also be invoked the following way
    * Events.instance().raiseAsynchronousEvent("saveBallotMailingState", vote, ballot);
    */
   @Observer("org.jboss.seam.example.webassoc.saveBallotMailingState")
   @Transactional(TransactionPropagationType.REQUIRED)
   public void saveBallotMailingState(Vote vote, Ballot ballot)
   {

      if ((ballot != null) && (vote != null))
      {

         ballot.setState(Ballot.MState.sent);
         entityManager.persist(ballot);
         entityManager.persist(vote);

      }

   }

   @Transactional(TransactionPropagationType.REQUIRED)
   public void fromMDB(Serializable inMsg)
   {

      if (inMsg instanceof MailProcessorCmd)
      {

         MailProcessorCmd cmd = (MailProcessorCmd)inMsg;
         MailProcessorCmd.CMDOrder order = cmd.getOrder();
         Long mailingControllerId = cmd.getMailingControllerId();

         if (order.equals(MailProcessorCmd.CMDOrder.setClubEventToAnnouncing))
         {

            ClubEvent clubEvent =
               entityManager.find(ClubEvent.class, mailingControllerId);
            clubEvent.setSendingState(ClubEvent.MCState.announcementStarted);
            entityManager.persist(clubEvent);

         }
         else if (order.equals(
               MailProcessorCmd.CMDOrder.setClubEventToAnnounced))
         {

            ClubEvent clubEvent =
               entityManager.find(ClubEvent.class, mailingControllerId);
            clubEvent.setSendingState(ClubEvent.MCState.announcementEnded);
            entityManager.persist(clubEvent);

            // Let's try to send again.
            scheduleSendEventAnnouncement2MailingList(Cts.Delays.CLUB_EVENT,
               mailingControllerId, clubEvent.getRegistrationLink(), 0);

         }
         else if (order.equals(
               MailProcessorCmd.CMDOrder.setClubEventToReminding))
         {

            ClubEvent clubEvent =
               entityManager.find(ClubEvent.class, mailingControllerId);
            clubEvent.setSendingState(ClubEvent.MCState.reminderedStarted);
            entityManager.persist(clubEvent);

         }
         else if (order.equals(
               MailProcessorCmd.CMDOrder.setClubEventToReminded))
         {

            ClubEvent clubEvent =
               entityManager.find(ClubEvent.class, mailingControllerId);
            clubEvent.setSendingState(ClubEvent.MCState.reminderedEnded);
            entityManager.persist(clubEvent);

            // Let's try to send again.
            scheduleSendEventAnnouncement2MailingList(Cts.Delays.CLUB_EVENT,
               mailingControllerId, clubEvent.getRegistrationLink(), 1);

         }
         else if (order.equals(
               MailProcessorCmd.CMDOrder.setClubEventToRemindingAgain))
         {

            ClubEvent clubEvent =
               entityManager.find(ClubEvent.class, mailingControllerId);
            clubEvent.setSendingState(ClubEvent.MCState.secondReminderStarted);
            entityManager.persist(clubEvent);

         }
         else if (order.equals(
               MailProcessorCmd.CMDOrder.setClubEventToRemindedAgain))
         {

            ClubEvent clubEvent =
               entityManager.find(ClubEvent.class, mailingControllerId);
            clubEvent.setSendingState(ClubEvent.MCState.secondReminderEnded);
            entityManager.persist(clubEvent);

            // Let's try to send again.
            scheduleSendEventAnnouncement2MailingList(Cts.Delays.CLUB_EVENT,
               mailingControllerId, clubEvent.getRegistrationLink(), 2);

         }
         else if (order.equals(
               MailProcessorCmd.CMDOrder.setClubEventToRemindingFinal))
         {

            ClubEvent clubEvent =
               entityManager.find(ClubEvent.class, mailingControllerId);
            clubEvent.setSendingState(ClubEvent.MCState.dontForgetStarted);
            entityManager.persist(clubEvent);

         }
         else if (order.equals(
               MailProcessorCmd.CMDOrder.setClubEventToRemindedFinal))
         {

            ClubEvent clubEvent =
               entityManager.find(ClubEvent.class, mailingControllerId);
            clubEvent.setSendingState(ClubEvent.MCState.dontForgetEnded);
            entityManager.persist(clubEvent);

            // Let's try to send again.
            scheduleSendEventAnnouncement2MailingList(Cts.Delays.CLUB_EVENT,
               mailingControllerId, clubEvent.getRegistrationLink(), 3);

         }
         else if (order.equals(
               MailProcessorCmd.CMDOrder.setNewsLetterToSending))
         {

            NewsLetter newsLetter =
               entityManager.find(NewsLetter.class, mailingControllerId);
            newsLetter.setSendingState(NewsLetter.MCState.announcementStarted);
            entityManager.persist(newsLetter);

         }
         else if (order.equals(MailProcessorCmd.CMDOrder.setNewsLetterToSent))
         {

            NewsLetter newsLetter =
               entityManager.find(NewsLetter.class, mailingControllerId);
            newsLetter.setSendingState(NewsLetter.MCState.announcementEnded);
            entityManager.persist(newsLetter);

            // Let's try to send again. This will put the vote in the sending state
            // finalState if everything is ok.
            scheduleSendNewsLetter2MailingList(Cts.Delays.NEWS_LETTER,
               mailingControllerId);

         }
         else if (order.equals(MailProcessorCmd.CMDOrder.setVoteToSending))
         {

            Vote vote = entityManager.find(Vote.class, mailingControllerId);
            vote.setSendingState(Vote.VState.announcementStarted);
            entityManager.persist(vote);

         }
         else if (order.equals(MailProcessorCmd.CMDOrder.setVoteToSent))
         {

            Vote vote = entityManager.find(Vote.class, mailingControllerId);
            vote.setSendingState(Vote.VState.announcementEnded);
            entityManager.persist(vote);

            // Let's try to send again. This will put the vote in the sending state
            // finalState if everything is ok.
            announceVote(Cts.Delays.VOTE, mailingControllerId);

         }

      }
      else if (inMsg instanceof Mailing)
      {

         Mailing mailing = (Mailing)inMsg;
         mailing = entityManager.merge(mailing);

         MailingController controller = mailing.getMailingController();

         if (controller instanceof NewsLetter)
         {

            sendNewsLetter2OneEntry(null, mailing, null);

         }
         else if (controller instanceof ClubEvent)
         {

            sendClubEvent2OneEntry(null, mailing, null, null);

         }

      }
      else if (inMsg instanceof Ballot)
      {

         Ballot ballot = (Ballot)inMsg;
         sendOneBallot(null, ballot, null);

      }

   }

   //================================================================================
   // FUTURE FUNCTIONS KEPT HERE AS EXAMPLE
   //================================================================================
   /**
    *
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.REQUIRED)
   public void remimderEmail(int clubEventId, @Expiration
      Date endDate)
   {

      ClubEvent newsLetter = entityManager.find(ClubEvent.class, clubEventId);
      entityManager.lock(newsLetter, LockModeType.WRITE);
      log.info("Envoi rappel pour " + clubEventId);
      entityManager.merge(newsLetter);

   }

   /**
    *
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.REQUIRED)
   public QuartzTriggerHandle scheduleMailing(@Expiration
      Date when, @IntervalDuration
      Long interval, @FinalExpiration
      Date stoptime, Mailing mailing)
   {

      mailing = entityManager.merge(mailing);

      if (!mailing.isSent())
      {

         mailing.setState(Mailing.MState.sent);

      }

      return timer;

   }

   /**
    *
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.REQUIRED)
   public QuartzTriggerHandle scheduleMailing(@Expiration
      Date when, @IntervalCron
      String cron, @FinalExpiration
      Date stoptime, Mailing mailing)
   {

      mailing = entityManager.merge(mailing);

      if (!mailing.isSent())
      {

         mailing.setState(Mailing.MState.sent);

      }

      return timer;

   }

}
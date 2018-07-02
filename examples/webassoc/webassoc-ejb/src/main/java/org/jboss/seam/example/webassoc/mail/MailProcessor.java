package org.jboss.seam.example.webassoc.mail;

import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Duration;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.FinalExpiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.example.webassoc.clubs.ClubEvent;
import org.jboss.seam.example.webassoc.clubs.ClubEventRegistration;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.news.NewsLetter;
import org.jboss.seam.example.webassoc.vote.Ballot;
import org.jboss.seam.example.webassoc.vote.Vote;

import java.io.Serializable;

import java.util.Date;

import javax.ejb.Local;

@Local
public interface MailProcessor
{

   //================================================================================
   // Alerts
   //================================================================================
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewAccountOrderAlert(Long newObjectId);

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewGuideOrderAlert(Long newObjectId);

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewClassifiedAlert(Long newObjectId);

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewArticleAlert(Long newObjectId);

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewYellowPageAlert(Long newObjectId);

   //================================================================================
   // ClubEvent HANDLING
   //================================================================================
   /**
    *
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public QuartzTriggerHandle scheduleSendEventAnnouncement2MailingList(
      @Duration
      long delay, Long clubEventId, String registrationLink, int kind);

   /**
    *
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendEventAnnouncement2Me(@Duration
      long delay, Long clubEventId, Long amemberId, String registrationLink);

   @Transactional(TransactionPropagationType.REQUIRED)
   public void sendClubEvent2OneEntry(ClubEvent clubEvent, Mailing mailing,
      AssocMember toMeOnly, String registrationLink);

   @Transactional(TransactionPropagationType.REQUIRED)
   public void saveClubEventMailingState(ClubEvent clubEvent, Mailing mailing);

   //================================================================================
   // NewsLetter HANDLING
   //================================================================================
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
      ClubEventRegistration registration, String directionLink);

   /**
    * Sends the same mail to all the entries in
    * the mailing list
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public QuartzTriggerHandle scheduleSendNewsLetter2MailingList(
      @Duration
      long delay, Long newsLetterId);

   /**
    * Sends the mail to myself
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendNewsLetter2Me(@Duration
      long delay, Long newsLetterId, Long amemberId);

   /**
    * Sends the mail to a unique entry
    */
   @Transactional(TransactionPropagationType.REQUIRED)
   public void sendNewsLetter2OneEntry(NewsLetter newsLetter, Mailing mailing,
      AssocMember toMeOnly);

   @Transactional(TransactionPropagationType.REQUIRED)
   public void saveNewsLetterMailingState(NewsLetter newsLetter,
      Mailing mailing);

   //================================================================================
   // VOTE HANDLING
   //================================================================================
   /**
    * Sends the same mail to all the entries in
    * the mailing list
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public QuartzTriggerHandle announceVote(@Duration
      long delay, Long voteId);

   /**
    * Sends the mail to myself
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendVote2Me(@Duration
      long delay, Long voteId, Long memberId);

   /**
    * Sends the mail to a unique entry
    */
   @Transactional(TransactionPropagationType.REQUIRED)
   public void sendOneBallot(Vote vote, Ballot ballot, AssocMember toMeOnly);

   @Transactional(TransactionPropagationType.REQUIRED)
   public void saveBallotMailingState(Vote vote, Ballot ballot);

   //================================================================================
   // HACK FOR ASYNCHRONOUS HANDLING
   //================================================================================
   @Transactional(TransactionPropagationType.REQUIRED)
   public void fromMDB(Serializable inMsg);

   //================================================================================
   // PASSWORD and ACCOUNT init
   //================================================================================
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendActivation(String userName, String userEmail, String userPasssword);
   
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendPasswordReset(String userName, String userEmail, String newPassword);
   
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendAccountInit(String userName, String userEmail, String userPasssword);
   
   @Transactional(TransactionPropagationType.NEVER)
   public void sendMembership(AssocMember amember);
   
   //================================================================================
   // FUTURE FUNCTIONS KEPT HERE AS EXAMPLE
   //================================================================================
   /**
    *
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.REQUIRED)
   public void remimderEmail(int clubEventId, @Expiration
      Date endDate);

   /**
    *
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.REQUIRED)
   public QuartzTriggerHandle scheduleMailing(@Expiration
      Date when, @IntervalDuration
      Long interval, @FinalExpiration
      Date stoptime, Mailing payment);

   /**
    *
    */
   @Asynchronous
   @Transactional(TransactionPropagationType.REQUIRED)
   public QuartzTriggerHandle scheduleMailing(@Expiration
      Date when, @IntervalCron
      String cron, @FinalExpiration
      Date stoptime, Mailing payment);

}
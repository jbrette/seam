package org.jboss.seam.example.webassoc.clubs;

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
import org.jboss.seam.example.webassoc.mail.MailingController;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.ClubMembership;
import org.jboss.seam.example.webassoc.mship.FamilyMember;
import org.jboss.seam.example.webassoc.mship.FamilyMember.FKind;
import org.jboss.seam.example.webassoc.news.NewsLetter;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import org.richfaces.event.UploadEvent;

import org.richfaces.model.UploadItem;

import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;
import javax.persistence.Transient;

@Name("clubEventHome")
@Stateful
public class ClubEventHomeImpl
        extends EntityHome<ClubEvent>
        implements ClubEventHome
{

   private static final long serialVersionUID = -2457560649874169684L;
   @In(required = false)
   private Club club;
   @In(required = false)
   private AssocMember authenticatedMember;
   @In(create = true)
   private MailProcessor mailProcessor;
   @In
   private FacesMessages facesMessages;
   @Logger
   private Log log;
   @RequestParameter
   private Integer attachmentId;
   @RequestParameter
   private Long mailingId;

   // Image download fields
   private byte[] m_uploadedData;
   private String m_uploadedContentType;
   private String m_uploadedFileName;
   private int m_richFacesUploadsAvailable = 25;
   private String m_richFacesUploadData;

   @Factory("clubEvent")
   public ClubEvent initClubEvent()
   {

      return getInstance();

   }

   public String persist()
   {

      String res = super.persist();
      buildClubMemberMailingList();

      return res;

   }

   protected ClubEvent createInstance()
   {

      ClubEvent cevent = new ClubEvent();

      // JEB: Not sure it needs to be done there
      cevent.setClub(club);
      club.getClubEvents().add(cevent);
      cevent.setTitle("New meeting of " + club.getName());
      cevent.setDate(Calendar.getInstance().getTime());
      cevent.setNKind(MailingController.NKind.eventAnnouncement);
      cevent.setMailingListBuildRequest(NewsLetter.RKind.clubMembers);
      cevent.setTemplateFile("/mailing/eventannouncement.xhtml");
      cevent.setCity(Cts.Dflt.CITY);
      cevent.setState(Cts.Dflt.STATE);
      cevent.setZip(Cts.Dflt.ZIP);
      cevent.setPhone(Cts.Dflt.PHONE);

      Calendar cal = Calendar.getInstance();
      cevent.setStartTime(cal.getTime());
      cevent.setAnnouncementTime(cal.getTime());
      cevent.setReminderTime(cal.getTime());
      cevent.setSecondReminderTime(cal.getTime());
      cevent.setDontForgetTime(cal.getTime());
      cal.add(Calendar.HOUR, 1);
      cevent.setEndTime(cal.getTime());
      cevent.setAdultMemberFee(0);
      cevent.setKidMemberFee(0);
      cevent.setAdultNonMemberFee(0);
      cevent.setKidNonMemberFee(0);
      cevent.setAdultQuota(999);
      cevent.setKidQuota(999);
      cevent.setAdultMinimum(1);
      cevent.setKidMinimum(0);
      cevent.setStatus(ClubEvent.CEState.open);
      cevent.setContent("<b>New Event</b>");
      cevent.setEventContact(findEventContact(club));
      cevent.setSendingState(ClubEvent.MCState.initialState);

      return cevent;

   }

   public boolean isStillEmpty()
   {

      ClubEvent event = getInstance();

      return event.isStillEmpty();

   }

   @SuppressWarnings("unchecked")
   public ClubMembership findEventCoordinator(Club theClub)
   {

      ClubMembership eventCoordinator = null;

      if (eventCoordinator == null)
      {

         Query membershipQuery =
            getEntityManager().createQuery(
               "select c from ClubMembership c where c.active = true and c.club = :club");
         membershipQuery.setParameter("club", theClub);

         List<ClubMembership> memberships = membershipQuery.getResultList();

         for (ClubMembership membership : memberships)
         {

            if (
               membership.getMember().getAssocMember().getId().equals(
                  authenticatedMember.getId())
               && (membership.getMember().getFKind() == FKind.main))
            {

               eventCoordinator = membership;

               break;

            }

         }

      }

      if (eventCoordinator == null)
      {

         // JEB: We should be able to remove the part once every AssocMember has been modified
         // to have a main contact
         Query membershipQuery =
            getEntityManager().createQuery(
               "select c from ClubMembership c where c.active = true and c.club = :club");
         membershipQuery.setParameter("club", theClub);

         List<ClubMembership> memberships = membershipQuery.getResultList();

         for (ClubMembership membership : memberships)
         {

            if (
               membership.getMember().getAssocMember().getId().equals(
                  authenticatedMember.getId())
               && (membership.getMember().isAdult()))
            {

               eventCoordinator = membership;

               break;

            }

         }

      }

      if (eventCoordinator == null)
      {

         Query membershipQuery =
            getEntityManager().createQuery(
               "select c from ClubMembership c where c.active = true and c.club = :club and c.FKind = :role");
         membershipQuery.setParameter("club", theClub);
         membershipQuery.setParameter("role",
            ClubMembership.FKind.clubcontact);

         List<ClubMembership> memberships = membershipQuery.getResultList();

         if ((memberships != null) && (memberships.size() != 0))
         {

            eventCoordinator = memberships.get(0);

         }

      }

      return eventCoordinator;

   }

   public FamilyMember findEventContact(Club theClub)
   {

      ClubMembership eventCoordinator = findEventCoordinator(theClub);

      if (eventCoordinator == null)
      {

         Query membershipQuery =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.id = "
               + Cts.PresidentIds.FamilyMember);

         return (FamilyMember)membershipQuery.getSingleResult();

      }
      else
      {

         return eventCoordinator.getMember();

      }

   }

   /**
    * Create a "newsLetter" that will sent to annonce the event
    *
    * @param clubEvent
    * @param resetList
    * @param members
    * @param template
    */
   public void buildMailingList(ClubEvent clubEvent, boolean resetList,
      Set<AssocMember> members, boolean isCancellation)
   {

      clubEvent.setDate(Calendar.getInstance().getTime());
      clubEvent.setMailingListBuildRequest(
         MailingController.RKind.clubMembers);
      clubEvent.setTemplateFile(isCancellation
         ? "/mailing/eventcancellation.xhtml"
         : "/mailing/eventannouncement.xhtml");
      getEntityManager().persist(clubEvent);

      // Let's cleanup the previous list
      if (resetList)
      {

         Query mailingQuery =
            getEntityManager().createQuery(
               "delete from Mailing c where c.mailingController = :mailingController and c.state = :state");
         mailingQuery.setParameter("mailingController", clubEvent);
         mailingQuery.setParameter("state", Mailing.MState.notSent);
         mailingQuery.executeUpdate();
         clubEvent.setMailings(new ArrayList<Mailing>());
         getEntityManager().persist(clubEvent);

      }

      Query check =
         getEntityManager().createQuery(
            "SELECT DISTINCT COUNT(c.id) FROM Mailing c WHERE c.member = :currentMember and c.mailingController = :currentEvent");
      check.setParameter("currentEvent", clubEvent);

      // Let's build a new list
      for (AssocMember member : members)
      {

         if (member.getEMailValid())
         {

            check.setParameter("currentMember", member);

            long count = (Long)check.getSingleResult();

            if (count == 0)
            {

               Mailing mailing = new Mailing();
               mailing.setMember(member);
               mailing.setMailingController(clubEvent);
               mailing.setState(Mailing.MState.notSent);
               clubEvent.getMailings().add(mailing);
               member.getMailings().add(mailing);
               getEntityManager().persist(mailing);

            }
            else
            {

               log.info("Mail for " + clubEvent.getTitle() + " to member "
                  + member.getAssocName() + " was already sent");

            }

         }
         else
         {

            log.info("Member " + member.getAssocName()
               + " does not have a valid EMail. skipping");

         }

      }

      getEntityManager().persist(clubEvent);

   }

   public String buildClubMemberMailingList()
   {

      ClubEvent clubEvent = getInstance();
      Set<AssocMember> clonedMembers = new HashSet<AssocMember>();
      Club clonedClub = clubEvent.getClub();

      for (ClubMembership mship : clonedClub.getClubMembers())
      {

         clonedMembers.add(mship.getMember().getAssocMember());

      }

      buildMailingList(clubEvent, true, clonedMembers, false);

      return "success";

   }

   public String emptyMailingList()
   {

      ClubEvent clubEvent = getInstance();
      Query mailingQuery =
         getEntityManager().createQuery(
            "delete from Mailing c where c.mailingController = :clubEvent and c.state = :state");
      mailingQuery.setParameter("clubEvent", clubEvent);
      mailingQuery.setParameter("state", Mailing.MState.notSent);
      mailingQuery.executeUpdate();
      clubEvent.setMailings(new ArrayList<Mailing>());
      getEntityManager().persist(clubEvent);

      return "success";

   }

   public String markAsSent()
   {

      ClubEvent clubEvent = getInstance();
      ClubEvent.MCState state = clubEvent.getSendingState();
      Query mailingQuery =
         getEntityManager().createQuery(
            "update Mailing c set c.state = :state where c.mailingController = :clubEvent");

      if (state.equals(ClubEvent.MCState.announcementEnded))
      {

         mailingQuery.setParameter("state", Mailing.MState.announced);

      }
      else if (state.equals(ClubEvent.MCState.reminderedEnded))
      {

         mailingQuery.setParameter("state", Mailing.MState.reminded);

      }
      else if (state.equals(ClubEvent.MCState.secondReminderEnded))
      {

         mailingQuery.setParameter("state", Mailing.MState.remindedAgain);

      }
      else if (state.equals(ClubEvent.MCState.dontForgetEnded))
      {

         mailingQuery.setParameter("state", Mailing.MState.dontForgetSent);

      }
      else
      {

         mailingQuery.setParameter("state", Mailing.MState.sent);
         clubEvent.setSendingState(ClubEvent.MCState.finalState);

      }

      mailingQuery.setParameter("clubEvent", clubEvent);
      mailingQuery.executeUpdate();
      getEntityManager().persist(clubEvent);

      return "success";

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
      ClubEvent.MCState state = getInstance().getSendingState();

      if (foundMailing != null)
      {

         if (state.equals(ClubEvent.MCState.announcementEnded))
         {

            foundMailing.setState((newState ? Mailing.MState.announced
                                            : Mailing.MState.notSent));

         }
         else if (state.equals(ClubEvent.MCState.reminderedEnded))
         {

            foundMailing.setState((newState ? Mailing.MState.reminded
                                            : Mailing.MState.announced));

         }
         else if (state.equals(ClubEvent.MCState.secondReminderEnded))
         {

            foundMailing.setState((newState ? Mailing.MState.remindedAgain
                                            : Mailing.MState.reminded));

         }
         else if (state.equals(ClubEvent.MCState.dontForgetEnded))
         {

            foundMailing.setState((newState ? Mailing.MState.dontForgetSent
                                            : Mailing.MState.remindedAgain));

         }
         else
         {

            foundMailing.setState((newState ? Mailing.MState.sent
                                            : Mailing.MState.notSent));

         }

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

      ClubEvent reg = getInstance();
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

   public String sendTestEmail()
   {

      ClubEvent clubEvent = getInstance();
      mailProcessor.sendEventAnnouncement2Me(1000, clubEvent.getId(),
         authenticatedMember.getId(), clubEvent.getRegistrationLink());

      return "success";

   }

   public String sendAnnouncementEmail()
   {

      ClubEvent clubEvent = getInstance();
      ClubEvent.MCState currState = clubEvent.getSendingState();

      if (!((currState.equals(ClubEvent.MCState.initialState))
            || (currState.equals(ClubEvent.MCState.announcementEnded))))
      {

         String msg = clubEvent.getTitle() + " still in state " + currState;
         facesMessages.add(msg);

         return "wrongstate";

      }
      else
      {

         try
         {

            QuartzTriggerHandle res =
               mailProcessor.scheduleSendEventAnnouncement2MailingList(
                  Cts.Delays.CLUB_EVENT, clubEvent.getId(),
                  clubEvent.getRegistrationLink(), 0);
            log.info("sendEventAnnouncement2MailingList returned "
               + res.toString());

         }
         catch (Exception ex)
         {

            log.info("sendEventAnnouncement2MailingList did not returned");

         }

         return "success";

      }

   }

   public String sendReminderEmail()
   {

      ClubEvent clubEvent = getInstance();
      ClubEvent.MCState currState = clubEvent.getSendingState();

      if (!((currState.equals(ClubEvent.MCState.announcementEnded))
            || (currState.equals(ClubEvent.MCState.reminderedEnded))))
      {

         String msg = clubEvent.getTitle() + " still in state " + currState;
         facesMessages.add(msg);

         return "wrongstate";

      }
      else
      {

         try
         {

            QuartzTriggerHandle res =
               mailProcessor.scheduleSendEventAnnouncement2MailingList(
                  Cts.Delays.CLUB_EVENT, clubEvent.getId(),
                  clubEvent.getRegistrationLink(), 1);
            log.info("sendEventAnnouncement2MailingList returned "
               + res.toString());

         }
         catch (Exception ex)
         {

            log.info("sendEventAnnouncement2MailingList did not returned");

         }

         return "success";

      }

   }

   public String sendSecondReminderEmail()
   {

      ClubEvent clubEvent = getInstance();
      ClubEvent.MCState currState = clubEvent.getSendingState();

      if (!((currState.equals(ClubEvent.MCState.reminderedEnded))
            || (currState.equals(ClubEvent.MCState.secondReminderEnded))))
      {

         String msg = clubEvent.getTitle() + " still in state " + currState;
         facesMessages.add(msg);

         return "wrongstate";

      }
      else
      {

         try
         {

            QuartzTriggerHandle res =
               mailProcessor.scheduleSendEventAnnouncement2MailingList(
                  Cts.Delays.CLUB_EVENT, clubEvent.getId(),
                  clubEvent.getRegistrationLink(), 2);
            log.info("sendReminderEmail returned " + res.toString());

         }
         catch (Exception ex)
         {

            log.info("sendEventAnnouncement2MailingList did not returned");

         }

         return "success";

      }

   }

   public String sendCancellationEmail()
   {

      ClubEvent event = getInstance();

      if (event.getStatus() == ClubEvent.CEState.closed)
      {

         facesMessages.add(
            "Event is closed to registration and can not be modified");

         return "regclosed";

      }

      List<ClubEventRegistration> regs = event.getRegistrations();

      for (ClubEventRegistration reg : regs)
      {

         reg = getEntityManager().merge(reg);

         if (reg.getStatus() != ClubEventRegistration.CERState.cancelled)
         {

            try
            {

               reg.cancelRegistration(authenticatedMember);
               getEntityManager().persist(reg);
               mailProcessor.sendEMailNow("/mailing/eventcancellation.xhtml",
                  event, reg, reg.getDrivingDirectionLink());

            }
            catch (Exception nobigdeal)
            {

            }

         }

      }

      return "regopened";

   }

   public String confirmAllRegistrations()
   {

      ClubEvent event = getInstance();

      if (event.getStatus() == ClubEvent.CEState.closed)
      {

         facesMessages.add(
            "Event is closed to registration and can not be modified");

         return "regclosed";

      }

      List<ClubEventRegistration> regs = event.getRegistrations();

      for (ClubEventRegistration reg : regs)
      {

         reg = getEntityManager().merge(reg);

         if (reg.getStatus() == ClubEventRegistration.CERState.registered)
         {

            try
            {

               reg.confirmRegistration(authenticatedMember);
               getEntityManager().persist(reg);
               mailProcessor.sendEMailNow("/mailing/confirmation.xhtml", event,
                  reg, reg.getDrivingDirectionLink());

            }
            catch (Exception nobigdeal)
            {

            }

         }

      }

      return "regopened";

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<ClubEvent> getAllEvents()
   {

      Query query = buildQuery(RKind.allevents);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<ClubEvent> getUpcomingEvents()
   {

      Query query = buildQuery(RKind.upcomingevents);

      return query.getResultList();

   }

   // Query Builder
   public Query buildQuery(ClubEventHome.RKind reqKind)
   {

      Query query = null;
      Calendar cal = Calendar.getInstance();

      if (reqKind == ClubEventHome.RKind.allevents)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM ClubEvent c "
               + "ORDER BY c.startTime");

      }
      else if (reqKind == ClubEventHome.RKind.upcomingevents)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM ClubEvent c "
               + "WHERE c.endTime > :nowdate " + "ORDER BY c.startTime");
         query.setParameter("nowdate", cal.getTime());

      }
      else
      {

         query = null;

      }

      return query;

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
         ((uploadedFileName != null)
            ? ((uploadedFileName.indexOf("\\") != -1)
               ? uploadedFileName.split("\\\\")
               : uploadedFileName.split("/")) : new String[] {""});
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

         ClubEvent clubEvent = getInstance();
         MailAttachment doc = new MailAttachment();
         doc.setMailingController(clubEvent);
         doc.setData(m_uploadedData);
         doc.setContentType(m_uploadedContentType);
         doc.setFileName(m_uploadedFileName);
         doc.updateThumbnail();
         clubEvent.getAttachments().add(doc);
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

         ClubEvent clubEvent = getInstance();
         List<MailAttachment> attachments = clubEvent.getAttachments();

         for (MailAttachment attachment : attachments)
         {

            if (attachment.getImageId().equals(attachmentId))
            {

               clubEvent.getAttachments().remove(attachment);
               getEntityManager().remove(attachment);
               getEntityManager().persist(clubEvent);

               return "attachmentDeleted";

            }

         }

         return "";

      }

   }

   public String uploadImage()
   {

      if ((m_uploadedData == null) || (m_uploadedData.length == 0))
      {

         FacesMessages.instance().add("No Image selected");

         return "";

      }
      else
      {

         ClubEvent clubEvent = getInstance();
         ClubEventImage doc = new ClubEventImage();
         doc.setEvent(clubEvent);
         doc.setData(m_uploadedData);
         doc.setContentType(m_uploadedContentType);
         doc.setFileName(m_uploadedFileName);
         doc.setPhotograph(authenticatedMember);
         doc.setVisible(Boolean.TRUE);
         doc.setArchived(Boolean.FALSE);
         doc.updateThumbnail();
         clubEvent.getImages().add(doc);
         clubEvent.setImagesCount(clubEvent.getImagesCount() + 1);
         getEntityManager().persist(doc);
         m_uploadedData = null;
         m_uploadedContentType = null;
         m_uploadedFileName = null;

         return "imageUploaded";

      }

   }

   public String deleteImage()
   {

      if ((attachmentId == null))
      {

         FacesMessages.instance().add("No Image selected");

         return "";

      }
      else
      {

         ClubEvent clubEvent = getInstance();
         List<ClubEventImage> images = clubEvent.getImages();

         for (ClubEventImage image : images)
         {

            if (image.getImageId().equals(attachmentId))
            {

               clubEvent.getImages().remove(image);
               clubEvent.setImagesCount(clubEvent.getImagesCount() - 1);
               getEntityManager().remove(image);
               getEntityManager().persist(clubEvent);

               return "imageDeleted";

            }

         }

         return "";

      }

   }

   public void richfacesUploadListener(UploadEvent event)
           throws Exception
   {

      UploadItem item = event.getUploadItem();
      setUploadedContentType(item.getContentType());
      setUploadedFileName(item.getFileName());

      if (item.isTempFile())
      {

         File file = item.getFile();
         byte[] fileBArray = new byte[(int)file.length()];
         FileInputStream fis = new FileInputStream(file);
         fis.read(fileBArray);
         setUploadedData(fileBArray);

      }
      else
      {

         setUploadedData(item.getData());

      }

      uploadImage();
      m_richFacesUploadsAvailable--;

   }

   public String clearRichFacesUploadData()
   {

      m_richFacesUploadsAvailable = 25;

      return null;

   }

   public int getRichFacesUploadsAvailable()
   {

      return m_richFacesUploadsAvailable;

   }

   public void setRichFacesUploadsAvailable(int uploadsAvailable)
   {

      this.m_richFacesUploadsAvailable = uploadsAvailable;

   }

   public String getRichFacesUploadData()
   {

      return m_richFacesUploadData;

   }

   public void setRichFacesUploadData(String uploadData)
   {

      this.m_richFacesUploadData = uploadData;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<ClubEvent> getPhotoAlbums()
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM ClubEvent c "
            + "WHERE c.imagesCount != 0 " + "ORDER BY c.startTime DESC");

      return query.getResultList();

   }

   @SuppressWarnings("unchecked")
   public void repeatLast()
   {

      Query query =
         getEntityManager().createQuery(
            "SELECT c FROM ClubEvent c WHERE (c.club = :theClub) ORDER BY c.startTime");
      query.setParameter("theClub", club);

      List<ClubEvent> res = (List<ClubEvent>)query.getResultList();

      if (res.size() != 0)
      {

         ClubEvent orgEvent = res.get(res.size() - 1);
         ClubEvent clonedEvent = createInstance();
         cloneEvent(clonedEvent, orgEvent, true, false);
         getEntityManager().persist(clonedEvent);
         cloneEventAnnouncement(clonedEvent, orgEvent);
         getEntityManager().persist(clonedEvent);

      }

   }

   public void iWillDoIt()
   {

      ClubEvent currentEvent = getInstance();

      if (club == null)
      {

         club = currentEvent.getClub();

      }

      ClubEvent clonedEvent = createInstance();
      cloneEvent(clonedEvent, currentEvent, false, true);
      getEntityManager().persist(clonedEvent);

   }

   /**
    * It may be better to design that method in the ClubEvent itself
    */
   public void cloneEvent(ClubEvent cevent, ClubEvent orgEvent,
      boolean changeTime, boolean pickUpTheWaitingList)
   {

      cevent.setTitle(orgEvent.getTitle());
      cevent.setNKind(orgEvent.getNKind());
      cevent.setMailingListBuildRequest(orgEvent.getMailingListBuildRequest());
      cevent.setTemplateFile(orgEvent.getTemplateFile());

      if (pickUpTheWaitingList)
      {

         cevent.setHostMember(authenticatedMember);
         cevent.setHostName(authenticatedMember.getAssocName());
         cevent.setAddress(authenticatedMember.getAddress());
         cevent.setCity(authenticatedMember.getCity());
         cevent.setState(authenticatedMember.getState());
         cevent.setZip(authenticatedMember.getZip());
         cevent.setPhone(orgEvent.getPhone());

      }
      else
      {

         cevent.setHostMember(orgEvent.getHostMember());
         cevent.setHostName(orgEvent.getHostName());
         cevent.setAddress(orgEvent.getAddress());
         cevent.setCity(orgEvent.getCity());
         cevent.setState(orgEvent.getState());
         cevent.setZip(orgEvent.getZip());
         cevent.setPhone(orgEvent.getPhone());

      }

      if (changeTime)
      {

         Calendar newCal = Calendar.getInstance();
         newCal.add(Calendar.WEEK_OF_YEAR, 1);

         Calendar orgStartCal = Calendar.getInstance();
         orgStartCal.setTime(orgEvent.getStartTime());
         newCal.set(Calendar.HOUR_OF_DAY,
            orgStartCal.get(Calendar.HOUR_OF_DAY));
         newCal.set(Calendar.MINUTE, orgStartCal.get(Calendar.MINUTE));
         cevent.setStartTime(newCal.getTime());
         cevent.setAnnouncementTime(newCal.getTime());
         cevent.setReminderTime(newCal.getTime());
         cevent.setSecondReminderTime(newCal.getTime());
         cevent.setDontForgetTime(newCal.getTime());

         Calendar orgEndCal = Calendar.getInstance();
         orgEndCal.setTime(orgEvent.getEndTime());
         newCal.set(Calendar.HOUR_OF_DAY, orgEndCal.get(Calendar.HOUR_OF_DAY));
         newCal.set(Calendar.MINUTE, orgEndCal.get(Calendar.MINUTE));
         cevent.setEndTime(newCal.getTime());

      }
      else
      {

         cevent.setStartTime(orgEvent.getStartTime());
         cevent.setAnnouncementTime(orgEvent.getAnnouncementTime());
         cevent.setReminderTime(orgEvent.getReminderTime());
         cevent.setSecondReminderTime(orgEvent.getSecondReminderTime());
         cevent.setDontForgetTime(orgEvent.getDontForgetTime());
         cevent.setEndTime(orgEvent.getEndTime());

      }

      cevent.setAdultMemberFee(orgEvent.getAdultMemberFee());
      cevent.setKidMemberFee(orgEvent.getKidMemberFee());
      cevent.setAdultNonMemberFee(orgEvent.getAdultNonMemberFee());
      cevent.setKidNonMemberFee(orgEvent.getKidNonMemberFee());
      cevent.setAdultQuota(orgEvent.getAdultQuota());
      cevent.setKidQuota(orgEvent.getKidQuota());
      cevent.setAdultMinimum(orgEvent.getAdultMinimum());
      cevent.setKidMinimum(orgEvent.getKidMinimum());
      cevent.setStatus(ClubEvent.CEState.open);
      cevent.setContent(orgEvent.getContent());
      cevent.setEventContact(orgEvent.getEventContact());
      cevent.setSendingState(ClubEvent.MCState.initialState);
      getEntityManager().persist(cevent);

   }

   /**
    * It may be better to design that method in the ClubEvent itself
    */
   public void cloneEventAnnouncement(ClubEvent cevent, ClubEvent orgEvent)
   {

      List<Mailing> oldMailings = orgEvent.getMailings();

      for (Mailing oldm : oldMailings)
      {

         Mailing newm = new Mailing();
         newm.setMailingController(cevent);
         cevent.getMailings().add(newm);
         newm.setMember(oldm.getMember());
         newm.setState(Mailing.MState.notSent);
         getEntityManager().persist(newm);

      }

      List<MailAttachment> oldAttachments = orgEvent.getAttachments();

      for (MailAttachment olda : oldAttachments)
      {

         MailAttachment newa = new MailAttachment();
         newa.setMailingController(cevent);
         cevent.getAttachments().add(newa);
         newa.setCaption(olda.getCaption());
         newa.setContentType(olda.getContentType());
         newa.setData(olda.getData());
         newa.setFileName(olda.getFileName());
         newa.setThumbnail(olda.getThumbnail());
         getEntityManager().persist(newa);

      }

      getEntityManager().persist(cevent);

   }

   public String preregisterClubMembers()
   {

      ClubEvent clubEvent = getInstance();
      Club clonedClub = clubEvent.getClub();
      AssocMember tmpRegisteringMemberHack = null;

      {

         Query hack =
            getEntityManager().createQuery(
               "SELECT c FROM AssocMember c WHERE c.id = :authenticatedAssocId ");
         hack.setParameter("authenticatedAssocId",
            ((authenticatedMember != null) ? authenticatedMember.getId()
                                           : Cts.PresidentIds.AssocMember));
         tmpRegisteringMemberHack = (AssocMember)hack.getSingleResult();

      }

      for (ClubMembership mship : clonedClub.getClubMembers())
      {

         AssocMember assocMember = mship.getMember().getAssocMember();
         ClubEventRegistration reg = new ClubEventRegistration();
         reg.setMember(assocMember);
         reg.setEvent(clubEvent);
         clubEvent.getRegistrations().add(reg);
         assocMember.getEvents().add(reg);
         reg.setRegisteringMember(tmpRegisteringMemberHack);

         //
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
         getEntityManager().persist(reg);
         getEntityManager().persist(assocMember);
         getEntityManager().persist(clubEvent);

         //
         reg.addDefaultTickets(getEntityManager());
         reg.updateAmount();
         getEntityManager().persist(reg);

         ClubEvent tmpEvent = reg.getEvent();
         tmpEvent.updateAmount();
         getEntityManager().persist(clubEvent);

      }

      return "success";

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
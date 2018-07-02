package org.jboss.seam.example.webassoc.clubs;

import org.richfaces.event.UploadEvent;

import java.util.List;

import javax.ejb.Local;

import javax.persistence.Query;

@Local
public interface ClubEventHome
{

   // =================================
   // Request Kind Definition
   // =================================
   public enum RKind
   {

      upcomingevents(0), allevents(3);

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
   ;

   // Methods from ClubEventHomeImpl
   public ClubEvent initClubEvent();

   public void ejbRemove();

   // Methods from EntityHome and Home
   public Object getId();

   public void setId(Object id);

   public String persist();

   public String update();

   public String remove();

   public boolean isManaged();

   public void create();

   public Object getInstance();

   public boolean isStillEmpty();

   // ================================
   // Business operations
   // ================================
   public String sendAnnouncementEmail();

   public String sendReminderEmail();

   public String sendSecondReminderEmail();

   public String sendCancellationEmail();

   public String confirmAllRegistrations();

   public String sendTestEmail();

   public String buildClubMemberMailingList();

   public String emptyMailingList();

   public String markAsSent();

   public String toggleToSent();

   public String toggleToUnsent();

   public String deleteMailing();

   public String preregisterClubMembers();

   // List of all valid members
   public List<ClubEvent> getAllEvents();

   // List of all valid members
   public List<ClubEvent> getUpcomingEvents();

   // To help club managers to clone their last event;
   public void repeatLast();

   public void iWillDoIt();

   // Query Builder
   public Query buildQuery(ClubEventHome.RKind reqKind);

   // Images handling
   public void setUploadedData(byte[] attachmentData);

   public void setUploadedContentType(String contentType);

   public void setUploadedFileName(String fileName);

   public String uploadAttachment();

   public String uploadImage();

   public String deleteAttachment();

   public String deleteImage();

   public List<ClubEvent> getPhotoAlbums();

   // For richfaces
   public void richfacesUploadListener(UploadEvent event)
           throws Exception;

   public String clearRichFacesUploadData();

   public int getRichFacesUploadsAvailable();

   public void setRichFacesUploadsAvailable(int uploadsAvailable);

   public String getRichFacesUploadData();

   public void setRichFacesUploadData(String res);

}
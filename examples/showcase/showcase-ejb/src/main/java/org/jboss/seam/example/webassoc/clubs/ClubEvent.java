package org.jboss.seam.example.webassoc.clubs;

import org.hibernate.validator.Digits;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.hibernate.validator.Range;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.clubs.ClubEventRegistration.CERState;
import org.jboss.seam.example.webassoc.mail.MailingController;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.FamilyMember;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.example.webassoc.util.ObjectSerializer;
import org.jboss.seam.log.Log;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import javax.servlet.http.HttpServletRequest;

@Entity
@Name("clubEvent")
public class ClubEvent
        extends MailingController
{

   private static final long serialVersionUID = -5311189253085996209L;
   @Logger
   private Log log;

   // =================================
   // CEState Definition
   // open: Registration happens normally. Over quota registration enter mailing list
   // closed: Nothing changes anymore
   // openWithoutWaitingList: Registration happens normally. No over the waiting list
   // automaticRegistration: For event where you don't want the any registration but the preregistered ones.
   // =================================
   public enum CEState
   {

      open(0), closed(1), openWithoutWaitingList(2), automaticRegistration(3);

      private int id;

      private CEState(int id)
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
   private Club m_club;
   private AssocMember m_hostMember;
   private FamilyMember m_eventContact;
   private List<ClubEventRegistration> m_registrations =
      new ArrayList<ClubEventRegistration>();
   private List<ClubEventImage> m_images = new ArrayList<ClubEventImage>();
   private Integer m_imagesCount = 0;
   private Date m_startTime;
   private Date m_endTime;
   private Date m_announcementTime;
   private Date m_reminderTime;
   private Date m_secondReminderTime;
   private Date m_dontForgetTime;
   private CEState m_status;
   private Integer m_adultMemberFee;
   private Integer m_kidMemberFee;
   private Integer m_adultNonMemberFee;
   private Integer m_kidNonMemberFee;
   private Integer m_adultQuota;
   private Integer m_kidQuota;
   private Integer m_adultMinimum;
   private Integer m_kidMinimum = new Integer(0);
   private Integer m_adultNb = new Integer(0);
   private Integer m_kidNb = new Integer(0);
   private Integer m_amount = new Integer(0);
   private Integer m_limitKidAdult = new Integer(12);
   private Integer m_limitFreeKid = new Integer(3);
   private String m_hostName;
   private String m_address;
   private String m_city;
   private String m_state;
   private String m_zip;
   private String m_phone;

   // =================================
   // Getter/Setter
   // =================================
   @NotNull
   public Date getStartTime()
   {

      return m_startTime;

   }

   public void setStartTime(Date startTime)
   {

      this.m_startTime = startTime;

   }

   @NotNull
   public Date getEndTime()
   {

      return m_endTime;

   }

   public void setEndTime(Date endTime)
   {

      this.m_endTime = endTime;

   }

   /**
    * Date at which the event must be announced
    */
   public Date getAnnouncementTime()
   {

      return m_announcementTime;

   }

   public void setAnnouncementTime(Date newDate)
   {

      this.m_announcementTime = newDate;

   }

   /**
    * Date at which the fist reminder must be sent
    */
   public Date getReminderTime()
   {

      return m_reminderTime;

   }

   public void setReminderTime(Date newDate)
   {

      this.m_reminderTime = newDate;

   }

   /**
    * Date at which the second reminder must be sent
    */
   public Date getSecondReminderTime()
   {

      return m_secondReminderTime;

   }

   public void setSecondReminderTime(Date newDate)
   {

      this.m_secondReminderTime = newDate;

   }

   /**
    * Date at which the mail: Don't forget tomorrow must be sent
    */
   public Date getDontForgetTime()
   {

      return m_dontForgetTime;

   }

   public void setDontForgetTime(Date newDate)
   {

      this.m_dontForgetTime = newDate;

   }

   /**
    * Is the club currently open for new membership. Some activities have a
    * limited number of places or the members are organized in team. It is up
    * to the responsible of the club to open the club to new enrollment.
    *
    * @return
    */
   @Transient
   public boolean getOpenRegistration()
   {

      return m_status != CEState.closed;

   }

   @Transient
   public boolean getAcceptNewRegistration()
   {

      return ((m_status == CEState.open)
            || ((m_status == CEState.openWithoutWaitingList)
               && (((m_adultQuota != 0) && (m_adultNb < m_adultQuota))
                  || ((m_kidQuota != 0) && (m_kidNb < m_kidQuota)))));

   }

   public CEState getStatus()
   {

      return m_status;

   }

   public void setStatus(CEState status)
   {

      this.m_status = status;

   }

   // =================================
   // Address and Classical Mail
   // =================================
   @Length(max = 100)
   public String getHostName()
   {

      return m_hostName;

   }

   public void setHostName(String hostName)
   {

      this.m_hostName = hostName;

   }

   @Length(max = 250)
   public String getAddress()
   {

      return m_address;

   }

   public void setAddress(String address)
   {

      this.m_address = address;

   }

   @Length(max = 50)
   public String getCity()
   {

      return m_city;

   }

   public void setCity(String city)
   {

      this.m_city = city;

   }

   @Length(max = 50)
   public String getState()
   {

      return m_state;

   }

   public void setState(String state)
   {

      this.m_state = state;

   }

   @Length(max = 6)
   @Digits(
      integerDigits = 5, fractionalDigits = 0,
      message = "#{messages['validator.custom.zip']}"
   )
   public String getZip()
   {

      return m_zip;

   }

   public void setZip(String zip)
   {

      this.m_zip = zip;

   }

   @Length(max = 20)
   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getPhone()
   {

      return m_phone;

   }

   public void setPhone(String phone)
   {

      this.m_phone = phone;

   }

   // ============================================
   // Announcement control
   // ============================================
   @Transient
   public boolean isReadyForAnnouncement()
   {

      MCState sendingState = this.getSendingState();

      return (getOpenRegistration()
            && (sendingState.equals(MCState.initialState)));

   }

   @Transient
   public boolean isReadyForReminder()
   {

      MCState sendingState = this.getSendingState();

      return (getOpenRegistration()
            && (sendingState.equals(MCState.announcementEnded)));

   }

   @Transient
   public boolean isReadyForSecondReminder()
   {

      MCState sendingState = this.getSendingState();

      return (getOpenRegistration()
            && (sendingState.equals(MCState.reminderedEnded)));

   }

   @Transient
   public boolean isReadyFinalReminder()
   {

      MCState sendingState = this.getSendingState();

      return (getOpenRegistration()
            && (sendingState.equals(MCState.secondReminderEnded)));

   }

   // ============================================
   // Quotas
   // ============================================
   @Transient
   public boolean isFree()
   {

      return ((m_adultMemberFee + m_adultNonMemberFee + m_kidMemberFee
               + m_kidNonMemberFee) == 0);

   }

   @NotNull
   public Integer getAdultMemberFee()
   {

      return m_adultMemberFee;

   }

   public void setAdultMemberFee(Integer fee)
   {

      this.m_adultMemberFee = fee;

   }

   @NotNull
   public Integer getAdultNonMemberFee()
   {

      return m_adultNonMemberFee;

   }

   public void setAdultNonMemberFee(Integer fee)
   {

      this.m_adultNonMemberFee = fee;

   }

   @NotNull
   public Integer getKidMemberFee()
   {

      return m_kidMemberFee;

   }

   public void setKidMemberFee(Integer fee)
   {

      this.m_kidMemberFee = fee;

   }

   @NotNull
   public Integer getKidNonMemberFee()
   {

      return m_kidNonMemberFee;

   }

   public void setKidNonMemberFee(Integer fee)
   {

      this.m_kidNonMemberFee = fee;

   }

   @NotNull
   @Range(min = 0, max = 999)
   public Integer getAdultQuota()
   {

      return m_adultQuota;

   }

   public void setAdultQuota(Integer quota)
   {

      this.m_adultQuota = quota;

   }

   @NotNull
   @Range(min = 0, max = 999)
   public Integer getKidQuota()
   {

      return m_kidQuota;

   }

   public void setKidQuota(Integer quota)
   {

      this.m_kidQuota = quota;

   }

   @NotNull
   @Range(min = 0, max = 999)
   public Integer getAdultMinimum()
   {

      return m_adultMinimum;

   }

   public void setAdultMinimum(Integer minimum)
   {

      this.m_adultMinimum = minimum;

   }

   @NotNull
   @Range(min = 0, max = 999)
   public Integer getKidMinimum()
   {

      return m_kidMinimum;

   }

   public void setKidMinimum(Integer minimum)
   {

      this.m_kidMinimum = minimum;

   }

   @NotNull
   public Integer getAdultNb()
   {

      return m_adultNb;

   }

   public void setAdultNb(Integer nb)
   {

      this.m_adultNb = nb;

   }

   public Integer getKidNb()
   {

      return m_kidNb;

   }

   public void setKidNb(Integer nb)
   {

      this.m_kidNb = nb;

   }

   public void setAmount(Integer amount)
   {

      this.m_amount = amount;

   }

   public Integer getAmount()
   {

      return m_amount;

   }

   public void setLimitKidAdult(Integer limitKidAdult)
   {

      this.m_limitKidAdult = limitKidAdult;

   }

   public Integer getLimitKidAdult()
   {

      return m_limitKidAdult;

   }

   public void setLimitFreeKid(Integer limitFreeKid)
   {

      this.m_limitFreeKid = limitFreeKid;

   }

   public Integer getLimitFreeKid()
   {

      return m_limitFreeKid;

   }

   // =================================
   // Relationship
   // =================================
   @NotNull @ManyToOne
   public Club getClub()
   {

      return m_club;

   }

   public void setClub(Club club)
   {

      this.m_club = club;

   }

   // @NotNull
   @OneToOne
   public AssocMember getHostMember()
   {

      return m_hostMember;

   }

   public void setHostMember(AssocMember hostMember)
   {

      this.m_hostMember = hostMember;

   }

   @NotNull @ManyToOne
   public FamilyMember getEventContact()
   {

      return m_eventContact;

   }

   public void setEventContact(FamilyMember eventContact)
   {

      this.m_eventContact = eventContact;

   }

   @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
   @OrderBy("registrationDate")
   public List<ClubEventRegistration> getRegistrations()
   {

      return m_registrations;

   }

   public void setRegistrations(List<ClubEventRegistration> registrations)
   {

      this.m_registrations = registrations;

   }

   @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
   public List<ClubEventImage> getImages()
   {

      return m_images;

   }

   public void setImages(List<ClubEventImage> images)
   {

      this.m_images = images;

   }

   // Not sure it is needed. Used to count the numbers of photos
   // in the photo album without bypassing lazy loading of images
   public Integer getImagesCount()
   {

      return m_imagesCount;

   }

   public void setImagesCount(Integer imagesCount)
   {

      this.m_imagesCount = imagesCount;

   }

   // ================================
   // Recompute the registration link
   // ================================
   @Transient
   public String getRegistrationLink()
   {

      // That code does not work in asynchronous mode, which explain the ugly hack
      FacesContext ctx = FacesContext.getCurrentInstance();
      ExternalContext extCtxt =
         ((ctx != null) ? ctx.getExternalContext() : null);
      String registrationLink =
         ((extCtxt != null)
            ? ((javax.servlet.http.HttpServletRequest)extCtxt.getRequest())
               .getRequestURL().toString() : null);

      // Temporary ugly hack
      registrationLink =
         "http://" + Cts.IPs.UGLYSERVERNAME + "/" + Cts.IPs.UGLYWARNAME
         + "/editClubEvent.seam";
      registrationLink =
         registrationLink
         + ((registrationLink.indexOf("?") != -1) ? "&pagesClubId="
                                                  : "?pagesClubId=")
         + getClub().getId();
      registrationLink =
         registrationLink
         + ((registrationLink.indexOf("?") != -1) ? "&pagesClubEventId="
                                                  : "?pagesClubEventId=")
         + getId();

      return registrationLink;

   }

   @Transient
   public String getFlashXMLDocument()
   {

      FacesContext context = FacesContext.getCurrentInstance();
      ExternalContext extCtx = context.getExternalContext();
      HttpServletRequest request = (HttpServletRequest)extCtx.getRequest();
      String downloadLinkPfx = request.getRequestURL().toString();
      String downloadLinkSfx = "&pagesClubId=" + getClub().getId();
      downloadLinkSfx += "&pagesClubEventId=" + getId();
      downloadLinkSfx +=
         "&actionMethod=photoAlbum.xhtml%3AdownloadAttachment.download";

      String res = "<photos>";

      for (ClubEventImage img : m_images)
      {

         res += "<image title='img-" + img.getImageId() + "'>";
         res += downloadLinkPfx;
         res +=
            ((downloadLinkPfx.indexOf("?") != -1) ? "&attachmentId="
                                                  : "?attachmentId=")
            + img.getImageId();
         res += downloadLinkSfx;
         res += "</image>";

      }

      res += "</photos>";

      return res;

   }

   @Transient
   public String getFlashXMLDocumentHex()
   {

      String org = getFlashXMLDocument();

      try
      {

         org = ObjectSerializer.serializeToString(org);

      }
      catch (IOException e)
      {

      }

      return org;

   }

   // ================================
   // Recompute the registration link
   // ================================
   @Transient
   public void fillupAddress()
   {

      AssocMember host = m_hostMember;

      if (host != null)
      {

         m_address = host.getAddress();
         m_city = host.getCity();
         m_zip = host.getZip();
         m_state = host.getState();
         m_hostName = host.getAssocName();
         m_phone = host.getHomePhone();

      }

   }

   // ================================
   // Recompute the registration link
   // ================================
   @Transient
   public boolean isStillEmpty()
   {

      return (getRegistrations().size() == 0);

   }

   // ================================
   // Recompute the registration link
   // ================================
   @Transient
   public String getLocationLink()
   {

      String googleMap = "http://maps.google.com/maps";

      try
      {

         String addr = null;

         if (getAddress() != null)
         {

            addr =
               getAddress().replaceFirst(" ", "+") + ","
               + getCity().replaceFirst(" ", "+") + ","
               + getState().replaceFirst(" ", "+") + ","
               + getZip().replaceFirst(" ", "+");

         }
         else
         {

            addr =
               getCity().replaceFirst(" ", "+") + ","
               + getState().replaceFirst(" ", "+") + ","
               + getZip().replaceFirst(" ", "+");

         }

         googleMap += "?q=" + addr;

      }
      catch (NullPointerException ex)
      {

         log.error("Invalid address for event " + getTitle());

      }

      return googleMap;

   }

   // ================================
   // Recompute the total data
   // ================================
   public void updateAmount()
   {

      m_adultNb = 0;
      m_kidNb = 0;
      m_amount = 0;

      boolean onWaitingList = false;

      for (ClubEventRegistration registration : m_registrations)
      {

         registration.updateAmount();

         ClubEventRegistration.CERState regState = registration.getStatus();

         if (regState != ClubEventRegistration.CERState.cancelled)
         {

            if (!onWaitingList)
            {

               int newAdultNb =
                  registration.getAdultMemberNb()
                  + registration.getAdultNonMemberNb();
               int newKidNb =
                  registration.getKidMemberNb()
                  + registration.getKidNonMemberNb();
               m_adultNb += newAdultNb;
               m_kidNb += newKidNb;
               m_amount += registration.getAmount();

               if (registration.getStatus() == CERState.waiting)
               {

                  // Great. It is out of the waiting list
                  registration.setStatus(CERState.registered);

               }

            }
            else
            {

               if (registration.getStatus() == CERState.registered)
               {

                  // You were not added directly on the waiting list. Strange
                  registration.setStatus(CERState.waiting);

               }

            }

         }

         if (((m_adultQuota != 0) && (m_adultNb >= m_adultQuota))
            || ((m_kidQuota != 0) && (m_kidNb >= m_kidQuota)))
         {

            onWaitingList = true;

         }

      }

   }

}
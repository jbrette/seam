package org.jboss.seam.example.webassoc.mship;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.example.webassoc.mny.MnyAccount;
import org.jboss.seam.example.webassoc.security.AccountMember;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.example.webassoc.util.UNCountryCode;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.Transient;

@Name("assocMemberHome")
@Stateful
public class AssocMemberHomeImpl
        extends EntityHome<AssocMember>
        implements AssocMemberHome
{

   private static final long serialVersionUID = 1968662189025570388L;
   @Logger
   private Log log;
   @In
   private FacesMessages facesMessages;
   @In
   private Renderer renderer;

   // It does not seem to work.
   @In(required = false)
   private AssocMember authenticatedMember;

   // Used to delete a clubEventHosting entry
   @RequestParameter
   private Long eventHostingId;

   // Image download fields
   private byte[] m_uploadedData;
   private String m_uploadedContentType;
   private String m_uploadedFileName;

   @Factory("assocMember")
   public AssocMember initAssocMember()
   {

      return getInstance();

   }

   protected AssocMember createInstance()
   {

      AssocMember amember = new AssocMember();
      amember.setAssocName("theassocname");
      amember.setUseUSPS(Boolean.FALSE);
      amember.setAddressValid(Boolean.FALSE);
      amember.setAddress(Cts.Dflt.ADDRESS);
      amember.setAptOrSuite(Cts.Dflt.APTORNB);
      amember.setCity(Cts.Dflt.CITY);
      amember.setCountry(Cts.Dflt.COUNTRY);
      amember.setState(Cts.Dflt.STATE);
      amember.setZip(Cts.Dflt.ZIP);
      amember.setHomePhone(Cts.Dflt.PHONE);
      amember.setHomeFax(Cts.Dflt.PHONE);
      amember.setNation1(UNCountryCode.USA);
      amember.setNation2(UNCountryCode.___);
      amember.setUseEMail(Boolean.FALSE);
      amember.setEMailValid(Boolean.TRUE);
      amember.setEmail(Cts.Dflt.E_MAIL);
      amember.setHomePage("");
      amember.setMembershipValid(Boolean.TRUE);
      amember.setMemberSince(Calendar.getInstance().getTime());
      amember.setMemberUntil(Calendar.getInstance().getTime());
      amember.setMembershipDues(0);
      amember.setLastPaymentDate(Calendar.getInstance().getTime());
      amember.setMKind(AssocMember.MKind.guest);
      amember.setFreeMembership(Boolean.FALSE);
      amember.setPaymentInfo("");
      amember.setFormulaire("");

      return amember;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

   public void toPastMember()
   {

      AssocMember assocMember = getInstance();
      List<FamilyMember> familyMembers = assocMember.getFamilyMembers();

      for (FamilyMember familyMember : familyMembers)
      {

         List<ClubMembership> mships =
            new ArrayList<ClubMembership>(familyMember.getMemberships());
         familyMember.setMemberships(new ArrayList<ClubMembership>());

         for (ClubMembership membership : mships)
         {

            membership.getClub().getClubMembers().remove(membership);

            // membership.getMember().getMemberships().remove(membership);
            getEntityManager().remove(membership);

         }

         getEntityManager().persist(familyMember);

      }

      assocMember.setMKind(AssocMember.MKind.pastmember);
      assocMember.setUseEMail(Boolean.FALSE);
      assocMember.setUseUSPS(Boolean.FALSE);
      getEntityManager().persist(assocMember);

   }

   public void emailMembership()
   {

      try
      {

         renderer.render("/mailing/membership.xhtml");
         facesMessages.add("Email sent successfully");

      }
      catch (Exception e)
      {

         log.error("Error sending mail", e);
         facesMessages.add("Email sending failed: " + e.getMessage());

      }

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<AssocMember> getLateMembers()
   {

      Query query = buildQuery(RKind.latemembers);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<AssocMember> getNewsLetterThruUspsMembers()
   {

      Query query = buildQuery(RKind.uspsbulletinmembers);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<AssocMember> getNewsLetterThruEMailMembers()
   {

      Query query = buildQuery(RKind.emailbulletinmembers);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<AssocMember> getAllMembers()
   {

      Query query = buildQuery(RKind.allmembers);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<AssocMember> getAllMembersForEvents()
   {

      Query query = buildQuery(RKind.allmembersforevents);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<AssocMember> getDirectory()
   {

      Query query = buildQuery(RKind.nonlatemembers);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<AssocMember> filterAssocMembers(String reqKindS)
   {

      int reqKindI =
         (((reqKindS == null) || (reqKindS.length() == 0))
            ? 2 : Integer.parseInt(reqKindS));
      RKind reqKind = null;

      switch (reqKindI)
      {

         case 0:
            reqKind = RKind.testmembers;

            break;

         case 1:
            reqKind = RKind.validentries;

            break;

         case 2:
            reqKind = RKind.allmembers;

            break;

         case 3:
            reqKind = RKind.nonlatebulletinmembers;

            break;

         case 4:
            reqKind = RKind.latebulletinmembers;

            break;

         case 5:
            reqKind = RKind.otherbulletinmembers;

            break;

         case 6:
            reqKind = RKind.nonlatemembers;

            break;

         case 7:
            reqKind = RKind.latemembers;

            break;

         case 8:
            reqKind = RKind.guestmembers;

            break;

         case 9:
            reqKind = RKind.sponsors;

            break;

         case 10:
            reqKind = RKind.advertisers;

            break;

         case 11:
            reqKind = RKind.partnerorgs;

            break;

         case 12:
            reqKind = RKind.committeemembers;

            break;

         case 13:
            reqKind = RKind.emailbulletinmembers;

            break;

         case 14:
            reqKind = RKind.uspsbulletinmembers;

            break;

         case 15:
            reqKind = RKind.pastmembers;

            break;

         case 16:
            reqKind = RKind.allmembersforevents;

            break;

         case 17:
            reqKind = RKind.corporate;

            break;

         case 18:
            reqKind = RKind.youngpro;

            break;

         default:
            reqKind = RKind.validentries;

      }

      Query query = buildQuery(reqKind);

      return ((query != null) ? query.getResultList()
                              : new ArrayList<AssocMember>());

   }

   @Transient
   public Query buildQuery(AssocMemberHome.RKind reqKind)
   {

      Query query = null;
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MONTH, Cts.LATE_OFFSET);

      if (reqKind == AssocMemberHome.RKind.nonlatebulletinmembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.useEMail = true " + "AND c.memberUntil > :nowdate "
               + "AND c.membershipValid = true "
               + "AND ((c.MKind = :theKind1) or ((c.MKind = :theKind2a) or (c.MKind = :theKind2b) or (c.MKind = :theKind2c))) "
               + "ORDER BY c.assocName");
         query.setParameter("nowdate", cal.getTime());
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2a", AssocMember.MKind.committee);
         query.setParameter("theKind2b", AssocMember.MKind.corporate);
         query.setParameter("theKind2c", AssocMember.MKind.youngpro);

      }
      else if (reqKind == AssocMemberHome.RKind.latebulletinmembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.useEMail = true " + "AND c.memberUntil < :nowdate "
               + "AND c.membershipValid = true "
               + "AND ((c.MKind = :theKind1) or ((c.MKind = :theKind2a) or (c.MKind = :theKind2b) or (c.MKind = :theKind2c))) "
               + "ORDER BY c.assocName");
         query.setParameter("nowdate", cal.getTime());
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2a", AssocMember.MKind.committee);
         query.setParameter("theKind2b", AssocMember.MKind.corporate);
         query.setParameter("theKind2c", AssocMember.MKind.youngpro);

      }
      else if (reqKind == AssocMemberHome.RKind.otherbulletinmembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.useEMail = true " + "AND c.membershipValid = true "
               + "AND ((c.MKind = :theKind1) or (c.MKind = :theKind2) or (c.MKind = :theKind3)) "
               + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.sponsor);
         query.setParameter("theKind2", AssocMember.MKind.advertiser);
         query.setParameter("theKind3", AssocMember.MKind.partnerorg);

      }
      else if (reqKind == AssocMemberHome.RKind.allmembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true "
               + "AND ((c.MKind = :theKind1) or ((c.MKind = :theKind2a) or (c.MKind = :theKind2b) or (c.MKind = :theKind2c))) "
               + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2a", AssocMember.MKind.committee);
         query.setParameter("theKind2b", AssocMember.MKind.corporate);
         query.setParameter("theKind2c", AssocMember.MKind.youngpro);

      }
      else if (reqKind == AssocMemberHome.RKind.nonlatemembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.memberUntil > :nowdate "
               + "AND c.membershipValid = true "
               + "AND ((c.MKind = :theKind1) or ((c.MKind = :theKind2a) or (c.MKind = :theKind2b) or (c.MKind = :theKind2c))) "
               + "ORDER BY c.assocName");
         query.setParameter("nowdate", cal.getTime());
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2a", AssocMember.MKind.committee);
         query.setParameter("theKind2b", AssocMember.MKind.corporate);
         query.setParameter("theKind2c", AssocMember.MKind.youngpro);

      }
      else if (reqKind == AssocMemberHome.RKind.latemembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.memberUntil < :nowdate "
               + "AND c.membershipValid = true "
               + "AND ((c.MKind = :theKind1) or ((c.MKind = :theKind2a) or (c.MKind = :theKind2b) or (c.MKind = :theKind2c))) "
               + "ORDER BY c.assocName");
         query.setParameter("nowdate", cal.getTime());
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2a", AssocMember.MKind.committee);
         query.setParameter("theKind2b", AssocMember.MKind.corporate);
         query.setParameter("theKind2c", AssocMember.MKind.youngpro);

      }
      else if (reqKind == AssocMemberHome.RKind.guestmembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true "
               + "AND (c.MKind = :theKind1) " + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.guest);

      }
      else if (reqKind == AssocMemberHome.RKind.sponsors)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true "
               + "AND (c.MKind = :theKind1) " + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.sponsor);

      }
      else if (reqKind == AssocMemberHome.RKind.advertisers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true "
               + "AND (c.MKind = :theKind1) " + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.advertiser);

      }
      else if (reqKind == AssocMemberHome.RKind.partnerorgs)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true "
               + "AND (c.MKind = :theKind1) " + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.partnerorg);

      }
      else if (reqKind == AssocMemberHome.RKind.committeemembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true "
               + "AND (c.MKind = :theKind1) " + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.committee);

      }
      else if (reqKind == AssocMemberHome.RKind.corporate)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true "
               + "AND (c.MKind = :theKind1) " + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.corporate);

      }
      else if (reqKind == AssocMemberHome.RKind.youngpro)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true "
               + "AND (c.MKind = :theKind1) " + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.youngpro);

      }
      else if (reqKind == AssocMemberHome.RKind.testmembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.id = :authenticatedAssocId ");
         query.setParameter("authenticatedAssocId",
            authenticatedMember.getId());

      }
      else if (reqKind == AssocMemberHome.RKind.validentries)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true " + "ORDER BY c.assocName");

      }
      else if (reqKind == AssocMemberHome.RKind.emailbulletinmembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true " + "AND (c.useEMail = true) "
               + "ORDER BY c.assocName");

      }
      else if (reqKind == AssocMemberHome.RKind.uspsbulletinmembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true " + "AND (c.useUSPS = true) "
               + "ORDER BY c.assocName");

      }
      else if (reqKind == AssocMemberHome.RKind.pastmembers)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = false "
               + "OR (c.MKind = :theKind1) " + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.pastmember);

      }
      else if (reqKind == AssocMemberHome.RKind.allmembersforevents)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true "
               + "AND ((c.MKind = :theKind1) or ((c.MKind = :theKind2a) or (c.MKind = :theKind2b) or (c.MKind = :theKind2c)) or (c.MKind = :theKind3)) "
               + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2a", AssocMember.MKind.committee);
         query.setParameter("theKind2b", AssocMember.MKind.corporate);
         query.setParameter("theKind2c", AssocMember.MKind.youngpro);
         query.setParameter("theKind3", AssocMember.MKind.guest);

      }
      else
      {

         query = null;

      }

      return query;

   }

   //
   public String cancelHosting()
   {

      if ((eventHostingId == null))
      {

         String msg = "No ClubEventHosting selected";
         FacesMessages.instance().add(msg);
         log.info(msg);

         return "";

      }

      AssocMember aMember = getInstance();
      List<ClubEventHosting> evtHostings = aMember.getEventHostings();

      for (ClubEventHosting mship : evtHostings)
      {

         if (mship.getId().equals(eventHostingId))
         {

            String msg =
               "ClubEventHosting for [" + mship.getId() + "] deleted";
            log.info(msg);
            facesMessages.add(msg);
            aMember.getEventHostings().remove(mship);
            getEntityManager().remove(mship);
            getEntityManager().persist(aMember);

            return "clubEventHostingDeleted";

         }

      }

      String msg =
         "ClubEventHosting with id [" + eventHostingId + "] not found";
      log.info(msg);
      facesMessages.add(msg);

      return "";

   }

   // ===============================
   // Money Account handling
   // ===============================
   @Transient
   public Long findMnyAccountId()
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM MnyAccount c "
            + "WHERE c.owner = :theOwner");
      query.setParameter("theOwner", getInstance());

      try
      {

         MnyAccount res = (MnyAccount)query.getSingleResult();

         return res.getId();

      }
      catch (NoResultException ex)
      {

         return new Long(0);

      }
      catch (NonUniqueResultException ex)
      {

         return new Long(0);

      }

   }

   @Transient
   public void createMnyAccount()
   {

      // TODO Auto-generated method stub
   }

   // ===============================
   // WebAccess Account handling
   // ===============================
   @Transient
   public String findWebAccountName()
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM AccountMember c "
            + "WHERE c.member = :theMember");
      query.setParameter("theMember", getInstance());

      try
      {

         AccountMember res = (AccountMember)query.getSingleResult();

         return res.getUserName();

      }
      catch (NoResultException ex)
      {

         return "";

      }
      catch (NonUniqueResultException ex)
      {

         return "";

      }

   }

   @Transient
   public void createWebAccount()
   {

      // TODO Auto-generated method stub
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

   public String uploadImage()
   {

      if ((m_uploadedData == null) || (m_uploadedData.length == 0))
      {

         FacesMessages.instance().add("No image selected");

         return "";

      }
      else
      {

         AssocMember member = getInstance();
         AssocMemberImage img = member.getPicture();

         if (img == null)
         {

            img = new AssocMemberImage();
            img.setAssocMember(member);

         }

         img.setData(m_uploadedData);
         img.setContentType(m_uploadedContentType);
         img.setFileName(m_uploadedFileName);
         img.updateThumbnail();
         member.setPicture(img);
         getEntityManager().persist(img);
         m_uploadedData = null;
         m_uploadedContentType = null;
         m_uploadedFileName = null;

         return "imageUploaded";

      }

   }

}
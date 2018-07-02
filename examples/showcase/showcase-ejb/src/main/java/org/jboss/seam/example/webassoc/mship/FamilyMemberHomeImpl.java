package org.jboss.seam.example.webassoc.mship;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.example.webassoc.clubs.Club;
import org.jboss.seam.example.webassoc.mship.FamilyMember.FKind;
import org.jboss.seam.example.webassoc.mship.FamilyMember.Gender;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.example.webassoc.util.OccupationCode;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;
import javax.persistence.Transient;

@Name("familyMemberHome")
@Stateful
public class FamilyMemberHomeImpl
        extends EntityHome<FamilyMember>
        implements FamilyMemberHome
{

   private static final long serialVersionUID = 6209175953423557916L;
   @In(required = false)
   private AssocMember assocMember;
   @In(required = false)
   private AssocMember authenticatedMember;
   @In
   private FacesMessages facesMessages;
   @Logger
   private Log log;

   // Used to delete a clubMembership entry
   @RequestParameter
   private Long membershipId;

   @Factory("familyMember")
   public FamilyMember initFamilyMember()
   {

      return getInstance();

   }

   protected FamilyMember createInstance()
   {

      FamilyMember fmember = new FamilyMember();
      fmember.setAssocMember(assocMember);
      assocMember.getFamilyMembers().add(fmember);
      fmember.setPosTitle("");
      fmember.setLastName(assocMember.getAssocName());
      fmember.setFirstName(Cts.Dflt.FIRST_NAME);
      fmember.setDob(Calendar.getInstance().getTime());
      fmember.setGender(Gender.male);
      fmember.setFKind(FKind.kid);
      fmember.setCellPhone(Cts.Dflt.PHONE);
      fmember.setPhoneExt(Cts.Dflt.PHONE);
      fmember.setFaxExt(Cts.Dflt.PHONE);
      fmember.setEmailExt(Cts.Dflt.E_MAIL);
      fmember.setOccupation(OccupationCode.___);
      fmember.setMovedAway(false);

      return fmember;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

   // ====================================================
   // Business operations
   // ====================================================
   public String removeMember()
   {

      return remove();

   }

   public String addMember()
   {

      String res = persist();
      FamilyMember fmember = getInstance();

      if (fmember.getAssocMember().isClassic())
      {

         fmember.addDefaultMemberships(getEntityManager());

      }

      getEntityManager().persist(fmember);

      return res;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<FamilyMember> getAllMembers()
   {

      Query query = buildQuery(RKind.all_Members);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<FamilyMember> getAllMembersForEvents()
   {

      Query query = buildQuery(RKind.all_Members_For_Events);

      return query.getResultList();

   }

   @SuppressWarnings("unchecked")
   @Transient
   public List<FamilyMember> filterFamilyMembers(String reqKindS)
   {

      int reqKindI =
         (((reqKindS == null) || (reqKindS.length() == 0))
            ? 1 : Integer.parseInt(reqKindS));
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
            reqKind = RKind.kid_0_3_Members;

            break;

         case 3:
            reqKind = RKind.kid_3_6_Members;

            break;

         case 4:
            reqKind = RKind.kid_6_10_Members;

            break;

         case 5:
            reqKind = RKind.kid_10_15_Members;

            break;

         case 6:
            reqKind = RKind.kid_15_18_Members;

            break;

         case 7:
            reqKind = RKind.kid_18_20_Members;

            break;

         case 8:
            reqKind = RKind.kid_21_plus_Members;

            break;

         case 9:
            reqKind = RKind.all_Members;

            break;

         case 10:
            reqKind = RKind.all_parent_Members;

            break;

         case 11:
            reqKind = RKind.all_kid_Members;

            break;

         case 12:
            reqKind = RKind.all_Members_For_Events;

            break;

         default:
            reqKind = RKind.validentries;

      }

      Query famMbQuery = buildQuery(reqKind);
      List<FamilyMember> members = new ArrayList<FamilyMember>();

      if (famMbQuery != null)
      {

         List<FamilyMember> fmembers = famMbQuery.getResultList();

         for (FamilyMember fmember : fmembers)
         {

            members.add(fmember);

         }

      }

      return members;

   }

   @Transient
   public Query buildQuery(FamilyMemberHome.RKind reqKind)
   {

      Query query = null;
      Calendar cal = Calendar.getInstance();

      if (reqKind == FamilyMemberHome.RKind.kid_0_3_Members)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.dob >= :min and c.dob <= :max and c.assocMember.membershipValid = true "
               + "AND ((c.assocMember.MKind = :theKind1) or ((c.assocMember.MKind = :theKind2a) or (c.assocMember.MKind = :theKind2b) or (c.assocMember.MKind = :theKind2c))) "
               + "ORDER BY c.assocMember.assocName");
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2a", AssocMember.MKind.committee);
         query.setParameter("theKind2b", AssocMember.MKind.corporate);
         query.setParameter("theKind2c", AssocMember.MKind.youngpro);
         cal.add(Calendar.YEAR, 0);
         query.setParameter("max", cal.getTime());
         cal.add(Calendar.YEAR, -3);
         query.setParameter("min", cal.getTime());

      }
      else if (reqKind == FamilyMemberHome.RKind.kid_3_6_Members)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.dob >= :min and c.dob <= :max and c.assocMember.membershipValid = true "
               + "AND ((c.assocMember.MKind = :theKind1) or (c.assocMember.MKind = :theKind2)) "
               + "ORDER BY c.assocMember.assocName");
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2", AssocMember.MKind.committee);
         cal.add(Calendar.YEAR, -3);
         query.setParameter("max", cal.getTime());
         cal.add(Calendar.YEAR, -3);
         query.setParameter("min", cal.getTime());

      }
      else if (reqKind == FamilyMemberHome.RKind.kid_6_10_Members)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.dob >= :min and c.dob <= :max and c.assocMember.membershipValid = true "
               + "AND ((c.assocMember.MKind = :theKind1) or (c.assocMember.MKind = :theKind2)) "
               + "ORDER BY c.assocMember.assocName");
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2", AssocMember.MKind.committee);
         cal.add(Calendar.YEAR, -6);
         query.setParameter("max", cal.getTime());
         cal.add(Calendar.YEAR, -4);
         query.setParameter("min", cal.getTime());

      }
      else if (reqKind == FamilyMemberHome.RKind.kid_10_15_Members)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.dob >= :min and c.dob <= :max and c.assocMember.membershipValid = true "
               + "AND ((c.assocMember.MKind = :theKind1) or (c.assocMember.MKind = :theKind2)) "
               + "ORDER BY c.assocMember.assocName");
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2", AssocMember.MKind.committee);
         cal.add(Calendar.YEAR, -10);
         query.setParameter("max", cal.getTime());
         cal.add(Calendar.YEAR, -5);
         query.setParameter("min", cal.getTime());

      }
      else if (reqKind == FamilyMemberHome.RKind.kid_15_18_Members)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.dob >= :min and c.dob <= :max and c.assocMember.membershipValid = true "
               + "AND ((c.assocMember.MKind = :theKind1) or (c.assocMember.MKind = :theKind2)) "
               + "ORDER BY c.assocMember.assocName");
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2", AssocMember.MKind.committee);
         cal.add(Calendar.YEAR, -15);
         query.setParameter("max", cal.getTime());
         cal.add(Calendar.YEAR, -2);
         query.setParameter("min", cal.getTime());

      }
      else if (reqKind == FamilyMemberHome.RKind.kid_18_20_Members)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.dob >= :min and c.dob <= :max and c.assocMember.membershipValid = true "
               + "AND ((c.assocMember.MKind = :theKind1) or (c.assocMember.MKind = :theKind2)) "
               + "ORDER BY c.assocMember.assocName");
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2", AssocMember.MKind.committee);
         cal.add(Calendar.YEAR, -17);
         query.setParameter("max", cal.getTime());
         cal.add(Calendar.YEAR, -4);
         query.setParameter("min", cal.getTime());

      }
      else if (reqKind == FamilyMemberHome.RKind.kid_21_plus_Members)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.dob <= :max and c.FKind = :theKind and c.assocMember.membershipValid = true "
               + "AND ((c.assocMember.MKind = :theKind1) or (c.assocMember.MKind = :theKind2)) "
               + "ORDER BY c.assocMember.assocName");
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2", AssocMember.MKind.committee);
         cal.add(Calendar.YEAR, -21);
         query.setParameter("max", cal.getTime());
         query.setParameter("theKind", FKind.kid);

      }
      else if (reqKind == FamilyMemberHome.RKind.testmembers)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.assocMember.id = :authenticatedAssocId");
         query.setParameter("authenticatedAssocId",
            authenticatedMember.getId());

      }
      else if (reqKind == FamilyMemberHome.RKind.validentries)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.assocMember.membershipValid = true");

      }
      else if (reqKind == FamilyMemberHome.RKind.all_Members)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where ((c.FKind = :theKind1a) or (c.FKind = :theKind1b) or (c.FKind = :theKind1c) or (c.FKind = :theKind2)) AND ((c.assocMember.MKind = :theKind3) or ((c.assocMember.MKind = :theKind4a) or (c.assocMember.MKind = :theKind4b) or (c.assocMember.MKind = :theKind4c))) AND c.assocMember.membershipValid = true ORDER BY c.assocMember.assocName");
         query.setParameter("theKind1a", FKind.spouse);
         query.setParameter("theKind1b", FKind.main);
         query.setParameter("theKind1c", FKind.contact);
         query.setParameter("theKind2", FKind.kid);
         query.setParameter("theKind3", AssocMember.MKind.normal);
         query.setParameter("theKind4a", AssocMember.MKind.committee);
         query.setParameter("theKind4b", AssocMember.MKind.corporate);
         query.setParameter("theKind4c", AssocMember.MKind.youngpro);

      }
      else if (reqKind == FamilyMemberHome.RKind.all_parent_Members)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where ((c.FKind = :theKind1a) or (c.FKind = :theKind1b) or (c.FKind = :theKind1c)) AND ((c.assocMember.MKind = :theKind3) or ((c.assocMember.MKind = :theKind4a) or (c.assocMember.MKind = :theKind4b) or (c.assocMember.MKind = :theKind4c))) AND c.assocMember.membershipValid = true ORDER BY c.assocMember.assocName");
         query.setParameter("theKind1a", FKind.spouse);
         query.setParameter("theKind1b", FKind.main);
         query.setParameter("theKind1c", FKind.contact);
         query.setParameter("theKind3", AssocMember.MKind.normal);
         query.setParameter("theKind4a", AssocMember.MKind.committee);
         query.setParameter("theKind4b", AssocMember.MKind.corporate);
         query.setParameter("theKind4c", AssocMember.MKind.youngpro);

      }
      else if (reqKind == FamilyMemberHome.RKind.all_kid_Members)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where c.FKind = :theKind AND ((c.assocMember.MKind = :theKind3) or (c.assocMember.MKind = :theKind4)) AND c.assocMember.membershipValid = true ORDER BY c.assocMember.assocName");
         query.setParameter("theKind", FKind.kid);
         query.setParameter("theKind3", AssocMember.MKind.normal);
         query.setParameter("theKind4", AssocMember.MKind.committee);

      }
      else if (reqKind == FamilyMemberHome.RKind.all_Members_For_Events)
      {

         query =
            getEntityManager().createQuery(
               "select c from FamilyMember c where ((c.FKind = :theKind1a) or (c.FKind = :theKind1b) or (c.FKind = :theKind1c)) AND ((c.assocMember.MKind = :theKind3) or ((c.assocMember.MKind = :theKind4a) or (c.assocMember.MKind = :theKind4b) or (c.assocMember.MKind = :theKind4c))) AND c.assocMember.membershipValid = true ORDER BY c.assocMember.assocName");
         query.setParameter("theKind1a", FKind.spouse);
         query.setParameter("theKind1b", FKind.main);
         query.setParameter("theKind1c", FKind.contact);
         query.setParameter("theKind3", AssocMember.MKind.normal);
         query.setParameter("theKind4a", AssocMember.MKind.committee);
         query.setParameter("theKind4b", AssocMember.MKind.corporate);
         query.setParameter("theKind4c", AssocMember.MKind.youngpro);

      }
      else
      {

         query = null;

      }

      return query;

   }

   //
   public String cancelMembership()
   {

      if ((membershipId == null))
      {

         String msg = "No Membership selected";
         FacesMessages.instance().add(msg);
         log.info(msg);

         return "";

      }

      FamilyMember reg = getInstance();
      List<ClubMembership> mships = reg.getMemberships();

      for (ClubMembership mship : mships)
      {

         if (mship.getId().equals(membershipId))
         {

            String msg = "Membership for [" + mship.getId() + "] deleted";
            log.info(msg);
            facesMessages.add(msg);
            reg.getMemberships().remove(mship);
            getEntityManager().remove(mship);
            getEntityManager().persist(reg);

            return "membershipDeleted";

         }

      }

      String msg = "Membership with id [" + membershipId + "] not found";
      log.info(msg);
      facesMessages.add(msg);

      return "";

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<Club> getAllowedClubs()
   {

      FamilyMember foundFamilyMember = getInstance();
      Query query = null;

      if (foundFamilyMember == null)
      {

         query =
            getEntityManager().createQuery("SELECT c FROM Club c "
               + "where (c.discontinued = :discFilter)");
         query.setParameter("discFilter", Boolean.FALSE);

      }
      else
      {

         boolean isFamilyMemberKid =
            (foundFamilyMember.getFKind().equals(FamilyMember.FKind.kid));
         boolean isFamilyMemberGuest =
            (foundFamilyMember.getAssocMember().getMKind().equals(
                  AssocMember.MKind.guest));

         if (isFamilyMemberKid)
         {

            if (isFamilyMemberGuest)
            {

               query =
                  getEntityManager().createQuery("SELECT c FROM Club c "
                     + " where (c.discontinued = :discFilter) "
                     + " and (c.openedToKids = :kidFilter) "
                     + " and (c.openedToGuests = :guestFilter)");
               query.setParameter("discFilter", Boolean.FALSE);
               query.setParameter("kidFilter", Boolean.TRUE);
               query.setParameter("guestFilter", Boolean.TRUE);

            }
            else
            {

               query =
                  getEntityManager().createQuery("SELECT c FROM Club c "
                     + " where (c.discontinued = :discFilter) "
                     + " and (c.openedToKids = :kidFilter) ");
               query.setParameter("discFilter", Boolean.FALSE);
               query.setParameter("kidFilter", Boolean.TRUE);

            }

         }
         else
         {

            if (isFamilyMemberGuest)
            {

               query =
                  getEntityManager().createQuery("SELECT c FROM Club c "
                     + " where (c.discontinued = :discFilter) "
                     + " and (c.openedToGuests = :guestFilter)");
               query.setParameter("discFilter", Boolean.FALSE);
               query.setParameter("guestFilter", Boolean.TRUE);

            }
            else
            {

               query =
                  getEntityManager().createQuery("SELECT c FROM Club c "
                     + " where (c.discontinued = :discFilter) ");
               query.setParameter("discFilter", Boolean.FALSE);

            }

         }

      }

      return query.getResultList();

   }

}
package org.jboss.seam.example.webassoc.admin;

import static org.jboss.seam.ScopeType.SESSION;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.example.webassoc.clubs.Club;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.ClubMembership;
import org.jboss.seam.example.webassoc.mship.FamilyMember;
import org.jboss.seam.example.webassoc.mship.FamilyMember.FKind;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.log.Log;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateful
@Scope(SESSION)
@Name("clubMembershipAdminOperations")
public class ClubMembershipAdminOperationsImpl
        implements AdminOperations,
           Serializable
{

   private static final long serialVersionUID = -6179677571135407921L;
   @Logger
   private Log log;
   @PersistenceContext
   private EntityManager m_em;

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton1()
   {

      log.info(
         "ClubMembershipAdminOperationsImpl:cleanupNonMemberClubMembership invoked");
      cleanupNonMemberClubMembership();

   }

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton2()
   {

      log.info(
         "ClubMembershipAdminOperationsImpl:invokedByMenuButton2 invoked");

   }

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton3()
   {

      log.info("ClubMembershipAdminOperationsImpl:checkPastMember invoked");
      checkPastMember();

   }

   @Restrict("#{s:hasRole('admin')}")
   public void checkPastMember()
   {

   }

   @SuppressWarnings("unchecked")
   @Restrict("#{s:hasRole('admin')}")
   public void simplifyKidsMembership()
   {

      Query query =
         m_em.createQuery(
            "SELECT c FROM FamilyMember c WHERE (c.FKind = :theKind1) ");
      query.setParameter("theKind1", FamilyMember.FKind.kid);

      List<FamilyMember> familyMembers = query.getResultList();

      for (FamilyMember familyMember : familyMembers)
      {

         List<ClubMembership> mships =
            new ArrayList<ClubMembership>(familyMember.getMemberships());

         for (ClubMembership membership : mships)
         {

            if ((membership.getClub().getId() == Cts.ClubIds.Putlock)
               || (membership.getClub().getId() == Cts.ClubIds.CafeRencontre))
            {

               membership.getClub().getClubMembers().remove(membership);
               membership.getMember().getMemberships().remove(membership);
               m_em.remove(membership);

            }

         }

         m_em.persist(familyMember);

      }

   }

   @SuppressWarnings("unchecked")
   @Restrict("#{s:hasRole('admin')}")
   public void cleanupNonMemberClubMembership()
   {

      AssocMember.MKind[] todos =
         new AssocMember.MKind[]
         {
            AssocMember.MKind.pastmember, AssocMember.MKind.guest,
            AssocMember.MKind.sponsor, AssocMember.MKind.advertiser,
            AssocMember.MKind.partnerorg
         };

      for (int i = 0; i < todos.length; i++)
      {

         Query query =
            m_em.createQuery("SELECT c FROM AssocMember c "
               + "WHERE (c.MKind = :theKind1) " + "ORDER BY c.assocName");
         query.setParameter("theKind1", todos[i]);

         List<AssocMember> assocMembers = query.getResultList();

         for (AssocMember assocMember : assocMembers)
         {

            List<FamilyMember> familyMembers = assocMember.getFamilyMembers();

            for (FamilyMember familyMember : familyMembers)
            {

               List<ClubMembership> mships =
                  new ArrayList<ClubMembership>(familyMember.getMemberships());
               familyMember.setMemberships(new ArrayList<ClubMembership>());

               for (ClubMembership membership : mships)
               {

                  if (todos[i].equals(AssocMember.MKind.pastmember))
                  {

                     log.info("PASTMEMBER: " + familyMember.getFirstName()
                        + " " + familyMember.getLastName()
                        + " still member of "
                        + membership.getClub().getName());
                     // membership.getClub().getClubMembers().remove(membership);
                     // m_em.remove(membership);

                  }
                  else if (todos[i].equals(AssocMember.MKind.guest))
                  {

                     if (!membership.getClub().getOpenedToGuests())
                     {

                        log.info("GUEST: " + familyMember.getFirstName() + " "
                           + familyMember.getLastName() + " still member of "
                           + membership.getClub().getName());

                     }

                  }
                  else
                  {

                     log.info(familyMember.getFirstName() + " "
                        + familyMember.getLastName() + " still member of "
                        + membership.getClub().getName());
                     // membership.getClub().getClubMembers().remove(membership);
                     // m_em.remove(membership);

                  }

               }

               //  m_em.persist(familyMember);
            }

         }

      }

   }

   @SuppressWarnings("unchecked")
   @Restrict("#{s:hasRole('admin')}")
   public void fillupMemberships()
   {

      Query cquery =
         m_em.createQuery("SELECT c FROM Club c WHERE c.id = "
            + Cts.ClubIds.EvtSpeciaux + " ");
      Club club = (Club)cquery.getSingleResult();
      Query query =
         m_em.createQuery(
            "SELECT c FROM FamilyMember c WHERE ((c.FKind = :theKind1a) or (c.FKind = :theKind1b) or (c.FKind = :theKind1c) or (c.FKind = :theKind2))");
      query.setParameter("theKind1a", FKind.spouse);
      query.setParameter("theKind1b", FKind.main);
      query.setParameter("theKind1c", FKind.contact);
      query.setParameter("theKind2", FKind.kid);

      List<FamilyMember> familyMembers = query.getResultList();

      for (FamilyMember familyMember : familyMembers)
      {

         List<ClubMembership> mships =
            new ArrayList<ClubMembership>(familyMember.getMemberships());
         boolean found = false;

         for (ClubMembership membership : mships)
         {

            if (membership.getClub().getId() == Cts.ClubIds.EvtSpeciaux)
            {

               found = true;

            }

         }

         if (!found)
         {

            ClubMembership membership = new ClubMembership();
            membership.setClub(club);
            club.getClubMembers().add(membership);
            membership.setMember(familyMember);
            familyMember.getMemberships().add(membership);
            membership.setActive(Boolean.TRUE);
            membership.setFKind(ClubMembership.FKind.member);
            m_em.persist(membership);
            m_em.persist(familyMember);

         }

      }

   }

   @SuppressWarnings("unchecked")
   @Restrict("#{s:hasRole('admin')}")
   public void initClubMemberships()
   {

      // List<FamilyMember> familyMembers = em
      // .createQuery("from FamilyMember a where a.fKind = :fKind")
      // .setParameter("fKind", FamilyMember.FKind.spouse).getResultList();
      List<FamilyMember> familyMembers =
         m_em.createQuery("from FamilyMember a").getResultList();
      List<Club> clubs =
         m_em.createQuery(
            "from Club c where c.automaticMembership = :automaticMembership")
         .setParameter("automaticMembership", Boolean.TRUE).getResultList();

      for (FamilyMember facilityMember : familyMembers)
      {

         facilityMember = m_em.merge(facilityMember);

         for (Club club : clubs)
         {

            facilityMember = m_em.merge(facilityMember);

            ClubMembership membership = new ClubMembership();
            membership.setClub(club);
            club.getClubMembers().add(membership);
            membership.setMember(facilityMember);
            facilityMember.getMemberships().add(membership);
            membership.setActive(Boolean.TRUE);
            membership.setFKind(ClubMembership.FKind.member);
            m_em.persist(membership);

         }

      }

   }

   @Remove
   public void destroy()
   {

      // TODO Auto-generated method stub
   }

}
package org.jboss.seam.example.webassoc.mship;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.clubs.Club;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;
import javax.persistence.Transient;

@Name("clubMembershipHome")
@Stateful
public class ClubMembershipHomeImpl
        extends EntityHome<ClubMembership>
        implements ClubMembershipHome
{

   private static final long serialVersionUID = 768164000040253147L;
   @In(required = false)
   private Club club;
   @In(required = false)
   private FamilyMember familyMember;
   @In
   private FacesMessages facesMessages;
   @Logger
   private Log log;

   @Factory("clubMembership")
   public ClubMembership initClubMembership()
   {

      return getInstance();

   }

   protected ClubMembership createInstance()
   {

      ClubMembership fmember = new ClubMembership();

      if (club != null)
      {

         fmember.setClub(club);
         club.getClubMembers().add(fmember);

      }

      if (familyMember != null)
      {

         fmember.setMember(familyMember);
         familyMember.getMemberships().add(fmember);

      }

      fmember.setFKind(ClubMembership.FKind.member);
      fmember.setActive(Boolean.TRUE);

      return fmember;

   }

   public String unregister()
   {

      ClubMembership mship = getInstance();

      if (!mship.getClub().getOpenEnrollment())
      {

         log.info("Club is currently closed to enrollment");
         facesMessages.add("Club is currently closed to enrollment");

         return null;

      }

      return remove();

   }

   public String register()
   {

      ClubMembership mship = getInstance();

      if (club != null)
      {

         getEntityManager().persist(club);

      }

      if (!mship.getClub().getOpenEnrollment())
      {

         log.info("Club is currently closed to enrollment");
         facesMessages.add("Club is currently closed to enrollment");

         return null;

      }

      String res = persist();

      return res;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<ClubMembership> selectMembersOfClub()
   {

      Club theClub = null;
      theClub = club;

      Query membershipQuery = null;

      if (theClub != null)
      {

         membershipQuery =
            getEntityManager().createQuery(
               "select c from ClubMembership c where c.active = true and c.club = :club");
         membershipQuery.setParameter("club", theClub);

      }
      else
      {

         membershipQuery =
            getEntityManager().createQuery(
               "select c from ClubMembership c where c.active = true");

      }

      List<ClubMembership> memberships = membershipQuery.getResultList();

      return memberships;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
package org.jboss.seam.example.webassoc.mship;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.clubs.Club;
import org.jboss.seam.framework.EntityHome;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;
import javax.persistence.Transient;

@Name("clubEventHostingHome")
@Stateful
public class ClubEventHostingHomeImpl
        extends EntityHome<ClubEventHosting>
        implements ClubEventHostingHome
{

   private static final long serialVersionUID = 4094270646462234223L;
   @In(required = false)
   private Club club;
   @In(required = false)
   private AssocMember assocMember;

   @Factory("clubEventHosting")
   public ClubEventHosting initClubEventHosting()
   {

      return getInstance();

   }

   protected ClubEventHosting createInstance()
   {

      ClubEventHosting fmember = new ClubEventHosting();

      if (club != null)
      {

         fmember.setClub(club);
         club.getClubHosts().add(fmember);

      }

      if (assocMember != null)
      {

         fmember.setMember(assocMember);
         assocMember.getEventHostings().add(fmember);

      }

      fmember.setFKind(ClubEventHosting.FKind.normal);
      fmember.setActive(Boolean.TRUE);

      return fmember;

   }

   public String unregister()
   {

      // ClubEventHosting mship = getInstance();
      return remove();

   }

   public String register()
   {

      // ClubEventHosting mship = getInstance();
      String res = persist();

      return res;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<ClubEventHosting> selectMembersOfClub()
   {

      Club theClub = null;
      theClub = club;

      Query membershipQuery = null;

      if (theClub != null)
      {

         membershipQuery =
            getEntityManager().createQuery(
               "select c from ClubEventHosting c where c.active = true and c.club = :club");
         membershipQuery.setParameter("club", theClub);

      }
      else
      {

         membershipQuery =
            getEntityManager().createQuery(
               "select c from ClubEventHosting c where c.active = true");

      }

      List<ClubEventHosting> memberships = membershipQuery.getResultList();

      return memberships;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
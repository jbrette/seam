package org.jboss.seam.example.webassoc.mship;

import java.util.List;

import javax.ejb.Local;

@Local
public interface ClubMembershipHome
{

   // Methods from ClubMembershipHomeImpl
   public ClubMembership initClubMembership();

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

   // ================================
   // Business operations
   // ================================
   // Register a familyMember to a club
   public String register();

   // Unregister a familyMember to a club
   public String unregister();

   // Returns the list membership for a particular club
   public List<ClubMembership> selectMembersOfClub();

}
package org.jboss.seam.example.webassoc.mship;

import java.util.List;

import javax.ejb.Local;

@Local
public interface ClubEventHostingHome
{

   // Methods from ClubEventHostingHomeImpl
   public ClubEventHosting initClubEventHosting();

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
   // Register a familyMemeber to a club
   public String register();

   // Unregister a familyMemeber to a club
   public String unregister();

   // Returns the list membership for a particular club
   public List<ClubEventHosting> selectMembersOfClub();

}
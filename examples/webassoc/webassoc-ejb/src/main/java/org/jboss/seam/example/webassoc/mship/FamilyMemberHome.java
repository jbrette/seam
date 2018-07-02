package org.jboss.seam.example.webassoc.mship;

import org.jboss.seam.example.webassoc.clubs.Club;

import java.util.List;

import javax.ejb.Local;

import javax.persistence.Query;

@Local
public interface FamilyMemberHome
{

   // =================================
   // Request Kind Definition
   // =================================
   public enum RKind
   {

      testmembers(0), validentries(1), kid_0_3_Members(2), kid_3_6_Members(3),
      kid_6_10_Members(4), kid_10_15_Members(5), kid_15_18_Members(6),
      kid_18_20_Members(7), kid_21_plus_Members(8), all_Members(9),
      all_parent_Members(10), all_kid_Members(11), all_Members_For_Events(12);

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

   // =================================
   // Methods from FamilyMemberHomeImpl
   // =================================
   public FamilyMember initFamilyMember();

   public void ejbRemove();

   // =================================
   // Methods from EntityHome and Home
   // =================================
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
   public String addMember();

   public String removeMember();

   // Query Builder
   public List<FamilyMember> filterFamilyMembers(String reqKindS);

   public Query buildQuery(FamilyMemberHome.RKind reqKind);

   // List of all valid members
   public List<FamilyMember> getAllMembers();

   // List of all valid members
   public List<FamilyMember> getAllMembersForEvents();

   // List of allowed clubs
   public List<Club> getAllowedClubs();

   //
   public String cancelMembership();

}
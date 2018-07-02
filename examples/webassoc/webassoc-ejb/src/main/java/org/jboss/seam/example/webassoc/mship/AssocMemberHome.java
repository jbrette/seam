package org.jboss.seam.example.webassoc.mship;

import java.util.List;

import javax.ejb.Local;

import javax.persistence.Query;

@Local
public interface AssocMemberHome
{

   // =================================
   // Request Kind Definition
   // =================================
   public enum RKind
   {

      testmembers(0), validentries(1), allmembers(2),
      nonlatebulletinmembers(3), latebulletinmembers(4),
      otherbulletinmembers(5), nonlatemembers(6), latemembers(7),
      guestmembers(8), sponsors(9), advertisers(10), partnerorgs(11),
      committeemembers(12), emailbulletinmembers(13), uspsbulletinmembers(14),
      pastmembers(15), allmembersforevents(16), corporate(17), youngpro(18);

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

   // =================================
   // Methods from AssocMemberHomeImpl
   // =================================
   public AssocMember initAssocMember();

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
   // To past member
   public void toPastMember();

   // EMail the membership
   public void emailMembership();

   // List of members who still need to pay their dues
   public List<AssocMember> getLateMembers();

   // List of members who requested to receive the NewsLetter through USPS
   public List<AssocMember> getNewsLetterThruUspsMembers();

   // List of members who requested to receive the NewsLetter through EMail
   public List<AssocMember> getNewsLetterThruEMailMembers();

   // List of all valid members
   public List<AssocMember> getAllMembers();

   // List of all valid members
   public List<AssocMember> getAllMembersForEvents();

   // List of all valid members
   public List<AssocMember> getDirectory();

   // Find MnyAccount
   public Long findMnyAccountId();

   public void createMnyAccount();

   // Find WebAccount
   public String findWebAccountName();

   public void createWebAccount();

   //
   public String cancelHosting();

   // Query Builder
   public List<AssocMember> filterAssocMembers(String reqKindS);

   public Query buildQuery(AssocMemberHome.RKind reqKind);

   // Image controls
   public void setUploadedData(byte[] uploadedData);

   public void setUploadedContentType(String contentType);

   public void setUploadedFileName(String fileName);

   public String uploadImage();

}
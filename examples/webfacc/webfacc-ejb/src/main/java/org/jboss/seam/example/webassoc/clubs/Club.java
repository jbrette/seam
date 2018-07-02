package org.jboss.seam.example.webassoc.clubs;

import org.hibernate.validator.Length;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.permission.Permission;
import org.jboss.seam.annotations.security.permission.Permissions;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.ClubEventHosting;
import org.jboss.seam.example.webassoc.mship.ClubMembership;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

@Entity
@Permissions(
   {
      @Permission(action = "clb_mgt"), // club management
      @Permission(action = "evt_mgt"), // event management
      @Permission(action = "mbr_mgt"), // member management
      @Permission(action = "art_mgt"), // article management
      @Permission(action = "pht_mgt"), // photo management
      @Permission(action = "cln_evt"), // clone event
      @Permission(action = "wri_art"), // write article
      @Permission(action = "view") // view
   }
)
@Name("club")
public class Club
        implements Serializable
{

   private static final long serialVersionUID = -7244895929699060186L;

   // =================================
   // Kind Definition
   // =================================
   public enum CKind
   {

      forEverybody(0), forTheHead(1), forTheHand(2), forTheFeet(3),
      forTheMouth(4), forTheGrownUp(5), forTheKids(6), other(7);

      private int id;

      private CKind(int id)
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
   private Long m_id;
   private String m_name;
   private CKind m_kind;
   private String m_description;
   private boolean m_automaticMembership;
   private boolean m_openEnrollment;
   private boolean m_discontinued;
   private boolean m_openedToGuests;
   private boolean m_openedToKids;
   private List<ClubMembership> m_clubMembers =
      new ArrayList<ClubMembership>();
   private List<ClubEvent> m_clubEvents = new ArrayList<ClubEvent>();
   private List<ClubEventHosting> m_clubHosts =
      new ArrayList<ClubEventHosting>();
   private List<ClubArticle> m_clubArticles = new ArrayList<ClubArticle>();
   private ClubImage m_picture;

   // =================================
   // Getter/Setter
   // =================================
   @Id @GeneratedValue
   public Long getId()
   {

      return m_id;

   }

   public void setId(Long id)
   {

      this.m_id = id;

   }

   @Length(max = 50)
   public String getName()
   {

      return m_name;

   }

   public void setName(String name)
   {

      this.m_name = name;

   }

   public CKind getCKind()
   {

      return m_kind;

   }

   public void setCKind(CKind kind)
   {

      this.m_kind = kind;

   }

   @Length(max = 5000)
   public String getDescription()
   {

      return m_description;

   }

   public void setDescription(String description)
   {

      this.m_description = description;

   }

   /**
    * Is the membership to that club/activity automatic. This is
    * useful register people to activities which are open to all the
    * members of the association.
    *
    * @return
    */
   public boolean getAutomaticMembership()
   {

      return m_automaticMembership;

   }

   public void setAutomaticMembership(boolean automaticMembership)
   {

      this.m_automaticMembership = automaticMembership;

   }

   /**
    * Is the club currently open for new membership. Some activities have a
    * limited number of places or the members are organized in team. It is up
    * to the responsable of the club to open the club to new enrollment.
    *
    * @return
    */
   public boolean getOpenEnrollment()
   {

      return m_openEnrollment;

   }

   public void setOpenEnrollment(boolean openEnrollment)
   {

      this.m_openEnrollment = openEnrollment;

   }

   /**
    * Is the club discontinued == No new events will created with it.
    * There is no club contact anymore
    *
    * @return
    */
   public boolean getDiscontinued()
   {

      return m_discontinued;

   }

   public void setDiscontinued(boolean discontinued)
   {

      this.m_discontinued = discontinued;

   }

   /**
    * Is the club opened to non members
    *
    * @return
    */
   public boolean getOpenedToGuests()
   {

      return m_openedToGuests;

   }

   public void setOpenedToGuests(boolean openedToGuests)
   {

      this.m_openedToGuests = openedToGuests;

   }

   /**
    * Is the club opened to non members
    *
    * @return
    */
   public boolean getOpenedToKids()
   {

      return m_openedToKids;

   }

   public void setOpenedToKids(boolean openedToKids)
   {

      this.m_openedToKids = openedToKids;

   }

   // =================================
   // GMAP
   // =================================
   @Transient
   public List<AssocMember> getGmapAddressList()
   {

      Set<AssocMember> clonedMembers = new HashSet<AssocMember>();

      for (ClubMembership mship : getClubMembers())
      {

         clonedMembers.add(mship.getMember().getAssocMember());

      }

      List<AssocMember> addressList = new ArrayList<AssocMember>();
      addressList.addAll(clonedMembers);

      return addressList;

   }

   // =================================
   // Relationship
   // =================================
   @OneToMany(mappedBy = "club", cascade = CascadeType.REMOVE)
   public List<ClubMembership> getClubMembers()
   {

      return m_clubMembers;

   }

   public void setClubMembers(List<ClubMembership> members)
   {

      this.m_clubMembers = members;

   }

   @OneToMany(mappedBy = "club", cascade = CascadeType.REMOVE)
   @OrderBy("startTime DESC")
   public List<ClubEvent> getClubEvents()
   {

      return m_clubEvents;

   }

   public void setClubEvents(List<ClubEvent> events)
   {

      this.m_clubEvents = events;

   }

   @OneToMany(mappedBy = "club", cascade = CascadeType.REMOVE)
   public List<ClubEventHosting> getClubHosts()
   {

      return m_clubHosts;

   }

   public void setClubHosts(List<ClubEventHosting> clubHosts)
   {

      this.m_clubHosts = clubHosts;

   }

   @OneToMany(mappedBy = "club", cascade = CascadeType.REMOVE)
   public List<ClubArticle> getClubArticles()
   {

      return m_clubArticles;

   }

   public void setClubArticles(List<ClubArticle> clubArticles)
   {

      this.m_clubArticles = clubArticles;

   }

   @OneToOne(fetch = FetchType.LAZY)
   public ClubImage getPicture()
   {

      return m_picture;

   }

   public void setPicture(ClubImage picture)
   {

      m_picture = picture;

   }

}
package org.jboss.seam.example.webassoc.mship;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.example.webassoc.clubs.Club;
import org.jboss.seam.example.webassoc.clubs.ClubHome;
import org.jboss.seam.example.webassoc.mship.FamilyMember.FKind;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Name("clubMembershipPicker")
@Scope(ScopeType.PAGE)
public class ClubMembershipPicker
        implements Serializable
{

   // Utility Converter
   public class FamilyMemberPickListConverter
           implements Converter
   {

      //converter uses a HashMap for switching from selectItems to your objects
      private HashMap<String, FamilyMember> map;

      public FamilyMemberPickListConverter(List<FamilyMember> familtMembers)
      {

         map = new HashMap<String, FamilyMember>();

         for (FamilyMember o : familtMembers)
         {

            map.put(Long.toString(o.getId()), o);

         }

      }

      public Object getAsObject(FacesContext arg0, UIComponent arg1,
         String string)
      {

         return map.get(string);

      }

      public String getAsString(FacesContext arg0, UIComponent arg1,
         Object obj)
      {

         if (obj instanceof FamilyMember)
         {

            return Long.toString(((FamilyMember)obj).getId());

         }

         return null;

      }

   }

   private static final long serialVersionUID = 4283770519301317889L;
   @In
   EntityManager entityManager;
   @In
   ClubHome clubHome;
   @Logger
   Log log;
   @In
   FacesMessages facesMessages;

   //the two PickLists
   private List<SelectItem> m_familyMemberPickList = null;
   private List<FamilyMember> m_membershipPickList = null;
   private List<FamilyMember> m_oldMembershipPickList = null;

   //Converting from SelectItems to your Object:
   private FamilyMemberPickListConverter m_converter = null;

   //rendering the dataTable depends on pickListResult.size()>0
   public boolean isResultListFilled()
   {

      List<FamilyMember> res = getMembershipPickList();

      if (res.size() > 0)
      {

         return true;

      }
      else
      {

         return false;

      }

   }

   @SuppressWarnings("unchecked")
   @Factory(value = "familyMemberPickList", scope = ScopeType.PAGE)
   public List<SelectItem> getFamilyMemberPickList()
   {

      if (m_familyMemberPickList == null)
      {

         m_familyMemberPickList = new ArrayList<SelectItem>();

         Club club = (Club)clubHome.getInstance();
         Query query = null;

         if (club.getDiscontinued())
         {

            // Dirty trick to be sure you can empty the clubmembership
            // if the club is closed
            query = entityManager.createQuery("select c from FamilyMember c");

         }

         if (club.getOpenedToKids())
         {

            if (club.getOpenedToGuests())
            {

               query =
                  entityManager.createQuery(
                     "select c from FamilyMember c where ((c.FKind = :theKind1a) or (c.FKind = :theKind1b) or (c.FKind = :theKind1c) or (c.FKind = :theKind2)) AND ((c.assocMember.MKind = :theKind3) or ((c.assocMember.MKind = :theKind4a) or (c.assocMember.MKind = :theKind4b) or (c.assocMember.MKind = :theKind4c)) or (c.assocMember.MKind = :theKind5)) AND c.assocMember.membershipValid = true ORDER BY c.lastName");
               query.setParameter("theKind1a", FKind.spouse);
               query.setParameter("theKind1b", FKind.main);
               query.setParameter("theKind1c", FKind.contact);
               query.setParameter("theKind2", FKind.kid);
               query.setParameter("theKind3", AssocMember.MKind.normal);
               query.setParameter("theKind4a", AssocMember.MKind.committee);
               query.setParameter("theKind4b", AssocMember.MKind.corporate);
               query.setParameter("theKind4c", AssocMember.MKind.youngpro);
               query.setParameter("theKind5", AssocMember.MKind.guest);

            }
            else
            {

               query =
                  entityManager.createQuery(
                     "select c from FamilyMember c where ((c.FKind = :theKind1a) or (c.FKind = :theKind1b) or (c.FKind = :theKind1c) or (c.FKind = :theKind2)) AND ((c.assocMember.MKind = :theKind3) or ((c.assocMember.MKind = :theKind4a) or (c.assocMember.MKind = :theKind4b) or (c.assocMember.MKind = :theKind4c)) AND c.assocMember.membershipValid = true ORDER BY c.lastName");
               query.setParameter("theKind1a", FKind.spouse);
               query.setParameter("theKind1b", FKind.main);
               query.setParameter("theKind1c", FKind.contact);
               query.setParameter("theKind2", FKind.kid);
               query.setParameter("theKind3", AssocMember.MKind.normal);
               query.setParameter("theKind4a", AssocMember.MKind.committee);
               query.setParameter("theKind4b", AssocMember.MKind.corporate);
               query.setParameter("theKind4c", AssocMember.MKind.youngpro);

            }

         }
         else
         {

            if (club.getOpenedToGuests())
            {

               query =
                  entityManager.createQuery(
                     "select c from FamilyMember c where ((c.FKind = :theKind1a) or (c.FKind = :theKind1b) or (c.FKind = :theKind1c)) AND ((c.assocMember.MKind = :theKind3) or ((c.assocMember.MKind = :theKind4a) or (c.assocMember.MKind = :theKind4b) or (c.assocMember.MKind = :theKind4c)) or (c.assocMember.MKind = :theKind5)) AND c.assocMember.membershipValid = true ORDER BY c.lastName");
               query.setParameter("theKind1a", FKind.spouse);
               query.setParameter("theKind1b", FKind.main);
               query.setParameter("theKind1c", FKind.contact);
               query.setParameter("theKind3", AssocMember.MKind.normal);
               query.setParameter("theKind4a", AssocMember.MKind.committee);
               query.setParameter("theKind4b", AssocMember.MKind.corporate);
               query.setParameter("theKind4c", AssocMember.MKind.youngpro);
               query.setParameter("theKind5", AssocMember.MKind.guest);

            }
            else
            {

               query =
                  entityManager.createQuery(
                     "select c from FamilyMember c where ((c.FKind = :theKind1a) or (c.FKind = :theKind1b) or (c.FKind = :theKind1c)) AND ((c.assocMember.MKind = :theKind3) or ((c.assocMember.MKind = :theKind4a) or (c.assocMember.MKind = :theKind4b) or (c.assocMember.MKind = :theKind4c))) AND c.assocMember.membershipValid = true ORDER BY c.lastName");
               query.setParameter("theKind1a", FKind.spouse);
               query.setParameter("theKind1b", FKind.main);
               query.setParameter("theKind1c", FKind.contact);
               query.setParameter("theKind3", AssocMember.MKind.normal);
               query.setParameter("theKind4a", AssocMember.MKind.committee);
               query.setParameter("theKind4b", AssocMember.MKind.corporate);
               query.setParameter("theKind4c", AssocMember.MKind.youngpro);

            }

         }

         ArrayList<FamilyMember> availableSelection =
            (ArrayList<FamilyMember>)query.getResultList();

         for (FamilyMember a : availableSelection)
         {

            SelectItem s =
               new SelectItem(a, a.getLastName() + " " + a.getFirstName());
            m_familyMemberPickList.add(s);

         }

      }

      return m_familyMemberPickList;

   }

   public void setFamilyMemberPickList(List<SelectItem> memberships)
   {

      m_familyMemberPickList = memberships;

   }

   @SuppressWarnings("unchecked")
   public FamilyMemberPickListConverter getConverter()
   {

      if (m_converter == null)
      {

         Query query =
            entityManager.createQuery(
               "SELECT c FROM FamilyMember c ORDER BY c.lastName");
         ArrayList<FamilyMember> availableSelection =
            (ArrayList<FamilyMember>)query.getResultList();
         m_converter = new FamilyMemberPickListConverter(availableSelection);

      }

      return m_converter;

   }

   public void setConverter(FamilyMemberPickListConverter cv)
   {

      m_converter = cv;

   }

   @SuppressWarnings("unchecked")
   @Factory(value = "membershipPickList", scope = ScopeType.PAGE)
   public List<FamilyMember> getMembershipPickList()
   {

      if (m_membershipPickList == null)
      {

         m_membershipPickList = new ArrayList<FamilyMember>();
         m_oldMembershipPickList = new ArrayList<FamilyMember>();

         Club club = (Club)clubHome.getInstance();
         Query query =
            entityManager.createQuery(
               "SELECT c FROM ClubMembership c WHERE c.club = :theClub ORDER BY c.member.lastName");
         query.setParameter("theClub", club);

         ArrayList<ClubMembership> preselected =
            (ArrayList<ClubMembership>)query.getResultList();

         for (ClubMembership pre : preselected)
         {

            m_membershipPickList.add(pre.getMember());
            m_oldMembershipPickList.add(pre.getMember());

         }

      }

      return m_membershipPickList;

   }

   public void setMembershipPickList(List<FamilyMember> memberships)
   {

      m_membershipPickList = memberships;

   }

   public void updateSelection()
   {

      log.info("Updating selection");

      if (m_membershipPickList.size() == 0)
      {

         // Let's prevent the clubContact to be deleted
         Club club = (Club)clubHome.getInstance();
         Query query =
            entityManager.createQuery(
               "DELETE FROM ClubMembership c WHERE c.club = :theClub and c.FKind = :theKind");
         query.setParameter("theClub", club);
         query.setParameter("theKind", ClubMembership.FKind.member);
         query.executeUpdate();
         m_oldMembershipPickList.clear();

         // Let's reinit all the list
         m_membershipPickList = null;
         m_oldMembershipPickList = null;
         getMembershipPickList();

      }
      else
      {

         // Let's prevent the clubContact to be deleted
         Club club = (Club)clubHome.getInstance();
         Query query =
            entityManager.createQuery(
               "DELETE FROM ClubMembership c WHERE c.club = :theClub and c.member = :theMember and c.FKind = :theKind");
         query.setParameter("theClub", club);
         query.setParameter("theKind", ClubMembership.FKind.member);

         for (FamilyMember oldfm : m_oldMembershipPickList)
         {

            if (!m_membershipPickList.contains(oldfm))
            {

               query.setParameter("theMember", oldfm);
               query.executeUpdate();

            }

         }

         Query check =
            entityManager.createQuery(
               "SELECT DISTINCT COUNT(c.id) FROM ClubMembership c WHERE c.member = :currentMember and c.club = :currentClub");
         check.setParameter("currentClub", club);

         for (FamilyMember newfm : m_membershipPickList)
         {

            if (!m_oldMembershipPickList.contains(newfm))
            {

               check.setParameter("currentMember", newfm);

               long count = (Long)check.getSingleResult();

               if (count == 0)
               {

                  ClubMembership fmember = new ClubMembership();

                  if (club != null)
                  {

                     fmember.setClub(club);
                     club.getClubMembers().add(fmember);

                  }

                  if (newfm != null)
                  {

                     fmember.setMember(newfm);
                     // Lazy instantion issue
                     // newfm.getMemberships().add(fmember);

                  }

                  fmember.setFKind(ClubMembership.FKind.member);
                  fmember.setActive(Boolean.TRUE);
                  entityManager.persist(fmember);

               }
               else
               {

                  log.error(newfm.getFirstName() + newfm.getLastName()
                     + " is already member of club " + club.getName());

               }

            }

         }

         m_oldMembershipPickList.clear();
         m_oldMembershipPickList.addAll(m_membershipPickList);

      }

   }

}
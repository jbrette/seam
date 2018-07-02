package org.jboss.seam.example.webassoc.clubs;

import static org.jboss.seam.ScopeType.CONVERSATION;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.ClubMembership;
import org.jboss.seam.example.webassoc.security.AccountMember;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionManager;
import org.jboss.seam.security.permission.action.PermissionSearch;

import java.io.Serializable;

import java.security.Principal;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Name("clubPermissionMgr")
@Scope(CONVERSATION)
public class ClubPermissionMgr
        implements Serializable
{

   private static final long serialVersionUID = -4943654157860780587L;
   private List<String> m_selectedRoles;
   private List<AssocMember> m_selectedClubMembers;
   private List<String> m_selectedActions;
   private List<String> m_originalActions;
   private List<AssocMember> m_availableClubMembers;
   private Club m_target;
   private Principal m_recipient;
   @In
   IdentityManager identityManager;
   @In
   PermissionManager permissionManager;
   @In
   EntityManager entityManager;
   @In
   PermissionSearch permissionSearch;
   @Logger
   private Log log;

   @SuppressWarnings("unchecked")
   @Begin(nested = true)
   public void createPermission()
   {

      m_target = (Club)permissionSearch.getTarget();
      m_selectedClubMembers = new ArrayList<AssocMember>();

      boolean listAllMembers = true;

      if (listAllMembers)
      {

         Query query =
            entityManager.createQuery("SELECT c FROM AssocMember c "
               + "WHERE c.membershipValid = true "
               + "AND ((c.MKind = :theKind1) or ((c.MKind = :theKind2a) or (c.MKind = :theKind2b) or (c.MKind = :theKind2c))) "
               + "ORDER BY c.assocName");
         query.setParameter("theKind1", AssocMember.MKind.normal);
         query.setParameter("theKind2a", AssocMember.MKind.committee);
         query.setParameter("theKind2b", AssocMember.MKind.corporate);
         query.setParameter("theKind2c", AssocMember.MKind.youngpro);
         m_availableClubMembers = query.getResultList();

      }
      else
      {

         Query query =
            entityManager.createQuery(
               "select a from ClubMembership a where a.club = :theClub");
         query.setParameter("theClub", m_target);

         List<ClubMembership> res = query.getResultList();
         m_availableClubMembers = new ArrayList<AssocMember>();

         for (ClubMembership mship : res)
         {

            m_availableClubMembers.add(mship.getMember().getAssocMember());

         }

      }

   }

   @Begin(nested = true)
   public void editPermission()
   {

      m_target = (Club)permissionSearch.getTarget();
      m_recipient = permissionSearch.getSelectedRecipient();

      List<Permission> permissions =
         permissionManager.listPermissions(m_target);
      m_selectedActions = new ArrayList<String>();

      for (Permission permission : permissions)
      {

         if (permission.getRecipient().equals(m_recipient))
         {

            if (!m_selectedActions.contains(permission.getAction()))
            {

               m_selectedActions.add(permission.getAction());

            }

         }

      }

      m_originalActions = new ArrayList<String>(m_selectedActions);

   }

   public List<String> getSelectedRoles()
   {

      return m_selectedRoles;

   }

   public void setSelectedRoles(List<String> selectedRoles)
   {

      this.m_selectedRoles = selectedRoles;

   }

   public List<AssocMember> getSelectedClubMembers()
   {

      return m_selectedClubMembers;

   }

   public void setSelectedClubMembers(List<AssocMember> selectedClubMembers)
   {

      this.m_selectedClubMembers = selectedClubMembers;

   }

   public List<String> getSelectedActions()
   {

      return m_selectedActions;

   }

   public void setSelectedActions(List<String> selectedActions)
   {

      this.m_selectedActions = selectedActions;

   }

   @SuppressWarnings("unchecked")
   public String applyPermissions()
   {

      // If the recipient isn't null, it means we're editing existing permissions
      if (m_recipient != null)
      {

         List<Permission> grantedPermissions = new ArrayList<Permission>();
         List<Permission> revokedPermissions = new ArrayList<Permission>();

         for (String action : m_selectedActions)
         {

            if (!m_originalActions.contains(action))
            {

               grantedPermissions.add(new Permission(m_target, action,
                     m_recipient));

            }

         }

         for (String action : m_originalActions)
         {

            if (!m_selectedActions.contains(action))
            {

               revokedPermissions.add(new Permission(m_target, action,
                     m_recipient));

            }

         }

         if (!grantedPermissions.isEmpty())
         {

            permissionManager.grantPermissions(grantedPermissions);

         }

         if (!revokedPermissions.isEmpty())
         {

            permissionManager.revokePermissions(revokedPermissions);

         }

      }

      // otherwise this is a set of new permissions
      else
      {

         if (m_selectedActions.size() == 0)
         {

            FacesMessages.instance().add(
               "You must select at least one action");

            return "failure";

         }

         List<Permission> permissions = new ArrayList<Permission>();

         if (m_selectedRoles != null)
         {

            for (String role : m_selectedRoles)
            {

               Principal r = new Role(role);

               for (String action : m_selectedActions)
               {

                  permissions.add(new Permission(m_target, action, r));

               }

            }

         }

         if (m_selectedClubMembers != null)
         {

            for (AssocMember clubMember : m_selectedClubMembers)
            {

               Query query =
                  entityManager.createQuery(
                     "select a from AccountMember a where a.member = :theMember");
               query.setParameter("theMember", clubMember);

               List<AccountMember> res =
                  (List<AccountMember>)query.getResultList();

               if (res.size() == 0)
               {

                  log.info("Nobody for : " + clubMember.getAssocName());

               }
               else if (res.size() == 1)
               {

                  AccountMember acct = res.get(0);
                  Principal p = new SimplePrincipal(acct.getUserName());

                  for (String action : m_selectedActions)
                  {

                     permissions.add(new Permission(m_target, action, p));

                  }

               }
               else
               {

                  log.info("TooMany for : " + clubMember.getAssocName());

               }

            }

         }

         permissionManager.grantPermissions(permissions);

      }

      Conversation.instance().endBeforeRedirect();

      return "success";

   }

   @SuppressWarnings("unchecked")
   @Begin(nested = true)
   public String setupClubMembersRights()
   {

      Club target = (Club)permissionSearch.getTarget();

      // Cleanup the existing permissions
      permissionManager.revokePermissions(permissionManager.listPermissions(
            target));

      // Retrieve all the available actions
      List<String> clubAdmActions =
         permissionManager.listAvailableActions(target);
      List<String> simpleMemberActions = new ArrayList<String>();

      for (String action : clubAdmActions)
      {

         if (action.indexOf("_mgt") == -1)
         {

            simpleMemberActions.add(action);

         }

      }

      // Let's select the clubContact
      Query mshipQuery =
         entityManager.createQuery(
            "select a from ClubMembership a where a.club = :theClub");
      mshipQuery.setParameter("theClub", target);

      List<ClubMembership> mshipQueryRes = mshipQuery.getResultList();
      List<AssocMember> allClubMembers = new ArrayList<AssocMember>();
      List<AssocMember> allClubContacts = new ArrayList<AssocMember>();

      for (ClubMembership mship : mshipQueryRes)
      {

         allClubMembers.add(mship.getMember().getAssocMember());

         if (mship.getFKind() == ClubMembership.FKind.clubcontact)
         {

            allClubContacts.add(mship.getMember().getAssocMember());

         }

      }

      List<Permission> permissions = new ArrayList<Permission>();

      for (AssocMember clubMember : allClubContacts)
      {

         Query acctQuery =
            entityManager.createQuery(
               "select a from AccountMember a where a.member = :theMember");
         acctQuery.setParameter("theMember", clubMember);

         List<AccountMember> acctQueryRes =
            (List<AccountMember>)acctQuery.getResultList();

         if (acctQueryRes.size() == 1)
         {

            AccountMember acct = acctQueryRes.get(0);
            Principal p = new SimplePrincipal(acct.getUserName());

            for (String action : clubAdmActions)
            {

               permissions.add(new Permission(target, action, p));

            }

         }

      }

      Role memberRole = new Role("member");

      for (String action : simpleMemberActions)
      {

         permissions.add(new Permission(target, action, memberRole));

      }

      Role clubAdminRole = new Role("clubadm");

      for (String action : clubAdmActions)
      {

         permissions.add(new Permission(target, action, clubAdminRole));

      }

      permissionManager.grantPermissions(permissions);
      Conversation.instance().endBeforeRedirect();

      return "success";

   }

   public List<AssocMember> getAvailableClubMembers()
   {

      return m_availableClubMembers;

   }

   public Club getTarget()
   {

      return m_target;

   }

   public Principal getRecipient()
   {

      return m_recipient;

   }

}
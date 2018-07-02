package org.jboss.seam.example.webassoc.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.management.IdentityManager;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Name("accountMemberSearch")
@Scope(ScopeType.SESSION)
public class AccountMemberSearch
        implements Serializable
{

   private static final long serialVersionUID = 8650501473021497413L;
   @Logger
   private Log log;
   @DataModel
   List<AccountMember> accountMembers;
   @DataModelSelection
   AccountMember selectedAccount;
   @In
   private IdentityManager identityManager;
   @In
   private EntityManager entityManager;

   @Factory("accountMembers")
   public void loadAccountMembers()
   {

      List<String> userNames = identityManager.listUsers();
      log.info("How many users: " + userNames.size());

      Query q =
         entityManager.createQuery(
            "select u from AccountMember u where u.userName = :userName");
      accountMembers = new ArrayList<AccountMember>();

      for (String userName : userNames)
      {

         q.setParameter("userName", userName);
         accountMembers.add((AccountMember)q.getSingleResult());

      }

   }

   public String getUserRoles(String username)
   {

      List<String> roles = identityManager.getGrantedRoles(username);

      if (roles == null)
      {

         return "";

      }

      StringBuilder sb = new StringBuilder();

      for (String role : roles)
      {

         sb.append(((sb.length() > 0) ? ", " : "") + role);

      }

      return sb.toString();

   }

   public AccountMember getSelectedAccount()
   {

      return selectedAccount;

   }

}
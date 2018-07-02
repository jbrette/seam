package org.jboss.seam.example.webassoc.security;

import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.security.management.PasswordSalt;
import org.jboss.seam.annotations.security.management.UserEnabled;
import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;
import org.jboss.seam.example.webassoc.mship.AssocMember;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
   name = "AccountMember",
   uniqueConstraints = @UniqueConstraint(columnNames = "userName")
)
public class AccountMember
        implements Serializable
{

   private static final long serialVersionUID = 6368734442192368866L;

   // =================================
   // Members
   // =================================
   private Integer m_accountId;
   private String m_userName;
   private String m_email;
   private String m_passwordHash;
   private String m_passwordSalt;
   private boolean m_enabled;
   private Set<AccountMemberRole> m_roles = new HashSet<AccountMemberRole>();
   private AssocMember m_member;
   private String m_activationKey = "";
   private String m_language = "en";
   private Date m_createdOn = Calendar.getInstance().getTime();
   private boolean m_temporaryPassword = true;
   private Date m_lastLogin;
   private int m_loginCount = 0;

   // =================================
   // Getter/Setter
   // =================================
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   public Integer getAccountId()
   {

      return m_accountId;

   }

   public void setAccountId(Integer accountId)
   {

      m_accountId = accountId;

   }

   @NotNull @UserPrincipal
   @Column(name = "userName", unique = true, nullable = false, length = 16)
   @Length(min = 4, max = 16)
   // @Pattern(regex = "^[a-zA-Z\\d_]{4,12}$", message = "{invalid_screen_name}")
   public String getUserName()
   {

      return m_userName;

   }

   public void setUserName(String username)
   {

      m_userName = username;

   }

   @Length(max = 100)
   @Email
   public String getEmail()
   {

      return m_email;

   }

   public void setEmail(String email)
   {

      m_email = email;

   }

   @Column(name = "password", nullable = false, length = 128)
   @NotNull
   @Length(max = 128)
   @UserPassword(hash = "SHA")
   public String getPasswordHash()
   {

      return m_passwordHash;

   }

   public void setPasswordHash(String passwordHash)
   {

      m_passwordHash = passwordHash;

   }

   @PasswordSalt
   public String getPasswordSalt()
   {

      return m_passwordSalt;

   }

   public void setPasswordSalt(String passwordSalt)
   {

      m_passwordSalt = passwordSalt;

   }

   @UserEnabled
   public boolean isEnabled()
   {

      return m_enabled;

   }

   public void setEnabled(boolean enabled)
   {

      m_enabled = enabled;

   }

   @Length(max = 60)
   public String getActivationKey()
   {

      return m_activationKey;

   }

   public void setActivationKey(String activationKey)
   {

      m_activationKey = activationKey;

   }

   @NotNull
   @Length(max = 3)
   public String getLanguage()
   {

      return m_language;

   }

   public void setLanguage(String language)
   {

      m_language = language;

   }

   @NotNull
   public Date getCreatedOn()
   {

      return m_createdOn;

   }

   public void setCreatedOn(Date createdOn)
   {

      m_createdOn = createdOn;

   }

   @NotNull
   public boolean isTemporaryPassword()
   {

      return m_temporaryPassword;

   }

   public void setTemporaryPassword(boolean temporaryPassword)
   {

      m_temporaryPassword = temporaryPassword;

   }

   // @NotNull
   public Date getLastLogin()
   {

      return m_lastLogin;

   }

   public void setLastLogin(Date lastLogin)
   {

      m_lastLogin = lastLogin;

   }

   public void updateLastLogin()
   {

      m_lastLogin = Calendar.getInstance().getTime();
      ;

   }

   // @NotNull
   public int getLoginCount()
   {

      return m_loginCount;

   }

   public void setLoginCount(int loginCount)
   {

      m_loginCount = loginCount;

   }

   public void incLoginCount()
   {

      m_loginCount++;

   }

   // =================================
   // Relationship
   // =================================
   @UserRoles
   @ManyToMany(targetEntity = AccountMemberRole.class)
   @JoinTable(
      name = "AccountMembership", joinColumns = @JoinColumn(name = "accountId"),
      inverseJoinColumns = @JoinColumn(name = "memberOf")
   )
   public Set<AccountMemberRole> getRoles()
   {

      return m_roles;

   }

   public void setRoles(Set<AccountMemberRole> roles)
   {

      m_roles = roles;

   }

   public boolean isUserInRole(String roleName)
   {

      Set<AccountMemberRole> myRoles = getRoles();

      for (AccountMemberRole next : myRoles)
      {

         if (next.getName().equals(roleName))
         {

            return true;

         }

      }

      return false;

   }

   public void removeRole(AccountMemberRole role)
   {

      m_roles.remove(role);

   }

   public void addRole(AccountMemberRole role)
   {

      m_roles.add(role);

   }

   @OneToOne
   public AssocMember getMember()
   {

      return m_member;

   }

   public void setMember(AssocMember member)
   {

      m_member = member;

   }

}
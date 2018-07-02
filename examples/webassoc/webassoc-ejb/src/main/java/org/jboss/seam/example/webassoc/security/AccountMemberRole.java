package org.jboss.seam.example.webassoc.security;

import org.jboss.seam.annotations.security.management.RoleConditional;
import org.jboss.seam.annotations.security.management.RoleGroups;
import org.jboss.seam.annotations.security.management.RoleName;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "AccountMemberRole")
public class AccountMemberRole
        implements Serializable
{

   private static final long serialVersionUID = 9177366120789064801L;

   // =================================
   // Members
   // =================================
   private Integer m_roleId;
   private String m_name;
   private String m_description;
   private boolean m_conditional;
   private Set<AccountMemberRole> m_groups = new HashSet<AccountMemberRole>();

   // =================================
   // Getter/Setter
   // =================================
   @Id @GeneratedValue
   public Integer getRoleId()
   {

      return m_roleId;

   }

   public void setRoleId(Integer roleId)
   {

      this.m_roleId = roleId;

   }

   @RoleName
   public String getName()
   {

      return m_name;

   }

   public void setName(String name)
   {

      this.m_name = name;

   }

   public String getDescription()
   {

      return m_description;

   }

   public void setDescription(String description)
   {

      this.m_description = description;

   }

   @RoleGroups
   @ManyToMany(targetEntity = AccountMemberRole.class)
   @JoinTable(
      name = "AccountMemberRoleGroup",
      joinColumns = @JoinColumn(name = "roleId"),
      inverseJoinColumns = @JoinColumn(name = "memberOf")
   )
   public Set<AccountMemberRole> getGroups()
   {

      return m_groups;

   }

   public void setGroups(Set<AccountMemberRole> groups)
   {

      this.m_groups = groups;

   }

   @RoleConditional
   public boolean isConditional()
   {

      return m_conditional;

   }

   public void setConditional(boolean conditional)
   {

      this.m_conditional = conditional;

   }

}
package org.jboss.seam.example.webassoc.security;

import org.jboss.seam.annotations.security.permission.PermissionAction;
import org.jboss.seam.annotations.security.permission.PermissionDiscriminator;
import org.jboss.seam.annotations.security.permission.PermissionRole;
import org.jboss.seam.annotations.security.permission.PermissionTarget;
import org.jboss.seam.annotations.security.permission.PermissionUser;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountPermission")
public class AccountPermission
        implements Serializable
{

   private static final long serialVersionUID = -5628863031792429938L;

   // =================================
   // Members
   // =================================
   private Integer m_permissionId;
   private String m_recipient;
   private String m_target;
   private String m_action;
   private String m_discriminator;

   // =================================
   // Getter/Setter
   // =================================
   @Id @GeneratedValue
   public Integer getPermissionId()
   {

      return m_permissionId;

   }

   public void setPermissionId(Integer permissionId)
   {

      this.m_permissionId = permissionId;

   }

   @PermissionUser @PermissionRole
   public String getRecipient()
   {

      return m_recipient;

   }

   public void setRecipient(String recipient)
   {

      this.m_recipient = recipient;

   }

   @PermissionTarget
   public String getTarget()
   {

      return m_target;

   }

   public void setTarget(String target)
   {

      this.m_target = target;

   }

   @PermissionAction
   public String getAction()
   {

      return m_action;

   }

   public void setAction(String action)
   {

      this.m_action = action;

   }

   @PermissionDiscriminator
   public String getDiscriminator()
   {

      return m_discriminator;

   }

   public void setDiscriminator(String discriminator)
   {

      this.m_discriminator = discriminator;

   }

}
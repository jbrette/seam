package org.jboss.seam.example.webassoc.security;

import org.hibernate.validator.Email;

public class InactiveNewUser
{

   private String displayName;
   private String userName;
   private String email;
   private String activationLink;

   public InactiveNewUser()
   {

   }

   public InactiveNewUser(String displayName, String userName, String email,
      String activationLink)
   {

      this.displayName = displayName;
      this.userName = userName;
      this.email = email;
      this.activationLink = activationLink;

   }

   public String getDisplayName()
   {

      return displayName;

   }

   public void setDisplayName(String displayName)
   {

      this.displayName = displayName;

   }

   public String getUserName()
   {

      return userName;

   }

   public void setUserName(String userName)
   {

      this.userName = userName;

   }

   @Email
   public String getEmail()
   {

      return email;

   }

   public void setEmail(String email)
   {

      this.email = email;

   }

   public String getActivationLink()
   {

      return activationLink;

   }

   public void setActivationLink(String activationLink)
   {

      this.activationLink = activationLink;

   }

}
package org.jboss.seam.example.webassoc.mail;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.Renderer;

@Name("registrationMailer")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class RegistrationMailer
{

   @In(create = true)
   private Renderer renderer;

   @Observer("userRegistered")
   public void sendActivationEmail()
   {

      renderer.render("/mailing/activation.xhtml");

   }

   @Observer("passwordReset")
   public void sendPasswordResetEmail()
   {

      renderer.render("/mailing/password_reset.xhtml");

   }

}
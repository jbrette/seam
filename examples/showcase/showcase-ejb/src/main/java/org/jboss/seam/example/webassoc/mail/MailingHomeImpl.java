package org.jboss.seam.example.webassoc.mail;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.framework.EntityHome;

import javax.ejb.Remove;
import javax.ejb.Stateful;

@Name("mailingHome")
@Stateful
public class MailingHomeImpl
        extends EntityHome<Mailing>
        implements MailingHome
{

   private static final long serialVersionUID = 480673514723181689L;
   @In(required = false)
   private MailingController mailingControler;
   @In(required = false, value = "newsLetter")
   private MailingController newsLetter;
   @In(required = false)
   private AssocMember assocMember;

   @Factory("mailing")
   public Mailing initMailing()
   {

      return getInstance();

   }

   protected Mailing createInstance()
   {

      Mailing mailing = new Mailing();

      if (mailingControler != null)
      {

         mailing.setMailingController(mailingControler);
         mailingControler.getMailings().add(mailing);

      }
      else if (newsLetter != null)
      {

         mailing.setMailingController(newsLetter);
         newsLetter.getMailings().add(mailing);

      }

      if (assocMember != null)
      {

         mailing.setMember(assocMember);
         assocMember.getMailings().add(mailing);

      }

      mailing.setState(Mailing.MState.notSent);

      return mailing;

   }

   public String subscribe()
   {

      getInstance();

      String res = persist();

      return res;

   }

   public String mail()
   {

      getInstance();

      return "";

   }

   public String unsubscribe()
   {

      getInstance();

      String res = remove();

      return res;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
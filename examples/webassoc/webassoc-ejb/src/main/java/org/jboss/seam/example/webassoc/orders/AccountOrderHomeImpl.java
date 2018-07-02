package org.jboss.seam.example.webassoc.orders;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mail.MailProcessor;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.framework.EntityHome;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;
import javax.persistence.Transient;

@Name("accountOrderHome")
@Stateful
public class AccountOrderHomeImpl
        extends EntityHome<AccountOrder>
        implements AccountOrderHome
{

   private static final long serialVersionUID = 528700983554976601L;
   @In(create = true)
   private MailProcessor mailProcessor;

   @Factory("accountOrder")
   public AccountOrder initAccountOrder()
   {

      return getInstance();

   }

   protected AccountOrder createInstance()
   {

      AccountOrder amember = new AccountOrder();
      amember.setBuyerName("MyAssocMemberName");
      amember.setAddress(Cts.Dflt.ADDRESS);
      amember.setAptOrSuite(Cts.Dflt.APTORNB);
      amember.setCity(Cts.Dflt.CITY);
      amember.setCountry(Cts.Dflt.COUNTRY);
      amember.setState(Cts.Dflt.STATE);
      amember.setZip(Cts.Dflt.ZIP);
      amember.setHomePhone(Cts.Dflt.PHONE);
      amember.setEmail(Cts.Dflt.E_MAIL);
      amember.setPaymentInfo("");
      amember.setPrice("");
      amember.setFirstName("");
      amember.setSpouseFirstName("");
      amember.setChildFirstName1("");
      amember.setChildFirstName2("");
      amember.setChildFirstName3("");
      amember.setChildFirstName4("");
      amember.setChildFirstName5("");

      return amember;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<AccountOrder> getAllOrders()
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM AccountOrder c");

      return query.getResultList();

   }

   /**
    * Create the object and sends the event.
    */
   public String persist()
   {

      String res = super.persist();

      if ("persisted".equals(res))
      {

         mailProcessor.sendNewAccountOrderAlert(getInstance().getId());

      }

      return res;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
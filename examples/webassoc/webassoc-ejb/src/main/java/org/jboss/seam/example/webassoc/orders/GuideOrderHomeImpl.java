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

@Name("guideOrderHome")
@Stateful
public class GuideOrderHomeImpl
        extends EntityHome<GuideOrder>
        implements GuideOrderHome
{

   private static final long serialVersionUID = 4244726526039802902L;
   @In(create = true)
   private MailProcessor mailProcessor;

   @Factory("guideOrder")
   public GuideOrder initGuideOrder()
   {

      return getInstance();

   }

   protected GuideOrder createInstance()
   {

      GuideOrder amember = new GuideOrder();
      amember.setBuyerName("thebuyername");
      amember.setAddress(Cts.Dflt.ADDRESS);
      amember.setAptOrSuite(Cts.Dflt.APTORNB);
      amember.setCity(Cts.Dflt.CITY);
      amember.setCountry(Cts.Dflt.COUNTRY);
      amember.setState(Cts.Dflt.STATE);
      amember.setZip(Cts.Dflt.ZIP);
      amember.setHomePhone(Cts.Dflt.PHONE);
      amember.setEmail(Cts.Dflt.E_MAIL);
      amember.setPaymentInfo("");
      amember.setPrice("$20 + frais d'envoi");

      return amember;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<GuideOrder> getAllOrders()
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM GuideOrder c");

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

         mailProcessor.sendNewGuideOrderAlert(getInstance().getId());

      }

      return res;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
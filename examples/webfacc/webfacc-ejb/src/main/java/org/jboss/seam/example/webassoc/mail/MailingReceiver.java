package org.jboss.seam.example.webassoc.mail;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.log.Log;

import java.io.Serializable;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@MessageDriven(
   activationConfig =
      {
         @ActivationConfigProperty(
            propertyName = "destinationType", propertyValue = "javax.jms.Queue"
         ),
         @ActivationConfigProperty(
            propertyName = "destination", propertyValue = Cts.MAILING_QUEUE
         ),
         @ActivationConfigProperty(
            propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"
         ),
         @ActivationConfigProperty(
            propertyName = "maxSession", propertyValue = "1"
         )
      }
)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Name("mailingReceiver")
public class MailingReceiver
        implements MessageListener
{

   @Logger
   private Log log;
   @In(create = true)
   private MailProcessor mailProcessor;

   public void onMessage(Message message)
   {

      try
      {

         Object obj = ((ObjectMessage)message).getObject();

         if (obj instanceof Serializable)
         {

            Serializable inMsg = (Serializable)obj;
            mailProcessor.fromMDB(inMsg);

         }

      }
      catch (JMSException ex)
      {

         log.error("Message payload did not contain a Payment object", ex);

      }

   }

}
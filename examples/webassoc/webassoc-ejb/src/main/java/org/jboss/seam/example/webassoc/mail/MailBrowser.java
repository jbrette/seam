package org.jboss.seam.example.webassoc.mail;

import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;

import javax.ejb.Local;

@Local
public interface MailBrowser
{

   //================================================================================
   // Alerts
   //================================================================================
   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void deleteMail(Long newObjectId);

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void replayMail(Long newObjectId);

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void forwardMail(Long newObjectId);

   @Asynchronous
   @Transactional(TransactionPropagationType.NEVER)
   public void sendMail(String to, String cc, String msgSubject,
      String msgBody);

}
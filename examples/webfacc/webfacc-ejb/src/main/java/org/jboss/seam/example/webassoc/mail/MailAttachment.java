package org.jboss.seam.example.webassoc.mail;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.Attachment;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Name("mailAttachment")
public class MailAttachment
        extends Attachment
{

   private static final long serialVersionUID = -9192742862121284029L;
   private MailingController m_mailingControler;

   @NotNull @ManyToOne
   public MailingController getMailingController()
   {

      return m_mailingControler;

   }

   public void setMailingController(MailingController mailingControler)
   {

      m_mailingControler = mailingControler;

   }

}
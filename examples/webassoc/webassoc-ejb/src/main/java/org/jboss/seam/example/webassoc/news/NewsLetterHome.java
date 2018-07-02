package org.jboss.seam.example.webassoc.news;

import javax.ejb.Local;

@Local
public interface NewsLetterHome
{

   // Methods from NewsLetterHomeImpl
   public NewsLetter initNewsLetter();

   public void ejbRemove();

   // Methods from EntityHome and Home
   public Object getId();

   public void setId(Object id);

   public String persist();

   public String update();

   public String remove();

   public boolean isManaged();

   public void create();

   public Object getInstance();

   // Additional business method
   public String buildMailingList();

   public String send2MailingList();

   public String sendTestEmail();

   public String emptyMailingList();

   public String markAsSent();

   // Manual modification of the mailing list
   public String toggleToSent();

   public String toggleToUnsent();

   public String deleteMailing();

   // Images handling
   public void setUploadedData(byte[] uploadedData);

   public void setUploadedContentType(String contentType);

   public void setUploadedFileName(String fileName);

   public String uploadAttachment();

   public String deleteAttachment();

}
package org.jboss.seam.example.webassoc.clubs;

import javax.ejb.Local;

@Local
public interface ClubArticleHome
{

   // Methods from ClubArticleHomeImpl
   public ClubArticle initClubArticle();

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

   // ================================
   // Business operations
   // ================================
   // Images handling
   public void setUploadedData(byte[] attachmentData);

   public void setUploadedContentType(String contentType);

   public void setUploadedFileName(String fileName);

   public String uploadAttachment();

   public String deleteAttachment();

}
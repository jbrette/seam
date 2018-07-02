package org.jboss.seam.example.webassoc.article;

import javax.ejb.Local;

@Local
public interface ArticleHome
{

   // Methods from ArticleHomeImpl
   public Article initArticle();

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

   // Images handling
   public void setUploadedData(byte[] uploadedData);

   public void setUploadedContentType(String contentType);

   public void setUploadedFileName(String fileName);

   public String uploadAttachment();

   public String deleteAttachment();

   public String uploadIllustration();

   public String deleteIllustration();

}
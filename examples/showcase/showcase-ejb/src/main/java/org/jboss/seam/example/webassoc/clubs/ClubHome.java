package org.jboss.seam.example.webassoc.clubs;

import javax.ejb.Local;

@Local
public interface ClubHome
{

   // Methods from ClubHomeImpl
   public Club initClub();

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

   // Image controls
   public void setUploadedData(byte[] uploadedData);

   public void setUploadedContentType(String contentType);

   public void setUploadedFileName(String fileName);

   public String uploadImage();

}
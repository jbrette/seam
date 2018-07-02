package org.jboss.seam.example.webassoc.yellowpages;

import java.util.List;

import javax.ejb.Local;

@Local
public interface YellowPageHome
{

   // Methods from SponsorHomeImpl
   public YellowPage initYellowPage();

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

   // List of all valid members
   public List<YellowPage> getDirectory();

   // Images handling
   public void setUploadedData(byte[] uploadedData);

   public void setUploadedContentType(String contentType);

   public void setUploadedFileName(String fileName);

   public String uploadImage();

}
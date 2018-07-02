package org.jboss.seam.example.webassoc.sponsors;

import java.util.List;

import javax.ejb.Local;

@Local
public interface SponsorHome
{

   // Methods from SponsorHomeImpl
   public Sponsor initSponsor();

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

   // Used to build sponsor pages.
   public List<Sponsor> getStandardSponsors();

   public List<Sponsor> getPremiumSponsors();

   public List<Sponsor> getBronzeSponsors();

   public List<Sponsor> getSilverSponsors();

   public List<Sponsor> getGoldSponsors();

   public List<Sponsor> getPlatinumSponsors();

   public List<Sponsor> getFPSponsors();

   // Images handling
   public void setUploadedData(byte[] uploadedData);

   public void setUploadedContentType(String contentType);

   public void setUploadedFileName(String fileName);

   public String uploadImage();

}
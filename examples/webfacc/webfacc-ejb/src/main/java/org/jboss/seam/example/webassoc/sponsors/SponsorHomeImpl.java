package org.jboss.seam.example.webassoc.sponsors;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.sponsors.Sponsor.FPState;
import org.jboss.seam.example.webassoc.sponsors.Sponsor.SKind;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;
import javax.persistence.Transient;

@Name("sponsorHome")
@Stateful
public class SponsorHomeImpl
        extends EntityHome<Sponsor>
        implements SponsorHome
{

   private static final long serialVersionUID = -8577539963135391851L;
   private byte[] m_imageData;
   private String m_uploadedContentType;
   private String m_uploadedFileName;

   @Factory("sponsor")
   public Sponsor initSponsor()
   {

      return getInstance();

   }

   protected Sponsor createInstance()
   {

      Sponsor res = new Sponsor();
      res.setSKind(SKind.standard);
      res.setFPState(FPState.invisible);
      res.setAddress("1500 Marilla St");
      res.setCity("Dallas");
      res.setCountry("USA");
      res.setState("TX");
      res.setZip("75201");
      res.setPhone("000-000-0000");

      return res;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<Sponsor> getStandardSponsors()
   {

      Query query = buildQuery(Sponsor.SKind.standard);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<Sponsor> getPremiumSponsors()
   {

      Query query = buildQuery(Sponsor.SKind.premium);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<Sponsor> getBronzeSponsors()
   {

      Query query = buildQuery(Sponsor.SKind.bronze);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<Sponsor> getSilverSponsors()
   {

      Query query = buildQuery(Sponsor.SKind.silver);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<Sponsor> getGoldSponsors()
   {

      Query query = buildQuery(Sponsor.SKind.gold);

      return query.getResultList();

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<Sponsor> getPlatinumSponsors()
   {

      Query query = buildQuery(Sponsor.SKind.platinum);

      return query.getResultList();

   }

   // Query Builder
   public Query buildQuery(Sponsor.SKind reqKind)
   {

      Query query = null;
      query =
         getEntityManager().createQuery("SELECT s FROM Sponsor s "
            + "WHERE s.SKind = :sponsorKind and s.sponsorshipValid = true");
      query.setParameter("sponsorKind", reqKind);

      return query;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<Sponsor> getFPSponsors()
   {

      Query query = null;
      query =
         getEntityManager().createQuery("SELECT s FROM Sponsor s "
            + "WHERE ((s.FPState = :fpState1) or (s.FPState = :fpState2)) and s.sponsorshipValid = true");
      query.setParameter("fpState1", Sponsor.FPState.visible);
      query.setParameter("fpState2", Sponsor.FPState.url);

      List<Sponsor> shuffled = new ArrayList<Sponsor>(query.getResultList());
      Collections.shuffle(shuffled);

      return shuffled;

   }

   // Images handling
   public void setUploadedData(byte[] uploadedData)
   {

      m_imageData = uploadedData;

   }

   public void setUploadedContentType(String contentType)
   {

      m_uploadedContentType = contentType;

   }

   public void setUploadedFileName(String uploadedFileName)
   {

      String[] tmp =
         ((uploadedFileName != null) ? uploadedFileName.split("/")
                                     : new String[] {""});
      m_uploadedFileName = tmp[tmp.length - 1];

   }

   public String uploadImage()
   {

      if ((m_imageData == null) || (m_imageData.length == 0))
      {

         FacesMessages.instance().add("No image selected");

         return "";

      }
      else
      {

         Sponsor sponsor = getInstance();
         SponsorImage img = sponsor.getPicture();

         if (img == null)
         {

            img = new SponsorImage();
            img.setSponsor(sponsor);

         }

         img.setData(m_imageData);
         img.setContentType(m_uploadedContentType);
         img.setFileName(m_uploadedFileName);
         img.updateThumbnail();
         sponsor.setPicture(img);
         getEntityManager().persist(img);
         m_imageData = null;
         m_uploadedContentType = null;
         m_uploadedFileName = null;

         return "imageUploaded";

      }

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
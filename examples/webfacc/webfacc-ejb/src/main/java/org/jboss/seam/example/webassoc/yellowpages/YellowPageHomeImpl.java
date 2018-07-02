package org.jboss.seam.example.webassoc.yellowpages;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mail.MailProcessor;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;

@Name("yellowPageHome")
@Stateful
public class YellowPageHomeImpl
        extends EntityHome<YellowPage>
        implements YellowPageHome
{

   @In(required = false)
   private AssocMember authenticatedMember;
   @In(create = true)
   private MailProcessor mailProcessor;
   private static final long serialVersionUID = -4199642707074650009L;
   private byte[] m_imageData;
   private String m_uploadedContentType;
   private String m_uploadedFileName;

   @Factory("yellowPage")
   public YellowPage initYellowPage()
   {

      return getInstance();

   }

   protected YellowPage createInstance()
   {

      YellowPage res = new YellowPage();
      res.setAddress("1500 Marilla St");
      res.setCity("Dallas");
      res.setCountry("USA");
      res.setState("TX");
      res.setZip("75201");
      res.setPhone1("000-000-0000");
      res.setPhone2("000-000-0000");
      res.setReferredBy(authenticatedMember);

      return res;

   }

   // List of all valid members
   @SuppressWarnings("unchecked")
   public List<YellowPage> getDirectory()
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM YellowPage c "
            + "ORDER BY c.cat1, c.cat2");

      return (List<YellowPage>)query.getResultList();

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

         YellowPage sponsor = getInstance();
         YellowPageImage img = sponsor.getPicture();

         if (img == null)
         {

            img = new YellowPageImage();
            img.setYellowPage(sponsor);

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

   /**
    * Create the object and sends the event.
    */
   public String persist()
   {

      String res = super.persist();

      if ("persisted".equals(res))
      {

         mailProcessor.sendNewYellowPageAlert(getInstance().getId());

      }

      return res;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
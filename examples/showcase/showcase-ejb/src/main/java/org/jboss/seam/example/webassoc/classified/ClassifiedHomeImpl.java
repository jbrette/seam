package org.jboss.seam.example.webassoc.classified;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.example.webassoc.classified.Classified.CKind;
import org.jboss.seam.example.webassoc.mail.MailProcessor;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;
import javax.persistence.Transient;

@Name("classifiedHome")
@Stateful
public class ClassifiedHomeImpl
        extends EntityHome<Classified>
        implements ClassifiedHome
{

   private static final long serialVersionUID = -8691115942046688602L;
   @In(required = false)
   private AssocMember authenticatedMember;
   @In(create = true)
   private MailProcessor mailProcessor;
   @RequestParameter
   private Integer attachmentId;
   private byte[] m_uploadedData;
   private String m_uploadedContentType;
   private String m_uploadedFileName;

   @Factory("classified")
   public Classified initClassified()
   {

      return getInstance();

   }

   protected Classified createInstance()
   {

      Classified res = new Classified();
      res.setCKind(CKind.sell);
      res.setTitle("ClassifiedName");
      res.setDescription("TheDescription");
      res.setPublisher(authenticatedMember);
      res.setVisible(Boolean.FALSE);
      res.setArchived(Boolean.FALSE);
      res.setDate(Calendar.getInstance().getTime());
      res.setHomePhone(Cts.Dflt.PHONE);
      res.setCellPhone(Cts.Dflt.PHONE);
      res.setEmail(Cts.Dflt.E_MAIL);

      return res;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<Classified> getFPClassifieds()
   {

      Query query = null;
      query =
         getEntityManager().createQuery("SELECT s FROM Classified s "
            + "WHERE (s.visible = true)");

      List<Classified> shuffled =
         new ArrayList<Classified>(query.getResultList());
      Collections.shuffle(shuffled);

      return shuffled;

   }

   // Images handling
   public void setUploadedData(byte[] uploadedData)
   {

      m_uploadedData = uploadedData;

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

      if ((m_uploadedData == null) || (m_uploadedData.length == 0))
      {

         FacesMessages.instance().add("No image selected");

         return "";

      }
      else
      {

         Classified classified = getInstance();
         ClassifiedImage img = new ClassifiedImage();
         img.setClassified(classified);
         img.setData(m_uploadedData);
         img.setContentType(m_uploadedContentType);
         img.setFileName(m_uploadedFileName);
         img.updateThumbnail();
         classified.getImages().add(img);
         getEntityManager().persist(img);
         m_uploadedData = null;
         m_uploadedContentType = null;
         m_uploadedFileName = null;

         return "imageUploaded";

      }

   }

   public String deleteImage()
   {

      if ((attachmentId == null))
      {

         FacesMessages.instance().add("No Image selected");

         return "";

      }
      else
      {

         Classified classified = getInstance();
         List<ClassifiedImage> images = classified.getImages();

         for (ClassifiedImage image : images)
         {

            if (image.getImageId().equals(attachmentId))
            {

               classified.getImages().remove(image);
               getEntityManager().remove(image);
               getEntityManager().persist(classified);

               return "imageDeleted";

            }

         }

         return "";

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

         mailProcessor.sendNewClassifiedAlert(getInstance().getId());

      }

      return res;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
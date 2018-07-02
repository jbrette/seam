package org.jboss.seam.example.webassoc.clubs;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;
import javax.persistence.Transient;

@Name("clubHome")
@Stateful
public class ClubHomeImpl
        extends EntityHome<Club>
        implements ClubHome
{

   private static final long serialVersionUID = 2293380668717084845L;

   // Image download fields
   private byte[] m_uploadedData;
   private String m_uploadedContentType;
   private String m_uploadedFileName;

   @Factory("club")
   public Club initClub()
   {

      return getInstance();

   }

   protected Club createInstance()
   {

      Club res = new Club();
      res.setName("TheClubName");
      res.setDescription("");
      res.setAutomaticMembership(false);
      res.setOpenEnrollment(true);
      res.setDiscontinued(false);
      res.setOpenedToGuests(true);
      res.setOpenedToKids(true);
      res.setCKind(Club.CKind.forEverybody);

      return res;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<Club> getAllClubs()
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM Club c "
            + "where c.discontinued := theFilter");
      query.setParameter("theFilter", Boolean.FALSE);

      return query.getResultList();

   }

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

         Club member = getInstance();
         ClubImage img = member.getPicture();

         if (img == null)
         {

            img = new ClubImage();
            img.setClub(member);

         }

         img.setData(m_uploadedData);
         img.setContentType(m_uploadedContentType);
         img.setFileName(m_uploadedFileName);
         img.updateThumbnail();
         member.setPicture(img);
         getEntityManager().persist(img);
         m_uploadedData = null;
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
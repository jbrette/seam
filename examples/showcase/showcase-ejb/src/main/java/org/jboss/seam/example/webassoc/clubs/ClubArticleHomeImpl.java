package org.jboss.seam.example.webassoc.clubs;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.util.HTextAttachment;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

@Name("clubArticleHome")
@Stateful
public class ClubArticleHomeImpl
        extends EntityHome<ClubArticle>
        implements ClubArticleHome
{

   private static final long serialVersionUID = 6740642004588007263L;
   @In(required = false)
   private Club club;
   @In(required = false)
   private AssocMember authenticatedMember;
   @RequestParameter
   private Integer attachmentId;

   // Image download fields
   private byte[] m_uploadedData;
   private String m_uploadedContentType;
   private String m_uploadedFileName;

   @Factory("clubArticle")
   public ClubArticle initClubArticle()
   {

      return getInstance();

   }

   protected ClubArticle createInstance()
   {

      ClubArticle article = new ClubArticle();

      // JEB: Not sure it needs to be done there
      article.setClub(club);
      club.getClubArticles().add(article);
      article.setTitle("TheName");
      article.setDescription("TheDescription");
      article.setVisible(Boolean.TRUE);
      article.setArchived(Boolean.FALSE);
      article.setSubject("subject");
      article.setAuthor(authenticatedMember);
      article.setTextDate(Calendar.getInstance().getTime());

      return article;

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

   public String uploadAttachment()
   {

      if ((m_uploadedData == null) || (m_uploadedData.length == 0))
      {

         FacesMessages.instance().add("No Attachment selected");

         return "";

      }
      else
      {

         ClubArticle article = getInstance();
         HTextAttachment doc = new HTextAttachment();
         doc.setHText(article);
         doc.setData(m_uploadedData);
         doc.setContentType(m_uploadedContentType);
         doc.setFileName(m_uploadedFileName);
         doc.updateThumbnail();
         article.getAttachments().add(doc);
         getEntityManager().persist(doc);
         m_uploadedData = null;
         m_uploadedContentType = null;
         m_uploadedFileName = null;

         return "attachmentUploaded";

      }

   }

   public String deleteAttachment()
   {

      if ((attachmentId == null))
      {

         FacesMessages.instance().add("No Attachment selected");

         return "";

      }
      else
      {

         ClubArticle article = getInstance();
         List<HTextAttachment> attachments = article.getAttachments();

         for (HTextAttachment attachment : attachments)
         {

            if (attachment.getImageId().equals(attachmentId))
            {

               article.getAttachments().remove(attachment);
               getEntityManager().remove(attachment);
               getEntityManager().persist(article);

               return "attachmentDeleted";

            }

         }

         return "";

      }

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
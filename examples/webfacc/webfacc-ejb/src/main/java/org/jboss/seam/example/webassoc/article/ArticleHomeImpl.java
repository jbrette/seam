package org.jboss.seam.example.webassoc.article;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.example.webassoc.mail.MailProcessor;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.util.HTextAttachment;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

@Name("articleHome")
@Stateful
public class ArticleHomeImpl
        extends EntityHome<Article>
        implements ArticleHome
{

   private static final long serialVersionUID = 2464046304937113725L;
   @In(required = false)
   private WebPage webPage;
   @In(required = false)
   private AssocMember authenticatedMember;
   @In(create = true)
   private MailProcessor mailProcessor;
   @RequestParameter
   private Integer attachmentId;

   // Image download fields
   private byte[] m_uploadedData;
   private String m_uploadedContentType;
   private String m_uploadedFileName;

   @Factory("article")
   public Article initArticle()
   {

      return getInstance();

   }

   protected Article createInstance()
   {

      Article article = new Article();
      article.setWebPage(webPage);
      webPage.getArticles().add(article);
      article.setTitle("TheTitle");
      article.setDescription("TheDescription");
      article.setVisible(Boolean.TRUE);
      article.setArchived(Boolean.FALSE);
      article.setSubject("TheSubject");
      article.setLanguage("en");
      article.setColumnInPage(1);
      article.setOrderInPage(1);
      article.setLayout(0);
      article.setIlstWidth(100);
      article.setIlstHref(null);
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

         Article article = getInstance();
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

         Article article = getInstance();
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

   public String uploadIllustration()
   {

      if ((m_uploadedData == null) || (m_uploadedData.length == 0))
      {

         FacesMessages.instance().add("No Illustration selected");

         return "";

      }
      else
      {

         Article article = getInstance();
         ArticleIllustration doc = new ArticleIllustration();
         doc.setArticle(article);
         doc.setData(m_uploadedData);
         doc.setContentType(m_uploadedContentType);
         doc.setFileName(m_uploadedFileName);
         doc.updateThumbnail();
         article.setIlst(doc);
         article.setLayout(5);
         article.setIlstWidth(200);
         article.setIlstHref(null);
         getEntityManager().persist(doc);
         getEntityManager().persist(article);
         m_uploadedData = null;
         m_uploadedContentType = null;
         m_uploadedFileName = null;

         return "illustrationUploaded";

      }

   }

   public String deleteIllustration()
   {

      Article article = getInstance();
      ArticleIllustration ilst = article.getIlst();

      if (ilst != null)
      {

         article.setIlst(null);
         article.setLayout(0);
         article.setIlstWidth(0);
         article.setIlstHref(null);
         getEntityManager().remove(ilst);
         getEntityManager().persist(article);

         return "illustrationDeleted";

      }
      else
      {

         FacesMessages.instance().add("No Illustration to delete");

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

         mailProcessor.sendNewArticleAlert(getInstance().getId());

      }

      return res;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
package org.jboss.seam.example.webassoc.article;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.HText;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
@Name("article")
public class Article
        extends HText
{

   private static final long serialVersionUID = -2677142182539822559L;

   // =================================
   // Members
   // =================================
   private String m_subject;
   private Integer m_column;
   private Integer m_order;
   private Integer m_layout;
   private Integer m_ilstWidth;
   private String m_ilstHref;
   private String m_language;
   private WebPage m_webPage;
   private ArticleIllustration m_illustration;

   // =================================
   // Getter/Setter
   // =================================
   public void setSubject(String subject)
   {

      m_subject = subject;

   }

   public String getSubject()
   {

      return m_subject;

   }

   public void setLanguage(String language)
   {

      this.m_language = language;

   }

   public String getLanguage()
   {

      return m_language;

   }

   public void setOrderInPage(Integer order)
   {

      this.m_order = order;

   }

   public Integer getOrderInPage()
   {

      return m_order;

   }

   public void setColumnInPage(Integer column)
   {

      this.m_column = column;

   }

   public Integer getColumnInPage()
   {

      return m_column;

   }

   public void setLayout(Integer layout)
   {

      this.m_layout = layout;

   }

   public Integer getLayout()
   {

      return m_layout;

   }

   public void setIlstWidth(Integer layout)
   {

      this.m_ilstWidth = layout;

   }

   public Integer getIlstWidth()
   {

      return m_ilstWidth;

   }

   public String getIlstHref()
   {

      return m_ilstHref;

   }

   public void setIlstHref(String href)
   {

      m_ilstHref = href;

   }

   @Transient
   public boolean isWithIlstHref()
   {

      return (m_ilstHref != null) && (m_ilstHref.length() != 0);

   }

   @Transient
   public boolean isWithoutIlstHref()
   {

      return !isWithIlstHref();

   }

   @Transient
   public String getIlstHrefTarget()
   {

      return (((m_ilstHref != null) && (m_ilstHref.indexOf(".seam") != -1))
            ? "_self" : "_blank");

   }

   // =================================
   // Relationship
   // =================================
   @NotNull @ManyToOne
   public WebPage getWebPage()
   {

      return m_webPage;

   }

   public void setWebPage(WebPage webPage)
   {

      this.m_webPage = webPage;

   }

   @OneToOne(fetch = FetchType.LAZY)
   public ArticleIllustration getIlst()
   {

      return m_illustration;

   }

   public void setIlst(ArticleIllustration picture)
   {

      m_illustration = picture;

   }

}
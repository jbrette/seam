package org.jboss.seam.example.webassoc.article;

import org.hibernate.validator.Length;
import org.hibernate.validator.Max;
import org.hibernate.validator.Min;
import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

@Entity
@Name("webPage")
public class WebPage
        implements Serializable
{

   private static final long serialVersionUID = -8815182414459782576L;

   // =================================
   // Kind Definition
   // =================================
   public enum CKind
   {

      forEverybody(0), forTheCommitee(1);

      private int id;

      private CKind(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }
   ;

   // =================================
   // Members
   // =================================
   private Long m_id;
   private String m_name;
   private CKind m_kind;
   private String m_layout;
   private String m_menuPosition;
   private Integer m_leftColumnSize;
   private Integer m_centerColumnSize;
   private Integer m_rightColumnSize;
   private String m_headerText;
   private String m_footerText;
   private List<Article> m_pageArticles = new ArrayList<Article>();

   // =================================
   // Getter/Setter
   // =================================
   @Id @GeneratedValue
   public Long getId()
   {

      return m_id;

   }

   public void setId(Long id)
   {

      this.m_id = id;

   }

   @NotNull
   @Length(max = 50)
   public String getName()
   {

      return m_name;

   }

   public void setName(String name)
   {

      this.m_name = name;

   }

   public CKind getCKind()
   {

      return m_kind;

   }

   public void setCKind(CKind kind)
   {

      this.m_kind = kind;

   }

   @Length(max = 50)
   public String getLayout()
   {

      return m_layout;

   }

   public void setLayout(String layout)
   {

      this.m_layout = layout;

   }

   @Length(max = 10)
   public String getMenuPosition()
   {

      return m_menuPosition;

   }

   public void setMenuPosition(String menuPosition)
   {

      this.m_menuPosition = menuPosition;

   }

   @Min(0)
   @Max(100)
   public Integer getLeftColumnSize()
   {

      return m_leftColumnSize;

   }

   public void setLeftColumnSize(Integer newSize)
   {

      this.m_leftColumnSize = newSize;

   }

   @Min(0)
   @Max(100)
   public Integer getCenterColumnSize()
   {

      return m_centerColumnSize;

   }

   public void setCenterColumnSize(Integer newSize)
   {

      this.m_centerColumnSize = newSize;

   }

   @Min(0)
   @Max(100)
   public Integer getRightColumnSize()
   {

      return m_rightColumnSize;

   }

   public void setRightColumnSize(Integer newSize)
   {

      this.m_rightColumnSize = newSize;

   }

   @Lob
   public String getHeaderText()
   {

      return m_headerText;

   }

   public void setHeaderText(String headerText)
   {

      if ((headerText != null) && (headerText.length() == 0))
      {

         // JEB: Protect against HIBERNATE-MYSQL CLOB issue
         m_headerText = " ";

      }
      else
      {

         m_headerText = headerText;

      }

   }

   @Lob
   public String getFooterText()
   {

      return m_footerText;

   }

   public void setFooterText(String footerText)
   {

      if ((footerText != null) && (footerText.length() == 0))
      {

         // JEB: Protect against HIBERNATE-MYSQL CLOB issue
         m_footerText = " ";

      }
      else
      {

         m_footerText = footerText;

      }

   }

   // =================================
   //
   // =================================
   @Transient
   public List<Article> getActiveArticles()
   {

      List<Article> allArticles = new ArrayList<Article>();

      for (Article article : m_pageArticles)
      {

         if (article.getVisible())
         {

            allArticles.add(article);

         }

      }

      return allArticles;

   }

   @Transient
   public List<Article> getArchivedArticles()
   {

      List<Article> allArticles = new ArrayList<Article>();

      for (Article article : m_pageArticles)
      {

         if (!article.getVisible())
         {

            allArticles.add(article);

         }

      }

      return allArticles;

   }

   @Transient
   public List<Article> getC1Articles()
   {

      List<Article> allArticles = new ArrayList<Article>();

      for (Article article : m_pageArticles)
      {

         if (article.getVisible() && (article.getColumnInPage() == 1))
         {

            allArticles.add(article);

         }

      }

      return allArticles;

   }

   @Transient
   public List<Article> getC2Articles()
   {

      List<Article> allArticles = new ArrayList<Article>();

      for (Article article : m_pageArticles)
      {

         if (article.getVisible() && (article.getColumnInPage() == 2))
         {

            allArticles.add(article);

         }

      }

      return allArticles;

   }

   @Transient
   public List<Article> getC3Articles()
   {

      List<Article> allArticles = new ArrayList<Article>();

      for (Article article : m_pageArticles)
      {

         if (article.getVisible() && (article.getColumnInPage() == 3))
         {

            allArticles.add(article);

         }

      }

      return allArticles;

   }

   // =================================
   // Relationship
   // =================================
   @OneToMany(mappedBy = "webPage", cascade = CascadeType.REMOVE)
   @OrderBy("columnInPage,orderInPage ASC")
   public List<Article> getArticles()
   {

      return m_pageArticles;

   }

   public void setArticles(List<Article> articles)
   {

      this.m_pageArticles = articles;

   }

}
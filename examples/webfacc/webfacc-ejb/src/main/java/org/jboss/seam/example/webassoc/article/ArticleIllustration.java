package org.jboss.seam.example.webassoc.article;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.article.Article;
import org.jboss.seam.example.webassoc.util.Attachment;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Name("articleIllustration")
public class ArticleIllustration
        extends Attachment
{

   private static final long serialVersionUID = -6207072644319001076L;
   private Article m_article;

   @NotNull @OneToOne
   public Article getArticle()
   {

      return m_article;

   }

   public void setArticle(Article article)
   {

      this.m_article = article;

   }

}
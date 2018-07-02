package org.jboss.seam.example.webassoc.article;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("articles")
public class ArticleQuery
        extends WebAssocEntityQuery<Article>
{

   private static final long serialVersionUID = 8642921514066139911L;

   public ArticleQuery()
   {

      super(Article.class);
      setOrderColumn("page");
      setOrderDirection(OrderDirection.ASC);
      setSimpleRestriction(
         "lower(c.title) like lower( concat(#{globals.searchedString}, '%' ) )");

   }

}
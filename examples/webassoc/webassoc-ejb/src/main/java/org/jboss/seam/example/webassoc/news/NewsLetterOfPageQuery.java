package org.jboss.seam.example.webassoc.news;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("newsLetterOfPage")
public class NewsLetterOfPageQuery
        extends WebAssocEntityQuery<NewsLetter>
{

   private static final long serialVersionUID = -7079691869503162918L;

   public NewsLetterOfPageQuery()
   {

      super(NewsLetter.class);
      setOrderColumn("date");
      setOrderDirection(OrderDirection.DESC);
      setSimpleRestriction("c.NKind = #{globals.newsLetterNKind}");

   }

}
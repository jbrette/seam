package org.jboss.seam.example.webassoc.news;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("newsLetters")
public class NewsLetterQuery
        extends WebAssocEntityQuery<NewsLetter>
{

   private static final long serialVersionUID = -9112585244007390995L;

   public NewsLetterQuery()
   {

      super(NewsLetter.class);
      setOrderColumn("date");
      setOrderDirection(OrderDirection.DESC);

   }

}
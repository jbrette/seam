package org.jboss.seam.example.webassoc.article;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("webPages")
public class WebPageQuery
        extends WebAssocEntityQuery<WebPage>
{

   private static final long serialVersionUID = 6818029770136944877L;

   public WebPageQuery()
   {

      super(WebPage.class);
      setOrderColumn("menuPosition");
      setOrderDirection(OrderDirection.ASC);
      // setSimpleRestriction("lower(c.title) like lower( concat(#{globals.searchedString}, '%' ) )");

   }

}
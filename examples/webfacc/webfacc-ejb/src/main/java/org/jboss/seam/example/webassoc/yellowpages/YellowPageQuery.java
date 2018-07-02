package org.jboss.seam.example.webassoc.yellowpages;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("yellowPages")
public class YellowPageQuery
        extends WebAssocEntityQuery<YellowPage>
{

   private static final long serialVersionUID = 59250158294227325L;

   public YellowPageQuery()
   {

      super(YellowPage.class);
      setOrderColumn("id");
      setOrderDirection(OrderDirection.ASC);
      // setSimpleRestriction("lower(c.name) like lower( concat(#{globals.searchedString}, '%' ) )");

   }

}
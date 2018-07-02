package org.jboss.seam.example.webassoc.classified;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("classifieds")
public class ClassifiedQuery
        extends WebAssocEntityQuery<Classified>
{

   private static final long serialVersionUID = 9098732572929417176L;

   public ClassifiedQuery()
   {

      super(Classified.class);
      setOrderColumn("date");
      setOrderDirection(OrderDirection.ASC);
      setSimpleRestriction(
         "lower(c.title) like lower( concat(#{globals.searchedString}, '%' ) )");

   }

}
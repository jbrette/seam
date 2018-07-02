package org.jboss.seam.example.webassoc.clubs;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("discontinuedClubs")
public class DiscontinuedClubQuery
        extends WebAssocEntityQuery<Club>
{

   private static final long serialVersionUID = 2412008604204864590L;

   public DiscontinuedClubQuery()
   {

      super(Club.class);
      setOrderColumn("CKind");
      setOrderDirection(OrderDirection.ASC);
      setSimpleRestriction("c.discontinued = #{globals.trueHack}");

   }

}
package org.jboss.seam.example.webassoc.clubs;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("clubs")
public class ClubQuery
        extends WebAssocEntityQuery<Club>
{

   private static final long serialVersionUID = 2396678145382909077L;

   public ClubQuery()
   {

      super(Club.class);
      setOrderColumn("CKind");
      setOrderDirection(OrderDirection.ASC);
      setSimpleRestriction("c.discontinued = #{globals.falseHack}");

   }

}
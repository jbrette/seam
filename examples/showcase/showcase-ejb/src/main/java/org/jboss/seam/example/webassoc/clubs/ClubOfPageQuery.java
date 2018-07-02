package org.jboss.seam.example.webassoc.clubs;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("clubOfPage")
public class ClubOfPageQuery
        extends WebAssocEntityQuery<Club>
{

   private static final long serialVersionUID = 7532223164275488037L;

   public ClubOfPageQuery()
   {

      super(Club.class);
      setOrderColumn("name");
      setOrderDirection(OrderDirection.ASC);
      setSimpleRestriction("c.CKind = #{globals.clubCKind}");

   }

}
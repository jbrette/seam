package org.jboss.seam.example.webassoc.vote;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("votes")
public class VoteQuery
        extends WebAssocEntityQuery<Vote>
{

   private static final long serialVersionUID = -5201901929664879877L;

   public VoteQuery()
   {

      super(Vote.class);
      setOrderColumn("startDate");
      setOrderDirection(OrderDirection.DESC);

   }

}
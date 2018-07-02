package org.jboss.seam.example.webassoc.clubs;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("clubEvents")
public class ClubEventQuery
        extends WebAssocEntityQuery<ClubEvent>
{

   private static final long serialVersionUID = -2791742705992034654L;

   public ClubEventQuery()
   {

      super(ClubEvent.class);
      setOrderColumn("startTime");
      setOrderDirection(OrderDirection.DESC);

   }

}
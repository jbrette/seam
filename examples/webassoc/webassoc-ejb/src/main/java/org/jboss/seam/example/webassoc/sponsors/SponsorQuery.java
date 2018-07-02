package org.jboss.seam.example.webassoc.sponsors;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("sponsors")
public class SponsorQuery
        extends WebAssocEntityQuery<Sponsor>
{

   private static final long serialVersionUID = -2696003094041334150L;

   public SponsorQuery()
   {

      super(Sponsor.class);
      setOrderColumn("sponsorName");
      setOrderDirection(OrderDirection.ASC);

   }

}
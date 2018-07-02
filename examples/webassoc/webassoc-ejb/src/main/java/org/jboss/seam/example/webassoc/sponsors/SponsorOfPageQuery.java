package org.jboss.seam.example.webassoc.sponsors;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("sponsorOfPage")
public class SponsorOfPageQuery
        extends WebAssocEntityQuery<Sponsor>
{

   private static final long serialVersionUID = -449881242914511665L;

   public SponsorOfPageQuery()
   {

      super(Sponsor.class);
      setOrderColumn("sponsorName");
      setOrderDirection(OrderDirection.ASC);
      setSimpleRestriction(
         "c.SKind = #{globals.sponsorSKind} and c.sponsorshipValid = true");

   }

}
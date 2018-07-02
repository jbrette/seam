package org.jboss.seam.example.webassoc.mship;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("assocMembers")
public class AssocMemberQuery
        extends WebAssocEntityQuery<AssocMember>
{

   private static final long serialVersionUID = 59250158294227325L;

   public AssocMemberQuery()
   {

      super(AssocMember.class);
      setOrderColumn("assocName");
      setOrderDirection(OrderDirection.ASC);
      setSimpleRestriction(
         "lower(c.assocName) like lower( concat(#{globals.searchedString}, '%' ) )");

   }

}
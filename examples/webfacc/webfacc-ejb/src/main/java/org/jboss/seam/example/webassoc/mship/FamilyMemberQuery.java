package org.jboss.seam.example.webassoc.mship;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("familyMembers")
public class FamilyMemberQuery
        extends WebAssocEntityQuery<FamilyMember>
{

   private static final long serialVersionUID = 4080601934887882055L;

   public FamilyMemberQuery()
   {

      super(FamilyMember.class);
      setOrderColumn("lastName");
      setOrderDirection(OrderDirection.ASC);
      setSimpleRestriction(
         "lower(c.lastName) like lower( concat(#{globals.searchedString}, '%' ) )");

   }

}
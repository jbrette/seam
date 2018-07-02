package org.jboss.seam.example.webassoc.classified;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery;

@Name("classifiedOfPage")
public class ClassifiedOfPageQuery
        extends WebAssocEntityQuery<Classified>
{

   private static final long serialVersionUID = 41465664450426896L;
   @In(required = false)
   protected AssocMember authenticatedMember;

   public ClassifiedOfPageQuery()
   {

      super(Classified.class);
      setOrderColumn("date");
      setOrderDirection(OrderDirection.DESC);

      Object hack = Contexts.getSessionContext().get("authenticatedMember");

      if (hack != null)
      {

         setSimpleRestriction(
            "(c.CKind = #{globals.classifiedCKind}) and ((c.visible = true) or (c.publisher.id = "
            + ((AssocMember)(hack)).getId() + "))");

      }
      else
      {

         setSimpleRestriction(
            "(c.CKind = #{globals.classifiedCKind}) and (c.visible = true)");

      }

   }

}
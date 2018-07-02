package org.jboss.seam.example.webassoc.test;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.example.webassoc.util.WebAssocEntityQuery.OrderDirection;
import org.jboss.seam.framework.EntityQuery;

import java.util.ArrayList;
import java.util.List;

@Name("bugs")
public class BugQuery
        extends EntityQuery<Bug>
{

   private static final long serialVersionUID = 3768009982559999269L;

   public BugQuery()
   {

      setEjbql("select c from Bug c");
      setMaxResults(Cts.QUERY_MAX_RESULT);
      setOrderColumn("commentDate");
      setOrderDirection(OrderDirection.DESC);
      setSimpleRestriction(
         "lower(c.title) like lower( concat(#{globals.searchedString}, '%' ) )");

   }

   @SuppressWarnings("rawtypes")
   public void setSimpleRestriction(String expressionString)
   {

      Expressions expressions = new Expressions();
      List<ValueExpression> restrictionVEs = new ArrayList<ValueExpression>(1);
      restrictionVEs.add(expressions.createValueExpression(expressionString));
      setRestrictions(restrictionVEs);

   }

}
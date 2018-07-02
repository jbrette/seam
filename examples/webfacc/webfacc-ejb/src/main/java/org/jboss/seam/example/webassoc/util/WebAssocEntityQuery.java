package org.jboss.seam.example.webassoc.util;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.framework.EntityQuery;

import java.util.ArrayList;
import java.util.List;

public class WebAssocEntityQuery<E>
        extends EntityQuery<E>
{

   private static final long serialVersionUID = 6492878245545706486L;

   public interface MaxResults
   {

      public static final int TWO_HUNDREDS = Cts.QUERY_MAX_RESULT;

   }

   public interface OrderDirection
   {

      public static final String ASC = "asc";
      public static final String DESC = "desc";

   }

   public WebAssocEntityQuery(Class<E> entityClass)
   {

      super();
      setEjbql("select c from " + entityClass.getName() + " c");
      setMaxResults(MaxResults.TWO_HUNDREDS);
      setOrderDirection(OrderDirection.ASC);

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
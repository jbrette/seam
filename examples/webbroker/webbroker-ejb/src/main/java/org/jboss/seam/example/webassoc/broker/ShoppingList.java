package org.jboss.seam.example.webassoc.broker;

import org.jboss.seam.annotations.Name;

import javax.persistence.Entity;

@Entity
@Name("shoppingList")
public class ShoppingList
        extends ExchangeList
{

   private static final long serialVersionUID = -5071483134692489829L;

}
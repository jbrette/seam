package org.jboss.seam.example.webassoc.broker;

import org.jboss.seam.annotations.Name;

import javax.persistence.Entity;

@Entity
@Name("forSell")
public class ForSell
        extends ExchangeList
{

   private static final long serialVersionUID = -5473685860465551221L;

}
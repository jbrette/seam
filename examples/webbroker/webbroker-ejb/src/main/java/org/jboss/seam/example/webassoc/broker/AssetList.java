package org.jboss.seam.example.webassoc.broker;

import org.jboss.seam.annotations.Name;

import javax.persistence.Entity;

@Entity
@Name("assetList")
public class AssetList
        extends ExchangeList
{

   private static final long serialVersionUID = 1336116309176745164L;

}
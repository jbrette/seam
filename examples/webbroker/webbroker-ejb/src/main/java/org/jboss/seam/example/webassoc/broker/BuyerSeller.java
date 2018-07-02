package org.jboss.seam.example.webassoc.broker;

import org.jboss.seam.annotations.Name;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Name("buyerSeller")
public class BuyerSeller
        extends Compagny
{

   private static final long serialVersionUID = -644624872137636125L;
   private ForSell m_inventoryForSell;
   private ShoppingList m_shoppingList;
   private AssetList m_assetList;

   @OneToOne
   public ForSell getInventoryForSell()
   {

      return m_inventoryForSell;

   }

   public void setInventoryForSell(ForSell forSell)
   {

      this.m_inventoryForSell = forSell;

   }

   @OneToOne
   public ShoppingList getShoppingList()
   {

      return m_shoppingList;

   }

   public void setShoppingList(ShoppingList shoppingList)
   {

      this.m_shoppingList = shoppingList;

   }

   @OneToOne
   public AssetList getAssetList()
   {

      return m_assetList;

   }

   public void setAssetList(AssetList assetList)
   {

      this.m_assetList = assetList;

   }

}
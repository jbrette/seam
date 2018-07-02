package org.jboss.seam.example.webassoc.broker;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Name("exchangedItem")
public class ExchangedItem
        implements Serializable
{

   private static final long serialVersionUID = 1683498474222472767L;

   // =================================
   // Members
   // =================================
   private Long m_id;
   private ExchangeList m_exchangeList;
   private String m_name;
   private List<QuoteItem> m_quoteItems = new ArrayList<QuoteItem>();

   // =================================
   // Constructor
   // =================================
   public ExchangedItem()
   {

   }

   public ExchangedItem(ExchangeList exchangeList)
   {

      this.m_exchangeList = exchangeList;
      exchangeList.getExchangedItems().add(this);

   }

   // =================================
   // Getter/Setter
   // =================================
   @Id @GeneratedValue
   public Long getId()
   {

      return m_id;

   }

   public void setId(Long id)
   {

      this.m_id = id;

   }

   public String getName()
   {

      return m_name;

   }

   public void setName(String name)
   {

      this.m_name = name;

   }

   // =================================
   // Relationship
   // =================================
   @NotNull @ManyToOne
   public ExchangeList getExchangeList()
   {

      return m_exchangeList;

   }

   public void setExchangeList(ExchangeList exchangeList)
   {

      this.m_exchangeList = exchangeList;

   }

   @OneToMany(mappedBy = "exchangedItem", cascade = CascadeType.REMOVE)
   public List<QuoteItem> getQuoteItems()
   {

      return m_quoteItems;

   }

   public void setQuoteItems(List<QuoteItem> quoteItems)
   {

      m_quoteItems = quoteItems;

   }

}
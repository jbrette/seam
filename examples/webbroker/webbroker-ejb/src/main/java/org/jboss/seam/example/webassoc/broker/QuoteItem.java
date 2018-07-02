package org.jboss.seam.example.webassoc.broker;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Name("quoteItem")
public class QuoteItem
        implements Serializable
{

   private static final long serialVersionUID = 283304173916337995L;

   // =================================
   // Members
   // =================================
   private Long m_id;
   private Quote m_quote;
   private ExchangedItem m_exchangedItem;

   // =================================
   // Constructor
   // =================================
   public QuoteItem()
   {

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

   // =================================
   // Relationship
   // =================================
   @NotNull @ManyToOne
   public Quote getQuote()
   {

      return m_quote;

   }

   public void setQuote(Quote quote)
   {

      this.m_quote = quote;

   }

   @NotNull @ManyToOne
   public ExchangedItem getExchangedItem()
   {

      return m_exchangedItem;

   }

   public void setExchangedItem(ExchangedItem exchangedItem)
   {

      this.m_exchangedItem = exchangedItem;

   }

}
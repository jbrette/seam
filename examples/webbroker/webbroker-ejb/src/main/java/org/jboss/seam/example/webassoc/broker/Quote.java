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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Name("quote")
@Table(
   name = "quote",
   uniqueConstraints =
      {
         @UniqueConstraint(
            columnNames = {"brokerEmployee_id", "buyerEmployee_id"}
         )
      }
)
public class Quote
        implements Serializable
{

   private static final long serialVersionUID = 4347090486388214142L;
   private Employee m_brokerEmployee;
   private Employee m_buyerEmployee;
   private List<QuoteItem> m_quoteItems = new ArrayList<QuoteItem>();

   // =================================
   // CERState Definition
   // =================================
   public enum CERState
   {

      registered(0), confirmed(1), waiting(2), cancelled(3);

      private int id;

      private CERState(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }
   ;

   // =================================
   // Members
   // =================================
   private Long m_id;

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
   public Employee getBrokerEmployee()
   {

      return m_brokerEmployee;

   }

   public void setBrokerEmployee(Employee brokerEmployee)
   {

      this.m_brokerEmployee = brokerEmployee;

   }

   @NotNull @ManyToOne
   public Employee getBuyerEmployee()
   {

      return m_buyerEmployee;

   }

   public void setBuyerEmployee(Employee buyerEmployee)
   {

      this.m_buyerEmployee = buyerEmployee;

   }

   @OneToMany(mappedBy = "quote", cascade = CascadeType.REMOVE)
   public List<QuoteItem> getQuoteItems()
   {

      return m_quoteItems;

   }

   public void setQuoteItems(List<QuoteItem> quoteItems)
   {

      m_quoteItems = quoteItems;

   }

}
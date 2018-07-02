package org.jboss.seam.example.webassoc.broker;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

@Entity
@Name("exchangeList")
@Inheritance(strategy = InheritanceType.JOINED)
public class ExchangeList
        implements Serializable
{

   private static final long serialVersionUID = 1439531001861901713L;
   private Long m_id;
   private List<ExchangedItem> m_exchangedItems =
      new ArrayList<ExchangedItem>();

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

      m_id = id;

   }

   // =================================
   // Relationship
   // =================================
   @OneToMany(mappedBy = "exchangeList", cascade = CascadeType.REMOVE)
   public List<ExchangedItem> getExchangedItems()
   {

      return m_exchangedItems;

   }

   public void setExchangedItems(List<ExchangedItem> exchangedItems)
   {

      m_exchangedItems = exchangedItems;

   }

}
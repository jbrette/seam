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
@Name("compagny")
@Inheritance(strategy = InheritanceType.JOINED)
public class Compagny
        implements Serializable
{

   private static final long serialVersionUID = -1169853728504622332L;
   private Long m_id;
   private List<Employee> m_employees = new ArrayList<Employee>();

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
   @OneToMany(mappedBy = "compagny", cascade = CascadeType.REMOVE)
   public List<Employee> getEmployees()
   {

      return m_employees;

   }

   public void setEmployees(List<Employee> employees)
   {

      m_employees = employees;

   }

}
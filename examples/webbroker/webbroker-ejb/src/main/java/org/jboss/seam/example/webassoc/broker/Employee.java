package org.jboss.seam.example.webassoc.broker;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Name("employee")
public class Employee
        implements Serializable
{

   private static final long serialVersionUID = 3327306429678829583L;

   // =================================
   // Kind Definition
   // =================================
   public enum FKind
   {

      kind0(0), kind1(1), kind2(2);

      private int id;

      private FKind(int id)
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
   private Compagny m_compagny;
   private String m_lastName;
   private String m_firstName;
   private String m_workPhone;
   private String m_cellPhone;
   private FKind m_fkind = FKind.kind1;

   // =================================
   // Constructor
   // =================================
   public Employee()
   {

   }

   public Employee(Compagny compagny)
   {

      this.m_compagny = compagny;
      compagny.getEmployees().add(this);

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

   @NotNull
   @Length(min = 3, max = 40)
   // @Pattern(regex = "[a-zA-Z \\-]+", message =
   // "Last Name must only contain letters")
   public String getLastName()
   {

      return m_lastName;

   }

   public void setLastName(String lastName)
   {

      this.m_lastName = lastName;

   }

   @NotNull
   @Length(min = 3, max = 40)
   // @Pattern(regex = "[a-zA-Z \\-]+", message =
   // "First Name must only contain letters")
   public String getFirstName()
   {

      return m_firstName;

   }

   public void setFirstName(String firstName)
   {

      this.m_firstName = firstName;

   }

   @NotNull
   public FKind getFKind()
   {

      return m_fkind;

   }

   public void setFKind(FKind fkind)
   {

      this.m_fkind = fkind;

   }

   @Length(max = 20)
   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getWorkPhone()
   {

      return m_workPhone;

   }

   public void setWorkPhone(String workPhone)
   {

      this.m_workPhone = workPhone;

   }

   @Length(max = 20)
   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getCellPhone()
   {

      return m_cellPhone;

   }

   public void setCellPhone(String cellPhone)
   {

      this.m_cellPhone = cellPhone;

   }

   // =================================
   // Relationship
   // =================================
   @NotNull @ManyToOne
   public Compagny getCompagny()
   {

      return m_compagny;

   }

   public void setCompagny(Compagny compagny)
   {

      this.m_compagny = compagny;

   }

}
package org.jboss.seam.example.webassoc.clubs;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Name("clubEventTicket")
public class ClubEventTicket
        implements Serializable
{

   private static final long serialVersionUID = 567806100533812520L;

   // =================================
   // Kind Definition
   // =================================
   public enum FKind
   {

      adultMember(0), kidMember(1), adultNonMember(2), kidNonMember(3),
      free(4);

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
   private ClubEventRegistration m_registration;
   private String m_lastName;
   private String m_firstName;
   private FKind m_fkind = FKind.adultMember;

   // =================================
   // Constructor
   // =================================
   public ClubEventTicket()
   {

   }

   public ClubEventTicket(ClubEventRegistration registration)
   {

      this.m_registration = registration;
      registration.getTickets().add(this);
      registration.resetFirstNames();

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
   // @Pattern(regex = "[a-zA-Z \\-]+", message = "Last Name must only contain letters")
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
   // @Pattern(regex = "[a-zA-Z \\-]+", message = "First Name must only contain letters")
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

   // =================================
   // Relationship
   // =================================
   @NotNull @ManyToOne
   public ClubEventRegistration getRegistration()
   {

      return m_registration;

   }

   public void setRegistration(ClubEventRegistration registration)
   {

      this.m_registration = registration;

   }

}
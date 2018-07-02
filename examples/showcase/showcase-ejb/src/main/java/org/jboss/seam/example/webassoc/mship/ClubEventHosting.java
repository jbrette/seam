package org.jboss.seam.example.webassoc.mship;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.clubs.Club;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Name("clubEventHosting")
@Table(
   name = "ClubEventHosting",
   uniqueConstraints =
      {@UniqueConstraint(columnNames = {"member_id", "club_id"})}
)
public class ClubEventHosting
        implements Serializable
{

   private static final long serialVersionUID = -6670775404976157167L;

   // =================================
   // Kind Definition
   // =================================
   public enum FKind
   {

      normal(0);

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
   private AssocMember m_member;
   private Club m_club;
   private FKind m_fkind = FKind.normal;
   private Boolean m_active;

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
   public FKind getFKind()
   {

      return m_fkind;

   }

   public void setFKind(FKind fkind)
   {

      this.m_fkind = fkind;

   }

   public void setActive(Boolean active)
   {

      this.m_active = active;

   }

   public Boolean getActive()
   {

      return m_active;

   }

   // =================================
   // Relationship
   // =================================
   @NotNull @ManyToOne
   public AssocMember getMember()
   {

      return m_member;

   }

   public void setMember(AssocMember member)
   {

      this.m_member = member;

   }

   @NotNull @ManyToOne
   public Club getClub()
   {

      return m_club;

   }

   public void setClub(Club club)
   {

      this.m_club = club;

   }

}
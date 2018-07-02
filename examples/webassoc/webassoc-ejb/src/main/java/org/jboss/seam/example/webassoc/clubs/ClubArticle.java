package org.jboss.seam.example.webassoc.clubs;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.HText;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Name("clubArticle")
public class ClubArticle
        extends HText
{

   private static final long serialVersionUID = -5333024856764713234L;

   // =================================
   // Members
   // =================================
   private String m_subject;
   private Club m_club;

   public void setSubject(String subject)
   {

      m_subject = subject;

   }

   public String getSubject()
   {

      return m_subject;

   }

   // =================================
   // Relationship
   // =================================
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
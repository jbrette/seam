package org.jboss.seam.example.webassoc.clubs;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.Attachment;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Name("clubImage")
public class ClubImage
        extends Attachment
{

   private static final long serialVersionUID = -2553071597578032128L;
   private Club m_club;

   @NotNull @OneToOne
   public Club getClub()
   {

      return m_club;

   }

   public void setClub(Club club)
   {

      m_club = club;

   }

}
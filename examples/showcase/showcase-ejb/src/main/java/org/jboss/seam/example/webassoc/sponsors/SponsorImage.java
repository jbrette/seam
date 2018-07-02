package org.jboss.seam.example.webassoc.sponsors;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.Attachment;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Name("sponsorImage")
public class SponsorImage
        extends Attachment
{

   private static final long serialVersionUID = -1480148516436085988L;
   private Sponsor m_sponsor;

   @NotNull @OneToOne
   public Sponsor getSponsor()
   {

      return m_sponsor;

   }

   public void setSponsor(Sponsor sponsor)
   {

      m_sponsor = sponsor;

   }

}
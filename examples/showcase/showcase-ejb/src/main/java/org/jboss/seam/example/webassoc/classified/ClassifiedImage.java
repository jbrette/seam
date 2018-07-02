package org.jboss.seam.example.webassoc.classified;

import org.hibernate.validator.NotNull;

import org.jboss.seam.example.webassoc.util.Attachment;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ClassifiedImage
        extends Attachment
{

   private static final long serialVersionUID = -1841950281771902201L;
   private Classified m_classified;

   @NotNull @ManyToOne
   public Classified getClassified()
   {

      return m_classified;

   }

   public void setClassified(Classified classified)
   {

      m_classified = classified;

   }

}
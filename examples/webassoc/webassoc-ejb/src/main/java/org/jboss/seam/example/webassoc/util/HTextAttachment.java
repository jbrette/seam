package org.jboss.seam.example.webassoc.util;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Name("htextAttachment")
public class HTextAttachment
        extends Attachment
{

   private static final long serialVersionUID = 3611876928547194194L;
   private HText m_htext;

   @NotNull @ManyToOne
   public HText getHText()
   {

      return m_htext;

   }

   public void setHText(HText htext)
   {

      m_htext = htext;

   }

}
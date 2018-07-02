package org.jboss.seam.example.webassoc.yellowpages;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.Attachment;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Name("yellowPageImage")
public class YellowPageImage
        extends Attachment
{

   private static final long serialVersionUID = -4730953770620179197L;
   private YellowPage m_yellowPage;

   @NotNull @OneToOne
   public YellowPage getYellowPage()
   {

      return m_yellowPage;

   }

   public void setYellowPage(YellowPage yellowPage)
   {

      m_yellowPage = yellowPage;

   }

}
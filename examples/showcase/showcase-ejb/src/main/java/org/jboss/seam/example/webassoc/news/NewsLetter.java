package org.jboss.seam.example.webassoc.news;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mail.MailingController;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@Name("newsLetter")
public class NewsLetter
        extends MailingController
{

   private static final long serialVersionUID = -8859136926836231649L;

   // ============================================
   // Announcement control
   // ============================================
   @Transient
   public boolean isReadyForAnnouncement()
   {

      MCState sendingState = getSendingState();

      return ((sendingState.equals(MCState.initialState))
            || (sendingState.equals(MCState.announcementEnded)));

   }

}
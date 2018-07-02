package org.jboss.seam.example.webassoc.vote;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.Attachment;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Name("voteAttachment")
public class VoteAttachment
        extends Attachment
{

   private static final long serialVersionUID = -142681523754500672L;
   private Vote m_vote;

   @NotNull @ManyToOne
   public Vote getVote()
   {

      return m_vote;

   }

   public void setVote(Vote vote)
   {

      m_vote = vote;

   }

}
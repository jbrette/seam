package org.jboss.seam.example.webassoc.mship;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.Attachment;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Name("assocMemberImage")
public class AssocMemberImage
        extends Attachment
{

   private static final long serialVersionUID = 3438030657443171223L;
   private AssocMember m_member;

   @NotNull @OneToOne
   public AssocMember getAssocMember()
   {

      return m_member;

   }

   public void setAssocMember(AssocMember member)
   {

      m_member = member;

   }

}
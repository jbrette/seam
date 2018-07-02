package org.jboss.seam.example.webassoc.test;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.framework.EntityHome;

import java.util.Calendar;

import javax.ejb.Remove;
import javax.ejb.Stateful;

@Name("bugHome")
@Stateful
public class BugHomeImpl
        extends EntityHome<Bug>
        implements BugHome
{

   private static final long serialVersionUID = 1968442189445570388L;

   // It does not seem to work.
   @In(required = false)
   private AssocMember authenticatedMember;

   @Factory("bug")
   public Bug initBug()
   {

      return getInstance();

   }

   protected Bug createInstance()
   {

      Bug amember = new Bug();
      amember.setTitle("Y'a un bug");
      amember.setCreatedBy(authenticatedMember.getAssocName());
      amember.setComment("");
      amember.setCommentDate(Calendar.getInstance().getTime());
      amember.setResolution(null);
      amember.setResolutionDate(null);
      amember.setBState(Bug.BState.created);

      return amember;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
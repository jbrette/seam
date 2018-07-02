package org.jboss.seam.example.webassoc.vote;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import java.util.Calendar;

import javax.ejb.Remove;
import javax.ejb.Stateful;

@Name("ballotHome")
@Stateful
public class BallotHomeImpl
        extends EntityHome<Ballot>
        implements BallotHome
{

   private static final long serialVersionUID = 5081151052844785927L;

   @Factory("ballot")
   public Ballot initBallot()
   {

      return getInstance();

   }

   protected Ballot createInstance()
   {

      Ballot reg = new Ballot();
      reg.setCastingDate(null);
      reg.setRes(Ballot.BRes.notCast);
      reg.setState(Ballot.MState.notSent);

      return reg;

   }

   public String update()
   {

      Ballot curB = getInstance();
      curB.setCastingDate(Calendar.getInstance().getTime());

      return super.update();

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
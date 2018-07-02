package org.jboss.seam.example.webassoc.vote;

import javax.ejb.Local;

@Local
public interface BallotHome
{

   // Methods from ClubEventRegistrationHomeImpl
   public Ballot initBallot();

   public void ejbRemove();

   // Methods from EntityHome and Home
   public Object getId();

   public void setId(Object id);

   public String persist();

   public String update();

   public String remove();

   public boolean isManaged();

   public void create();

   public Object getInstance();

}
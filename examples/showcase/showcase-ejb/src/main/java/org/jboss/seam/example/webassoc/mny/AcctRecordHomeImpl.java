package org.jboss.seam.example.webassoc.mny;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import javax.ejb.Remove;
import javax.ejb.Stateful;

@Name("acctRecordHome")
@Stateful
public class AcctRecordHomeImpl
        extends EntityHome<AcctRecord>
        implements AcctRecordHome
{

   /**
    *
    */
   private static final long serialVersionUID = 5439144844960324434L;

   @Factory("acctRecord")
   public AcctRecord initAcctRecord()
   {

      return getInstance();

   }

   protected AcctRecord createInstance()
   {

      AcctRecord fmember = new AcctRecord();

      return fmember;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}
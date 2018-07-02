package org.jboss.seam.example.webassoc.util;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Transactional;

import java.io.Serializable;

@Scope(ScopeType.SESSION)
@Name("sessionListener")
@Startup
public class SessionListener
        implements Serializable
{

   private static final long serialVersionUID = -4068461253926653635L;

   @Destroy @Transactional
   @Observer("org.jboss.seam.sessionExpired")
   public void onDestroy()
   {

   }

}
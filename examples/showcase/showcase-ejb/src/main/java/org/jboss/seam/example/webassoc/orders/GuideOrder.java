package org.jboss.seam.example.webassoc.orders;

import org.jboss.seam.annotations.Name;

import javax.persistence.Entity;

@Entity
@Name("guideOrder")
public class GuideOrder
        extends CoreOrder
{

   private static final long serialVersionUID = 3090209745025136449L;

}
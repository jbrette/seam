package org.jboss.seam.example.webassoc.broker;

import org.jboss.seam.annotations.Name;

import javax.persistence.Entity;

@Entity
@Name("broker")
public class Broker
        extends Compagny
{

   private static final long serialVersionUID = -1169853728504622332L;

}
/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
  *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.metadata.process.chain;

import java.util.List;

import org.jboss.metadata.ejb.jboss.JBossMetaData;
import org.jboss.metadata.process.ProcessingException;
import org.jboss.metadata.process.processor.JBossMetaDataProcessor;

/**
 * ProcessorChain
 * 
 * Defines the contract for a chain of Processors upon 
 * metadata
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface ProcessorChain<T extends JBossMetaData>
{
   // --------------------------------------------------------------------------------||
   // Contracts ----------------------------------------------------------------------||
   // --------------------------------------------------------------------------------||

   /**
    * Processes the specified metadata against the configured
    * processors, returning the output
    * 
    * @param metadata
    */
   T process(T metadata) throws ProcessingException;

   /**
    * Obtains all processors in the chain.  Will return
    * an immutable view of the configured processors as to not allow
    * published mutation of the List except as provided 
    * 
    * @return
    */
   List<JBossMetaDataProcessor<T>> getProcessors();

   /**
    * Adds the specified processor to the chain
    * 
    * @param processor
    */
   void addProcessor(JBossMetaDataProcessor<T> processor);

}

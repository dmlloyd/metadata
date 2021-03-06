/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.metadata.annotation.creator.ejb.jboss;

import java.lang.reflect.AnnotatedElement;

import javax.ejb.TransactionAttribute;

import org.jboss.metadata.annotation.creator.Processor;
import org.jboss.metadata.annotation.finder.AnnotationFinder;
import org.jboss.metadata.ejb.jboss.JBossEnterpriseBeanMetaData;
import org.jboss.metadata.ejb.spec.ContainerTransactionMetaData;


/**
 * A TransactionAttributeClassProcessor.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class TransactionAttributeClassProcessor<T extends JBossEnterpriseBeanMetaData>
   extends AbstractTransactionAttributeProcessor<Class<?>, T>
   implements Processor<T, Class<?>>
{

   protected TransactionAttributeClassProcessor(AnnotationFinder<AnnotatedElement> finder)
   {
      super(finder);
      // TODO Auto-generated constructor stub
   }

   @Override
   protected ContainerTransactionMetaData createContainerTransaction(String ejbName, TransactionAttribute annotation, Class<?> element)
   {
      ContainerTransactionMetaData containerTransaction = new ContainerTransactionMetaData();
      containerTransaction.setMethods(createMethods(ejbName, null));
      containerTransaction.setTransAttribute(createTransAttributeType(annotation));
      return containerTransaction;
   }
}

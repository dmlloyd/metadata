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
package org.jboss.metadata.annotation.creator.ejb.jboss;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import javax.ejb.Asynchronous;

import org.jboss.metadata.annotation.creator.EjbProcessorUtils;
import org.jboss.metadata.annotation.creator.Processor;
import org.jboss.metadata.annotation.finder.AnnotationFinder;
import org.jboss.metadata.ejb.jboss.JBossSessionBean31MetaData;
import org.jboss.metadata.ejb.spec.AsyncMethodMetaData;
import org.jboss.metadata.ejb.spec.AsyncMethodsMetaData;
import org.jboss.metadata.ejb.spec.MethodParametersMetaData;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class AsyncMethodProcessor extends AbstractAsyncProcessor<Method>
   implements Processor<JBossSessionBean31MetaData, Method> // FIXME: because AbstractProcessor.getProcessorMetaDataType doesn't take inheritence into account
{
   protected AsyncMethodProcessor(AnnotationFinder<AnnotatedElement> finder)
   {
      super(finder);
   }

   @Override
   public void process(JBossSessionBean31MetaData metaData, Method method)
   {
      Asynchronous asynchronous = finder.getAnnotation(method, Asynchronous.class);
      if(asynchronous == null)
         return;
      
      AsyncMethodsMetaData asyncMethods = metaData.getAsyncMethods();
      if(asyncMethods == null)
      {
         asyncMethods = new AsyncMethodsMetaData();
         metaData.setAsyncMethods(asyncMethods);
      }
      
      MethodParametersMetaData methodParameters = EjbProcessorUtils.getMethodParameters(method);
      AsyncMethodMetaData asyncMethod = new AsyncMethodMetaData();
      asyncMethod.setMethodName(method.getName());
      asyncMethod.setMethodParams(methodParameters);
      
      asyncMethods.add(asyncMethod);
   }
}

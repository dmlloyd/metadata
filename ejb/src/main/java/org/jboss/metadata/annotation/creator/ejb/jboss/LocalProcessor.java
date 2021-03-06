/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import javax.ejb.EJBLocalObject;
import javax.ejb.Local;

import org.jboss.metadata.annotation.creator.AbstractFinderUser;
import org.jboss.metadata.annotation.creator.Processor;
import org.jboss.metadata.annotation.creator.ProcessorUtils;
import org.jboss.metadata.annotation.finder.AnnotationFinder;
import org.jboss.metadata.ejb.jboss.JBossSessionBeanMetaData;
import org.jboss.metadata.ejb.spec.BusinessLocalsMetaData;
import org.jboss.metadata.lang.ClassHelper;

/**
 * Local annotation processor.
 *
 * @author <a href="mailto:carlo.dewolf@jboss.com">Carlo de Wolf</a>
 * @version $Revision: 67998 $
 */
public class LocalProcessor extends AbstractFinderUser implements Processor<JBossSessionBeanMetaData, Class<?>>
{  
   public LocalProcessor(AnnotationFinder<AnnotatedElement> finder)
   {
      super(finder);
   }
   
   protected void addBusinessInterface(JBossSessionBeanMetaData metaData, Class<?> businessInterface)
   {
      // The business interface must not extend EJBLocalObject
      if(EJBLocalObject.class.isAssignableFrom(businessInterface))
      {
         throw new IllegalStateException("EJB 3.0 Core Specification Violation (4.6.6): The session bean’s business interface "+ businessInterface + " must not extend the javax.ejb.EJBLocalObject interface.");
      }
      else
      {
         if(metaData.getBusinessLocals() == null)
            metaData.setBusinessLocals(new BusinessLocalsMetaData());
         
         metaData.getBusinessLocals().add(businessInterface.getName());
      }
   }
   
   public void process(JBossSessionBeanMetaData metaData, Class<?> type)
   {
      Local local = finder.getAnnotation(type, Local.class);
      if(local == null)
         return;

      Class<?> beanClass = EjbClassThreadLocal.ejbClass.get();

      if(type.isInterface())
      {
         // make sure it's a directly implemented interface
         if (beanClass != null && this.isDirectlyImplementedInterface(beanClass, type))
         {
            addBusinessInterface(metaData, type);
         }
      }
      else if (type.getName().equals(metaData.getEjbClass())) // we ignore super classes and pick up @Local only from the bean class 
      {
         if(local.value() == null || local.value().length == 0)
         {
            Class<?> businessInterface = ClassHelper.getDefaultInterface(type);
            addBusinessInterface(metaData, businessInterface);
         }
         else
         {
            for(Class<?> businessInterface : local.value())
            {
               addBusinessInterface(metaData, businessInterface);
            }
         }
      }
   }
   
   public Collection<Class<? extends Annotation>> getAnnotationTypes()
   {
      return ProcessorUtils.createAnnotationSet(Local.class);
   }
   
   /**
    * Returns true if the passed interface <code>intf</code> is in the implements 
    * clause of the <code>beanClass</code> or if the <code>intf</code> is a superinterface
    * of one of the interfaces in the implements clause of the <code>beanClass</code>.
    * 
    * Returns false otherwise
    * @param beanClass The EJB class
    * @param intf The interface being checked
    * @return
    */
   private boolean isDirectlyImplementedInterface(Class<?> beanClass, Class<?> intf)
   {
      if (!intf.isInterface())
      {
         throw new IllegalArgumentException(intf + " is not an interface");
      }
      Class<?>[] directlyImplementedInterfaces = beanClass.getInterfaces();
      for (Class<?> directlyImplementedInterface : directlyImplementedInterfaces)
      {
         if (intf.isAssignableFrom(directlyImplementedInterface))
         {
            return true;
         }
      }
      return false;
   }
}

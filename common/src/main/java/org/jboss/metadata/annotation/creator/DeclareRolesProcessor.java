/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.metadata.annotation.creator;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import javax.annotation.security.DeclareRoles;

import org.jboss.annotation.javaee.Descriptions;
import org.jboss.metadata.annotation.finder.AnnotationFinder;
import org.jboss.metadata.javaee.spec.SecurityRoleMetaData;
import org.jboss.metadata.javaee.spec.SecurityRolesMetaData;

/**
 * @DeclareRoles processor
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision: 76002 $
 */
public class DeclareRolesProcessor
   extends AbstractFinderUser
   implements Processor<SecurityRolesMetaData, Class<?>>
{
   public DeclareRolesProcessor(AnnotationFinder<AnnotatedElement> finder)
   {
      super(finder);
   }

   public void process(SecurityRolesMetaData metaData, Class<?> element)
   {
      DeclareRoles roles = finder.getAnnotation(element, DeclareRoles.class);
      if(roles == null)
         return;

      for(String role : roles.value())
      {
         SecurityRoleMetaData sr = new SecurityRoleMetaData();
         sr.setRoleName(role);
         Descriptions descriptions = ProcessorUtils.getDescription("DeclareRoles("+roles.value()+") on class: "+element.getName());
         sr.setDescriptions(descriptions);
         metaData.add(sr);
      }
   }

   public Collection<Class<? extends Annotation>> getAnnotationTypes()
   {
      return ProcessorUtils.createAnnotationSet(DeclareRoles.class);
   }
   
}

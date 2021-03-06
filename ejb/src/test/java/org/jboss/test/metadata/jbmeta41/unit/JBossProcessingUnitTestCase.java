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
package org.jboss.test.metadata.jbmeta41.unit;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

import org.jboss.metadata.annotation.creator.ejb.jboss.JBoss50Creator;
import org.jboss.metadata.annotation.finder.AnnotationFinder;
import org.jboss.metadata.annotation.finder.DefaultAnnotationFinder;
import org.jboss.metadata.ejb.jboss.JBoss50MetaData;
import org.jboss.metadata.ejb.jboss.JBossSessionBeanMetaData;
import org.jboss.metadata.ejb.spec.EjbJarMetaData;
import org.jboss.test.metadata.common.PackageScanner;
import org.jboss.test.metadata.jbmeta41.MultipleReturnTypeBean;
import org.jboss.test.metadata.jbmeta41.MyFailingStatelessBean;
import org.jboss.test.metadata.jbmeta41.MyOtherFailingStateLessBean;
import org.jboss.test.metadata.jbmeta41.MyStateful21Local;
import org.jboss.test.metadata.jbmeta41.MyStateful21Remote;
import org.jboss.test.metadata.jbmeta41.MyStatefulBean;
import org.jboss.test.metadata.jbmeta41.WrongCreateStatelessBean;
import org.jboss.test.metadata.jbmeta42.MyStateless21Local;
import org.jboss.test.metadata.jbmeta42.MyStateless21Remote;
import org.jboss.test.metadata.jbmeta42.MyStatelessLocal;
import org.jboss.test.metadata.jbmeta42.MyStatelessRemote;

/**
 * Test processing of the local and remote business interfaces of a EJB 2.x Local/Remote Home
 * (JBoss50Creator)
 * 
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class JBossProcessingUnitTestCase extends TestCase
{
      
   public void testStatelessHomeandLocalHome()
   {
      Collection<Class<?>> classes = PackageScanner.loadClasses("org.jboss.test.metadata.jbmeta42");
      
      JBossSessionBeanMetaData sessionBeanMetaData = getSessionBeanMetaData(classes, "MyStatelessBean");
      assertNotNull(sessionBeanMetaData);
      
      // Check if EjbHome and EjbLocalHome are defined
      assertEquals(MyStateless21Local.class.getName(), sessionBeanMetaData.getLocal());
      assertEquals(MyStateless21Remote.class.getName(), sessionBeanMetaData.getRemote());
      
      // 
      assertTrue(sessionBeanMetaData.getBusinessLocals().contains(MyStatelessLocal.class.getName()));
      assertFalse(sessionBeanMetaData.getBusinessLocals().contains(MyStateless21Local.class.getName()));
      
      assertTrue(sessionBeanMetaData.getBusinessRemotes().contains(MyStatelessRemote.class.getName()));
      assertFalse(sessionBeanMetaData.getBusinessRemotes().contains(MyStateless21Remote.class.getName()));
   }
   
   public void testStatefulHomeandLocalHome()
   {
      Collection<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(MyStatefulBean.class);
      
      JBossSessionBeanMetaData sessionBeanMetaData = getSessionBeanMetaData(classes, "MyStatefulBean");
      assertNotNull(sessionBeanMetaData);
      
      //
      assertEquals(MyStateful21Local.class.getName(), sessionBeanMetaData.getLocal());
      assertEquals(MyStateful21Remote.class.getName(), sessionBeanMetaData.getRemote());
   }
   
   // Test multiple create methods on a stateless bean 
   public void testMyFailingStatelessBean()
   {
      try
      {
         Collection<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(MyFailingStatelessBean.class);
         
         getSessionBeanMetaData(classes, "MyFailingStatelessBean");
         fail("A stateless session bean must define exactly one create method with no arguments");
      }
      catch(Exception e)
      {
         // ok
      }
   }
   
   // Test a wrong remote interface as a return type of a localHome
   public void testMyOhterFailingStatelessBean()
   {
      try
      {
         Collection<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(MyOtherFailingStateLessBean.class);
         
         getSessionBeanMetaData(classes, "MyOtherFailingStateLessBean");
         fail("The session bean’s local interface interface org.jboss.test.metadata.jbmeta41.MyStateful21Remote must extend the javax.ejb.EJBLocalObject");
      }
      catch(Exception e)
      {
         // ok
      }
   }
   
   //
   public void testMultipleReturnTypeBean()
   {
      try
      {
         Collection<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(MultipleReturnTypeBean.class);
         
         getSessionBeanMetaData(classes, "MultipleReturnTypeBean");
         fail("An EJB 2.1 view can't have multiple remote/local interfaces");
      }
      catch(Exception e)
      {
         // ok
      }
   }
   
   public void testWrongCreateStatelessBean()
   {
      try
      {
         Collection<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(WrongCreateStatelessBean.class);
         
         getSessionBeanMetaData(classes, "WrongCreateStatelessBean");
         fail("A stateless session bean must define exactly one create method with no arguments.");
      }
      catch(Exception e)
      {
         // ok
      }
   }
   
   
   private JBossSessionBeanMetaData getSessionBeanMetaData(Collection<Class<?>> classes, String enterpriseBean)
   {
      AnnotationFinder<AnnotatedElement> finder = new DefaultAnnotationFinder<AnnotatedElement>();
      JBoss50Creator creator = new JBoss50Creator(finder);

      JBoss50MetaData jbossMetaData = creator.create(classes);
      
      assertEquals(EjbJarMetaData.LATEST_EJB_JAR_XSD_VERSION, jbossMetaData.getEjbVersion());
      assertEquals("5.0", jbossMetaData.getVersion());      
      return (JBossSessionBeanMetaData) jbossMetaData.getEnterpriseBean(enterpriseBean);
   }
}
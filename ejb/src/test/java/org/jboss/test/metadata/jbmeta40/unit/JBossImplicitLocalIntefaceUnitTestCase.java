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
package org.jboss.test.metadata.jbmeta40.unit;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import junit.framework.TestCase;

import org.jboss.metadata.annotation.creator.ejb.jboss.JBoss50Creator;
import org.jboss.metadata.annotation.finder.AnnotationFinder;
import org.jboss.metadata.annotation.finder.DefaultAnnotationFinder;
import org.jboss.metadata.ejb.jboss.JBoss50MetaData;
import org.jboss.metadata.ejb.jboss.JBossSessionBeanMetaData;
import org.jboss.metadata.ejb.spec.EjbJarMetaData;
import org.jboss.metadata.process.processor.ejb.jboss.SetDefaultLocalBusinessInterfaceProcessor;
import org.jboss.test.metadata.common.PackageScanner;
import org.jboss.test.metadata.common.ScanPackage;
import org.jboss.test.metadata.jbmeta40.ExpectedLocalInterface;
import org.jboss.test.metadata.jbmeta40.OtherInterface;
import org.jboss.test.metadata.jbmeta40.RemoteInterface;

/**
 * Test implicit home interfaces (JBoss50Creator)
 * 
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class JBossImplicitLocalIntefaceUnitTestCase extends TestCase
{

   private JBoss50MetaData jbossMetaData;
   
   @Override
   @ScanPackage("org.jboss.test.metadata.jbmeta40")
   protected void setUp() throws Exception
   {
      super.setUp();
      
      AnnotationFinder<AnnotatedElement> finder = new DefaultAnnotationFinder<AnnotatedElement>();

      Collection<Class<?>> classes = PackageScanner.loadClasses();
      
      //enableTrace("org.jboss.metadata.annotation.creator");
      JBoss50Creator creator = new JBoss50Creator(finder);

      jbossMetaData = creator.create(classes);
      
      // Run the implicit local processor
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      SetDefaultLocalBusinessInterfaceProcessor<JBoss50MetaData> processor = new SetDefaultLocalBusinessInterfaceProcessor<JBoss50MetaData>(cl);
      jbossMetaData = processor.process(jbossMetaData);

      assertTrue(jbossMetaData.isEJB3x());
      assertEquals(EjbJarMetaData.LATEST_EJB_JAR_XSD_VERSION, jbossMetaData.getEjbVersion());
      assertEquals("5.0", jbossMetaData.getVersion());

      assertNotNull("no beans defined", jbossMetaData.getEnterpriseBeans());
   }
   
   public void testExpectedLocal()
   {
      JBossSessionBeanMetaData sb = (JBossSessionBeanMetaData) jbossMetaData.getEnterpriseBean("MyBean");
      assertNotNull(sb);
      assertEquals(1, sb.getBusinessLocals().size());
      assertTrue( sb.getBusinessLocals().contains(ExpectedLocalInterface.class.getName()));
      assertNull(sb.getBusinessRemotes());
   }
   
   public void testRemoteInterface()
   {
      JBossSessionBeanMetaData sb = (JBossSessionBeanMetaData) jbossMetaData.getEnterpriseBean("RemoteBean");
      assertNotNull(sb);
      assertNull(sb.getBusinessLocals());
   }
   
   public void testAnotherStatelessBean()
   {
      JBossSessionBeanMetaData sb = (JBossSessionBeanMetaData) jbossMetaData.getEnterpriseBean("AnotherStatelessBean");
      assertNotNull(sb);
      assertNull(sb.getBusinessLocals());
      assertNull(sb.getBusinessRemotes());
   }
   
   public void testMyOtherBean()
   {
      JBossSessionBeanMetaData sb = (JBossSessionBeanMetaData) jbossMetaData.getEnterpriseBean("MyOtherBean");
      assertNotNull(sb);
      assertNull(sb.getBusinessLocals());
      assertNotNull(sb.getBusinessRemotes());
   }
   
   public void testMyStatelessBean()
   {
      JBossSessionBeanMetaData sb = (JBossSessionBeanMetaData) jbossMetaData.getEnterpriseBean("MyStatelessBean");
      assertNotNull(sb);
      assertNotNull(sb.getBusinessLocals());
      assertTrue(sb.getBusinessLocals().contains(ExpectedLocalInterface.class.getName()));
      assertFalse(sb.getBusinessLocals().contains(OtherInterface.class.getName()));
   }
}


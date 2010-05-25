/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Simon McDuff - maintenance
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * NPE in ChangeSubscriptionManager.isPending() while subscribing a pending TRANSIENT-by-removal object
 * <p>
 * See https://bugs.eclipse.org/251087
 * 
 * @author Victor Roldan Betancort
 */
public class Bugzilla_251087_Test extends AbstractCDOTest
{
  public void testSubscription() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();

    transaction1.options().setInvalidationNotificationEnabled(true);
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);

    Company obj2 = getModel1Factory().createCompany();

    TestAdapter testAdapter = new TestAdapter();

    obj2.eAdapters().add(testAdapter);
    res.getContents().add(obj2);
    transaction1.commit();

    res.getContents().remove(obj2);
    res.getContents().add(obj2);
    res.getContents().remove(obj2);
    transaction1.commit();
  }

  public void testSubscriptionDetached() throws Exception
  {
    CDOSession sessionA = openModel1Session();
    CDOSession sessionB = openModel1Session();
    CDOTransaction transaction1 = sessionA.openTransaction();

    transaction1.options().setInvalidationNotificationEnabled(true);
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    Company obj2 = getModel1Factory().createCompany();
    res.getContents().add(obj2);
    transaction1.commit();

    CDOTransaction transB1 = sessionB.openTransaction();
    CDOID companyID = CDOUtil.getCDOObject(obj2).cdoID();
    Company companyB = (Company)transB1.getObject(companyID);
    sessionB.options().setPassiveUpdateEnabled(false);
    transB1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    final TestAdapter testAdapter = new TestAdapter();
    companyB.eAdapters().add(testAdapter);
    assertEquals(true, ((InternalCDOTransaction)transB1).hasSubscription(companyID));

    res.getContents().remove(obj2);
    transaction1.commit();

    msg("Checking after commit");
    boolean timedOut = new PollingTimeOuter(10, 200)
    {
      @Override
      protected boolean successful()
      {
        return testAdapter.getNotifications().size() == 1;
      }
    }.timedOut();

    assertEquals(false, timedOut);
    assertEquals(false, ((InternalCDOTransaction)transB1).hasSubscription(companyID));
  }

  /**
   * @author Simon McDuff
   */
  private static class TestAdapter implements Adapter
  {
    private List<Notification> notifications = new ArrayList<Notification>();

    private Notifier notifier;

    public TestAdapter()
    {
    }

    public Notifier getTarget()
    {
      return notifier;
    }

    public List<Notification> getNotifications()
    {
      return notifications;
    }

    public boolean isAdapterForType(Object type)
    {
      return false;
    }

    public void notifyChanged(Notification notification)
    {
      notifications.add(notification);
    }

    public void setTarget(Notifier newTarget)
    {
      notifier = newTarget;
    }
  }
}

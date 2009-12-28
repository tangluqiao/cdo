/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.ecore.EObject;

/**
 * Persisted objects keeps references to detached objects through deltas
 * <p>
 * See bug 250757
 * 
 * @author Victor Roldan Betancort
 */
public class Bugzilla_250757_Test extends AbstractCDOTest
{
  public void testAddAndRemoveFromPersistedList() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    EObject obj = getModel1Factory().createCompany();
    res.getContents().add(obj);
    res.getContents().remove(obj);

    try
    {
      transaction1.commit();
    }
    catch (TransactionException e)
    {
      fail("Should not have an exception");
    }
  }

  public void testAddAndModifyAndRemoveFromPersistedList() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    res.getContents().add(supplier);
    res.getContents().add(purchaseOrder);
    supplier.getPurchaseOrders().add(purchaseOrder);
    transaction1.commit();

    res.getContents().remove(purchaseOrder);
    supplier.getPurchaseOrders().remove(purchaseOrder);
    purchaseOrder.setSupplier(null);
    transaction1.commit();
  }

  public void testAddAndMoveAndRemoveFromPersistedList() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    EObject obj = getModel1Factory().createCompany();
    res.getContents().add(obj);
    res.getContents().move(1, 0);
    res.getContents().remove(obj);

    try
    {
      transaction1.commit();
    }
    catch (TransactionException e)
    {
      fail("Should not have an exception");
    }
  }

  public void testAddAndMoveAndRemoveFromPersistedListWithSavePoint() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    EObject obj = getModel1Factory().createCompany();
    res.getContents().add(obj);

    transaction1.setSavepoint();

    res.getContents().move(1, 0);
    res.getContents().remove(obj);

    try
    {
      transaction1.commit();
    }
    catch (TransactionException ex)
    {
      IOUtil.print(ex);
      fail("Should not have an exception");
    }
  }

  public void testAddAndMoveAndRemoveFromPersistedListWithManySavePoint() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    transaction1.setSavepoint();

    EObject obj = getModel1Factory().createCompany();
    res.getContents().add(obj);
    transaction1.setSavepoint();

    res.getContents().move(1, 0);
    transaction1.setSavepoint();

    res.getContents().remove(obj);
    transaction1.setSavepoint();

    try
    {
      transaction1.commit();
    }
    catch (TransactionException ex)
    {
      IOUtil.print(ex);
      fail("Should not have an exception");
    }
  }
}

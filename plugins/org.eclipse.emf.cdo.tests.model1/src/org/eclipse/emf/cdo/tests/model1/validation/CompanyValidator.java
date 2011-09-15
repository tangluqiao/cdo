/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.validation;

import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.common.util.EList;

/**
 * A sample validator interface for {@link org.eclipse.emf.cdo.tests.model1.Company}. This doesn't really do anything,
 * and it's not a real EMF artifact. It was generated by the org.eclipse.emf.examples.generator.validator plug-in to
 * illustrate how EMF's code generator can be extended. This can be disabled with -vmargs
 * -Dorg.eclipse.emf.examples.generator.validator=false.
 * 
 * @since 2.0
 */
public interface CompanyValidator
{
  boolean validate();

  boolean validateCategories(EList<Category> value);

  boolean validateSuppliers(EList<Supplier> value);

  boolean validatePurchaseOrders(EList<PurchaseOrder> value);

  boolean validateCustomers(EList<Customer> value);

  boolean validateSalesOrders(EList<SalesOrder> value);
}

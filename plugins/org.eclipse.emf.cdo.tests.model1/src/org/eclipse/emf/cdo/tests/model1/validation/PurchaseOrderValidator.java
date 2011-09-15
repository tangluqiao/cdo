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

import org.eclipse.emf.cdo.tests.model1.Supplier;

import java.util.Date;

/**
 * A sample validator interface for {@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder}. This doesn't really do
 * anything, and it's not a real EMF artifact. It was generated by the org.eclipse.emf.examples.generator.validator
 * plug-in to illustrate how EMF's code generator can be extended. This can be disabled with -vmargs
 * -Dorg.eclipse.emf.examples.generator.validator=false.
 * 
 * @since 2.0
 */
public interface PurchaseOrderValidator
{
  boolean validate();

  boolean validateDate(Date value);

  boolean validateSupplier(Supplier value);
}

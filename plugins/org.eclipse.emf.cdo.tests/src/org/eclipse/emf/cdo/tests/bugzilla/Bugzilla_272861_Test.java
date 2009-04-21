/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class Bugzilla_272861_Test extends AbstractCDOTest
{
  public void test_Bugzilla_271861_Case1() throws IOException
  {

    CDOSession session = openModel1Session();

    CDOTransaction trans = session.openTransaction();
    CDOResource res = trans.createResource("/test/1");

    trans.commit();

    res.delete(null);
    res = trans.createResource("/test/1");
    trans.commit();
    trans.close();
    session.close();

  }

  // TODO SIMON Is it a bug or not??
  public void te2st_Bugzilla_272861_Case2() throws IOException
  {

    CDOSession session = openModel1Session();

    CDOTransaction trans1 = session.openTransaction();

    CDOTransaction trans2 = session.openTransaction();
    CDOResource res1 = trans1.createResource("/test1");
    CDOResource res2 = trans2.createResource("/test2");

    trans1.commit();
    trans2.commit();
    trans1.close();
    trans2.close();
    session.close();

  }
}

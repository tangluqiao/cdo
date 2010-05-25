/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * A simple in-memory store.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public interface IMEMStore extends IStore
{
  public static final int UNLIMITED = -1;

  /**
   * Returns the number of {@link CDORevision revisions} per {@link CDOID} that are stored.
   */
  public int getListLimit();

  /**
   * Limits the number of {@link CDORevision revisions} per {@link CDOID} to the given value.
   * <p>
   * A value of 2, for example, stores the current and the immediately preceding revisions whereas older revisions are
   * dropped from thids store. A value of 1 only stores the current revisions. A value of {@link #UNLIMITED} does not
   * limit the number of revisions to be stored for any id.
   * <p>
   * The list limit can be set and enforced at any time before or after the {@link LifecycleUtil#activate(Object)
   * activation} of this store.
   */
  public void setListLimit(int listLimit);
}

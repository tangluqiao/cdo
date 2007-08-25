/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.db.IMapping;

/**
 * @author Eike Stepper
 */
public class VerticalMappingStrategy extends MappingStrategy
{
  private RootMapping rootMapping;

  public VerticalMappingStrategy()
  {
    throw new UnsupportedOperationException();
  }

  public String getType()
  {
    return "vertical";
  }

  public boolean hasEfficientTypeLookup()
  {
    return true;
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    return null;
  }

  public RootMapping getRootMapping()
  {
    if (rootMapping == null)
    {
      rootMapping = new RootMapping(this);
    }

    return rootMapping;
  }

  @Override
  protected IMapping createMapping(CDOClass cdoClass)
  {
    return new VerticalMapping(this, cdoClass);
  }
}

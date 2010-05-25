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
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class CDOIDAndVersionImpl implements CDOIDAndVersion
{
  private CDOID id;

  private int version;

  public CDOIDAndVersionImpl(CDOID id, int version)
  {
    this.id = id;
    this.version = version;
  }

  public CDOIDAndVersionImpl(CDODataInput in) throws IOException
  {
    id = in.readCDOID();
    version = in.readInt();
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOID(id);
    out.writeInt(version);
  }

  public CDOID getID()
  {
    return id;
  }

  public int getVersion()
  {
    return version;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOIDAndVersion)
    {
      CDOIDAndVersion that = (CDOIDAndVersion)obj;
      return id.equals(that.getID()) && version == that.getVersion();
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return id.hashCode() ^ version;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}v{1}", id, version); //$NON-NLS-1$
  }
}

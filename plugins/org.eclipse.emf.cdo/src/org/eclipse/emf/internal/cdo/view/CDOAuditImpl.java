/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOAudit;

import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOAuditImpl extends CDOViewImpl implements CDOAudit
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_AUDIT, CDOAuditImpl.class);

  private long timeStamp;

  /**
   * @since 2.0
   */
  public CDOAuditImpl(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  @Override
  public Type getViewType()
  {
    return Type.AUDIT;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  /**
   * @since 2.0
   */
  public void setTimeStamp(long timeStamp)
  {
    checkActive();
    if (this.timeStamp != timeStamp)
    {
      List<InternalCDOObject> invalidObjects = getInvalidObjects(timeStamp);
      boolean[] existanceFlags = getSession().getSessionProtocol().setAudit(getViewID(), timeStamp, invalidObjects);
      this.timeStamp = timeStamp;
      if (TRACER.isEnabled())
      {
        TRACER.format("Changed audit time: {0,date} {0,time}", timeStamp);
      }

      int i = 0;
      for (InternalCDOObject invalidObject : invalidObjects)
      {
        boolean existanceFlag = existanceFlags[i++];
        if (existanceFlag)
        {
          // --> PROXY
          CDOStateMachine.INSTANCE.invalidate(invalidObject, CDORevision.UNSPECIFIED_VERSION);
        }
        else
        {
          // --> DETACHED
          CDOStateMachine.INSTANCE.detachRemote(invalidObject);
        }
      }
    }
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    checkActive();
    InternalCDOSession session = getSession();
    int initialChunkSize = session.options().getCollectionLoadingPolicy().getInitialChunkSize();

    CDORevisionManager revisionManager = session.getRevisionManager();
    return (InternalCDORevision)revisionManager.getRevisionByTime(id, initialChunkSize, CDORevision.DEPTH_NONE,
        timeStamp, loadOnDemand);
  }

  @Override
  protected void prefetchRevisions(CDOID id, int depth, int initialChunkSize)
  {
    getSession().getRevisionManager().getRevisionByTime(id, initialChunkSize, depth, timeStamp);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOAudit({0})", getViewID()); //$NON-NLS-1$
  }
}

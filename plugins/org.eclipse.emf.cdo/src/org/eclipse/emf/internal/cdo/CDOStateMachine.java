/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=201266
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.protocol.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.protocol.util.TransportException;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ServerException;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.protocol.ResourceIDRequest;
import org.eclipse.emf.internal.cdo.protocol.VerifyRevisionRequest;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.fsm.FiniteStateMachine;
import org.eclipse.net4j.util.fsm.ITransition;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOStateMachine extends FiniteStateMachine<CDOState, CDOEvent, InternalCDOObject>
{
  // @Singleton
  public static final CDOStateMachine INSTANCE = new CDOStateMachine();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOStateMachine.class);

  private InternalCDOObject lastTracedObject;

  private CDOState lastTracedState;

  private CDOEvent lastTracedEvent;

  @SuppressWarnings("unchecked")
  private CDOStateMachine()
  {
    super(CDOState.class, CDOEvent.class);

    init(CDOState.TRANSIENT, CDOEvent.ATTACH, new PrepareAttachTransition());
    init(CDOState.TRANSIENT, CDOEvent.DETACH, FAIL);
    init(CDOState.TRANSIENT, CDOEvent.READ, IGNORE);
    init(CDOState.TRANSIENT, CDOEvent.WRITE, IGNORE);
    init(CDOState.TRANSIENT, CDOEvent.INVALIDATE, FAIL);
    init(CDOState.TRANSIENT, CDOEvent.RELOAD, IGNORE);
    init(CDOState.TRANSIENT, CDOEvent.COMMIT, FAIL);
    init(CDOState.TRANSIENT, CDOEvent.ROLLBACK, FAIL);

    init(CDOState.PREPARED_ATTACH, CDOEvent.ATTACH, new FinalizeAttachTransition());
    init(CDOState.PREPARED_ATTACH, CDOEvent.DETACH, FAIL);
    init(CDOState.PREPARED_ATTACH, CDOEvent.READ, IGNORE);
    init(CDOState.PREPARED_ATTACH, CDOEvent.WRITE, FAIL);
    init(CDOState.PREPARED_ATTACH, CDOEvent.INVALIDATE, FAIL);
    init(CDOState.PREPARED_ATTACH, CDOEvent.RELOAD, FAIL);
    init(CDOState.PREPARED_ATTACH, CDOEvent.COMMIT, FAIL);
    init(CDOState.PREPARED_ATTACH, CDOEvent.ROLLBACK, FAIL);

    init(CDOState.NEW, CDOEvent.ATTACH, FAIL);
    init(CDOState.NEW, CDOEvent.DETACH, FAIL);
    init(CDOState.NEW, CDOEvent.READ, IGNORE);
    init(CDOState.NEW, CDOEvent.WRITE, IGNORE);
    init(CDOState.NEW, CDOEvent.INVALIDATE, FAIL);
    init(CDOState.NEW, CDOEvent.RELOAD, FAIL);
    init(CDOState.NEW, CDOEvent.COMMIT, new CommitTransition());
    init(CDOState.NEW, CDOEvent.ROLLBACK, FAIL);

    init(CDOState.CLEAN, CDOEvent.ATTACH, FAIL);
    init(CDOState.CLEAN, CDOEvent.DETACH, FAIL);
    init(CDOState.CLEAN, CDOEvent.READ, IGNORE);
    init(CDOState.CLEAN, CDOEvent.WRITE, new WriteTransition());
    init(CDOState.CLEAN, CDOEvent.INVALIDATE, new InvalidateTransition());
    init(CDOState.CLEAN, CDOEvent.RELOAD, new ReloadTransition());
    init(CDOState.CLEAN, CDOEvent.COMMIT, FAIL);
    init(CDOState.CLEAN, CDOEvent.ROLLBACK, FAIL);

    init(CDOState.DIRTY, CDOEvent.ATTACH, FAIL);
    init(CDOState.DIRTY, CDOEvent.DETACH, FAIL);
    init(CDOState.DIRTY, CDOEvent.READ, IGNORE);
    init(CDOState.DIRTY, CDOEvent.WRITE, new RewriteTransition());
    init(CDOState.DIRTY, CDOEvent.INVALIDATE, new ConflictTransition());
    init(CDOState.DIRTY, CDOEvent.RELOAD, new ReloadTransition());
    init(CDOState.DIRTY, CDOEvent.COMMIT, new CommitTransition());
    init(CDOState.DIRTY, CDOEvent.ROLLBACK, new RollbackTransition());

    init(CDOState.PROXY, CDOEvent.ATTACH, new LoadResourceTransition());
    init(CDOState.PROXY, CDOEvent.DETACH, new DetachTransition());
    init(CDOState.PROXY, CDOEvent.READ, new LoadTransition(false));
    init(CDOState.PROXY, CDOEvent.WRITE, new LoadTransition(true));
    init(CDOState.PROXY, CDOEvent.INVALIDATE, IGNORE);
    init(CDOState.PROXY, CDOEvent.RELOAD, new ReloadTransition());
    init(CDOState.PROXY, CDOEvent.COMMIT, FAIL);
    init(CDOState.PROXY, CDOEvent.ROLLBACK, FAIL);

    init(CDOState.CONFLICT, CDOEvent.ATTACH, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.DETACH, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.READ, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.WRITE, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.INVALIDATE, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.RELOAD, FAIL);
    init(CDOState.CONFLICT, CDOEvent.COMMIT, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.ROLLBACK, new RollbackTransition());
  }

  public void attach(InternalCDOObject object, CDOResource resource, CDOViewImpl view)
  {
    ResourceAndView data = new ResourceAndView();
    data.resource = resource;
    data.view = view;

    // Phase 1: TRANSIENT --> PREPARED_ATTACH
    if (TRACER.isEnabled())
    {
      TRACER.format("ATTACH: {0} --> {1}", object, view);
    }

    process(object, CDOEvent.ATTACH, data);

    // Phase 2: PREPARED_ATTACH --> NEW
    if (TRACER.isEnabled())
    {
      TRACER.format("FINALIZE_ATTACH: {0} --> {1}", object, view);
    }

    process(object, CDOEvent.ATTACH, null);
  }

  public void detach(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.DETACH);
    }

    process(object, CDOEvent.DETACH, null);
  }

  public void read(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.READ);
    }

    process(object, CDOEvent.READ, null);
  }

  public void write(InternalCDOObject object)
  {
    write(object, null);
  }

  public void write(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.WRITE);
    }

    process(object, CDOEvent.WRITE, featureDelta);
  }

  public void reload(InternalCDOObject... objects)
  {
    CDOView view = null;
    Map<CDOID, InternalCDOObject> ids = new HashMap<CDOID, InternalCDOObject>();
    List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
    List<InternalCDORevision> revised = new ArrayList<InternalCDORevision>();
    for (InternalCDOObject object : objects)
    {
      CDOState state = object.cdoState();
      if (state != CDOState.TRANSIENT && state != CDOState.PREPARED_ATTACH && state != CDOState.NEW
          && state != CDOState.CONFLICT)
      {
        if (view == null)
        {
          view = object.cdoView();
        }

        InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
        if (revision.isCurrent())
        {
          revisions.add(revision);
        }
        else
        {
          revised.add(revision);
        }

        ids.put(object.cdoID(), object);
      }
    }

    if (view != null)
    {
      try
      {
        IChannel channel = view.getSession().getChannel();
        VerifyRevisionRequest request = new VerifyRevisionRequest(channel, revisions);
        revisions = request.send();
      }
      catch (Exception ex)
      {
        throw new TransportException(ex);
      }

      revisions.addAll(revised);
      for (InternalCDORevision revision : revisions)
      {
        InternalCDOObject object = ids.get(revision.getID());
        if (TRACER.isEnabled())
        {
          trace(object, CDOEvent.RELOAD);
        }

        process(object, CDOEvent.RELOAD, null);
      }
    }
  }

  public void invalidate(InternalCDOObject object, long timeStamp)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.INVALIDATE);
    }

    process(object, CDOEvent.INVALIDATE, timeStamp);
  }

  public void commit(InternalCDOObject object, CommitTransactionResult result)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.COMMIT);
    }

    process(object, CDOEvent.COMMIT, result);
  }

  public void rollback(InternalCDOObject object, boolean remote)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.ROLLBACK);
    }

    process(object, CDOEvent.ROLLBACK, remote);
  }

  @Override
  protected CDOState getState(InternalCDOObject object)
  {
    return object.cdoState();
  }

  @Override
  protected void setState(InternalCDOObject object, CDOState state)
  {
    object.cdoInternalSetState(state);
  }

  /**
   * Removes clutter from the trace log
   */
  private void trace(InternalCDOObject object, CDOEvent event)
  {
    CDOState state = object.cdoState();
    if (lastTracedObject != object || lastTracedState != state || lastTracedEvent != event)
    {
      TRACER.format("{0}: {1}", event, object.getClass().getName());
      lastTracedObject = object;
      lastTracedState = state;
      lastTracedEvent = event;
    }
  }

  @SuppressWarnings("unused")
  private void testAttach(InternalCDOObject object)
  {
    process(object, CDOEvent.ATTACH, null);
  }

  @SuppressWarnings("unused")
  private void testReload(InternalCDOObject object)
  {
    process(object, CDOEvent.RELOAD, null);
  }

  /**
   * @author Eike Stepper
   */
  private static final class ResourceAndView
  {
    public CDOResource resource;

    public CDOViewImpl view;

    @Override
    public String toString()
    {
      return MessageFormat.format("ResourceAndView({0}, {1})", resource, view);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class PrepareAttachTransition implements
      ITransition<CDOState, CDOEvent, InternalCDOObject, ResourceAndView>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, ResourceAndView data)
    {
      CDOTransactionImpl transaction = data.view.toTransaction();
      CDORevisionManagerImpl revisionManager = transaction.getSession().getRevisionManager();

      // Prepare object
      CDOID id = transaction.getNextTemporaryID();
      object.cdoInternalSetID(id);
      object.cdoInternalSetResource(data.resource);
      object.cdoInternalSetView(data.view);
      changeState(object, CDOState.PREPARED_ATTACH);

      // Create new revision
      InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil
          .create(revisionManager, object.cdoClass(), id);
      revision.setVersion(-1);
      revision.setResourceID(data.resource.cdoID());
      object.cdoInternalSetRevision(revision);

      // Register object
      data.view.registerObject(object);
      transaction.registerNew(object);

      // Attach content tree
      for (Iterator<InternalCDOObject> it = FSMUtil.iterator(object.eContents(), transaction); it.hasNext();)
      {
        InternalCDOObject content = it.next();
        INSTANCE.process(content, CDOEvent.ATTACH, data);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class FinalizeAttachTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object NULL)
    {
      CDOTransactionImpl transaction = (CDOTransactionImpl)object.cdoView();
      object.cdoInternalPostAttach();
      changeState(object, CDOState.NEW);

      // Finalize content tree
      for (Iterator<?> it = FSMUtil.iterator(object.eContents(), transaction); it.hasNext();)
      {
        InternalCDOObject content = (InternalCDOObject)it.next();
        INSTANCE.process(content, CDOEvent.ATTACH, null);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DetachTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object NULL)
    {
      // TODO Implement method DetachTransition.execute()
      throw new UnsupportedOperationException("Not yet implemented");
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class CommitTransition implements
      ITransition<CDOState, CDOEvent, InternalCDOObject, CommitTransactionResult>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, CommitTransactionResult data)
    {
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      Map<CDOID, CDOID> idMappings = data.getIDMappings();

      // Adjust object
      CDOID id = object.cdoID();
      CDOID newID = idMappings.get(id);
      if (newID != null)
      {
        object.cdoInternalSetID(newID);
        view.remapObject(id);
        id = newID;
      }

      // Adjust revision
      InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
      revision.setID(id);
      revision.setUntransactional();
      revision.setCreated(data.getTimeStamp());
      revision.adjustReferences(idMappings);

      CDORevisionManagerImpl revisionManager = view.getSession().getRevisionManager();
      revisionManager.addRevision(revision);

      changeState(object, CDOState.CLEAN);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class RollbackTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Boolean>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Boolean remote)
    {
      CDOViewImpl view = (CDOViewImpl)object.cdoView();

      // Adjust object
      CDOID id = object.cdoID();
      CDORevision transactionalRevision = object.cdoRevision();
      int version = transactionalRevision.getVersion();

      CDORevisionManagerImpl revisionManager = view.getSession().getRevisionManager();
      InternalCDORevision previousRevision = revisionManager.getRevisionByVersion(id, 0, version - 1);
      object.cdoInternalSetRevision(previousRevision);

      changeState(object, remote ? CDOState.PROXY : CDOState.CLEAN);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class WriteTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object featureDelta)
    {
      // Copy revision
      InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.copy(object.cdoRevision());
      revision.setTransactional();
      object.cdoInternalSetRevision(revision);

      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDOTransactionImpl transaction = view.toTransaction();
      transaction.registerDirty(object, (CDOFeatureDelta)featureDelta);
      changeState(object, CDOState.DIRTY);
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class RewriteTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object featureDelta)
    {
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDOTransactionImpl transaction = view.toTransaction();
      transaction.registerFeatureDelta(object, (CDOFeatureDelta)featureDelta);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ReloadTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object NULL)
    {
      changeState(object, CDOState.PROXY);
    }
  }

  /**
   * @author Eike Stepper
   */
  private class InvalidateTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Long>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Long timeStamp)
    {
      reviseObject(object, timeStamp);
      changeState(object, CDOState.PROXY);
    }

    protected void reviseObject(InternalCDOObject object, Long timeStamp)
    {
      InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
      revision.setRevised(timeStamp - 1);

      if (revision.isTransactional())
      {
        CDOViewImpl view = (CDOViewImpl)object.cdoView();
        InternalCDORevision sessionRevision = view.getRevision(object.cdoID());
        if (sessionRevision.getVersion() < revision.getVersion())
        {
          sessionRevision.setRevised(timeStamp - 1);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ConflictTransition extends InvalidateTransition
  {
    @Override
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Long timeStamp)
    {
      reviseObject(object, timeStamp);
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDOTransactionImpl transaction = view.toTransaction();
      transaction.setConflict(object);
      changeState(object, CDOState.CONFLICT);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class LoadTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    private boolean forWrite;

    public LoadTransition(boolean forWrite)
    {
      this.forWrite = forWrite;
    }

    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object NULL)
    {
      CDOID id = object.cdoID();
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      InternalCDORevision revision = view.getRevision(id);
      object.cdoInternalSetRevision(revision);
      changeState(object, CDOState.CLEAN);
      object.cdoInternalPostLoad();

      if (forWrite)
      {
        INSTANCE.write(object);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class LoadResourceTransition implements
      ITransition<CDOState, CDOEvent, InternalCDOObject, ResourceAndView>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, ResourceAndView data)
    {
      CDOID id = requestID(data.resource, data.view);
      if (id == CDOID.NULL)
      {
        throw new ServerException("Resource not available: " + data.resource.getPath());
      }

      // Prepare object
      object.cdoInternalSetID(id);
      object.cdoInternalSetResource(data.resource);
      object.cdoInternalSetView(data.view);

      // Register object
      data.view.registerObject(object);
    }

    private CDOID requestID(CDOResource resource, CDOViewImpl view)
    {
      try
      {
        String path = CDOUtil.extractResourcePath(resource.getURI());
        CDOSession session = view.getSession();

        IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
        ResourceIDRequest request = new ResourceIDRequest(session.getChannel(), path);
        return failOverStrategy.send(request);
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new TransportException(ex);
      }
    }
  }
}

/**
 * @author Eike Stepper
 */
enum CDOEvent
{
  ATTACH, DETACH, READ, WRITE, INVALIDATE, RELOAD, COMMIT, ROLLBACK
}

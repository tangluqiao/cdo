/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 230832
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheUtil;
import org.eclipse.emf.cdo.common.revision.cache.InternalCDORevisionCache;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.revision.RevisionResult;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDORevisionManagerImpl extends Lifecycle implements InternalCDORevisionManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionManagerImpl.class);

  private boolean supportingBranches;

  private RevisionLoader revisionLoader;

  private RevisionLocker revisionLocker;

  private CDORevisionFactory factory;

  private InternalCDORevisionCache cache;

  @ExcludeFromDump
  private Object loadAndAddLock = new Object();

  @ExcludeFromDump
  private Object revisedLock = new Object();

  public CDORevisionManagerImpl()
  {
  }

  public boolean isSupportingBranches()
  {
    return supportingBranches;
  }

  public void setSupportingBranches(boolean on)
  {
    checkInactive();
    supportingBranches = on;
  }

  public RevisionLoader getRevisionLoader()
  {
    return revisionLoader;
  }

  public void setRevisionLoader(RevisionLoader revisionLoader)
  {
    checkInactive();
    this.revisionLoader = revisionLoader;
  }

  public RevisionLocker getRevisionLocker()
  {
    return revisionLocker;
  }

  public void setRevisionLocker(RevisionLocker revisionLocker)
  {
    checkInactive();
    this.revisionLocker = revisionLocker;
  }

  public CDORevisionFactory getFactory()
  {
    return factory;
  }

  public void setFactory(CDORevisionFactory factory)
  {
    checkInactive();
    this.factory = factory;
  }

  public InternalCDORevisionCache getCache()
  {
    return cache;
  }

  public void setCache(CDORevisionCache cache)
  {
    checkInactive();
    this.cache = (InternalCDORevisionCache)cache;
  }

  public EClass getObjectType(CDOID id)
  {
    return cache.getObjectType(id);
  }

  public boolean containsRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    if (supportingBranches)
    {
      return getRevision(id, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, false, null) != null;
    }

    return getCachedRevision(id, branchPoint) != null;
  }

  public boolean containsRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    return cache.getRevisionByVersion(id, branchVersion) != null;
  }

  public void reviseLatest(CDOID id, CDOBranch branch)
  {
    acquireAtomicRequestLock(revisedLock);

    try
    {
      InternalCDORevision revision = (InternalCDORevision)cache.getRevision(id, branch.getHead());
      if (revision != null)
      {
        cache.removeRevision(id, branch.getVersion(revision.getVersion()));
      }
    }
    finally
    {
      releaseAtomicRequestLock(revisedLock);
    }
  }

  public void reviseVersion(CDOID id, CDOBranchVersion branchVersion, long timeStamp)
  {
    acquireAtomicRequestLock(revisedLock);

    try
    {
      InternalCDORevision revision = getCachedRevisionByVersion(id, branchVersion);
      if (revision != null)
      {
        if (timeStamp == CDORevision.UNSPECIFIED_DATE)
        {
          cache.removeRevision(id, branchVersion);
        }
        else
        {
          revision.setRevised(timeStamp - 1);
        }
      }
    }
    finally
    {
      releaseAtomicRequestLock(revisedLock);
    }
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk,
      boolean loadOnDemand)
  {
    checkArg(branchVersion.getVersion() > 0, "Invalid version");
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      InternalCDORevision revision = getCachedRevisionByVersion(id, branchVersion);
      if (revision == null)
      {
        if (loadOnDemand)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Loading revision {0} from {1}", id, branchVersion); //$NON-NLS-1$
          }

          revision = revisionLoader.loadRevisionByVersion(id, branchVersion, referenceChunk);
          addRevision(revision);
        }
      }

      return revision;
    }
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
  }

  public CDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth,
      boolean loadOnDemand)
  {
    return getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, null);
  }

  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth,
      boolean loadOnDemand, RevisionResult[] result)
  {
    List<CDOID> ids = Collections.singletonList(id);
    List<CDORevision> revisions = getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, result);
    return (InternalCDORevision)revisions.get(0);
  }

  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk,
      int prefetchDepth, boolean loadOnDemand)
  {
    return getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, null);
  }

  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk,
      int prefetchDepth, boolean loadOnDemand, RevisionResult[] results)
  {
    RevisionInfo[] infos = new RevisionInfo[ids.size()];
    List<RevisionInfo> infosToLoad = createRevisionInfos(ids, branchPoint, loadOnDemand, infos);
    if (infosToLoad != null)
    {
      loadRevisions(infosToLoad, branchPoint, referenceChunk, prefetchDepth);
    }

    return getRevisionAndResults(infos, results);
  }

  private List<RevisionInfo> createRevisionInfos(List<CDOID> ids, CDOBranchPoint branchPoint, boolean loadOnDemand,
      RevisionInfo[] infos)
  {
    List<RevisionInfo> infosToLoad = null;
    Iterator<CDOID> idIterator = ids.iterator();
    for (int i = 0; i < infos.length; i++)
    {
      CDOID id = idIterator.next();
      RevisionInfo info = createRevisionInfo(id, branchPoint);
      infos[i] = info;

      if (info.isLoadNeeded() && loadOnDemand)
      {
        if (infosToLoad == null)
        {
          infosToLoad = new ArrayList<RevisionInfo>(1);
        }

        infosToLoad.add(info);
      }
    }

    return infosToLoad;
  }

  private RevisionInfo createRevisionInfo(CDOID id, CDOBranchPoint branchPoint)
  {
    InternalCDORevision revision = getCachedRevision(id, branchPoint);
    if (revision != null)
    {
      return createRevisionInfoAvailable(revision, branchPoint);
    }

    if (supportingBranches)
    {
      revision = getCachedRevisionRecursively(id, branchPoint);
      if (revision != null)
      {
        return createRevisionInfoAvailable(revision, branchPoint);
      }
    }

    return createRevisionInfoMissing(id, branchPoint);
  }

  private RevisionInfo.Available createRevisionInfoAvailable(InternalCDORevision revision,
      CDOBranchPoint requestedBranchPoint)
  {
    if (revision instanceof PointerCDORevision)
    {
      PointerCDORevision pointer = (PointerCDORevision)revision;
      InternalCDORevision target = getCachedRevisionByVersion(pointer.getID(), pointer.getTarget());
      return new RevisionInfo.Available.Pointer(pointer.getID(), requestedBranchPoint, pointer, target);
    }

    if (revision instanceof DetachedCDORevision)
    {
      DetachedCDORevision detached = (DetachedCDORevision)revision;
      return new RevisionInfo.Available.Detached(detached.getID(), requestedBranchPoint, detached);
    }

    return new RevisionInfo.Available.Normal(revision.getID(), requestedBranchPoint, revision);
  }

  private RevisionInfo.Missing createRevisionInfoMissing(CDOID id, CDOBranchPoint requestedBranchPoint)
  {
    if (requestedBranchPoint.getBranch().isMainBranch())
    {
      return new RevisionInfo.Missing.MainBranch(id, requestedBranchPoint);
    }

    return new RevisionInfo.Missing.SubBranch(id, requestedBranchPoint);
  }

  private void loadRevisions(List<RevisionInfo> infosToLoad, CDOBranchPoint branchPoint, int referenceChunk,
      int prefetchDepth)
  {
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      revisionLoader.loadRevisions(infosToLoad, branchPoint, referenceChunk, prefetchDepth);
      //
      // CDOBranch branch = branchPoint.getBranch();
      //
      // Iterator<MissingRevisionInfo> itInfo = missingInfos.iterator();
      // Iterator<InternalCDORevision> it = missingRevisions.iterator();
      // for (int i = 0; i < revisions.size(); i++)
      // {
      // InternalCDORevision revision = (InternalCDORevision)revisions.get(i);
      // if (revision == null)
      // {
      // MissingRevisionInfo info = itInfo.next();
      // revision = it.next();
      //
      // revisions.set(i, revision);
      // addRevision(revision);
      //
      // long revised = info.getRevised();
      // if (revisedPointers != null)
      // {
      // revisedPointers.put(revision, revised);
      // }
      //
      // if (revision.getBranch() != branch && info.getType() == MissingRevisionInfo.Type.EXACTLY_KNOWN)
      // {
      // PointerCDORevision pointer = new PointerCDORevision(info.getID(), branch);
      // pointer.setTarget(revision);
      // pointer.setRevised(revised);
      // addRevision(pointer);
      // }
      // }
      //
      // if (revision instanceof PointerCDORevision)
      // {
      // revisions.set(i, null);
      // }
      // }
    }
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
  }

  private List<CDORevision> getRevisionAndResults(RevisionInfo[] infos, RevisionResult[] results)
  {
    List<CDORevision> revisions = new ArrayList<CDORevision>(infos.length);
    for (int i = 0; i < infos.length; i++)
    {
      RevisionInfo info = infos[i];
      revisions.add(info.getRevision());

      if (results != null)
      {
        results[i] = info.getResult();
      }
    }

    return revisions;
  }

  public boolean addRevision(CDORevision revision)
  {
    if (revision != null)
    {
      boolean added = cache.addRevision(revision);
      if (added)
      {
        int oldVersion = revision.getVersion() - 1;
        if (oldVersion >= CDORevision.UNSPECIFIED_VERSION)
        {
          CDOBranchVersion old = CDOBranchUtil.createBranchVersion(revision.getBranch(), oldVersion);
          InternalCDORevision oldRevision = getCachedRevisionByVersion(revision.getID(), old);
          if (oldRevision != null)
          {
            oldRevision.setRevised(revision.getTimeStamp() - 1);
          }

          return true;
        }
      }
    }

    return false;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (factory == null)
    {
      factory = CDORevisionFactory.DEFAULT;
    }

    if (cache == null)
    {
      cache = (InternalCDORevisionCache)CDORevisionCacheUtil.createDefaultCache(supportingBranches);
    }

    if (supportingBranches && !cache.isSupportingBranches())
    {
      throw new IllegalStateException("Revision cache does not support branches");
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    LifecycleUtil.activate(cache);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(cache);
    super.doDeactivate();
  }

  private void acquireAtomicRequestLock(Object key)
  {
    if (revisionLocker != null)
    {
      revisionLocker.acquireAtomicRequestLock(key);
    }
  }

  private void releaseAtomicRequestLock(Object key)
  {
    if (revisionLocker != null)
    {
      revisionLocker.releaseAtomicRequestLock(key);
    }
  }

  private InternalCDORevision getCachedRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    return (InternalCDORevision)cache.getRevisionByVersion(id, branchVersion);
  }

  private InternalCDORevision getCachedRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    return (InternalCDORevision)cache.getRevision(id, branchPoint);
  }

  private InternalCDORevision getCachedRevisionRecursively(CDOID id, CDOBranchPoint branchPoint)
  {
    CDOBranch branch = branchPoint.getBranch();
    if (!branch.isMainBranch())
    {
      CDOBranchPoint base = branch.getBase();
      InternalCDORevision revision = getCachedRevision(id, base);
      if (revision != null)
      {
        return revision;
      }

      // Recurse
      return getCachedRevisionRecursively(id, base);
    }

    // Reached main branch
    return null;
  }
}

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
package org.eclipse.emf.cdo.internal.server.clone;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.collection.IndexedList;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CloneRepository extends Repository.Default implements CDOReplicationContext
{
  private CloneSynchronizer synchronizer;

  private InternalSession replicatorSession;

  private int lastReplicatedBranchID;

  private long lastReplicatedCommitTime;

  private int lastTransactionID;

  public CloneRepository()
  {
    setState(State.OFFLINE);
  }

  @Override
  public Type getType()
  {
    return Type.CLONE;
  }

  public CloneSynchronizer getSynchronizer()
  {
    return synchronizer;
  }

  public void setSynchronizer(CloneSynchronizer synchronizer)
  {
    checkInactive();
    this.synchronizer = synchronizer;
  }

  @Override
  public Object[] getElements()
  {
    List<Object> list = Arrays.asList(super.getElements());
    list.add(synchronizer);
    return list.toArray();
  }

  public int getLastReplicatedBranchID()
  {
    return lastReplicatedBranchID;
  }

  public long getLastReplicatedCommitTime()
  {
    return lastReplicatedCommitTime;
  }

  public void handleBranch(CDOBranch branch)
  {
    int branchID = branch.getID();
    String name = branch.getName();

    CDOBranchPoint base = branch.getBase();
    InternalCDOBranch baseBranch = (InternalCDOBranch)base.getBranch();
    long baseTimeStamp = base.getTimeStamp();

    InternalCDOBranchManager branchManager = getBranchManager();
    branchManager.createBranch(branchID, name, baseBranch, baseTimeStamp);
    lastReplicatedBranchID = branchID;
  }

  public void handleCommitInfo(CDOCommitInfo commitInfo)
  {
    long timeStamp = commitInfo.getTimeStamp();
    CDOBranchPoint head = commitInfo.getBranch().getHead();

    InternalTransaction transaction = replicatorSession.openTransaction(++lastTransactionID, head);
    ReplicatorCommitContext commitContext = new ReplicatorCommitContext(transaction, commitInfo);
    commitContext.preWrite();
    boolean success = false;

    try
    {
      commitContext.write(new Monitor());
      commitContext.commit(new Monitor());

      setLastCommitTimeStamp(timeStamp);
      lastReplicatedCommitTime = timeStamp;
      success = true;
    }
    finally
    {
      commitContext.postCommit(success);
      transaction.close();
    }
  }

  @Override
  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    return new WriteThroughCommitContext(transaction);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(synchronizer, "synchronizer"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    replicatorSession = getSessionManager().openSession(null);
    replicatorSession.options().setPassiveUpdateEnabled(false);

    synchronizer.setClone(this);
    synchronizer.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronizer.deactivate();
    super.doDeactivate();
  }

  @Override
  protected void initRootResource()
  {
    setState(State.INITIAL);
  }

  /**
   * @author Eike Stepper
   */
  private final class WriteThroughCommitContext extends TransactionCommitContext
  {
    public WriteThroughCommitContext(InternalTransaction transaction)
    {
      super(transaction);
    }

    @Override
    public void write(OMMonitor monitor)
    {
      // Do nothing
    }

    @Override
    public void commit(OMMonitor monitor)
    {
      CDOBranch branch = getTransaction().getBranch();
      String userID = getUserID();
      String comment = getCommitComment();
      CDOCommitData commitData = new CommitData();

      InternalCDOSession master = (InternalCDOSession)synchronizer.getMaster();
      CDOSessionProtocol sessionProtocol = master.getSessionProtocol();
      CommitTransactionResult result = sessionProtocol.commitDelegation(branch, userID, comment, commitData, monitor);

      String rollbackMessage = result.getRollbackMessage();
      if (rollbackMessage != null)
      {
        throw new TransactionException(rollbackMessage);
      }

      long timeStamp = result.getTimeStamp();
      setTimeStamp(timeStamp);

      for (CDOPackageUnit newPackageUnit : commitData.getNewPackageUnits())
      {
        for (CDOPackageInfo packageInfo : newPackageUnit.getPackageInfos())
        {
          addMetaIDRange(packageInfo.getMetaIDRange());
        }
      }

      Map<CDOID, CDOID> idMappings = result.getIDMappings();
      for (Map.Entry<CDOID, CDOID> idMapping : idMappings.entrySet())
      {
        CDOID oldID = idMapping.getKey();
        CDOID newID = idMapping.getValue();
        addIDMapping(oldID, newID);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class CommitData implements CDOCommitData
    {
      public List<CDOPackageUnit> getNewPackageUnits()
      {
        final InternalCDOPackageUnit[] newPackageUnits = WriteThroughCommitContext.this.getNewPackageUnits();
        return new IndexedList<CDOPackageUnit>()
        {
          @Override
          public CDOPackageUnit get(int index)
          {
            return newPackageUnits[index];
          }

          @Override
          public int size()
          {
            return newPackageUnits.length;
          }
        };
      }

      public List<CDOIDAndVersion> getNewObjects()
      {
        final InternalCDORevision[] newObjects = WriteThroughCommitContext.this.getNewObjects();
        return new IndexedList<CDOIDAndVersion>()
        {
          @Override
          public CDOIDAndVersion get(int index)
          {
            return newObjects[index];
          }

          @Override
          public int size()
          {
            return newObjects.length;
          }
        };
      }

      public List<CDORevisionKey> getChangedObjects()
      {
        final InternalCDORevisionDelta[] changedObjects = getDirtyObjectDeltas();
        return new IndexedList<CDORevisionKey>()
        {
          @Override
          public CDORevisionKey get(int index)
          {
            return changedObjects[index];
          }

          @Override
          public int size()
          {
            return changedObjects.length;
          }
        };
      }

      public List<CDOIDAndVersion> getDetachedObjects()
      {
        final CDOID[] detachedObjects = WriteThroughCommitContext.this.getDetachedObjects();
        return new IndexedList<CDOIDAndVersion>()
        {
          @Override
          public CDOIDAndVersion get(int index)
          {
            return CDOIDUtil.createIDAndVersion(detachedObjects[index], CDOBranchVersion.UNSPECIFIED_VERSION);
          }

          @Override
          public int size()
          {
            return detachedObjects.length;
          }
        };
      }
    }
  }
}

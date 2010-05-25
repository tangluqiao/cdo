/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444
 *    Christopher Albert - 254455: [DB] Support FeatureMaps https://bugs.eclipse.org/bugs/show_bug.cgi?id=254455
 *    Victor Roldan Betancort - 289360: [DB] [maintenance] Support FeatureMaps
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * This is a featuremap-to-table mapping optimized for non-audit-mode. It doesn't care about version and has delta
 * support.
 * 
 * @author Eike Stepper
 */
public class NonAuditFeatureMapTableMapping extends AbstractFeatureMapTableMapping implements IListMapping,
    IListMappingDeltaSupport
{
  private static final FieldInfo[] KEY_FIELDS = { new FieldInfo(CDODBSchema.FEATUREMAP_REVISION_ID, DBType.BIGINT) };

  private static final int TEMP_INDEX = -1;

  private static final int UNBOUNDED_MOVE = -1;

  private String sqlClear;

  private String sqlUpdateIndex;

  private String sqlUpdateValue;

  private String sqlDeleteItem;

  private String sqlMoveDownWithLimit;

  private String sqlMoveDown;

  private String sqlMoveUpWithLimit;

  private String sqlMoveUp;

  public NonAuditFeatureMapTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);

    initSqlStrings();
  }

  private void initSqlStrings()
  {
    // TODO: add key fields length support

    StringBuilder builder = new StringBuilder();

    // ----------- clear list -------------------------

    builder.append("DELETE FROM "); //$NON-NLS-1$
    builder.append(getTable().getName());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append(" = ? "); //$NON-NLS-1$

    sqlClear = builder.toString();

    builder.append(" AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(" = ? "); //$NON-NLS-1$

    sqlDeleteItem = builder.toString();

    // ----------- update one item index --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable().getName());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(" = ? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append(" = ? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(" = ? "); //$NON-NLS-1$
    sqlUpdateIndex = builder.toString();

    // ----------- update one item value --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable().getName());
    builder.append(" SET "); //$NON-NLS-1$

    builder.append(CDODBSchema.FEATUREMAP_TAG);
    builder.append(" = ?,"); //$NON-NLS-1$

    Iterator<String> iter = getColumnNames().iterator();
    while (iter.hasNext())
    {
      String column = iter.next();
      builder.append(column);
      builder.append(" = ?"); //$NON-NLS-1$

      if (iter.hasNext())
      {
        builder.append(", "); //$NON-NLS-1$
      }
    }

    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append(" = ? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(" = ? "); //$NON-NLS-1$
    sqlUpdateValue = builder.toString();

    // ----------- move down --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable().getName());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(" = "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("-1 WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("= ? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(" > ? "); //$NON-NLS-1$
    sqlMoveDown = builder.toString();

    builder.append(" AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(" <= ?"); //$NON-NLS-1$
    sqlMoveDownWithLimit = builder.toString();

    // ----------- move up --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable().getName());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(" = "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("+1 WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("= ? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(" >= ? "); //$NON-NLS-1$
    sqlMoveUp = builder.toString();

    builder.append(" AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(" < ?"); //$NON-NLS-1$
    sqlMoveUpWithLimit = builder.toString();
  }

  @Override
  protected FieldInfo[] getKeyFields()
  {
    return KEY_FIELDS;
  }

  @Override
  protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    stmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
  }

  public void objectRevised(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    clearList(accessor, id);
  }

  /**
   * Clear a list of a given revision.
   * 
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision from which to remove all items
   */
  public void clearList(IDBStoreAccessor accessor, CDOID id)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlClear, ReuseProbability.HIGH);
      stmt.setLong(1, CDOIDUtil.getLong(id));
      CDODBUtil.sqlUpdate(stmt, false);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  /**
   * Insert a list item at a specified position.
   * 
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision to insert the value
   * @param index
   *          the index where to insert the element
   * @param value
   *          the value to insert.
   */
  public void insertListItem(IDBStoreAccessor accessor, CDOID id, int index, Object value)
  {
    move1up(accessor, id, index, UNBOUNDED_MOVE);
    insertValue(accessor, id, index, value);
  }

  private void insertValue(IDBStoreAccessor accessor, CDOID id, int index, Object value)
  {
    PreparedStatement stmt = null;

    try
    {
      FeatureMap.Entry entry = (FeatureMap.Entry)value;
      EStructuralFeature entryFeature = entry.getEStructuralFeature();
      Long tag = getTagByFeature(entryFeature);
      String column = getColumnName(tag);

      String sql = sqlInsert;
      stmt = accessor.getStatementCache().getPreparedStatement(sql, ReuseProbability.HIGH);
      stmt.setLong(1, CDOIDUtil.getLong(id));
      int stmtIndex = getKeyFields().length + 1;

      for (int i = 0; i < getColumnNames().size(); i++)
      {
        if (getColumnNames().get(i).equals(column))
        {
          getTypeMapping(tag).setValue(stmt, stmtIndex++, entry.getValue());
        }
        else
        {
          stmt.setNull(stmtIndex++, getDBTypes().get(i).getCode());
        }
      }

      stmt.setInt(stmtIndex++, index);
      stmt.setLong(stmtIndex++, tag);
      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  /**
   * Move a list item from one position to another. Indices between both positions are updated so that the list remains
   * consistent.
   * 
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision in which to move the item
   * @param oldPosition
   *          the old position of the item.
   * @param newPosition
   *          the new position of the item.
   */
  public void moveListItem(IDBStoreAccessor accessor, CDOID id, int oldPosition, int newPosition)
  {
    if (oldPosition == newPosition)
    {
      return;
    }

    // move element away temporarily
    updateOneIndex(accessor, id, oldPosition, TEMP_INDEX);

    // move elements in between
    if (oldPosition < newPosition)
    {
      move1down(accessor, id, oldPosition, newPosition);
    }
    else
    {
      // oldPosition > newPosition -- equal case is handled above
      move1up(accessor, id, newPosition, oldPosition);
    }

    // move temporary element to new position
    updateOneIndex(accessor, id, TEMP_INDEX, newPosition);
  }

  private void updateOneIndex(IDBStoreAccessor accessor, CDOID id, int oldIndex, int newIndex)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlUpdateIndex, ReuseProbability.HIGH);
      stmt.setInt(1, newIndex);
      stmt.setLong(2, CDOIDUtil.getLong(id));
      stmt.setInt(3, oldIndex);
      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  /**
   * Remove a list item from a specified a position.
   * 
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision from which to remove the item
   * @param index
   *          the index of the item to remove
   */
  public void removeListItem(IDBStoreAccessor accessor, CDOID id, int index)
  {
    deleteItem(accessor, id, index);
    move1down(accessor, id, index, UNBOUNDED_MOVE);
  }

  /**
   * Move references downwards to close a gap at position <code>index</code>. Only indexes starting with
   * <code>index + 1</code> and ending with <code>upperIndex</code> are moved down.
   */
  private void move1down(IDBStoreAccessor accessor, CDOID id, int index, int upperIndex)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(
          upperIndex == UNBOUNDED_MOVE ? sqlMoveDown : sqlMoveDownWithLimit, ReuseProbability.HIGH);

      stmt.setLong(1, CDOIDUtil.getLong(id));
      stmt.setInt(2, index);
      if (upperIndex != UNBOUNDED_MOVE)
      {
        stmt.setInt(3, upperIndex);
      }

      CDODBUtil.sqlUpdate(stmt, false);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  /**
   * Move references downwards to close a gap at position <code>index</code>. Only indexes starting with
   * <code>index + 1</code> and ending with <code>upperIndex</code> are moved down.
   */
  private void move1up(IDBStoreAccessor accessor, CDOID id, int index, int upperIndex)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(
          upperIndex == UNBOUNDED_MOVE ? sqlMoveUp : sqlMoveUpWithLimit, ReuseProbability.HIGH);
      stmt.setLong(1, CDOIDUtil.getLong(id));
      stmt.setInt(2, index);
      if (upperIndex != UNBOUNDED_MOVE)
      {
        stmt.setInt(3, upperIndex);
      }

      CDODBUtil.sqlUpdate(stmt, false);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  private void deleteItem(IDBStoreAccessor accessor, CDOID id, int index)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlDeleteItem, ReuseProbability.HIGH);
      stmt.setLong(1, CDOIDUtil.getLong(id));
      stmt.setInt(2, index);
      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  /**
   * Set a value at a specified position to the given value.
   * 
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision to set the value
   * @param index
   *          the index of the item to set
   * @param value
   *          the value to be set.
   */
  public void setListItem(IDBStoreAccessor accessor, CDOID id, int index, Object value)
  {
    PreparedStatement stmt = null;

    FeatureMap.Entry entry = (FeatureMap.Entry)value;
    EStructuralFeature entryFeature = entry.getEStructuralFeature();
    Long tag = getTagByFeature(entryFeature);
    String column = getColumnName(tag);
    ITypeMapping mapping = getTypeMapping(tag);

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlUpdateValue, ReuseProbability.HIGH);
      stmt.setLong(1, tag);
      int colIndex = 2;

      for (int i = 0; i < getColumnNames().size(); i++)
      {
        String columnName = getColumnNames().get(i);
        if (columnName.equals(column))
        {
          mapping.setValue(stmt, colIndex++, entry.getValue());
        }
        else
        {
          stmt.setNull(colIndex++, getDBTypes().get(i).getCode());
        }
      }

      stmt.setLong(colIndex++, CDOIDUtil.getLong(id));
      stmt.setInt(colIndex++, index);
      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  public void processDelta(final IDBStoreAccessor accessor, final CDOID id, int oldVersion, final int newVersion,
      long created, CDOListFeatureDelta listDelta)
  {
    CDOFeatureDeltaVisitor visitor = new CDOFeatureDeltaVisitor()
    {
      public void visit(CDOMoveFeatureDelta delta)
      {
        moveListItem(accessor, id, delta.getOldPosition(), delta.getNewPosition());
      }

      public void visit(CDOAddFeatureDelta delta)
      {
        insertListItem(accessor, id, delta.getIndex(), delta.getValue());
      }

      public void visit(CDORemoveFeatureDelta delta)
      {
        removeListItem(accessor, id, delta.getIndex());
      }

      public void visit(CDOSetFeatureDelta delta)
      {
        setListItem(accessor, id, delta.getIndex(), delta.getValue());
      }

      public void visit(CDOUnsetFeatureDelta delta)
      {
        throw new ImplementationError("Should not be called"); //$NON-NLS-1$
      }

      public void visit(CDOListFeatureDelta delta)
      {
        throw new ImplementationError("Should not be called"); //$NON-NLS-1$
      }

      public void visit(CDOClearFeatureDelta delta)
      {
        clearList(accessor, id);
      }

      public void visit(CDOContainerFeatureDelta delta)
      {
        throw new ImplementationError("Should not be called"); //$NON-NLS-1$
      }
    };

    for (CDOFeatureDelta delta : listDelta.getListChanges())
    {
      delta.accept(visitor);
    }
  }
}

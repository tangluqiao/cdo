/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 213402
 *    Martin Taal - Added subtype handling and EClass conversion, bug 283106
 */
package org.eclipse.emf.cdo.common.id;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOID.Type;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndBranchImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaRangeImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectLongImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectLongWithClassifierImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectStringImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectStringWithClassifierImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectUUIDImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectImpl;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOID;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDLong;
import org.eclipse.emf.cdo.spi.common.id.InternalCDOIDObject;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOIDUtil
{
  private CDOIDUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static boolean isNull(CDOID id)
  {
    return id == null || id.isNull();
  }

  public static long getLong(CDOID id)
  {
    if (id == null)
    {
      return AbstractCDOIDLong.NULL_VALUE;
    }

    switch (id.getType())
    {
    case NULL:
      return AbstractCDOIDLong.NULL_VALUE;

    case OBJECT:
      if (id instanceof AbstractCDOIDLong)
      {
        return ((AbstractCDOIDLong)id).getLongValue();
      }

      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.0"), id.getClass().getName())); //$NON-NLS-1$

    case META:
      return ((CDOIDMeta)id).getLongValue();

    case TEMP_META:
    case TEMP_OBJECT:
      throw new IllegalArgumentException(Messages.getString("CDOIDUtil.1")); //$NON-NLS-1$

    case EXTERNAL_OBJECT:
    case EXTERNAL_TEMP_OBJECT:
      throw new IllegalArgumentException(Messages.getString("CDOIDUtil.2")); //$NON-NLS-1$

    default:
      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.3"), id.getClass().getName())); //$NON-NLS-1$
    }
  }

  /**
   * @since 3.0
   */
  public static CDOClassifierRef getClassifierRef(CDOID id)
  {
    if (id instanceof CDOClassifierRef.Provider)
    {
      return ((CDOClassifierRef.Provider)id).getClassifierRef();
    }

    return null;
  }

  public static CDOIDTemp createTempMeta(int value)
  {
    return new CDOIDTempMetaImpl(value);
  }

  public static CDOIDTemp createTempObject(int value)
  {
    return new CDOIDTempObjectImpl(value);
  }

  /**
   * @since 3.0
   */
  public static CDOIDExternal createTempObjectExternal(String uri)
  {
    return new CDOIDTempObjectExternalImpl(uri);
  }

  public static CDOID createLong(long value)
  {
    if (value == AbstractCDOIDLong.NULL_VALUE)
    {
      return CDOID.NULL;
    }

    return new CDOIDObjectLongImpl(value);
  }

  /**
   * @since 3.0
   */
  public static CDOID createStringWithClassifier(CDOClassifierRef classifierRef, String value)
  {
    return new CDOIDObjectStringWithClassifierImpl(classifierRef, value);
  }

  /**
   * @since 3.0
   */
  public static CDOID createLongWithClassifier(CDOClassifierRef classifierRef, long value)
  {
    return new CDOIDObjectLongWithClassifierImpl(classifierRef, value);
  }

  /**
   * @since 2.0
   */
  public static CDOIDExternal createExternal(String uri)
  {
    return new CDOIDExternalImpl(uri);
  }

  public static CDOIDMeta createMeta(long value)
  {
    return new CDOIDMetaImpl(value);
  }

  public static CDOIDMetaRange createMetaRange(CDOID lowerBound, int count)
  {
    return new CDOIDMetaRangeImpl(lowerBound, count);
  }

  public static CDOIDAndVersion createIDAndVersion(CDOID id, int version)
  {
    return new CDOIDAndVersionImpl(id, version);
  }

  /**
   * @since 3.0
   */
  public static CDOIDAndVersion createIDAndVersion(CDOIDAndVersion source)
  {
    return createIDAndVersion(source.getID(), source.getVersion());
  }

  /**
   * @since 3.0
   */
  public static CDOIDAndBranch createIDAndBranch(CDOID id, CDOBranch branch)
  {
    return new CDOIDAndBranchImpl(id, branch);
  }

  /**
   * Creates the correct implementation class for the passed {@link CDOID.ObjectType}.
   * 
   * @param subType
   *          the subType for which to create an empty CDOID instance
   * @return the instance of CDOIDObject which represents the subtype.
   * @since 3.0
   */
  public static AbstractCDOID createCDOIDObject(CDOID.ObjectType subType)
  {
    if (subType == null)
    {
      throw new IllegalArgumentException("SubType may not be null");
    }

    InternalCDOIDObject id;
    switch (subType)
    {
    case LONG:
      id = new CDOIDObjectLongImpl();
      break;

    case STRING:
      id = new CDOIDObjectStringImpl();
      break;

    case LONG_WITH_CLASSIFIER:
      id = new CDOIDObjectLongWithClassifierImpl();
      break;

    case STRING_WITH_CLASSIFIER:
      id = new CDOIDObjectStringWithClassifierImpl();
      break;

    case UUID:
      id = new CDOIDObjectUUIDImpl();
      break;

    default:
      throw new IllegalArgumentException("Subtype " + subType.name() + " not supported");
    }

    if (id.getSubType() != subType)
    {
      throw new IllegalStateException("Subtype of created id " + id + " is unequal (" + id.getSubType().name()
          + ") to requested subtype " + subType.name());
    }

    return (AbstractCDOID)id;
  }

  /**
   * Format of the uri fragment.
   * <p>
   * Non-legacy: <code>&lt;ID TYPE>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * <p>
   * Legacy: <code>&lt;ID TYPE>/&lt;PACKAGE URI>/&lt;CLASSIFIER ID>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * 
   * @since 2.0
   */
  public static void write(StringBuilder builder, CDOID id)
  {
    if (id == null)
    {
      id = CDOID.NULL;
    }

    if (id instanceof InternalCDOIDObject)
    {
      ObjectType subType = ((InternalCDOIDObject)id).getSubType();
      builder.append(subType.getID());
    }
    else
    {
      Type type = id.getType();
      builder.append(type.getID());
    }

    builder.append(id.toURIFragment());
  }

  /**
   * Format of the URI fragment.
   * <p>
   * Non-legacy: <code>&lt;ID TYPE>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * <p>
   * Legacy: <code>&lt;ID TYPE>/&lt;PACKAGE URI>/&lt;CLASSIFIER ID>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * 
   * @since 3.0
   */
  public static CDOID read(String uriFragment)
  {
    char typeID = uriFragment.charAt(0);
    Enum<?> literal = CDOID.Type.getLiteral(typeID);
    if (literal == null)
    {
      throw new ImplementationError("Unknown type ID: " + typeID);
    }

    String fragment = uriFragment.substring(1);
    if (literal instanceof ObjectType)
    {
      return readCDOIDObject((ObjectType)literal, fragment);
    }

    Type type = (Type)literal;
    switch (type)
    {
    case NULL:
      return CDOID.NULL;

    case TEMP_OBJECT:
      return new CDOIDTempObjectImpl(Integer.valueOf(fragment));

    case TEMP_META:
      return new CDOIDTempMetaImpl(Integer.valueOf(fragment));

    case META:
      return new CDOIDMetaImpl(Long.valueOf(fragment));

    case EXTERNAL_OBJECT:
      return new CDOIDExternalImpl(fragment);

    case EXTERNAL_TEMP_OBJECT:
      return new CDOIDTempObjectExternalImpl(fragment);

    case OBJECT:
    {
      // Normally this case should not occur (is an OBJECT subtype).
      throw new ImplementationError();
    }

    default:
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOIDUtil.5"), uriFragment)); //$NON-NLS-1$
    }
  }

  private static CDOID readCDOIDObject(CDOID.ObjectType subType, String fragment)
  {
    AbstractCDOID id = createCDOIDObject(subType);
    id.read(fragment);
    return id;
  }

  /**
   * @since 2.0
   */
  public static boolean equals(CDOID id1, CDOID id2)
  {
    if (id1 == null)
    {
      id1 = CDOID.NULL;
    }

    if (id2 == null)
    {
      id2 = CDOID.NULL;
    }

    return ObjectUtil.equals(id1, id2);
  }
}

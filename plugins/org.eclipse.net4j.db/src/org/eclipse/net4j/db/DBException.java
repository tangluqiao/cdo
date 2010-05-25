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
package org.eclipse.net4j.db;

/**
 * TODO Provide consistent exception hierarchy
 * 
 * @author Eike Stepper
 */
public class DBException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public DBException()
  {
  }

  public DBException(String message)
  {
    super(message);
  }

  public DBException(Throwable cause)
  {
    super(cause);
  }

  public DBException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

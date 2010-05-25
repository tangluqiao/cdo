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
package org.eclipse.net4j.buddies.common;

/**
 * @author Eike Stepper
 */
public interface ISessionProvider
{
  public ISession[] getSessions();

  public ISession getSession(String userID);

  public ISession getSession(IBuddy buddy);
}

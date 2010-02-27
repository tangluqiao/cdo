/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;

import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.List;

/**
 * TODO Simon: JavaDoc
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOQuery extends CDOQueryInfo
{
  /**
   * Returns the {@link CDOView view} this query was created by and is associated with.
   * 
   * @return Never <code>null</code>.
   */
  public CDOView getView();

  /**
   * Sends this query to the server and returns a typed {@link CloseableIterator iterator} over the query result.
   * <p>
   * As opposed to the {@link #getResult(Class)} method, this method <b>asynchronously</b> communicates with the server.
   * In other words, the returned iterator can be used immediately, even if the server is still about to send pending
   * result elements.
   */
  public <T> CloseableIterator<T> getResultAsync(Class<T> classObject);

  /**
   * Sends this query to the server and returns a typed {@link List list} containing the query result.
   * <p>
   * As opposed to the {@link #getResultAsync(Class)} method, this method <b>synchronously</b> communicates with the
   * server. In other words, the result list is only returned after all result elements have been received by the
   * client.
   */
  public <T> List<T> getResult(Class<T> classObject);

  /**
   * Sets the maximum number of results to retrieve from the server.
   * 
   * @param maxResults
   *          the maximum number of results to retrieve or {@link #UNLIMITED_RESULTS} for no limitation.
   * @return the same query instance.
   */
  public CDOQuery setMaxResults(int maxResults);

  /**
   * Binds an argument value to a named parameter.
   * 
   * @param name
   *          the parameter name
   * @param value
   *          the value to bind
   * @return the same query instance
   * @throws IllegalArgumentException
   *           if the parameter name does not correspond to a parameter in the query string or if the argument value is
   *           of incorrect type
   */
  public CDOQuery setParameter(String name, Object value);
}

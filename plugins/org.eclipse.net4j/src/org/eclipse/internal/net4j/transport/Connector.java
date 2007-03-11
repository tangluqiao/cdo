/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.ConnectorException;
import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.transport.ConnectorState;
import org.eclipse.net4j.transport.IBuffer;
import org.eclipse.net4j.transport.IBufferProvider;
import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.transport.IConnector;
import org.eclipse.net4j.transport.IConnectorCredentials;
import org.eclipse.net4j.transport.IConnectorStateEvent;
import org.eclipse.net4j.transport.IProtocol;
import org.eclipse.net4j.transport.IProtocolFactory;
import org.eclipse.net4j.transport.IProtocolFactoryID;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.util.container.LifecycleEventConverter;
import org.eclipse.internal.net4j.util.event.Event;
import org.eclipse.internal.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public abstract class Connector extends Lifecycle implements IConnector, IContainer<IChannel>
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_CONNECTOR, Connector.class);

  private static final Channel NULL_CHANNEL = new NullChannel();

  private String userID;

  private IConnectorCredentials credentials;

  private IRegistry<IProtocolFactoryID, IProtocolFactory> protocolFactoryRegistry;

  private IBufferProvider bufferProvider;

  /**
   * An optional executor to be used by the {@link IChannel}s to process their
   * {@link Channel#receiveQueue} instead of the current thread. If not
   * <code>null</code> the sender and the receiver peers become decoupled.
   * <p>
   */
  private ExecutorService receiveExecutor;

  /**
   * TODO synchronize on channels?
   */
  private List<Channel> channels = new ArrayList(0);

  private ConnectorState connectorState = ConnectorState.DISCONNECTED;

  /**
   * Is registered with each {@link IChannel} of this {@link IConnector}.
   * <p>
   */
  private transient IListener lifecycleEventConverter = new LifecycleEventConverter(this);

  private transient CountDownLatch finishedConnecting;

  private transient CountDownLatch finishedNegotiating;

  public Connector()
  {
  }

  public abstract void multiplexBuffer(IChannel channel);

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public void setReceiveExecutor(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IRegistry<IProtocolFactoryID, IProtocolFactory> getProtocolFactoryRegistry()
  {
    return protocolFactoryRegistry;
  }

  public void setProtocolFactoryRegistry(IRegistry<IProtocolFactoryID, IProtocolFactory> protocolFactoryRegistry)
  {
    this.protocolFactoryRegistry = protocolFactoryRegistry;
  }

  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public boolean isClient()
  {
    return getLocation() == ConnectorLocation.CLIENT;
  }

  public boolean isServer()
  {
    return getLocation() == ConnectorLocation.SERVER;
  }

  public String getUserID()
  {
    return userID;
  }

  public void setUserID(String userID)
  {
    this.userID = userID;
  }

  public ConnectorLocation getLocation()
  {
    return null;
  }

  public IConnectorCredentials getCredentials()
  {
    return credentials;
  }

  public void setCredentials(IConnectorCredentials credentials)
  {
    this.credentials = credentials;
  }

  public ConnectorState getState()
  {
    return connectorState;
  }

  public void setState(ConnectorState newState) throws ConnectorException
  {
    ConnectorState oldState = getState();
    if (newState != oldState)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Setting state " + newState + " (was " + oldState.toString().toLowerCase() //$NON-NLS-1$ //$NON-NLS-2$
            + ")"); //$NON-NLS-1$
      }

      connectorState = newState;
      fireEvent(new ConnectorStateEventImpl(this, oldState, newState));
      switch (newState)
      {
      case DISCONNECTED:
        if (finishedConnecting != null)
        {
          finishedConnecting.countDown();
          finishedConnecting = null;
        }

        if (finishedNegotiating != null)
        {
          finishedNegotiating.countDown();
          finishedNegotiating = null;
        }
        break;

      case CONNECTING:
        finishedConnecting = new CountDownLatch(1);
        finishedNegotiating = new CountDownLatch(1);
        if (isServer())
        {
          setState(ConnectorState.NEGOTIATING);
        }
        break;

      case NEGOTIATING:
        finishedConnecting.countDown();
        setState(ConnectorState.CONNECTED); // TODO Implement negotiation
        break;

      case CONNECTED:
        finishedConnecting.countDown(); // Just in case of suspicion
        finishedNegotiating.countDown();
        break;

      }
    }
  }

  public boolean isConnected()
  {
    return getState() == ConnectorState.CONNECTED;
  }

  public void connectAsync() throws ConnectorException
  {
    try
    {
      activate();
    }
    catch (ConnectorException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ConnectorException(ex);
    }
  }

  public boolean waitForConnection(long timeout) throws ConnectorException
  {
    ConnectorState connectorState = getState();
    if (connectorState == ConnectorState.DISCONNECTED)
    {
      return false;
    }

    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Waiting for connection..."); //$NON-NLS-1$
      }

      return finishedNegotiating.await(timeout, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException ex)
    {
      return false;
    }
  }

  public boolean connect(long timeout) throws ConnectorException
  {
    connectAsync();
    return waitForConnection(timeout);
  }

  public ConnectorException disconnect()
  {
    Exception ex = deactivate();
    if (ex == null)
    {
      return null;
    }

    if (ex instanceof ConnectorException)
    {
      return (ConnectorException)ex;
    }

    return new ConnectorException(ex);
  }

  public IChannel[] getChannels()
  {
    final List<IChannel> result = new ArrayList(channels.size());
    synchronized (channels)
    {
      for (final Channel channel : channels)
      {
        if (channel != NULL_CHANNEL)
        {
          result.add(channel);
        }
      }
    }

    return result.toArray(new IChannel[result.size()]);
  }

  public IChannel[] getElements()
  {
    return getChannels();
  }

  public IChannel openChannel() throws ConnectorException
  {
    return openChannel(null);
  }

  public IChannel openChannel(String protocolID) throws ConnectorException
  {
    return openChannel(protocolID, null);
  }

  public IChannel openChannel(String protocolID, Object protocolData) throws ConnectorException
  {
    waitForConnection(Long.MAX_VALUE);
    short channelIndex = findFreeChannelIndex();
    Channel channel = createChannel(channelIndex, protocolID, protocolData);
    registerChannelWithPeer(channelIndex, protocolID);

    try
    {
      channel.activate();
    }
    catch (ConnectorException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ConnectorException(ex);
    }

    return channel;
  }

  public Channel createChannel(short channelIndex, String protocolID, Object protocolData)
  {
    Channel channel = new Channel(receiveExecutor);
    IProtocol protocol = createProtocol(protocolID, channel, protocolData);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Opening channel " + channelIndex //$NON-NLS-1$
          + (protocol == null ? " without protocol" : " with protocol " + protocolID)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    channel.setChannelIndex(channelIndex);
    channel.setConnector(this);
    channel.setReceiveHandler(protocol);
    channel.addListener(lifecycleEventConverter);
    addChannel(channel);
    return channel;
  }

  public Channel getChannel(short channelIndex)
  {
    try
    {
      Channel channel = channels.get(channelIndex);
      if (channel == NULL_CHANNEL)
      {
        channel = null;
      }

      return channel;
    }
    catch (IndexOutOfBoundsException ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Invalid channelIndex " + channelIndex); //$NON-NLS-1$
      }

      return null;
    }
  }

  protected List<Queue<IBuffer>> getChannelBufferQueues()
  {
    final List<Queue<IBuffer>> result = new ArrayList(channels.size());
    synchronized (channels)
    {
      for (final Channel channel : channels)
      {
        if (channel != NULL_CHANNEL && channel.isActive())
        {
          Queue<IBuffer> bufferQueue = channel.getSendQueue();
          result.add(bufferQueue);
        }
      }
    }

    return result;
  }

  protected short findFreeChannelIndex()
  {
    synchronized (channels)
    {
      int size = channels.size();
      for (short i = 0; i < size; i++)
      {
        if (channels.get(i) == NULL_CHANNEL)
        {
          return i;
        }
      }

      return (short)size;
    }
  }

  protected void addChannel(Channel channel)
  {
    short channelIndex = channel.getChannelIndex();
    while (channelIndex >= channels.size())
    {
      channels.add(NULL_CHANNEL);
    }

    channels.set(channelIndex, channel);
  }

  protected void removeChannel(Channel channel)
  {
    channel.removeListener(lifecycleEventConverter);
    int channelIndex = channel.getChannelIndex();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Removing channel " + channelIndex); //$NON-NLS-1$
    }

    channels.set(channelIndex, NULL_CHANNEL);
  }

  protected IProtocol createProtocol(String protocolID, IChannel channel, Object protocolData)
  {
    if (protocolID == null || protocolID.length() == 0 || protocolFactoryRegistry == null)
    {
      return null;
    }

    IProtocolFactoryID protocolFactoryID = ProtocolFactoryID.create(getLocation(), protocolID);
    IProtocolFactory factory = protocolFactoryRegistry.get(protocolFactoryID);

    if (factory == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Unknown protocol " + protocolID); //$NON-NLS-1$
      }

      return null;
    }

    return factory.createProtocol(channel, protocolData);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (bufferProvider == null)
    {
      throw new IllegalStateException("bufferProvider == null"); //$NON-NLS-1$
    }

    if (protocolFactoryRegistry == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("No protocolFactoryRegistry!"); //$NON-NLS-1$
    }

    if (receiveExecutor == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("No receiveExecutor!"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    setState(ConnectorState.CONNECTING);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    setState(ConnectorState.DISCONNECTED);
    for (short i = 0; i < channels.size(); i++)
    {
      Channel channel = channels.get(i);
      if (channel != null)
      {
        LifecycleUtil.deactivate(channel);
      }
    }

    channels.clear();
    super.doDeactivate();
  }

  protected abstract void registerChannelWithPeer(short channelIndex, String protocolID) throws ConnectorException;

  /**
   * @author Eike Stepper
   */
  private static final class NullChannel extends Channel
  {
    private NullChannel()
    {
      super(null);
    }

    @Override
    public boolean isInternal()
    {
      return true;
    }

    @Override
    public String toString()
    {
      return "NullChannel"; //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class ConnectorStateEventImpl extends Event implements IConnectorStateEvent
  {
    private static final long serialVersionUID = 1L;

    private ConnectorState oldState;

    private ConnectorState newState;

    public ConnectorStateEventImpl(INotifier notifier, ConnectorState oldState, ConnectorState newState)
    {
      super(notifier);
      this.oldState = oldState;
      this.newState = newState;
    }

    public ConnectorState getOldState()
    {
      return oldState;
    }

    public ConnectorState getNewState()
    {
      return newState;
    }
  }
}

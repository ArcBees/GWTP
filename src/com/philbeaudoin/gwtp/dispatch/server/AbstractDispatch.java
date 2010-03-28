package com.philbeaudoin.gwtp.dispatch.server;

import java.util.List;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;
import com.philbeaudoin.gwtp.dispatch.shared.UnsupportedActionException;

public abstract class AbstractDispatch implements Dispatch {

  private static class DefaultExecutionContext implements ExecutionContext {
    private final AbstractDispatch dispatch;

    private final List<ActionResult<?, ?>> actionResults;

    private DefaultExecutionContext( AbstractDispatch dispatch ) {
      this.dispatch = dispatch;
      this.actionResults = new java.util.ArrayList<ActionResult<?, ?>>();
    }

    @Override
    public <A extends Action<R>, R extends Result> R execute( A action ) 
    throws ActionException, ServiceException {
      R result = dispatch.doExecute( action, this );
      actionResults.add( new ActionResult<A, R>( action, result, true ) );
      return result;
    }

    @Override
    public <A extends Action<R>, R extends Result> void undo( A action, R result ) 
    throws ActionException, ServiceException {
      dispatch.doExecute( action, this );
      actionResults.add( new ActionResult<A, R>( action, result, false ) );
    }

    /**
     * Rolls back all logged executed actions.
     * 
     * @throws ActionException
     *             If there is an action exception while rolling back.
     * @throws ServiceException
     *             If there is a low level problem while rolling back.
     */
    private void rollback() throws ActionException, ServiceException {
      DefaultExecutionContext ctx = new DefaultExecutionContext(dispatch);
      for ( int i = actionResults.size() - 1; i >= 0; i-- ) {
        ActionResult<?, ?> actionResult = actionResults.get( i );
        rollback( actionResult, ctx );
      }
    }

    private <A extends Action<R>, R extends Result> void rollback( 
        ActionResult<A, R> actionResult, ExecutionContext ctx )
    throws ActionException, ServiceException {
      if( actionResult.isExecuted() )
        dispatch.doUndo( actionResult.getAction(), actionResult.getResult(), ctx );
      else
        dispatch.doExecute( actionResult.getAction(), ctx );
    }


  };

  protected abstract ActionHandlerRegistry getHandlerRegistry();    

  @Override
  public <A extends Action<R>, R extends Result> R execute( A action ) 
  throws ActionException, ServiceException {
    DefaultExecutionContext ctx = new DefaultExecutionContext( this );
    try {
      return doExecute( action, ctx );
    } catch ( ActionException e ) {
      ctx.rollback();
      throw e;
    } catch ( ServiceException e ) {
      ctx.rollback();
      throw e;
    }
  }

  @Override
  public <A extends Action<R>, R extends Result> void undo( A action, R result ) 
  throws ActionException, ServiceException {
    DefaultExecutionContext ctx = new DefaultExecutionContext( this );
    try {
      doUndo( action, result, ctx );
    } catch ( ActionException e ) {
      ctx.rollback();
      throw e;
    } catch ( ServiceException e ) {
      ctx.rollback();
      throw e;
    }
  }

  private <A extends Action<R>, R extends Result> R doExecute( A action, ExecutionContext ctx )
  throws ActionException, ServiceException {
    ActionHandler<A, R> handler = findHandler( action );
    try {
      return handler.execute( action, ctx );
    }
    catch( ActionException e ) {
      throw e;
    }
    catch( Exception e ) {
      throw new ServiceException( e );
    }
  }


  private <A extends Action<R>, R extends Result> void doUndo( A action, R result, ExecutionContext ctx )
  throws ActionException, ServiceException {
    ActionHandler<A, R> handler = findHandler( action );
    try {
      handler.undo( action, result, ctx );
    }
    catch( ActionException e ) {
      throw e;
    }
    catch( Exception cause ) {
      throw new ServiceException( cause );
    }
  }


  private <A extends Action<R>, R extends Result> ActionHandler<A, R> findHandler( A action )
  throws UnsupportedActionException {
    ActionHandler<A, R> handler = getHandlerRegistry().findHandler( action );
    if ( handler == null )
      throw new UnsupportedActionException( action );

    return handler;
  }

}

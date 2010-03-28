package com.philbeaudoin.gwtp.dispatch.shared;

import com.philbeaudoin.gwtp.dispatch.shared.BatchAction.OnException;

import java.util.List;

/**
 * Contains the list of {@link Result}s from successful actions in the
 * {@link BatchAction}. The order will match the order of the original
 * {@link Action}s listed in the {@link BatchAction}.
 * 
 * <p>
 * If the {@link BatchAction} was specified to have an {@link OnException} value
 * of {@link OnException#CONTINUE}, failed actions will have a
 * <code>null</code> value.
 * 
 * @author David Peterson
 */
public class BatchResult implements Result {

  private static final long serialVersionUID = -7169695166206704296L;

  private List<Result> results;

  /**
   * For serialization.
   */
  BatchResult() {
  }

  /**
   * Creates a new result with the list of results from the batch action, in
   * order.
   * 
   * @param results The list of results.
   */
  public BatchResult( List<Result> results ) {
    this.results = results;
  }

  public List<Result> getResults() {
    return results;
  }
}

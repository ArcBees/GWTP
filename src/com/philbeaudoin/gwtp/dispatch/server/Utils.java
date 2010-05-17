package com.philbeaudoin.gwtp.dispatch.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collection of utility methods
 * 
 * @author Philippe Beaudoin
 */
public class Utils {

  /**
   * Logs an entire stack trace to the logger, so that it can easily be
   * viewed in the App Engine log. The exception will be logged
   * as a severe error. See also {@link #logStackTrace(Level, Logger, Exception)}.
   * 
   * @param log The {@link Logger} to use.
   * @param e The {@link Exception} with the stack trace to log.
   */
  public static void logStackTrace(Logger log, Exception e) {
    logStackTrace( log, Level.SEVERE, e );
  }

  /**
   * Logs an entire stack trace to the logger, so that it can easily be
   * viewed in the App Engine log.
   * 
   * @param log The {@link Logger} to use.
   * @param level The {@link Level} at which to log the stac trace.
   * @param e The {@link Exception} with the stack trace to log.
   */
  public static void logStackTrace(Logger log, Level level, Exception e) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter( stringWriter );
    e.printStackTrace( printWriter );
    log.log( level, stringWriter.toString() );
  }

}

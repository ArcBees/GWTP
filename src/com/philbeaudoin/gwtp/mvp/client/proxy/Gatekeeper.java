package com.philbeaudoin.gwtp.mvp.client.proxy;


/**
 * Inherit from this class to define a gatekeeper that locks access
 * to your {@link Place} in specific situation. For example:
 * <pre>
 * public class AdminGatekeeper implements Gatekeeper {
 *   
 *   private final CurrentUser currentUser;
 *   
 *  {@code @}Inject
 *   public AdminGatekeeper( CurrentUser currentUser ) {
 *     this.currentUser = currentUser;
 *   }
 *   
 *  {@code @}Override
 *   public boolean canReveal() {
 *     return currentUser.isAdministrator();
 *   }
 * 
 * }
 * </pre>
 * 
 * You must also make sure that your custom Ginjector provides a {@code get}
 * method returning this {@link Gatekeeper} if you want to use it with the
 * {@link com.philbeaudoin.gwtp.mvp.client.annotations.UseGatekeeper}
 * annotation.
 * <p />
 * You should usually bind your {@link Gatekeeper} as a singleton.
 * 
 * @author Philippe Beaudoin
 * @author Olivier Monaco
 */
public interface Gatekeeper {
  /**
   * Checks whether or not the {@link Place} controlled by this gatekeeper can be
   * revealed.
   * 
   * @return {@code true} if the {@link Place} can be revealed, {@code false} otherwise.
   */
  public boolean canReveal();
}

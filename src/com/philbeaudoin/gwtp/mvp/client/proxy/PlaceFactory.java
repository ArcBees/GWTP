package com.philbeaudoin.gwtp.mvp.client.proxy;


/**
 * Inherit from this class to define a factory for creating your
 * custom {@link Place}. For example:
 * <pre>
 * public class AdminSecurePlaceFactory implements PlaceFactory {
 *   
 *   private final CurrentUser currentUser;
 *   
 *  {@code @}Inject
 *   public AdminSecurePlaceFactory( CurrentUser currentUser ) {
 *     this.currentUser = currentUser;
 *   }
 *   
 *  {@code @}Override
 *   public Place create(String nameToken) {
 *     return new AdminSecurePlace( nameToken, currentUser );
 *   }
 * 
 * }
 * </pre>
 * 
 * You must also make sure that your custom Ginjector provides a {@code get}
 * method returning this factory if you want to use it with the
 * {@link com.philbeaudoin.gwtp.mvp.client.annotations.PlaceProvider}
 * annotation.
 * 
 * @author Philippe Beaudoin
 * @author Olivier Monaco
 */
public interface PlaceFactory {
  /**
   * Creates the {@link Place} provided by this factory.
   * 
   * @param nameToken The name token for which to create a place.
   * @return The newly created {@link Place}.
   */
  public Place create(String nameToken);
}

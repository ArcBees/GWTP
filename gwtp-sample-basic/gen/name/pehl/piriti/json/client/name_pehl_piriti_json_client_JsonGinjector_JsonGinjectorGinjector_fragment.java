package name.pehl.piriti.json.client;

import com.google.gwt.core.client.GWT;
import name.pehl.piriti.json.client.name_pehl_piriti_json_client_JsonGinjector_JsonGinjectorGinjector;

public class name_pehl_piriti_json_client_JsonGinjector_JsonGinjectorGinjector_fragment {
  public void memberInject_Key$type$name$pehl$piriti$json$client$JsonRegistry$_annotation$$none$$(name.pehl.piriti.json.client.JsonRegistry injectee) {
    
  }
  
  private name.pehl.piriti.json.client.JsonRegistry singleton_Key$type$name$pehl$piriti$json$client$JsonRegistry$_annotation$$none$$ = null;
  
  public name.pehl.piriti.json.client.JsonRegistry get_Key$type$name$pehl$piriti$json$client$JsonRegistry$_annotation$$none$$() {
    
    if (singleton_Key$type$name$pehl$piriti$json$client$JsonRegistry$_annotation$$none$$ == null) {
    Object created = GWT.create(name.pehl.piriti.json.client.JsonRegistry.class);
    assert created instanceof name.pehl.piriti.json.client.JsonRegistry;
    name.pehl.piriti.json.client.JsonRegistry result = (name.pehl.piriti.json.client.JsonRegistry) created;
    
    memberInject_Key$type$name$pehl$piriti$json$client$JsonRegistry$_annotation$$none$$(result);
    
        singleton_Key$type$name$pehl$piriti$json$client$JsonRegistry$_annotation$$none$$ = result;
    }
    return singleton_Key$type$name$pehl$piriti$json$client$JsonRegistry$_annotation$$none$$;
    
  }
  
  
  /**
   * Field for the enclosing injector.
   */
  private final name_pehl_piriti_json_client_JsonGinjector_JsonGinjectorGinjector injector;
  public name_pehl_piriti_json_client_JsonGinjector_JsonGinjectorGinjector_fragment(name_pehl_piriti_json_client_JsonGinjector_JsonGinjectorGinjector injector) {
    this.injector = injector;
  }
  
}

package com.google.gwt.uibinder.rebind;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

public class FieldWriterOfInjectedType extends FieldWriterOfExistingType {

  private final JClassType ginjectorClass;
  private final String ginjectorMethod;

  public FieldWriterOfInjectedType(JClassType type, String name, JClassType ginjectorClass, 
      String ginjectorMethod, MortalLogger logger) {
    super(type, name, logger);
    this.ginjectorClass = ginjectorClass;
    this.ginjectorMethod = ginjectorMethod;
  }

  public void write(IndentedWriter w) throws UnableToCompleteException {
    // Preempt creation of initializer, provide our own based on gin.
    setInitializer( String.format("(%1$s) (((%2$s)GWT.create(%2$s.class)).%3$s())",
        getQualifiedSourceName(), ginjectorClass.getQualifiedSourceName(), ginjectorMethod) );
    super.write(w);
  }

}

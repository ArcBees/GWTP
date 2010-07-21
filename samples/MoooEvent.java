package com.example;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import javax.annotation.Generated;
import com.gwtplatform.mvp.client.EventBus;
import com.google.gwt.event.shared.HasHandlers;

@Generated(value = "com.gwtplatform.annotation.processor.GenEventAptProcessor", date = "Thu Jul 22 06:02:23 NZST 2010")
class MoooEvent extends GwtEvent<MoooEvent.MoooHandler> { 

public static final Type<MoooHandler> TYPE = new Type<MoooHandler>();
  private int apple;
  private java.lang.String[] banana;
  private int bob;
  private boolean cats;

  public MoooEvent(int apple, java.lang.String[] banana, int bob, boolean cats) {
  }

  public static Type<MoooHandler> getType() {
    return TYPE;
  }

  public static void fire(EventBus eventBus, int apple, java.lang.String[] banana, int bob, boolean cats) {
    eventBus.fireEvent(new MoooEvent(apple, banana, bob, cats));
  }

  @Override
  public Type<MoooHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(MoooHandler handler) {
    handler.onMooo(this);
  }

  public int getApple() {
    return apple;
  }
  public java.lang.String[] getBanana() {
    return banana;
  }
  public int getBob() {
    return bob;
  }
  public boolean isCats() {
    return cats;
  }

  @Override
  public boolean equals(Object other) {
    if (other != null && other.getClass().equals(this.getClass())) {
          MoooEvent o = (MoooEvent) other;
      return true
          && o.apple == this.apple
          && java.util.Arrays.deepEquals(o.banana, this.banana)
          && o.bob == this.bob
          && o.cats == this.cats
        ;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = (hashCode * 37) + getClass().hashCode();
    hashCode = (hashCode * 37) + new Integer(apple).hashCode();
    hashCode = (hashCode * 37) + java.util.Arrays.deepHashCode(banana);
    hashCode = (hashCode * 37) + new Integer(bob).hashCode();
    hashCode = (hashCode * 37) + new Boolean(cats).hashCode();
    return hashCode;
  }

  @Override
  public String toString() {
    return "MoooEvent["
			            + apple
			            + banana
			            + bob
			            + cats
    + "]";
  }

  public static interface MoooHandler extends EventHandler {
    public void onMooo(MoooEvent event);
  }

  public interface HasMoooHandlers extends HasHandlers {
    HandlerRegistration addMoooHandler(MoooHandler handler);
  }
}

package com.example;

import com.gwtplatform.dispatch.shared.Result;

import javax.annotation.Generated;

@Generated(value = "com.gwtplatform.annotation.processor.GenDispatchAptProcessor", date = "Thu Jul 22 08:14:46 NZST 2010")
class NewUserResult implements Result { 

  private int userId;

  protected NewUserResult() { }

  public NewUserResult(int userId) { 
    this.userId = userId;
  }

  public int getUserId() {
    return userId;
  }

  @Override
  public boolean equals(Object other) {
    if (other != null && other.getClass().equals(this.getClass())) {
          NewUserResult o = (NewUserResult) other;
      return true
          && o.userId == this.userId
        ;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = (hashCode * 37) + getClass().hashCode();
    hashCode = (hashCode * 37) + new Integer(userId).hashCode();
    return hashCode;
  }

  @Override
  public String toString() {
    return "NewUserResult["
                 + userId
    + "]";
  }

}

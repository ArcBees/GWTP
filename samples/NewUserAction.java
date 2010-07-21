package com.example;

import com.gwtplatform.dispatch.shared.Action;

import javax.annotation.Generated;

@Generated(value = "com.gwtplatform.annotation.processor.GenDispatchAptProcessor", date = "Thu Jul 22 08:14:46 NZST 2010")
class NewUserAction implements Action<NewUserResult> { 

  private java.lang.String firstName;
  private java.lang.String lastName;

  protected NewUserAction() { }

  public NewUserAction(java.lang.String firstName, java.lang.String lastName) { 
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public java.lang.String getFirstName() {
    return firstName;
  }

  public java.lang.String getLastName() {
    return lastName;
  }

  @Override
  public String getServiceName() {
    return Action.DEFAULT_SERVICE_NAME + "NewUser";
  }

  @Override
  public boolean isSecured() {
    return true;
  }

  @Override
  public boolean equals(Object other) {
    if (other != null && other.getClass().equals(this.getClass())) {
          NewUserAction o = (NewUserAction) other;
      return true
          && ((o.firstName == null && this.firstName == null) || (o.firstName != null && o.firstName.equals(this.firstName)))
          && ((o.lastName == null && this.lastName == null) || (o.lastName != null && o.lastName.equals(this.lastName)))
        ;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = (hashCode * 37) + getClass().hashCode();
    hashCode = (hashCode * 37) + (firstName == null ? 1 : firstName.hashCode());
    hashCode = (hashCode * 37) + (lastName == null ? 1 : lastName.hashCode());
    return hashCode;
  }

  @Override
  public String toString() {
    return "NewUserAction["
                 + firstName
                 + lastName
    + "]";
  }

}

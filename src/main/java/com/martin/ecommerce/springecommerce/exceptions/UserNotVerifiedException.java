package com.martin.ecommerce.springecommerce.exceptions;

public class UserNotVerifiedException extends Exception {

    private boolean newEmailSent;

    public UserNotVerifiedException(boolean newEmailSent){
        this.newEmailSent = newEmailSent;
    }

    public boolean isNewEmailSent() {
        return newEmailSent;
    }

    public void setNewEmailSent(boolean newEmailSent) {
        this.newEmailSent = newEmailSent;
    }

    

}

/**
 * Created by Azat on 30.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.NotAuthenticatedException;

public interface UserInterface {
    User getUser();
    void checkAuthenticated() throws NotAuthenticatedException;
}

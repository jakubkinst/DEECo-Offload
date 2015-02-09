/**
 * Copyright 2005-2014 Restlet
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or or EPL 1.0 (the "Licenses"). You can
 * select the license that you prefer but you may not use this file except in
 * compliance with one of these Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet S.A.S.
 */

package org.restlet.security;

import java.security.Principal;

import org.restlet.data.ClientInfo;

/**
 * Updates an authenticated client user with assigned roles. Typically, it is
 * invoked by an {@link Authenticator} after successful authentication to add
 * {@link org.restlet.security.Role} instances based on available {@link org.restlet.security.User}.
 * 
 * @see Authenticator#getEnroler()
 * @see Authenticator#setEnroler(org.restlet.security.Enroler)
 * @see org.restlet.data.ClientInfo#getUser()
 * @see org.restlet.data.ClientInfo#getRoles()
 * @author Jerome Louvel
 */
public interface Enroler {

    /**
     * Attempts to update an authenticated client, with a {@link org.restlet.security.User} properly
     * defined, by adding the {@link org.restlet.security.Role} that are assigned to this user. Note
     * that principals could also be added to the {@link org.restlet.data.ClientInfo} if
     * necessary. The addition could also potentially be based on the presence
     * of {@link java.security.Principal}.
     * 
     * @param clientInfo
     *            The clientInfo to update.
     */
    void enrole(ClientInfo clientInfo);

}

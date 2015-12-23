/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *          Nuxeo
 */
package org.nuxeo.java.client.api.objects;

import java.io.IOException;
import java.util.List;

import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.java.client.api.methods.CurrentUserAPI;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;

import retrofit.Response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 1.0
 */
public class CurrentUser extends NuxeoObject {

    @JsonIgnore
    protected CurrentUserAPI currentUserAPI;

    protected String username;

    @JsonProperty("isAdministrator")
    protected boolean isAdministrator;

    protected List<String> groups;

    public CurrentUser() {
        super(ConstantsV1.ENTITY_TYPE_LOGIN);
    }

    public CurrentUser(NuxeoClient nuxeoClient) {
        super(ConstantsV1.ENTITY_TYPE_LOGIN, nuxeoClient);
        currentUserAPI = nuxeoClient.getRetrofit().create(CurrentUserAPI.class);
    }

    public List<String> getGroups() {
        return groups;
    }

    public boolean isAdministrator() {
        return isAdministrator;
    }

    public String getUsername() {
        return username;
    }

    public CurrentUser getCurrentUser() {
        try {
            Response<CurrentUser> response = currentUserAPI.getCurrentUser().execute();
            return response.body();
        } catch (IOException reason) {
            throw new NuxeoClientException(reason);
        }
    }

}

/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
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
package org.nuxeo.java.client;

import javax.inject.Inject;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * @since 1.0
 */
public class BaseTest {

    @Inject
    public CoreSession session;

    public NuxeoClient nuxeoClient;

    public void login()  {
        nuxeoClient = new NuxeoClient("http://localhost:18090", "Administrator", "Administrator").timeout(60);
    }

    public void login(String username, String pwd)  {
        nuxeoClient = new NuxeoClient("http://localhost:18090", username, pwd);
    }

    public void logout()  {
        nuxeoClient.logout();
    }

    protected void fetchInvalidations() {
        session.save();
        if (TransactionHelper.isTransactionActiveOrMarkedRollback()) {
            TransactionHelper.commitOrRollbackTransaction();
            TransactionHelper.startTransaction();
        }
    }
}

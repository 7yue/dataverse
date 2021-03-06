package edu.harvard.iq.dataverse.api.datadeposit;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.swordapp.server.ServiceDocumentAPI;
import org.swordapp.server.servlets.SwordServlet;

public class SWORDv2ServiceDocumentServlet extends SwordServlet {

    @Inject
    ServiceDocumentManagerImpl serviceDocumentManagerImpl;
    protected ServiceDocumentAPI api;

    public void init() throws ServletException {
        super.init();
        this.api = new ServiceDocumentAPI(serviceDocumentManagerImpl, this.config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.api.get(req, resp);
    }

}

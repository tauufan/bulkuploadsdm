package com.bankbjb.itcore.bulkupload.common;

import com.misys.equation.common.core.EQEnquiry;
import com.misys.equation.common.core.EQEnvironment;
import com.misys.equation.common.core.EQException;
import com.misys.equation.common.core.EQSessionImpl;
import com.misys.equation.common.core.EQSessionProperties;
import org.apache.log4j.Logger;

public class EqUtil {

    private EQEnvironment environment;
    private EQSessionImpl session;
    private EQEnquiry enquiry;
    private String configPath;
    
    protected final Logger logger = Logger.getLogger(this.getClass());

    public EqUtil() {
    }

    public EqUtil(String path) {
        this.configPath = path;
    }

    public EQEnvironment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(EQEnvironment environment) {
        this.environment = environment;
    }

    public EQSessionImpl getSession() {
        return this.session;
    }

    public void setSession(EQSessionImpl session) {
        this.session = session;
    }

    public EQEnquiry getEnquiry() {
        return this.enquiry;
    }

    public void setEnquiry(EQEnquiry enquiry) {
        this.enquiry = enquiry;
    }

    public void createEnvironment()
            throws EQException {
        EQEnvironment.setConfigFileName(this.configPath);

        this.environment = EQEnvironment.getAppEnvironment();
    }

    public void createSession(String systemName, String unitMnemonic, String databaseUser, String databasePassword, String equationUser, String equationPassword)
            throws EQException {
        EQSessionProperties sessionProperties = new EQSessionProperties(this.environment);

        // Now set up the properties as required 	
        sessionProperties.setAddToActiveUsersList(true);
        sessionProperties.setApplication("BulkUpload");
        sessionProperties.setTransactionIsolationLevel(EQSessionProperties.TRANSACTION_ISOLATION_EQUATION_ONLY);
        sessionProperties.setAutoEQCommit(true);
        sessionProperties.setTimeOut(30);
        sessionProperties.setWorkstationIPAddr("127.000.000.001");

        this.session = ((EQSessionImpl) this.environment.createEQSession(sessionProperties, databaseUser, databasePassword.toCharArray(), equationUser, equationPassword.toCharArray()));
        try {
            String processingDate = this.session.getProcessingDate();
            String txt = "Processing date = " + processingDate;
            logger.info(txt);
        } catch (Exception e) {
            logger.error("Failed to get processing date.");
            logger.error(e);
            return;
        }
    }

    public void createEnquiry()
            throws EQException {
        this.enquiry = ((EQEnquiry) this.session.createEQObject("AS"));

        this.enquiry.setAuditUserID(this.session.getEquationUserIdentifier());
    }

    public void populateKeyFields(String zlab, String zlan, String zlas) {
        this.enquiry.reset();

        this.enquiry.setFieldValue("ZLCUS", "ComCyp");
    }

    public boolean executeEnquiry()
            throws EQException {
        return this.enquiry.retrieve(this.session);
    }

    protected static void print(String s) {
        System.out.println(s);
    }
}
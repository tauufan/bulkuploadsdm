package com.bankbjb.itcore.bulkupload.common;

import com.misys.equation.common.core.EQEnquiry;
import com.misys.equation.common.core.EQEnvironment;
import com.misys.equation.common.core.EQException;
import com.misys.equation.common.core.EQField;
import com.misys.equation.common.core.EQFieldDefinition;
import com.misys.equation.common.core.EQFieldMessage;
import com.misys.equation.common.core.EQList;
import com.misys.equation.common.core.EQMessage;
import com.misys.equation.common.core.EQObject;
import com.misys.equation.common.core.EQPrompt;
import com.misys.equation.common.core.EQPromptImpl;
import com.misys.equation.common.core.EQSessionImpl;
import com.misys.equation.common.core.EQTransaction;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class indEqUtil {

    private EQEnvironment environment;
    private EQSessionImpl session;
    private EQPrompt prompt;
    private EQTransaction transaction;
    private EQEnquiry enquiry;
    private EqUtil util;
    private int status;
    private boolean ready = false;
    private String errorMessage = "";
    private String configPath = "";
    private ArrayList<String> errorList = new ArrayList();
    
    protected final Logger logger1 = Logger.getLogger(this.getClass());

    public indEqUtil() {
        this.util = new EqUtil();
        try {
            logger1.info("== Try to Create Environment ==");
            this.util.createEnvironment();
            logger1.info("== Try to Assign Environment ==");
            this.environment = this.util.getEnvironment();
            logger1.info("== Try to Fill Variable ==");
            fillVariable();

            if (getSession() == null) {
                setReady(false);
            } else {
                setReady(true);
            }
        } catch (Exception e) {
            setReady(false);
            e.printStackTrace();
        }
    }

    public indEqUtil(String configPath) {
        this.util = new EqUtil(configPath);
        try {
            logger1.info("== Try to Create Environment ==");
            this.util.createEnvironment();
            logger1.info("== Try to Assign Environment ==");
            this.environment = this.util.getEnvironment();
            logger1.info("== Try to Fill Variable ==");
            fillVariable();

            if (getSession() == null) {
                setReady(false);
            } else {
                setReady(true);
            }
        } catch (Exception e) {
            setReady(false);
            e.printStackTrace();
        }
    }

    private void fillVariable() {
        String systemName = this.util.getEnvironment().getProperty("systemName");
        String unitMnemonic = this.util.getEnvironment().getProperty("unitMnemonic");
        String DBUser = this.util.getEnvironment().getProperty("DBUser");
        String DBUserPassword = this.util.getEnvironment().getProperty("DBUserPassword");
        String equationUser = this.util.getEnvironment().getProperty("equationUser");
        String equationUserPassword = this.util.getEnvironment().getProperty("equationUserPassword");

//        logger1.info("== Variable ==");
//        logger1.info("== System name [" + systemName + "] ==");
//        logger1.info("== Unit mnemonic [" + unitMnemonic + "] ==");
//        logger1.info("== Database user [" + DBUser + "]");
//        logger1.info("== Databse password [" + DBUserPassword + "] ==");
//        logger1.info("== Equation user [" + equationUser + "] ==");
//        logger1.info("== Equation password [" + equationUserPassword + "] ==");
        try {
            this.util.createSession(systemName, unitMnemonic, DBUser, DBUserPassword, equationUser, equationUserPassword);
            setSession(this.util.getSession());
            logger1.info("== Connected ==");
            logger1.info("== Job id = " + this.util.getSession().getJobNumber() + " ==");
        } catch (Exception e) {
            logger1.error("== Not Connected ==");
            setErrorMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    public ArrayList<HashMap<String, String>> actPrompt(String pageNum, String promptCode, HashMap<String, String> param, ArrayList<String> paramResult) {
        return this.actPrompt(pageNum, promptCode, param, paramResult, 10);
    }
    
    public ArrayList<HashMap<String, String>> actPrompt(String pageNum, String promptCode, HashMap<String, String> param, ArrayList<String> paramResult, int perPage) {
        ArrayList result = new ArrayList();

        String k = "";
        String v = "";
        String kr = "";
        String vr = "";
        int i = 0;
        int page = pageNum.equals("FR") || pageNum.equals("LR") ? 0 : Integer.parseInt(pageNum);
        boolean promptResult = false;

        logger1.info("promptCode = " + promptCode);
        try {
            setPrompt((EQPrompt) getSession().createEQObject(promptCode));
            ((EQPromptImpl) getPrompt()).reset();
            ((EQPromptImpl) getPrompt()).getList().setPageSize(perPage);

            Iterator itr = param.keySet().iterator();
            while (itr.hasNext()) {
                k = (String) itr.next();
                v = (String) param.get(k);
                logger1.info("Populate field input");
                logger1.info("key = " + k + ",value = " + v);
                getPrompt().setFieldValue(k, v);
            }

            if (pageNum.equals("FR") || pageNum.equals("LR")) {
                if (pageNum.equals("FR")) {
                    promptResult = executePrompt("F", getSession());
                } else {
                    promptResult = executePrompt("F", getSession());
                    while (!getPrompt().getList().isLastPage()) {
                        promptResult = (promptResult) && (executePrompt("N", getSession()));
                    }
                }
                
                if (promptResult) {
                    i = pageNum.equals("FR") ? 0 : getPrompt().getList().getNumRows() - 1;
                    HashMap ap = new HashMap();
                    HashMap eqFields = getPrompt().getList().getRowFields(i);
                    Set keys = eqFields.keySet();
                    Iterator keyIterator = keys.iterator();
                    while (keyIterator.hasNext()) {
                        EQField eqField = (EQField) eqFields.get(keyIterator.next());
                        if (!eqField.getValue().trim().equals(new String(new byte[]{127}))) {
                            if (paramResult.contains(eqField.getDefinition().getFieldName())) {
                                ap.put(eqField.getDefinition().getFieldName(), eqField.getValue());
                            }
                        }
                    }

                    result.add(ap);
                }
            } else {
                if (page < 2) {
                    promptResult = executePrompt("F", getSession());
                } else {
                    promptResult = executePrompt("F", getSession());
                    for (i = 1; i < page; i++) {
                        promptResult = (promptResult) && (executePrompt("N", getSession()));
                    }
                }

                if (promptResult) {
                    logger1.info("Prompt executed Succesfully");

                    int startIndex = getPrompt().getList().getPageStartIndex();
                    int endIndex = getPrompt().getList().getPageEndIndex();
                    for (i = startIndex; i < endIndex; i++) {
                        HashMap ap = new HashMap();
                        HashMap eqFields = getPrompt().getList().getRowFields(i);
                        Set keys = eqFields.keySet();
                        Iterator keyIterator = keys.iterator();
                        while (keyIterator.hasNext()) {
                            EQField eqField = (EQField) eqFields.get(keyIterator.next());
                            if (!eqField.getValue().trim().equals(new String(new byte[]{127}))) {
                                if (paramResult.contains(eqField.getDefinition().getFieldName())) {
                                    ap.put(eqField.getDefinition().getFieldName(), eqField.getValue());
                                }
                            }
                        }

                        result.add(ap);
                    }
                } else {
                    logger1.warn("Prompt executed Not Succesfully");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger1.error("session = " + getSession());
            logger1.error("error = " + e.getMessage());
        }
        logger1.info("banyak rec = " + result.size());
        return result;
    }

    public ArrayList<HashMap<String, String>> actEnquiry(String enquiryCode, HashMap<String, String> param, ArrayList<String> paramResult) {
        ArrayList result = new ArrayList();

        String k = "";
        String v = "";
        String kr = "";
        String vr = "";

        logger1.info("== enquiryCode = " + enquiryCode + " ==");
        try {
            logger1.info("== Assign enquiry to session  ==");
            setEnquiry((EQEnquiry) getSession().createEQObject(enquiryCode));
            logger1.info("== SetAuditUserID enquiry to session  ==");
            getEnquiry().setAuditUserID(getSession().getEquationUserIdentifier());

            logger1.info("== Populate field input ==");

            Iterator itr = param.keySet().iterator();
            while (itr.hasNext()) {
                k = (String) itr.next();
                v = (String) param.get(k);
                logger1.info("== key = " + k + ",value = " + v + " ==");
                getEnquiry().setFieldValue(k, v);
            }

            if (executeEnquiry(this.session)) {
                logger1.info("== Enquiry executed Succesfully ==");

                int startIndex = 0;
                int endIndex = -1;
                if (getEnquiry().getMetaData().getFunctionType() == 3) {
                    endIndex = getEnquiry().getFields().values().size();
                } else {
                    endIndex = getEnquiry().getList().getRows().size();
                }

                int functionType = getEnquiry().getMetaData().getFunctionType();
                for (int i = startIndex; i < endIndex; i++) {
                    HashMap ap = new HashMap();
                    HashMap eqFields = null;
                    Set keys = null;
                    if (functionType == 3) {
                        eqFields = getEnquiry().getFields();
                        keys = getEnquiry().getFields().keySet();
                    } else {
                        eqFields = getEnquiry().getList().getRowFields(i);
                        keys = eqFields.keySet();
                    }
                    Iterator keyIterator = keys.iterator();
                    while (keyIterator.hasNext()) {
                        EQField eqField = (EQField) eqFields.get(keyIterator.next());
                        if (!eqField.getValue().trim().equals(new String(new byte[]{127}))) {
                            if (paramResult.contains(eqField.getDefinition().getFieldName())) {
                                ap.put(eqField.getDefinition().getFieldName(), eqField.getValue());
                            }
                        }
                    }

                    result.add(ap);
                }
            } else {
                logger1.warn("Enquiry executed Not Succesfully,Status : " + getEnquiry().getStatusString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger1.error("error = " + e.getMessage());
        }
        logger1.info("banyak rec = " + result.size());
        return result;
    }

    public String actTransaction(String transactionCode, HashMap<String, String> param, int transAction) {
        return this.actTransaction(transactionCode, param, transAction, getEnvironment().getProperty("supervisor"), getEnvironment().getProperty("supervisorPassword"));
    }
    
    public String actTransaction(String transactionCode, HashMap<String, String> param, int transAction, String supervisor, String supervisorPassword) {
        String result = "";
        String k = "";
        String v = "";
        String kr = "";
        String vr = "";
        try {
            logger1.info("== Create Transaction ==");

            setTransaction((EQTransaction) getSession().createEQObject(transactionCode));

            logger1.info("== Set the audit user ID ==");

            getTransaction().setAuditUserID(getSession().getEquationUserIdentifier());

            logger1.info("== Reset ==");
            getTransaction().reset();

            logger1.info("== Populate Key ==");

            Iterator itr = param.keySet().iterator();
            while (itr.hasNext()) {
                k = (String) itr.next();
                v = (String) param.get(k);
                logger1.info("Field : " + k + ",Value : " + v);
                getTransaction().setFieldValue(k, v);
            }

            boolean autoOverride = true;

//            String supervisor = "";
//            String supervisorPassword = "";

            boolean useSuper = true;

            if (useSuper) {
//                supervisor = getEnvironment().getProperty("supervisor");
//                supervisorPassword = getEnvironment().getProperty("supervisorPassword");
                logger1.info("== Using supervisor [" + supervisor + "] ==");
                logger1.info("== Using supervisor Password [" + supervisorPassword + "] ==");
            }

            logger1.info("== Validate Transaction.... ==");
            this.status = validateTransaction(autoOverride, supervisor, supervisorPassword, getEnvironment(), getSession(), transAction);
            logger1.info("== Execute Transaction.... ==");
            this.status = executeTransaction(autoOverride, supervisor, supervisorPassword, transAction);

            if (this.status == 8) {
                result = "complete";
            } else {
                result = "error";
                Iterator i = this.transaction.getMessages().iterator();
                getErrorList().clear();

                while (i.hasNext()) {
                    EQFieldMessage err = (EQFieldMessage) i.next();
                    String stringError = err.getMessageID() + " = " + err.getDescription();
                    if (!this.errorList.contains(stringError)) {
                        getErrorList().add(stringError);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger1.error(e);
        }

        return result;
    }

    public String actValidateTransaction(String transactionCode, HashMap<String, String> param, int transAction) {
        return this.actValidateTransaction(transactionCode, param, transAction, getEnvironment().getProperty("supervisor"), getEnvironment().getProperty("supervisorPassword"));
    }
    
    public String actValidateTransaction(String transactionCode, HashMap<String, String> param, int transAction, String supervisor, String supervisorPassword) {
        String result = "";
        String k = "";
        String v = "";
        String kr = "";
        String vr = "";
        try {
            setTransaction((EQTransaction) getSession().createEQObject(transactionCode));

            getTransaction().setAuditUserID(getSession().getEquationUserIdentifier());

            getTransaction().reset();

            Iterator itr = param.keySet().iterator();
            while (itr.hasNext()) {
                k = (String) itr.next();
                v = (String) param.get(k);
                getTransaction().setFieldValue(k, v);
            }

            boolean autoOverride = true;

//            String supervisor = "";
//            String supervisorPassword = "";

            boolean useSuper = true;

            if (useSuper) {
//                supervisor = getEnvironment().getProperty("supervisor");
//                supervisorPassword = getEnvironment().getProperty("supervisorPassword");
                logger1.info("Using supervisor [" + supervisor + "]");
                logger1.info("Using supervisor Password [" + supervisorPassword + "]");
            }

            logger1.info("****** Validate Transaction....");
            this.status = validateTransaction(autoOverride, supervisor, supervisorPassword, getEnvironment(), getSession(), transAction);

            if (this.status == 7) {
                result = "complete";
            } else {
                result = "error,<br/>";
                Iterator i = this.transaction.getMessages().iterator();

                while (i.hasNext()) {
                    EQFieldMessage err = (EQFieldMessage) i.next();

                    result = result.concat(err.getFormattedMessage() + "<br/>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean executePrompt(String action, EQSessionImpl es) throws EQException {
        boolean successful = false;

        if (action.equals("F")) {
            successful = getPrompt().retrieve(es);
        } else if (action.equals("N")) {
            successful = getPrompt().getList().nextPage();
            if (getPrompt().getStatus() == 2) {
                successful = getPrompt().retrieve(es);
            }

        } else if (action.equals("P")) {
            successful = getPrompt().getList().previousPage();
            if (getPrompt().getStatus() == 2) {
                successful = getPrompt().retrieve(es);
            }
        } else {
            successful = false;
        }

        return successful;
    }

    public boolean executeEnquiry(EQSessionImpl es) throws EQException {
        boolean successful = false;

        successful = getEnquiry().retrieve(es);

        while (getEnquiry().getStatus() == 2) {
            successful = getEnquiry().retrieve(es);
        }

        if (getEnquiry().getMetaData().getFunctionType() == 3) {
            logger1.info("enquiry field : " + getEnquiry().getFields().values().size());
        } else {
            logger1.info("enquiry list : " + getEnquiry().getList().getRows().size());
        }

        return successful;
    }

    private void printMessages(EQTransaction trans) {
        logger1.info("=== Status = " + trans.getStatusString() + "====");
        
        if (trans.getStatusString().equals("SQL ERROR")) {
            try {
                logger1.error("job number: " + this.session.getJobNumber());
                logger1.error(this.session == null ? "session is null" : "session not null");
                logger1.error(this.session.isLoggedOn() ? "session logged on" : "session not logged on");
                logger1.error(this.session.getConnection().isClosed() ? "session connection closed" : "session connection open");
            } catch (Exception e) {
                logger1.error(e.getMessage());
            }
        }

        Iterator i = trans.getMessages().iterator();
        int op = 0;
        while (i.hasNext()) {
            EQMessage msg = (EQMessage) i.next();
            String txt = msg.getFormattedMessage();
            if ((msg instanceof EQFieldMessage)) {
                txt = ((EQFieldMessage) msg).getFormattedMessage();
                op++;
                logger1.info("****Messege " + String.valueOf(op) + txt);
            }
        }
    }

    private int validateTransaction(boolean autoOverride, String supervisor, String supervisorPassword, EQEnvironment ev, EQSessionImpl es, int transAction)
            throws EQException {
        getTransaction().setAutoOverride(autoOverride);

        boolean success = true;
        if (transAction == 3) {
            success = getTransaction().delete(es, 1);
        } else if (transAction == 2) {
            success = getTransaction().maintain(es, 1);
        } else {
            success = getTransaction().add(es, 1);
        }

        printMessages(getTransaction());

        int initialNumberMessages = 0;
        boolean continueProcessing = true;
        do {
            initialNumberMessages = getTransaction().getMessages().size();

            if (getTransaction().getStatus() == 5) {
                continueProcessing = getTransaction().override(es);

                printMessages(getTransaction());
            } else if (getTransaction().getStatus() == 3) {
                getTransaction().setSupervisor(es, supervisor, supervisorPassword.toCharArray(), "");

                continueProcessing = getTransaction().override(es);

                getTransaction().setSupervisor(es, "", "".toCharArray(), "");

                printMessages(getTransaction());
            } else {
                continueProcessing = false;
            }
        } while ((getTransaction().getMessages().size() != initialNumberMessages) && (continueProcessing));

        return getTransaction().getStatus();
    }

    private int executeTransaction(boolean autoOverride, String supervisor, String supervisorPassword, int transAction)
            throws EQException {
        getTransaction().setAutoOverride(autoOverride);

        if (transAction == 3) {
            getTransaction().delete(getSession(), 2);
        } else if (transAction == 2) {
            getTransaction().maintain(getSession(), 2);
        } else {
            getTransaction().add(getSession(), 2);
        }

        printMessages(getTransaction());

        int initialNumberMessages = 0;
        boolean continueProcessing = true;
        do {
            initialNumberMessages = getTransaction().getMessages().size();

            if (getTransaction().getStatus() == 5) {
                continueProcessing = getTransaction().override(getSession());

                printMessages(getTransaction());
            } else if (getTransaction().getStatus() == 3) {
                getTransaction().setSupervisor(getSession(), supervisor, supervisorPassword.toCharArray(), "");

                continueProcessing = getTransaction().override(getSession());

                getTransaction().setSupervisor(getSession(), "", "".toCharArray(), "");

                printMessages(getTransaction());
            } else {
                continueProcessing = false;
            }
        } while ((getTransaction().getMessages().size() != initialNumberMessages) && (continueProcessing));

        return getTransaction().getStatus();
    }

    public void printInputFields(EQTransaction trans) {
        logger1.info("All input fields:");
        Iterator i = trans.getFields().values().iterator();
        while (i.hasNext()) {
            EQField eqf = (EQField) (EQField) i.next();
            if (eqf.getDefinition().isInputCapable()) {
                String fieldName = eqf.getDefinition().getFieldName();
                String fieldValue = eqf.getValue();
                logger1.info(fieldName + ": [" + fieldValue + "]");
            }
        }
    }

    public HashMap<String, String> getInputFields(EQTransaction trans) {
        logger1.info("Get all input fields:");
        Iterator i = trans.getFields().values().iterator();
        HashMap result = new HashMap();
        while (i.hasNext()) {
            EQField eqf = (EQField) (EQField) i.next();
            if (eqf.getDefinition().isInputCapable()) {
                String fieldName = eqf.getDefinition().getFieldName();
                String fieldValue = eqf.getValue();
                logger1.info(fieldName + ": [" + fieldValue + "]");
                result.put(fieldName, fieldValue);
            }
        }

        return result;
    }

    public void printOutputFields(EQTransaction trans) {
        logger1.info("All output fields:");
        Iterator i = trans.getFields().values().iterator();

        while (i.hasNext()) {
            EQField eqf = (EQField) (EQField) i.next();
            if (!eqf.getDefinition().isInputCapable()) {
                String fieldName = eqf.getDefinition().getFieldName();
                String fieldValue = eqf.getValue();
                logger1.info(fieldName + ": [" + fieldValue + "]");
            }
        }
    }

    public HashMap<String, String> getOutputFields(EQTransaction trans) {
        logger1.info("Get all input fields:");
        Iterator i = trans.getFields().values().iterator();
        HashMap result = new HashMap();
        while (i.hasNext()) {
            EQField eqf = (EQField) (EQField) i.next();
            if (!eqf.getDefinition().isInputCapable()) {
                String fieldName = eqf.getDefinition().getFieldName();
                String fieldValue = eqf.getValue();
                logger1.info(fieldName + ": [" + fieldValue + "]");
                result.put(fieldName, fieldValue);
            }
        }

        return result;
    }

    public List<Map<String, String>> getLastOutputFieldTransaction(EQTransaction trans, ArrayList<String> paramResult) {
        List result = new ArrayList();
        String kr = "";
        try {
            int startIndex = trans.getList().getPageStartIndex();
            int endIndex = trans.getList().getPageEndIndex();

            for (int i = startIndex; i < endIndex; i++) {
                Map ap = new HashMap();

                Iterator itrRes = paramResult.iterator();
                while (itrRes.hasNext()) {
                    kr = (String) itrRes.next();
                    ap.put(kr, trans.getList().getFieldValue(i, kr));
                }

                result.add(ap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Map<String, String>> getLastOutputField(EQObject trans, ArrayList<String> paramResult) {
        List result = new ArrayList();
        String kr = "";
        try {
            int startIndex = trans.getList().getPageStartIndex();
            int endIndex = trans.getList().getPageEndIndex();

            for (int i = startIndex; i < endIndex; i++) {
                Map ap = new HashMap();

                Iterator itrRes = paramResult.iterator();
                while (itrRes.hasNext()) {
                    kr = (String) itrRes.next();
                    ap.put(kr, trans.getList().getFieldValue(i, kr));
                }

                result.add(ap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
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
    
    public EQPrompt getPrompt() {
        return this.prompt;
    }

    public void setPrompt(EQPrompt prompt) {
        this.prompt = prompt;
    }

    public int getPromptNumRows() {
        int numRows = 0;
        boolean hasNext = true;

        while (hasNext) {            
            hasNext = this.prompt.getList().nextPage() && !this.prompt.getList().isLastPage();
            numRows += hasNext ? this.prompt.getList().getPageSize() : this.prompt.getList().getNumRows();
        }

        return numRows;
    }
    
    public int getPromptNumPages() {
        int numPage = this.getPromptNumRows() / this.prompt.getList().getPageSize();
        
        if (this.getPromptNumRows() % this.prompt.getList().getPageSize() > 0) {
            numPage++;
        }
        
        return numPage;
    }

    public EQTransaction getTransaction() {
        return this.transaction;
    }

    public void setTransaction(EQTransaction transaction) {
        this.transaction = transaction;
    }

    public EQEnquiry getEnquiry() {
        return this.enquiry;
    }

    public void setEnquiry(EQEnquiry enquiry) {
        this.enquiry = enquiry;
    }

    public ArrayList<String> getErrorList() {
        return this.errorList;
    }

    public boolean isReady() {
        return this.ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
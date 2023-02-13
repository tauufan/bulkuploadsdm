/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.service;

import com.bankbjb.itcore.bulkupload.common.indEqUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author C994
 */
public class EquationService {
    
    protected static final Logger logger = Logger.getLogger(EquationService.class);
    private static final String configPath = "EQ.config";
    private static indEqUtil transaction = null;
    
    private static void connectToEquation() {
        try {
            transaction = new indEqUtil(configPath);
        } catch (Exception exc) {
            logger.error("create transaction failed: " + exc.getMessage());
        }
    }
    
    public static indEqUtil getEQTransaction() {
        return getEQTransaction(true);
    }
    
    public static indEqUtil getEQTransaction(boolean tryConnect) {
        if (tryConnect) {
            if (transaction == null) {
                connectToEquation();
            }

            try {
                if (transaction.getSession() == null || transaction.getSession().getConnection().isClosed()) {
                    transaction = null;
                    connectToEquation();
                }
            } catch (Exception e) {
                logger.error(e.getStackTrace());
            }
        }
        
        return transaction;
    }
    
    public static void setEQTransaction(indEqUtil trans) {
        transaction = trans;
    }
    
}

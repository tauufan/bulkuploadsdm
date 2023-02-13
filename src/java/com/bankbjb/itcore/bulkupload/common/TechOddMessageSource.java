/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.common;

import javax.annotation.PostConstruct;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

/**
 *
 * @author C994
 */
@Component("messageSource")
public class TechOddMessageSource extends ReloadableResourceBundleMessageSource {
    
    @PostConstruct
    public void init() {
        setBasename("/WEB-INF/messages/messages");
    }
    
}

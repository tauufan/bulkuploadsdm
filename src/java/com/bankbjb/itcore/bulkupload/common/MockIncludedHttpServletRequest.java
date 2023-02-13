
package com.bankbjb.itcore.bulkupload.common;

import javax.servlet.DispatcherType;

import org.springframework.mock.web.MockHttpServletRequest;


public class MockIncludedHttpServletRequest extends MockHttpServletRequest {
	
	public MockIncludedHttpServletRequest() {
		super();
	}
	
	public DispatcherType getDispatcherType() {		
		return DispatcherType.INCLUDE;
	}

	public boolean isAsyncSupported() {
		return false;
	}
}

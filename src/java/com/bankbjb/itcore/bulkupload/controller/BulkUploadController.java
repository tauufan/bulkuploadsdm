/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.controller;

import com.bankbjb.itcore.bulkupload.common.SwallowingJspRenderer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.bankbjb.itcore.bulkupload.service.ConfigService;
import com.bankbjb.itcore.bulkupload.service.EquationService;

import com.bankbjb.itcore.bulkupload.service.LoginService;
import com.bankbjb.itcore.bulkupload.service.SQLConnectionService;
import com.bankbjb.itcore.bulkupload.util.MyStringUtils;
import com.misys.equation.common.core.EQFieldMessage;

import jxl.*;
import jxl.read.biff.BiffException;

import flexjson.JSONSerializer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 *
 * @author C994
 */
@Controller
@RequestMapping("/index")
//@SessionAttributes("bulkUpload")
public class BulkUploadController {
    
    protected final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(method = {RequestMethod.GET})
    public ModelAndView index(HttpServletRequest request) {
        Map pPost = request.getParameterMap();
        
        ModelAndView modelAndView = new ModelAndView(ConfigService.getProperty("template.path") + ConfigService.getProperty("template.layout") + "/layout");
        modelAndView.addObject("jsfile", "bulk-upload/script.jsp");
        modelAndView.addObject("pgtitle", "bulk_upload");
        modelAndView.addObject("pgcontent", "bulk-upload/index.jsp");
        modelAndView.addObject("activeMenu", "bulk-upload");

        return modelAndView;
    }
    
    @RequestMapping(value = "form", method = {RequestMethod.POST})
    public @ResponseBody String showForm(@RequestParam(value="mode", required=false, defaultValue="") String mode, HttpServletRequest request) {
        String out = "";
        boolean success = false;
        
        RequestContext ctx = new RequestContext(request);
        
        if (mode.equals("OTO") || mode.equals("OTM")) {
            HashMap data = new HashMap();
            data.put("mode", mode);
            if (mode.equals("OTM")) {
                ArrayList reks = new ArrayList();
                HashMap row = new HashMap();
                
                SQLConnectionService sqlConnection = new SQLConnectionService("jdbc");
                if (sqlConnection.createConnection()) {
                    LoginService loginService = new LoginService();
                    HashMap userData = loginService.getUserSession(request);
                    String owner = (String) userData.get("userCabang");
                    
                    try {
                        String query = "SELECT * FROM debet_accounts WHERE debet_acc_owner = ? AND is_active = ?";
                        PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                        pstmt.setString(1, owner);
                        pstmt.setInt(2, 1);

                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            row = new HashMap();
                            row.put("rekening", rs.getString("debet_acc_no"));
                            row.put("namaNasabah", rs.getString("debet_acc_name"));
                            reks.add(row);
                        }
                    } catch (SQLException e) {
                        logger.error("SQLException: " + e.getMessage());
                    } finally {
                        sqlConnection.closeConnection();
                    }
                }

                data.put("reks", reks);
            }

            try {
                Locale locale = new java.util.Locale("id", "ID");
                String requestedLocale = (String) request.getAttribute("locale");

                // If the user passes in a ?locale=foo line, parse it and 
                // use that as a locale
                if(requestedLocale != null && StringUtils.hasText(requestedLocale)) {
                    if(requestedLocale.contains("_")) {
                        String[] parts = requestedLocale.split("_", 2);
                        locale = new Locale(parts[0], parts[1]);
                    } else {
                        locale = new Locale(requestedLocale);
                    }
                }

                SwallowingJspRenderer jspRenderer = new SwallowingJspRenderer();
                jspRenderer.setServletContext(request.getServletContext());
                out = jspRenderer.render("bulk-upload/form", data, locale);
                success = true;
            } catch (Exception e) {
                logger.error(e.getStackTrace());
            }
        }
        
        HashMap output = new HashMap();
        output.put("status", success ? "success" : "attention");
        output.put("message", success ? "" : ctx.getMessage("bulk_upload.mode_not_valid"));
        output.put("content", out);
        
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.deepSerialize(output);
        
        return jsonString;
    }
    
    @RequestMapping(value = "validate-sundry", method = {RequestMethod.POST})
    public @ResponseBody String validateSundryItem(@RequestParam(value="txtAccount", required=true) String txtAccount,
                                                @RequestParam(value="txtAccountCR", required=true) String txtAccountCR,
                                                @RequestParam(value="txtTransAmount", required=true) String txtTransAmount,
                                                @RequestParam(value="txtDesc", required=true) String txtDesc,
                                                @RequestParam(value="txtKodeKantor", required=true) String txtKodeKantor,
                                                @RequestParam(value="txtUser", required=true) String txtUser,
                                                @RequestParam(value="txtNoRef", required=true) String txtNoRef,
                                                @RequestParam(value="mode", required=true) String mode,
                                                HttpServletRequest request) {
        String status = "";
        String message = "";
        String supervisor_code = "";
        String supervisor_pass = "";
        
        RequestContext ctx = new RequestContext(request);

        ArrayList errorList = new ArrayList();
        HashMap<String, String> output_fields = new HashMap<String, String>();
        
        LoginService loginService = new LoginService();
        HashMap userData = loginService.getUserSession(request);
        String userUID = (String) userData.get("userUID");
        
        SQLConnectionService sqlConnection = new SQLConnectionService("jdbc");
        if (sqlConnection.createConnection()) {
            try {
                String query = "SELECT * FROM supervisors WHERE user_id = ?";
                PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, userUID);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    supervisor_code = rs.getString("supervisor_code");
                    supervisor_pass = rs.getString("supervisor_pass");
                }
            } catch (SQLException e) {
                logger.error("SQLException: " + e.getMessage());
            } finally {
                sqlConnection.closeConnection();
            }
        }
        
        try {
            HashMap param = new HashMap();
            param.put("ZLEAN", txtAccount);
//            param.put("ZLREF", "zzz999");
            param.put("ZLREF", txtNoRef);
            param.put("ZLTCD1", "011");
            param.put("ZLAMZ1", txtTransAmount);
            param.put("ZLEAN1", txtAccountCR);
            param.put("ZLTCD2", "511");
            
            param.put("ZLNR1", MyStringUtils.leftString(txtDesc, 35));
            param.put("ZLNR2", MyStringUtils.leftString(txtAccountCR, 35));
            param.put("ZLNR3", MyStringUtils.leftString(txtKodeKantor, 35));
            param.put("ZLNR4", MyStringUtils.leftString(txtUser, 35));
            param.put("ZLNR5", MyStringUtils.leftString(txtDesc, 35));
            param.put("ZLNR6", "");
            param.put("ZLNR7", MyStringUtils.leftString(txtKodeKantor, 35));
            param.put("ZLNR8", MyStringUtils.leftString(txtUser, 35));
            
            String result = EquationService.getEQTransaction().actValidateTransaction("ITA", param, 1, supervisor_code, supervisor_pass);

            if (result.equalsIgnoreCase("complete")) {
                output_fields = EquationService.getEQTransaction().getOutputFields(EquationService.getEQTransaction().getTransaction());
                status = "success";
                message = txtAccountCR + "|" + output_fields.get("ZLSHN2") + "|" + output_fields.get("ZLTCN2") + "|" + 
                        output_fields.get("ZLCCY2") + "|" + txtTransAmount + "|" + txtDesc + "|" + txtAccount + "|" + output_fields.get("ZLSHN1") +
                        "|" + txtKodeKantor + "|" + txtUser + "|" + txtNoRef;
            } else {
                status = "error";
                message = txtNoRef + ", " + (mode.equals("OTO") ? txtAccount + ", " : "") + txtAccountCR + ", " + txtTransAmount + ", " + txtDesc + ", " + txtKodeKantor + ", " + txtUser + "<br />";
                message += ctx.getMessage("bulk_upload.validate_failed") + "<br />";
                errorList = EquationService.getEQTransaction().getErrorList().size() > 0 ? EquationService.getEQTransaction().getErrorList() : EquationService.getEQTransaction().getTransaction().getMessages();
                Iterator itr = errorList.iterator();
                while (itr.hasNext()) {
                    Object m = itr.next();
                    try {
                        message += "<li>" + (String) m + "</li>";
                    } catch (Exception e) {
                        message += "<li>" + ((EQFieldMessage) m).toString() + "</li>";
                    }
                }
                message = "<ul>" + message + "</ul>";
            }
        } catch (Exception exc) {
            status = "error";
            message = txtNoRef + ", " + (mode.equals("OTO") ? txtAccount + ", " : "") + txtAccountCR + ", " + txtTransAmount + ", " + txtDesc + ", " + txtKodeKantor + ", " + txtUser + "<br />";
            message += ctx.getMessage("bulk_upload.validate_failed") + " [exception]<br />";
            message += exc.getMessage();
            logger.error("actValidateTransaction failed: " + exc.getMessage());
        }
        
        HashMap<String, String> outputObject = new HashMap<String, String>();
        outputObject.put("status", status);
        outputObject.put("message", message);
        
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.serialize(outputObject);
        
        return jsonString;
    }
    
    @RequestMapping(value = "form-post", method = {RequestMethod.POST})
    public @ResponseBody String prosesForm(@RequestParam(value="accdb", required = false, defaultValue = "") String accdb,
                                                @RequestParam(value="accnamedb", required = false, defaultValue = "") String accnamedb,
                                                @RequestParam(value="acccr", required = false, defaultValue = "") String acccr,
                                                @RequestParam(value="accnamecr", required = false, defaultValue = "") String accnamecr,
                                                @RequestParam(value="amo", required = false, defaultValue = "") String amo,
                                                @RequestParam(value="dsc", required = false, defaultValue = "") String dsc,
                                                @RequestParam(value="kodekantor", required = false, defaultValue = "") String kodekantor,
                                                @RequestParam(value="user", required = false, defaultValue = "") String user,
                                                @RequestParam(value="trf", required = false, defaultValue = "") String trf,
                                                @RequestParam(value="mode", required=true) String mode,
                                                HttpServletRequest request) {
        String status = "";
        String message = "";
//        String trf = "";
        String supervisor_code = "";
        String supervisor_pass = "";
        
        RequestContext ctx = new RequestContext(request);
        
//        Format formatter = new SimpleDateFormat("MMyy");
//        String monyer = formatter.format(new Date());

        ArrayList errorList = new ArrayList();
        HashMap param = new HashMap();

//        try {
//            param.put("ZLREFC", monyer + "-*");
//
//            ArrayList paramResult = new ArrayList();
//            paramResult.add("ZLREFS");
//
//            ArrayList result = EquationService.getEQTransaction().actPrompt("LR", "X001", param, paramResult);
//
//            if (result.size() > 0) {
//                Iterator itr = result.iterator();
//                while (itr.hasNext()) {
//                    HashMap hash = (HashMap) itr.next();
//
//                    trf = (String) hash.get("ZLREFS");
//                }
//                String[] arrTransRef = trf.split("-");
//                trf = String.valueOf(Integer.parseInt(arrTransRef[1]) + 1);
//            } else {
//                trf = "1";
//            }
//            while (trf.length() < 11) {
//                trf = "0" + trf;
//            }
//            trf = monyer + "-" + trf;
//        } catch (Exception exc) {
//            logger.error("actPrompt failed: " + exc.getMessage());
//        }

        if (param.size() > 0) {
            param.clear();
        }
        
        LoginService loginService = new LoginService();
        HashMap userData = loginService.getUserSession(request);
        String userUID = (String) userData.get("userUID");
        
        SQLConnectionService sqlConnection = new SQLConnectionService("jdbc");
        if (sqlConnection.createConnection()) {
            try {
                String query = "SELECT * FROM supervisors WHERE user_id = ?";
                PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, userUID);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    supervisor_code = rs.getString("supervisor_code");
                    supervisor_pass = rs.getString("supervisor_pass");
                }
            } catch (SQLException e) {
                logger.error("SQLException: " + e.getMessage());
            } finally {
                sqlConnection.closeConnection();
            }
        }

        try {
            String result = "";

            param.put("ZLEAN", accdb);
            param.put("ZLREF", trf);
            param.put("ZLTCD1", "011");
            param.put("ZLAMZ1", String.valueOf(amo));
            param.put("ZLEAN1", acccr);
            param.put("ZLTCD2", "511");
            
            param.put("ZLNR1", MyStringUtils.leftString(dsc, 35));
            param.put("ZLNR2", MyStringUtils.leftString(acccr + " " + accnamecr, 35));
            param.put("ZLNR3", MyStringUtils.leftString(kodekantor, 35));
            param.put("ZLNR4", MyStringUtils.leftString(user, 35));
            param.put("ZLNR5", MyStringUtils.leftString(dsc, 35));
            param.put("ZLNR6", MyStringUtils.leftString(accnamedb, 35));
            param.put("ZLNR7", MyStringUtils.leftString(kodekantor, 35));
            param.put("ZLNR8", MyStringUtils.leftString(user, 35));
            
//            String[] arrKet = dsc.split(" ");
//            String tempKet = "";
//            int idxKet = 1;
//            for (int j = 0; j < arrKet.length; j++) {
//                String xx = (tempKet.length() > 0 ? " " : "") + arrKet[j];
//                if ((tempKet + xx).length() <= 35) {
//                    tempKet += xx;
//                } else if (idxKet <= 4) {
//                    param.put("ZLNR" + String.valueOf(idxKet), tempKet);
//                    tempKet = "";
//                    idxKet++;
//                }
//                
//                if (j == arrKet.length-1 && idxKet <= 4) {
//                    param.put("ZLNR" + String.valueOf(idxKet), tempKet);
//                }
//            }

            result = EquationService.getEQTransaction().actTransaction("ITA", param, 1, supervisor_code, supervisor_pass);

            if (result.equalsIgnoreCase("complete")) {
                status = "success";
                message = ctx.getMessage("bulk_upload.save_success");
            } else {
                status = "error";
                message = trf + ", " + (mode.equals("OTO") ? accdb + ", " : "") + acccr + ", " + amo + ", " + dsc + ", " + kodekantor + ", " + user + "<br />" + ctx.getMessage("bulk_upload.save_failed") + "<br />";
                errorList = EquationService.getEQTransaction().getErrorList();
                Iterator itr = errorList.iterator();
                while (itr.hasNext()) {
                    message += "<li>" + (String) itr.next() + "</li>";
                }
            }
        } catch (Exception exc) {
            status = "error";
            message = trf + ", " + (mode.equals("OTO") ? accdb + ", " : "") + acccr + ", " + amo + ", " + dsc + ", " + kodekantor + ", " + user + "<br />" + ctx.getMessage("bulk_upload.save_failed") + "<br />";
            message += exc.getMessage();
            logger.error("actTransaction failed: " + exc.getMessage());
        }
        
        HashMap<String, String> outputObject = new HashMap<String, String>();
        outputObject.put("status", status);
        outputObject.put("message", message);
        
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.serialize(outputObject);
        
        return jsonString;
    }
    
    @RequestMapping(value = "upload-form", method = {RequestMethod.GET})
    public ModelAndView uploadForm() {
        ModelAndView modelAndView = new ModelAndView("bulk-upload/upload-form");

        return modelAndView;
    }
    
    @RequestMapping(value = "upload-form", method = {RequestMethod.POST})
    public @ResponseBody String prosesUpload(@RequestParam(value="mode", required=false, defaultValue="OTO") String mode,
                                                HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String status = "";
        String message = "";
        HashMap rows = new HashMap();
        
        RequestContext ctx = new RequestContext(request);
        
        String temppath = ConfigService.getProperty("bulkupload.temppath");
        int excelmaxsize = Integer.parseInt(ConfigService.getProperty("bulkupload.excelmaxsize"));
        
        if (request instanceof MultipartHttpServletRequest) {
            try {
                logger.info("== Start file upload ==");
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                MultipartFile file = multipartRequest.getFile("txtUploadFile");
                String fileName = null;
                InputStream inputStream = null;
                OutputStream outputStream = null;
                if (file.getSize() > 0) {
                    inputStream = file.getInputStream();
                    if (file.getSize() > excelmaxsize) {
                        logger.error("Max File Size Limit Exceeded: " + file.getSize());
                        status = "error";
                        message = String.format(ctx.getMessage("global.upload_max_size_limit"), String.valueOf(excelmaxsize / 1024 / 1024) + "MB");
                    } else {
                        logger.info("File size: " + file.getSize());
                        if (file.getContentType().equals("application/octet-stream") || file.getContentType().equals("application/vnd.ms-excel") || file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                            logger.info("File mime type: " + file.getContentType());
                            fileName = request.getServletContext().getRealPath("") + "/" + temppath + file.getOriginalFilename();
                            outputStream = new FileOutputStream(fileName);
                            logger.info("Filename: " + file.getOriginalFilename());

                            int readBytes = 0;
                            byte[] buffer = new byte[excelmaxsize];
                            while ((readBytes = inputStream.read(buffer, 0, excelmaxsize)) != -1) {
                                outputStream.write(buffer, 0, readBytes);
                            }
                            outputStream.close();
                            
                            // read excel
                            HashMap cols = null;
                            int ix = 0;
                            try {
                                Workbook workbook = Workbook.getWorkbook(new File(fileName));
                                Sheet sheet = null;
                                
                                for (int r = 0; r < workbook.getNumberOfSheets(); r++) {
                                    sheet = workbook.getSheet(r);
                                    ix = 1;
                                    while (ix < sheet.getRows() && !sheet.getCell("A" + String.valueOf(ix + 1)).getContents().isEmpty()) {
                                        ix++;
                                        cols = new HashMap();
                                        if (mode.equals("OTM")) {
                                            cols.put("txtNoRef", sheet.getCell("A" + String.valueOf(ix)).getContents());
                                            cols.put("txtAccountCR", sheet.getCell("B" + String.valueOf(ix)).getContents());
                                            cols.put("txtTransAmount", sheet.getCell("C" + String.valueOf(ix)).getContents());
                                            cols.put("txtDesc", sheet.getCell("D" + String.valueOf(ix)).getContents());
                                            cols.put("txtKodeKantor", sheet.getCell("E" + String.valueOf(ix)).getContents());
                                            cols.put("txtUser", sheet.getCell("F" + String.valueOf(ix)).getContents());
                                        } else {
                                            cols.put("txtNoRef", sheet.getCell("A" + String.valueOf(ix)).getContents());
                                            cols.put("txtAccount", sheet.getCell("B" + String.valueOf(ix)).getContents());
                                            cols.put("txtAccountCR", sheet.getCell("C" + String.valueOf(ix)).getContents());
                                            cols.put("txtTransAmount", sheet.getCell("D" + String.valueOf(ix)).getContents());
                                            cols.put("txtDesc", sheet.getCell("E" + String.valueOf(ix)).getContents());
                                            cols.put("txtKodeKantor", sheet.getCell("F" + String.valueOf(ix)).getContents());
                                            cols.put("txtUser", sheet.getCell("G" + String.valueOf(ix)).getContents());
                                        }
                                        rows.put(rows.size(), cols);
                                    }
                                }
                                
                                workbook.close();
                                status = "success";
                            } catch (BiffException e) {
                                status = "error";
                                message = e.getMessage();
                                logger.error(e.getMessage());
                            } catch (ArrayIndexOutOfBoundsException e) {
                                status = "error";
                                message = ctx.getMessage("bulk_upload.format_notvalid");
                                logger.error(message);
                            } catch (IOException e) {
                                status = "error";
                                message = e.getMessage();
                                logger.error(e.getMessage());
                            }
                        } else {
                            status = "error";
                            message = String.format(ctx.getMessage("global.upload_file_type_not_support"), "*.xls, *.xlsx");
                            logger.error("Mime type not supported: " + file.getContentType());
                        }
                    }
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                status = "error";
                message = e.getMessage();
            }
        } else {
            status = "error";
            message = ctx.getMessage("global.upload_no_file");
            logger.error("There are no file to upload");
        }
        
        HashMap outputObject = new HashMap();
        outputObject.put("status", status);
        outputObject.put("message", message);
        outputObject.put("rows", rows);
        
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.serialize(outputObject);
        
        return jsonString;
    }
    
    @RequestMapping(value = "export-log", method = {RequestMethod.POST})
    public void exportLog(@RequestParam(value="content", required=false, defaultValue="") String content,
                            @RequestParam(value="title", required=false, defaultValue="") String title,
                            HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline;filename='" + title + ".pdf'");
        
        content = content.replaceAll("<br>", "<br></br>").replaceAll("<br />", "<br></br>");
        
        try {
            StringBuffer buf = new StringBuffer();
            buf.append(content);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new StringBufferInputStream(buf.toString()));
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(doc, null);
            renderer.layout();
            OutputStream os = response.getOutputStream();
            renderer.createPDF(os);
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}

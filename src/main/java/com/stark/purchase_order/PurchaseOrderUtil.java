package com.stark.purchase_order;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.kie.api.runtime.process.ProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurchaseOrderUtil {
    private static Logger logger = LoggerFactory.getLogger(PurchaseOrderUtil.class);

    /**
     * Web Service request utility for purchase order
     * 
     * @param kcontext
     */
    public static void assignPurchaseOrderRequestToContext(ProcessContext kcontext) {
        logger.debug("About to assign variable for webservice request (Purchase Order) to process instance {}",
                kcontext.getProcessInstance().getId());

        String partCode = String.valueOf(kcontext.getVariable("partCode"));

        String sQuantity = String.valueOf(kcontext.getVariable("quantity"));
        Integer quantity = 0;

        try {
            quantity = Integer.parseInt(sQuantity);
        } catch (NumberFormatException e) {
            logger.error("Failed to read quantity from variables, got (" + sQuantity + ")", e);
        }

        PurchaseOrderRequest por = new PurchaseOrderRequest();
        por.setPartCode(partCode);
        por.setQuantity(quantity);

        ObjectMapper obj = new ObjectMapper();
        String result = "{}";

        try {
            result = obj.writeValueAsString(por);
        } catch (JsonProcessingException e) {
            logger.error("Error while processing Purchase Order Request", result);
        }

        kcontext.setVariable("wsJsonRequest", result);
        logger.debug("Json Request set as {} for Purchase Order Web Service", result);
    }

    /**
     * Utility to parse the purchase order web service response into kcontext
     * process variables.
     * 
     * @param kcontext
     */
    public static void assignPurchaseOrderRequestIdToContext(ProcessContext kcontext) {
        logger.debug("Parsing response from web service (Purchase Order) to process instance {}",
                kcontext.getProcessInstance().getId());

        Pattern p = Pattern.compile("\"purchaseOrderReceiverId\":\\s*\\\"?([a-zA-Z-\\d]+)\"?");
        String response = (String) kcontext.getVariable("wsJsonResponse");
        if (response == null) {
            logger.debug("No web service response found in kcontext for Purchase Order Web service");
            kcontext.setVariable("purchaseOrderRequestId", "N/A");
            return;
        }

        Matcher m = p.matcher(response);
        if (m.find()) {
            String purchaseOrderRequestId = m.group(1);
            kcontext.setVariable("purchaseOrderRequestId", purchaseOrderRequestId);
            logger.debug("Purchase Order Identifier from web service response as {}", purchaseOrderRequestId);
        } else {
            logger.debug("Purchase Order Identifier not found in response from web service: {}", response);
            kcontext.setVariable("purchaseOrderRequestId", "N/A");
        }
    }
}

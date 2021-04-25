package com.stark.purchase_order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.stark.test.MockProcessContext;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessContext;

public class PurchaseOrderUtilTest {
    @Test
    public void testPurchaseOrderRequest() {
        ProcessContext kcontext = new MockProcessContext();
        kcontext.setVariable("partCode", "A");
        kcontext.setVariable("quantity", 1);

        PurchaseOrderUtil.assignPurchaseOrderRequestToContext(kcontext);
        String expectedWsJsonRequest = "{\"partCode\":\"A\",\"quantity\":1}";
        assertEquals(expectedWsJsonRequest, kcontext.getVariable("wsJsonRequest"));
    }

    @Test
    public void testPurchaseOrderRequestWithNoPartCode() {
        ProcessContext kcontext = new MockProcessContext();
        kcontext.setVariable("quantity", 1);

        PurchaseOrderUtil.assignPurchaseOrderRequestToContext(kcontext);
        String expectedWsJsonRequest = "{\"partCode\":\"null\",\"quantity\":1}";
        assertEquals(expectedWsJsonRequest, kcontext.getVariable("wsJsonRequest"));
    }

    @Test
    public void testPurchaseOrderRequestIntegerPartCode() {
        ProcessContext kcontext = new MockProcessContext();
        kcontext.setVariable("partCode", 1);
        kcontext.setVariable("quantity", 1);

        PurchaseOrderUtil.assignPurchaseOrderRequestToContext(kcontext);
        String expectedWsJsonRequest = "{\"partCode\":\"1\",\"quantity\":1}";
        assertEquals(expectedWsJsonRequest, kcontext.getVariable("wsJsonRequest"));
    }

    @Test
    public void testPurchaseOrderRequestWithNoQuantity() {
        ProcessContext kcontext = new MockProcessContext();
        kcontext.setVariable("partCode", "A");

        PurchaseOrderUtil.assignPurchaseOrderRequestToContext(kcontext);
        String expectedWsJsonRequest = "{\"partCode\":\"A\",\"quantity\":0}";
        assertEquals(expectedWsJsonRequest, kcontext.getVariable("wsJsonRequest"));
    }

    @Test
    public void testPurchaseOrderRequestStringQuantity() {
        ProcessContext kcontext = new MockProcessContext();
        kcontext.setVariable("partCode", 1);
        kcontext.setVariable("quantity", "test");

        PurchaseOrderUtil.assignPurchaseOrderRequestToContext(kcontext);
        String expectedWsJsonRequest = "{\"partCode\":\"1\",\"quantity\":0}";
        assertEquals(expectedWsJsonRequest, kcontext.getVariable("wsJsonRequest"));
    }

    @Test
    public void testPurchaseOrderResponse() {
        ProcessContext kcontext = new MockProcessContext();
        kcontext.setVariable("wsJsonResponse",
                "{\"partCode\":\"A\",\"quantity\":1,\"purchaseOrderReceiverId\":\"Abc-123-cde\"}");
        PurchaseOrderUtil.assignPurchaseOrderRequestIdToContext(kcontext);
        assertEquals("Abc-123-cde", kcontext.getVariable("purchaseOrderRequestId"));
    }

    @Test
    public void testPurchaseOrderResponseNoIdentifier() {
        ProcessContext kcontext = new MockProcessContext();
        kcontext.setVariable("wsJsonResponse", "{\"partCode\":\"A\",\"quantity\":1}");
        PurchaseOrderUtil.assignPurchaseOrderRequestIdToContext(kcontext);
        assertEquals("N/A", kcontext.getVariable("purchaseOrderRequestId"));
    }

    @Test
    public void testPurchaseOrderResponseNotFound() {
        ProcessContext kcontext = new MockProcessContext();
        PurchaseOrderUtil.assignPurchaseOrderRequestIdToContext(kcontext);
        assertEquals("N/A", kcontext.getVariable("purchaseOrderRequestId"));
    }

    @Test
    public void testPurchaseOrderResponseNumericIdentifier() {
        ProcessContext kcontext = new MockProcessContext();
        kcontext.setVariable("wsJsonResponse", "{\"partCode\":\"A\",\"quantity\":1,\"purchaseOrderReceiverId\":1}");
        PurchaseOrderUtil.assignPurchaseOrderRequestIdToContext(kcontext);
        assertTrue("expected 1, received " + kcontext.getVariable("purchaseOrderRequestId"),
                1 == Integer.parseInt((String) kcontext.getVariable("purchaseOrderRequestId")));
    }
}

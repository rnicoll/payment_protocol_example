package uk.me.jrn.payment_protocol.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import com.google.protobuf.ByteString;

import freemarker.template.Template;

import com.dogecoin.protocols.payments.Protos.Output;
import com.dogecoin.protocols.payments.Protos.PaymentDetails;
import com.dogecoin.protocols.payments.Protos.PaymentRequest;
import org.hibernate.Session;

import uk.me.jrn.payment_protocol.model.Network;
import uk.me.jrn.payment_protocol.model.PurchaseOrder;

/**
 *
 * @author jrn
 */
public class OrderServlet extends AbstractServlet {    
    public static final long EXPIRE_INTERVAL = 60 * 60 * 1000; // One hour
    
    public static final String MIME_TYPE_BITCOIN_PAYMENT_REQUEST = "application/bitcoin-paymentrequest";
    public static final String MIME_TYPE_DOGECOIN_PAYMENT_REQUEST = "application/vnd.doge.payment.request";
    
    public static final int PAYMENT_DETAILS_VERSION = 1;
    
    private PaymentDetails buildPaymentDetails(final Output output, final NetworkParameters network) {
        final long secondsSinceEpoch = System.currentTimeMillis() / 1000;
        final PaymentDetails.Builder paymentDetailsBuilder = PaymentDetails.newBuilder();
        
        paymentDetailsBuilder.setGenesis(network.getGenesisBlock().getHashAsString());
        paymentDetailsBuilder.addOutputs(output);
        paymentDetailsBuilder.setTime(secondsSinceEpoch);
        paymentDetailsBuilder.setExpires(secondsSinceEpoch + EXPIRE_INTERVAL);
        
        return paymentDetailsBuilder.build();
    }

    private Output buildRequestOutput(final PurchaseOrder purchaseOrder, final NetworkParameters network)
            throws AddressFormatException {
        final Address address = new Address(network, purchaseOrder.getAddress());
        final Script script = ScriptBuilder.createOutputScript(address);
        final Output.Builder outputBuilder = Output.newBuilder();
        
        outputBuilder.setAmount(purchaseOrder.getAmount());
        outputBuilder.setScript(ByteString.copyFrom(script.getProgram()));
        
        return outputBuilder.build();
    }
    
    private PurchaseOrder popOrderFromPath(final Session session, final Queue<String> pathInfo)
        throws HttpThrowable {
        final String path = pathInfo.poll();
        
        if (null == path) {
            throw new HttpThrowable(HttpServletResponse.SC_NOT_FOUND, "No order ID provided in URL.");
        }
        
        // Validate the order ID
        try {
            UUID.fromString(path);
        } catch(IllegalArgumentException e) {
            throw new HttpThrowable(HttpServletResponse.SC_NOT_FOUND, "Could not parse order ID \""
                + path + "\".");
        }
        
        final PurchaseOrder order = (PurchaseOrder)session.get(PurchaseOrder.class, path);
        
        if (null == order) {
            throw new HttpThrowable(HttpServletResponse.SC_NOT_FOUND, "There is no order with ID \""
                + path + "\".");
        }
        
        return order;
    }

    @Override
    public Template doGet(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session)
            throws Exception, HttpThrowable {
        final Queue<String> path = getPath(request);
        
        // Drop the leading blank empty path element
        path.poll();
        
        final PurchaseOrder order = popOrderFromPath(session, path);
        final String view = path.poll();
        
        if (null != view) {
            switch(view) {
                case "request":
                    this.displayPaymentRequest(request, response, order);
                    return null;
            }
        }
        
        return this.displayOrder(request, response, root, session, order);
    }

    @Override
    public Template doPost(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session)
            throws Exception, HttpThrowable {
        final Queue<String> path = getPath(request);
        
        // Drop the leading blank empty path element
        path.poll();
        
        final PurchaseOrder order = popOrderFromPath(session, path);
        
        this.displayPaymentRequest(request, response, order);
        
        return null;
    }

    private PaymentRequest getPaymentRequest(final PaymentDetails paymentDetails)
            throws AddressFormatException {
        final PaymentRequest.Builder paymentRequestBuilder
                = PaymentRequest.newBuilder();
                
        paymentRequestBuilder.setPaymentDetailsVersion(PAYMENT_DETAILS_VERSION);
        paymentRequestBuilder.setSerializedPaymentDetails(paymentDetails.toByteString());
        
        return paymentRequestBuilder.build();
    }

    private Queue<String> getPath(final HttpServletRequest request) {
        final String pathInfo = request.getPathInfo();
        final String[] pathElements = pathInfo.split("/");
        
        return new ArrayDeque<String>(Arrays.asList(pathElements));
    }

    private Template displayOrder(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session, final PurchaseOrder order)
        throws IOException {
        root.put("order_id", order.getId());
        if (null != order.getMemo()) {
            root.put("memo", order.getMemo());
        }
        
        return this.getConfiguration().getTemplate("order.ftl");
    }
    
    private void displayPaymentRequest(final HttpServletRequest request, final HttpServletResponse response,
            final PurchaseOrder order)
            throws Exception, HttpThrowable {        
        final Network network = order.getNetwork();
        final NetworkParameters networkParameters = getNetworkParameters(network);
        final Output output;
        final PaymentDetails paymentDetails;
        final PaymentRequest paymentRequest;
        
        try {
            output = buildRequestOutput(order, networkParameters);
        } catch(AddressFormatException ex) {
            throw new ServletException(ex);
        }
        
        paymentDetails = buildPaymentDetails(output, networkParameters);
        paymentRequest = getPaymentRequest(paymentDetails);
        
        response.setContentType(MIME_TYPE_DOGECOIN_PAYMENT_REQUEST);
        
        final OutputStream out = response.getOutputStream();
        
        out.write(paymentRequest.toByteArray());
        
        return;
    }
}

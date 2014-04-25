package uk.me.jrn.payment_protocol.servlet;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.protobuf.ByteString;
import freemarker.template.Template;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bitcoin.protocols.payments.Protos.Output;
import org.bitcoin.protocols.payments.Protos.PaymentDetails;
import org.bitcoin.protocols.payments.Protos.PaymentRequest;
import org.hibernate.Session;
import uk.me.jrn.payment_protocol.model.Network;
import uk.me.jrn.payment_protocol.model.PurchaseOrder;
import uk.me.jrn.payment_protocol.servlet.throwable.InputValidationThrowable;

/**
 *
 * @author jrn
 */
public class PaymentRequestServlet extends AbstractServlet {    
    public static final long EXPIRE_INTERVAL = 60 * 60 * 1000; // One hour
    
    public static final String MIME_TYPE_BITCOIN_PAYMENT_REQUEST = "application/bitcoin-paymentrequest";
    public static final String MIME_TYPE_DOGECOIN_PAYMENT_REQUEST = "application/x-dogecoin-paymentrequest";
    
    public static final int PAYMENT_DETAILS_VERSION = 1;

    @Override
    public Template doGet(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session)
            throws Exception {
        return this.getConfiguration().getTemplate("default.ftl");
    }

    @Override
    public Template doPost(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session)
            throws Exception, HttpThrowable {
        final String path = request.getPathInfo();
        final UUID orderId;
        
        try {
            orderId = UUID.fromString(path);
        } catch(IllegalArgumentException e) {
            throw new HttpThrowable(HttpServletResponse.SC_NOT_FOUND);
        }
        
        final PurchaseOrder order = (PurchaseOrder)session.get(PurchaseOrder.class, orderId);
        
        if (null == order) {
            throw new HttpThrowable(HttpServletResponse.SC_NOT_FOUND);
        }
        
        final Network network = order.getNetwork();
        final NetworkParameters networkParameters = getNetworkParameters(network);
        final Output output;
        final PaymentDetails paymentDetails;
        final PaymentRequest paymentRequest;
        
        try {
            output = getRequestOutput(order, networkParameters);
        } catch(AddressFormatException ex) {
            throw new ServletException(ex);
        }
        
        paymentDetails = getPaymentDetails(request, output, network);
        paymentRequest = getPaymentRequest(request, paymentDetails);
        
        response.setContentType(MIME_TYPE_BITCOIN_PAYMENT_REQUEST);
        
        final OutputStream out = response.getOutputStream();
        
        out.write(paymentRequest.toByteArray());
        
        return null;
    }
    
    private PaymentDetails getPaymentDetails(final HttpServletRequest request,
            final Output output, final Network network) {
        final long secondsSinceEpoch = System.currentTimeMillis() / 1000;
        final PaymentDetails.Builder paymentDetailsBuilder = PaymentDetails.newBuilder();
        
        paymentDetailsBuilder.setNetwork(network.getCode());
        paymentDetailsBuilder.addOutputs(output);
        paymentDetailsBuilder.setTime(secondsSinceEpoch);
        paymentDetailsBuilder.setExpires(secondsSinceEpoch + EXPIRE_INTERVAL);
        
        return paymentDetailsBuilder.build();
    }

    private PaymentRequest getPaymentRequest(final HttpServletRequest request, final PaymentDetails paymentDetails)
            throws AddressFormatException {
        final PaymentRequest.Builder paymentRequestBuilder
                = PaymentRequest.newBuilder();
                
        paymentRequestBuilder.setPaymentDetailsVersion(PAYMENT_DETAILS_VERSION);
        paymentRequestBuilder.setSerializedPaymentDetails(paymentDetails.toByteString());
        
        return paymentRequestBuilder.build();
    }

    private Output getRequestOutput(final PurchaseOrder purchaseOrder, final NetworkParameters network)
            throws AddressFormatException {
        final Address address = new Address(network, purchaseOrder.getAddress());
        final Script script = ScriptBuilder.createOutputScript(address);
        final Output.Builder outputBuilder = Output.newBuilder();
        
        outputBuilder.setAmount(purchaseOrder.getAmount());
        outputBuilder.setScript(ByteString.copyFrom(script.getProgram()));
        
        return outputBuilder.build();
    }
    
}

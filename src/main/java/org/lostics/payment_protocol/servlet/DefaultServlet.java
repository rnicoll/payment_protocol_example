package org.lostics.payment_protocol.servlet;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.protobuf.ByteString;
import freemarker.template.Template;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bitcoin.protocols.payments.Protos.Output;
import org.bitcoin.protocols.payments.Protos.PaymentDetails;
import org.bitcoin.protocols.payments.Protos.PaymentRequest;

/**
 *
 * @author jrn
 */
public class DefaultServlet extends AbstractServlet {
    public enum Network {
        BitcoinMain("main"),
        BitcoinTest("test"),
        DogecoinMain("doge-main"),
        DogecoinTest("doge-test");
        
        private final String code;
        
                Network(final String setCode) {
            this.code = setCode;
        }
        
        public String getCode() {
            return this.code;
        }
    }
    
    
    public static final BigDecimal AMOUNT_PIP = new BigDecimal("0.00000001");
    
    public static final long EXPIRE_INTERVAL = 60 * 60 * 1000; // One hour
    
    public static final String MIME_TYPE_BITCOIN_PAYMENT_REQUEST = "application/bitcoin-paymentrequest";
    public static final String MIME_TYPE_DOGECOIN_PAYMENT_REQUEST = "application/x-dogecoin-paymentrequest";
    
    public static final int PAYMENT_DETAILS_VERSION = 1;

    @Override
    public Template doGet(HttpServletRequest request, HttpServletResponse response, Map<String, Object> root) throws Exception {
        return this.getConfiguration().getTemplate("default.ftl");
    }

    @Override
    public Template doPost(HttpServletRequest request, HttpServletResponse response, Map<String, Object> root)
            throws Exception {
        final Network network = getNetwork(request);
        final NetworkParameters networkParameters = getNetworkParameters(network);
        final Output output;
        final PaymentDetails paymentDetails;
        final PaymentRequest paymentRequest;
        
        try {
            output = getRequestOutput(request, networkParameters);
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

    private Network getNetwork(final HttpServletRequest request) {
        // FIXME: Resolve the actual selected network
        return Network.BitcoinMain;
    }

    private NetworkParameters getNetworkParameters(final Network network) {
        // FIXME: Resolve the actual selected network
        return new MainNetParams();
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

    private Output getRequestOutput(final HttpServletRequest request, final NetworkParameters network)
            throws AddressFormatException {
        final Address address = new Address(network, request.getParameter("address"));
        final BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        final Script script = ScriptBuilder.createOutputScript(address);
        final Output.Builder outputBuilder = Output.newBuilder();
        
        outputBuilder.setAmount(amount.divide(AMOUNT_PIP).longValue());
        outputBuilder.setScript(ByteString.copyFrom(script.getProgram()));
        
        return outputBuilder.build();
    }
    
}

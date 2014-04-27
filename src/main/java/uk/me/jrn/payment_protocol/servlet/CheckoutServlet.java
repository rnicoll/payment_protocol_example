package uk.me.jrn.payment_protocol.servlet;

import java.math.BigDecimal;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.NetworkParameters;

import freemarker.template.Template;
import org.hibernate.Session;
import org.hibernate.Transaction;

import uk.me.jrn.payment_protocol.model.Network;
import uk.me.jrn.payment_protocol.model.PurchaseOrder;
import uk.me.jrn.payment_protocol.servlet.throwable.InputValidationThrowable;

/**
 *
 * @author jrn
 */
public class CheckoutServlet extends AbstractServlet {
    @Override
    public Template doGet(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session)
            throws Exception {
        return this.getConfiguration().getTemplate("checkout.ftl");
    }

    @Override
    public Template doPost(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session)
            throws Exception {
        final long amount;
        final BigDecimal amountDbl;
        final Address address;
        final Network network;
        final NetworkParameters networkParams;
        
        try {
            network = this.getEnumParameter(request, "network", Network.class);
            amountDbl = this.getBigDecimalParameter(request, "amount");
        } catch(InputValidationThrowable e) {
            throw new ServletException(e.getMessage(), e);
        }
        
        amount = amountDbl.divide(AMOUNT_PIP).longValue();
        
        networkParams = getNetworkParameters(network);
        
        try {
            address = this.getAddressParameter(request, "address", networkParams);
        } catch(InputValidationThrowable e) {
            throw new ServletException(e.getMessage(), e);
        }
        
        final PurchaseOrder order = new PurchaseOrder();
        
        order.setNetwork(network);
        order.setAddress(address.toString());
        order.setAmount(amount);
        
        final Transaction tx = session.beginTransaction();
        
        session.save(order);
        
        tx.commit();
        
        
        root.put("order_id", order.getId());
        
        return this.getConfiguration().getTemplate("pay_now.ftl");
    }
}

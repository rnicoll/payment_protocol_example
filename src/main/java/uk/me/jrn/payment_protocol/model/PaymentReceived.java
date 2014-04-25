/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.me.jrn.payment_protocol.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author jrn
 */
@Entity
@Table(name="payment_received")
public class PaymentReceived {
    private int id;
    private PurchaseOrder order;
    private List<PaymentTransaction> paymentTransaction;
    private String refundToAddress;

    /**
     * @return the internal ID for this payment.
     */
    @Id
    @Column(name="payment_id")
    public int getId() {
        return id;
    }

    /**
     * @return the order this payment is for.
     */
    @ManyToOne
    public PurchaseOrder getOrder() {
        return order;
    }

    /**
     * @return the transaction(s) containing payment for this order.
     */
    @OneToMany
    public List<PaymentTransaction> getPaymentTransaction() {
        return paymentTransaction;
    }

    /**
     * @return the address to send a refund to, if applicable.
     */
    @Column(name="refund_to_address")
    public String getRefundToAddress() {
        return refundToAddress;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrder(PurchaseOrder order) {
        this.order = order;
    }

    public void setPaymentTransaction(List<PaymentTransaction> paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }

    public void setRefundToAddress(String refundTo) {
        this.refundToAddress = refundTo;
    }
}

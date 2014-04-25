package uk.me.jrn.payment_protocol.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A transaction sent from the remote user, as partial or full payment.
 */
@Entity
@Table(name="payment_transaction")
public class PaymentTransaction {
    private int id;
    private PaymentReceived receivedPayment;
    private String originalHash;
    private String confirmedHash;

    @Id
    @Column(name="transaction_internal_id")
    public int getId() {
        return id;
    }

    @ManyToOne
    public PaymentReceived getReceivedPayment() {
        return receivedPayment;
    }

    @Column(name="original_hash")
    public String getOriginalHash() {
        return originalHash;
    }

    @Column(name="confirmed_hash")
    public String getConfirmedHash() {
        return confirmedHash;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setReceivedPayment(PaymentReceived receivedPayment) {
        this.receivedPayment = receivedPayment;
    }

    public void setOriginalHash(String originalHash) {
        this.originalHash = originalHash;
    }

    public void setConfirmedHash(String confirmedHash) {
        this.confirmedHash = confirmedHash;
    }
}

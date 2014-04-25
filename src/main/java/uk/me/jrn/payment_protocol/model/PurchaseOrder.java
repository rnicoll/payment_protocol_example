package uk.me.jrn.payment_protocol.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a theoretical order which a payment 
 */
@Entity
@Table(name="purchase_order")
public class PurchaseOrder implements Serializable {
    private UUID id;
    private long amount;
    private Network network;
    private String address;
    private String memo;
    private List<PaymentReceived> paymentsReceived;

    /**
     * Get the ID of this order.
     * 
     * @return the ID of this order.
     */
    @Id
    @Column(name="order_id")
    public UUID getId() {
        return id;
    }

    /**
     * @return the address that payment for this order is expected at.
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the amount this order is for.
     */
    public long getAmount() {
        return amount;
    }

    /**
     * @return the network that payment for this order is expected on.
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * @return the memo attached to this order.
     */
    public String getMemo() {
        return memo;
    }

    /**
     * @return the paymentsReceived
     */
    public List<PaymentReceived> getPaymentsReceived() {
        return paymentsReceived;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * @param paymentsReceived the paymentsReceived to set
     */
    public void setPaymentsReceived(List<PaymentReceived> paymentsReceived) {
        this.paymentsReceived = paymentsReceived;
    }
}

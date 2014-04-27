package uk.me.jrn.payment_protocol.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * Represents a theoretical order which a payment 
 */
@Entity
@Table(name="purchase_order")
public class PurchaseOrder implements Serializable {
    private String id;
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
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name="order_id")
    public String getId() {
        return id;
    }

    /**
     * @return the address that payment for this order is expected at.
     */
    @Column(name="address")
    public String getAddress() {
        return address;
    }

    /**
     * @return the amount this order is for.
     */
    @Column(name="amount")
    public long getAmount() {
        return amount;
    }

    /**
     * @return the network that payment for this order is expected on.
     */
    @Column(name="network")
    @Enumerated(EnumType.STRING)
    public Network getNetwork() {
        return network;
    }

    /**
     * @return the memo attached to this order.
     */
    @Column(name="memo")
    public String getMemo() {
        return memo;
    }

    /**
     * @return the paymentsReceived
     */
    @OneToMany(mappedBy="order")
    public List<PaymentReceived> getPaymentsReceived() {
        return paymentsReceived;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setId(final String id) {
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
